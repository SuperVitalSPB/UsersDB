package com.example.metanitdatabase

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.supervital.usersdb.User

// Объект доступа к данным или DAO (Data Access Object)
// предоставляет способ доступа к данным, хранящимся в базе данных SQLite
@Dao
interface UserDao {
    @Query("SELECT * FROM users")
    fun getUsers(): LiveData<List<User>>

    @Query("SELECT count(*) FROM users WHERE userName = :nameUser")
    fun existsName(nameUser: String): LiveData<List<Int>>

    @Insert
    fun addUser(user: User)

    @Query("DELETE FROM users WHERE userId = :id")
    fun deleteUser(id: Int)
}