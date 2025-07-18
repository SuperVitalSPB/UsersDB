package com.example.metanitdatabase

import android.app.Application
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.supervital.usersdb.R
import com.supervital.usersdb.ResultCheck
import com.supervital.usersdb.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserViewModel(val application: Application) : ViewModel() {
    val userList: LiveData<List<User>>
    private val repository: UserRepository
    var userName = mutableStateOf("")
    var resultCheck = mutableStateOf(Any())
    var userAge = mutableStateOf("")
    private val _foundUsers = MutableLiveData<Boolean>()
    val foundUsers: LiveData<Boolean> = _foundUsers

    val getStringUserNameExists = application.getString(R.string.user_name_exists)

    init {
        // Строит базу данных (если она еще не существует)
        val userDb = UserRoomDatabase.getInstance(application)
        val userDao = userDb.userDao

        repository = UserRepository(userDao)
        userList = repository.userList
    }

    fun changeName(value: String) {
        userName.value = value
        checkData()
        checkNameExists()
    }

    fun checkNameExists() {
        if (userName.value.isEmpty()) {
            _foundUsers.postValue(false)
            return
        }
        viewModelScope.launch (Dispatchers.IO ) {
            val isError = repository.getCountUsers(userName.value).get(0) != 0
            _foundUsers.postValue(isError)
            if (isError && resultCheck.value is ResultCheck.ResultOk) {
                resultCheck.value = ResultCheck.NameExists()
            }
        }
    }

    fun changeAge(value: String) {
        if(value.isNotEmpty() && !value.isNumeric()) {
            return
        }
        userAge.value = value
        checkData()
    }

    fun checkData() {
        resultCheck.value = when {
            userName.value.isEmpty() -> ResultCheck.NameMustEnter()
            userAge.value.length == 0 || !userAge.value.isNumeric() -> ResultCheck.BadAge()
            else -> ResultCheck.ResultOk()
        }
    }

    fun addUser() {
        repository.addUser(User(userName.value, userAge.value.toInt()))
    }

    fun deleteUser(id: Int) {
        repository.deleteUser(id)
    }

}

class UserViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return UserViewModel(application) as T
    }
}

fun String.isNumeric(): Boolean {
    val regex = "-?[0-9]+(\\.[0-9]+)?".toRegex()
    return this.matches(regex)
}