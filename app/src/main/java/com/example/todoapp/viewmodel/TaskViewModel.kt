package com.example.todoapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.models.Task
import com.example.todoapp.repository.TaskRepository
import kotlinx.coroutines.launch

class TaskViewModel(private val taskRepository: TaskRepository) : ViewModel() {

    // Directly backed by Room LiveData — auto-updates on DB change
    val tasks: LiveData<List<Task>>  = taskRepository.allTasks
    val pendingCount: LiveData<Int>  = taskRepository.pendingCount

    private val _taskError = MutableLiveData<String?>()
    val taskError: LiveData<String?> = _taskError

    fun addTask(title: String) {
        if (title.isBlank()) {
            _taskError.value = "Task title cannot be empty"
            return
        }
        viewModelScope.launch {
            taskRepository.addTask(title)
        }
    }

    fun updateTask(task: Task, newTitle: String) {
        if (newTitle.isBlank()) {
            _taskError.value = "Task title cannot be empty"
            return
        }
        viewModelScope.launch {
            taskRepository.updateTask(task, newTitle)
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            taskRepository.deleteTask(task)
        }
    }

    fun toggleComplete(task: Task) {
        viewModelScope.launch {
            taskRepository.toggleComplete(task)
        }
    }

    fun clearCompleted() {
        viewModelScope.launch {
            taskRepository.clearCompleted()
        }
    }

    fun clearError() {
        _taskError.value = null
    }
}