package com.example.todoapp

import android.app.Application
import com.example.todoapp.database.AppDatabase

class TaskApplication: Application() {
    val database by lazy { AppDatabase.getDatabase(this) }

    override fun onCreate() {
        super.onCreate()
    }
}