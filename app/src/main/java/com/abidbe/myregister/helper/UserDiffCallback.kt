package com.abidbe.myregister.helper

import androidx.recyclerview.widget.DiffUtil
import com.abidbe.myregister.database.User

class UserDiffCallback(private val oldUserList: List<User>, private val newUserList: List<User>) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldUserList.size
    override fun getNewListSize(): Int = newUserList.size
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldUserList[oldItemPosition].id == newUserList[newItemPosition].id
    }
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldUser = oldUserList[oldItemPosition]
        val newUser = newUserList[newItemPosition]
        return oldUser.name == newUser.name && oldUser.date == newUser.date
    }
}