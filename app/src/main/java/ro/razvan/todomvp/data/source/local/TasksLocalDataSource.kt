package ro.razvan.todomvp.data.source.local

import ro.razvan.todomvp.data.Task
import ro.razvan.todomvp.data.source.TasksDataSource
import ro.razvan.todomvp.util.AppExecutors

class TasksLocalDataSource private constructor(
        val appExecutors: AppExecutors,
        val tasksDao: TasksDao
) : TasksDataSource {

    override fun getTasks(callback: TasksDataSource.LoadTasksCallback) {
        appExecutors.diskIO.execute {
            val tasks = tasksDao.getTasks()
            appExecutors.mainThread.execute {
                if (tasks.isEmpty()) {
                    callback.onDataNotAvailable()
                } else {
                    callback.onTasksLoaded(tasks)
                }
            }
        }
    }

    override fun getTask(taskID: String, callback: TasksDataSource.GetTaskCallback) {
        appExecutors.diskIO.execute {
            val task = tasksDao.getTask(taskID)
            appExecutors.mainThread.execute {
                if (task != null) {
                    callback.onTaskLoaded(task)
                } else {
                    callback.onDataNotAvailable()
                }
            }
        }
    }

    override fun saveTask(task: Task) {
        appExecutors.diskIO.execute { tasksDao.insertTask(task) }
    }

    override fun completeTask(task: Task) {
        appExecutors.diskIO.execute { tasksDao.insertTask(task) }
    }

    override fun completeTask(taskID: String) {
        // Not required for the local data source because the {@link TasksRepository} handles
        // converting from a {@code taskId} to a {@link task} using its cached data.
    }

    override fun activateTask(task: Task) {
        appExecutors.diskIO.execute { tasksDao.updateCompleted(task.id, false) }
    }

    override fun activateTask(taskID: String) {
        // Not required for the local data source because the {@link TasksRepository} handles
        // converting from a {@code taskId} to a {@link task} using its cached data.
    }

    override fun clearCompletedTasks() {
        appExecutors.diskIO.execute { tasksDao.deleteCompletedTasks() }
    }

    override fun refreshTasks() {
        // Not required because the {@link TasksRepository} handles the logic of refreshing the
        // tasks from all the available data sources.
    }

    override fun deleteAllTasks() {
        appExecutors.diskIO.execute { tasksDao.deleteAll() }
    }

    override fun deleteTask(taskID: String) {
        appExecutors.diskIO.execute { tasksDao.deleteTask(taskID) }
    }

    companion object {

        private var INSTANCE: TasksLocalDataSource? = null

        @JvmStatic
        fun getInstance(appExecutors: AppExecutors, tasksDao: TasksDao): TasksLocalDataSource {
            if (INSTANCE == null) {
                synchronized(TasksLocalDataSource::javaClass) {
                    INSTANCE = TasksLocalDataSource(appExecutors, tasksDao)
                }
            }

            return INSTANCE!!
        }
    }

}