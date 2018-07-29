package ro.razvan.todomvp

import android.content.Context
import ro.razvan.todomvp.data.source.TasksRepository
import ro.razvan.todomvp.data.source.local.TasksLocalDataSource
import ro.razvan.todomvp.data.source.local.ToDoDatabase
import ro.razvan.todomvp.data.source.remote.TasksRemoteDataSource
import ro.razvan.todomvp.util.AppExecutors

object Injection {
    fun provideTasksRepository(context: Context): TasksRepository {
        val database = ToDoDatabase.getInstance(context)
        return TasksRepository.getInstance(TasksRemoteDataSource, TasksLocalDataSource.getInstance(AppExecutors(), database.taskDao()))
    }
}