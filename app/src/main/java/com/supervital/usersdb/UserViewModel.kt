package com.example.metanitdatabase

import android.app.Application
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.supervital.usersdb.ResultCheck
import com.supervital.usersdb.User

class UserViewModel(application: Application) : ViewModel() {
    val userList: LiveData<List<User>>
    private val repository: UserRepository
    var userName = mutableStateOf("")
    var resultCheck = mutableStateOf(Any())
    var userAge = mutableStateOf("")

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
    }

    fun changeAge(value: String) {
        if(value.length > 0 && !value.isNumeric()) {
            return
        }
        userAge.value = value
        checkData()
    }

    fun checkData() {
        resultCheck.value = when {
            userName.value.isNullOrEmpty() -> ResultCheck.NameMustEnter()
            userAge.value.length == 0 || !userAge.value.isNumeric() -> ResultCheck.BadAge()
            else -> ResultCheck.ResultOk()
        }
/*
        if (resultCheck.value is ResultCheck.ResultOk) {
            repository.existsName(userName.value).observe() != 0 -> ResultCheck.NameExists()
            resultCheck.value
            LiveData<List<Int>>
        }
*/
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