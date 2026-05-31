package com.example.todoapp.repository

import androidx.lifecycle.LiveData
import com.example.todoapp.dao.TaskDao
import com.example.todoapp.models.Task


class TaskRepository(private val taskDao: TaskDao) {

    val allTasks: LiveData<List<Task>> = taskDao.getAllTasks()
    val pendingCount: LiveData<Int>    = taskDao.getPendingCount()

    suspend fun addTask(title: String) {
        taskDao.insertTask(Task(title = title.trim()))
    }

    suspend fun updateTask(task: Task, newTitle: String) {
        taskDao.updateTask(task.copy(title = newTitle.trim()))
    }

    suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(task)
    }

    suspend fun toggleComplete(task: Task) {
        taskDao.updateTask(task.copy(isCompleted = !task.isCompleted))
    }

    suspend fun clearCompleted() {
        taskDao.deleteCompletedTasks()
    }
}