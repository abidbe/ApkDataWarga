package com.abidbe.myregister.ui.insert

import android.app.Application
import androidx.lifecycle.ViewModel
import com.abidbe.myregister.database.User
import com.abidbe.myregister.repository.UserRepository

class RegisterViewModel (application: Application) : ViewModel() {
    private val mUserRepository: UserRepository = UserRepository(application)
    fun insert(user: User) {
        mUserRepository.insert(user)
    }
    fun update(user: User) {
        mUserRepository.update(user)
    }
    fun delete(user: User) {
        mUserRepository.delete(user)
    }
}