package com.abidbe.myregister.ui.main

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.abidbe.myregister.database.User
import com.abidbe.myregister.repository.UserRepository

class MainViewModel(application: Application) : ViewModel() {
    private val mUserRepository: UserRepository = UserRepository(application)
    fun getAllUsers(): LiveData<List<User>> = mUserRepository.getAllUsers()
}