package com.example.metanitdatabase

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.supervital.usersdb.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// Репозиторий содержит код, который вызывает методы DAO для выполнения операций с базой данных.
class UserRepository(private val userDao : UserDao) {
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    val userList: LiveData<List<User>> = userDao.getUsers()

    fun getCountUsers(userName: String) = userDao.getCountUsers(userName)

    fun addUser(user: User) {
        coroutineScope.launch(Dispatchers.IO) {
            userDao.addUser(user)
        }
    }

    fun deleteUser(id: Int) {
        coroutineScope.launch(Dispatchers.IO) {
            userDao.deleteUser(id)
        }
    }


}