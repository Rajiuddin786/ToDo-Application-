package com.example.todoapp

import android.os.Bundle
import android.widget.Adapter
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.database.AppDatabase
import com.example.todoapp.databinding.ActivityMainBinding
import com.example.todoapp.repository.TaskRepository
import com.example.todoapp.viewmodel.TaskViewModel

class MainActivity : AppCompatActivity() {
    private var taskInput: EditText?=null
    private var addTaskButton: Button?=null
    private var tvTaskCount: TextView?=null
    private var tvClearCompleted:TextView?=null
    private lateinit var rvTasks: RecyclerView
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var taskViewModel: TaskViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        taskInput=findViewById(R.id.etTaskInput)
        addTaskButton=findViewById(R.id.btnAddTask)
        tvClearCompleted=findViewById(R.id.tvClearCompleted)
        tvTaskCount=findViewById(R.id.tvTaskCount)
        rvTasks=findViewById(R.id.rvTasks)

        val repository= TaskRepository(
            (application as TaskApplication).database.taskDao()
        )

        taskAdapter= TaskAdapter(
            onComplete = {task->taskViewModel.toggleComplete(task)},
            onEdit = {task->
                val input= EditText(this).apply {
                    setText(task.title)
                    setPadding(40,20,40,20)
                }
                AlertDialog.Builder(this)
                    .setTitle("Edit Task")
                    .setView(input)
                    .setPositiveButton("Save"){_,_ ->
                        taskViewModel.updateTask(task,input.text.toString())
                    }
                    .setNegativeButton("Cancel",null)
                    .show()
            },
            onDelete = {task->
                AlertDialog.Builder(this)
                    .setTitle("Delete Task")
                    .setMessage("Are you sure you want to Delete \"${task.title}\"?")
                    .setPositiveButton("Delete"){_,_ -> taskViewModel.deleteTask(task)}
                    .setNegativeButton("Cancel",null)
                    .show()
            }
        )

        rvTasks.layoutManager= LinearLayoutManager(this)
        rvTasks.adapter= taskAdapter

        taskViewModel= TaskViewModel(repository)
        addTaskButton?.setOnClickListener {
            val task=taskInput?.text.toString()
            if(task.isNotEmpty()){
                taskViewModel.addTask(task)
                taskInput?.setText("")
            }
        }

        tvClearCompleted?.setOnClickListener {
            taskViewModel.clearCompleted()
        }

        taskViewModel.tasks.observe(this){
            task->taskAdapter.submitList(task)
        }
        taskViewModel.pendingCount.observe(this) { count ->
            tvTaskCount?.text = "$count task${if (count != 1) "s" else ""} pending"
        }

        taskViewModel.taskError.observe(this) { error ->
            error?.let {
                android.widget.Toast.makeText(this, it, android.widget.Toast.LENGTH_SHORT).show()
                taskViewModel.clearError()
            }
        }
    }
}