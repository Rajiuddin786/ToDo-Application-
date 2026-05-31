package com.example.todoapp.repository


import com.example.todoapp.dao.UserDao
import com.example.todoapp.models.User

class UserRepository(private val userDao: UserDao) {

    suspend fun signUp(name: String, email: String, password: String): Result<User> {
        val exists = userDao.doesEmailExist(email)
        if (exists > 0) {
            return Result.failure(Exception("Email already registered"))
        }
        val user = User(name = name, email = email, password = password)
        userDao.insertUser(user)
        return Result.success(user)
    }

    suspend fun login(email: String, password: String): Result<User> {
        val user = userDao.login(email, password)
            ?: return Result.failure(Exception("Invalid email or password"))
        return Result.success(user)
    }
}