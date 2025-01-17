package com.abidbe.myregister.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.abidbe.myregister.database.User
import com.abidbe.myregister.database.UserDao
import com.abidbe.myregister.database.UserRoomDatabase
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class UserRepository(application: Application) {
    private val mUsersDao: UserDao
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()
    init {
        val db = UserRoomDatabase.getDatabase(application)
        mUsersDao = db.userDao()
    }
    fun getAllUsers(): LiveData<List<User>> = mUsersDao.getAllUsers()
    fun insert(user: User) {
        executorService.execute { mUsersDao.insert(user) }
    }
    fun delete(user: User) {
        executorService.execute { mUsersDao.delete(user) }
    }
    fun update(user: User) {
        executorService.execute { mUsersDao.update(user) }
    }
}