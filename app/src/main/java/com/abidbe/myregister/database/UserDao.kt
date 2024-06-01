package com.abidbe.myregister.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(user: User)
    @Update
    fun update(user: User)
    @Delete
    fun delete(user: User)
    @Query("SELECT * from user ORDER BY id ASC")
    fun getAllUsers(): LiveData<List<User>>
}