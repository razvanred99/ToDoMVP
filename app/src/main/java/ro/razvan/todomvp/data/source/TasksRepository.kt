package ro.razvan.todomvp.data.source

import ro.razvan.todomvp.data.Task

class TasksRepository(
        val tasksRemoteDataSource: TasksDataSource,
        val tasksLocalDataSource: TasksDataSource) : TasksDataSource {

    var cachedTasks: LinkedHashMap<String, Task> = LinkedHashMap()

    var cacheIsDirty = false

    override fun getTasks(callback: TasksDataSource.LoadTasksCallback) {
        if (cachedTasks.isNotEmpty() && !cacheIsDirty) {
            callback.onTasksLoaded(ArrayList(cachedTasks.values))
            return
        }

        if (cacheIsDirty) {
            //if the cache is dirty we need to fetch new data from network
            getTasksFromRemoteDataSource(callback)
        } else {
            tasksLocalDataSource.getTasks(object : TasksDataSource.LoadTasksCallback {
                override fun onTasksLoaded(tasks: List<Task>) {
                    refreshCache(tasks)
                    callback.onTasksLoaded(ArrayList(cachedTasks.values))
                }

                override fun onDataNotAvailable() {
                    getTasksFromRemoteDataSource(callback)
                }
            })
        }
    }

    override fun saveTask(task: Task) {
        cacheAndPerform(task) {
            tasksRemoteDataSource.saveTask(it)
            tasksLocalDataSource.saveTask(it)
        }
    }

    override fun completeTask(task: Task) {
        cacheAndPerform(task) {
            it.isCompleted = true
            tasksRemoteDataSource.completeTask(it)
            tasksLocalDataSource.completeTask(it)
        }
    }

    override fun completeTask(taskID: String) {
        getTaskWithId(taskID)?.let {
            completeTask(it)
        }
    }

    override fun activateTask(task: Task) {
        cacheAndPerform(task) {
            it.isCompleted = false
            tasksRemoteDataSource.activateTask(it)
            tasksLocalDataSource.activateTask(it)
        }
    }

    override fun activateTask(taskID: String) {
        getTaskWithId(taskID)?.let {
            activateTask(it)
        }
    }

    override fun clearCompletedTasks() {
        tasksRemoteDataSource.clearCompletedTasks()
        tasksLocalDataSource.clearCompletedTasks()

        cachedTasks = cachedTasks.filterValues {
            !it.isCompleted
        } as LinkedHashMap<String, Task>
    }

    override fun getTask(taskID: String, callback: TasksDataSource.GetTaskCallback) {
        val taskInCache = getTaskWithId(taskID)

        if (taskInCache != null) {
            callback.onTaskLoaded(taskInCache)
            return
        }

        tasksLocalDataSource.getTask(taskID, object : TasksDataSource.GetTaskCallback {
            override fun onTaskLoaded(task: Task) {
                cacheAndPerform(task) {
                    callback.onTaskLoaded(it)
                }
            }

            override fun onDataNotAvailable() {
                tasksRemoteDataSource.getTask(taskID, object : TasksDataSource.GetTaskCallback {
                    override fun onTaskLoaded(task: Task) {
                        cacheAndPerform(task) {
                            callback.onTaskLoaded(it)
                        }
                    }

                    override fun onDataNotAvailable() {
                        callback.onDataNotAvailable()
                    }
                })
            }
        })
    }

    override fun refreshTasks() {
        cacheIsDirty = true
    }

    override fun deleteAllTasks() {
        tasksRemoteDataSource.deleteAllTasks()
        tasksLocalDataSource.deleteAllTasks()
        cachedTasks.clear()
    }

    override fun deleteTask(taskID: String) {
        tasksLocalDataSource.deleteTask(taskID)
        tasksRemoteDataSource.deleteTask(taskID)
        cachedTasks.remove(taskID)
    }

    private fun getTasksFromRemoteDataSource(callback: TasksDataSource.LoadTasksCallback) {

    }

    private fun refreshCache(tasks: List<Task>) {
        cachedTasks.clear()
        tasks.forEach {
            cacheAndPerform(it) {}
        }
        cacheIsDirty = false
    }

    private inline fun cacheAndPerform(task: Task, perform: (Task) -> Unit) {
        val cachedTask = Task(task.title, task.description, task.id).apply {
            isCompleted = task.isCompleted
        }
        cachedTasks[cachedTask.id] = cachedTask
        perform(cachedTask)
    }

    private fun getTaskWithId(id: String) = cachedTasks[id]

    companion object {

        private var INSTANCE: TasksRepository? = null

        /**
         * Returns the single instance of this class, creating it if necessary.
         * @param tasksRemoteDataSource the backend data source
         * *
         * @param tasksLocalDataSource  the device storage data source
         * *
         * @return the [TasksRepository] instance
         */
        @JvmStatic
        fun getInstance(tasksRemoteDataSource: TasksDataSource, tasksLocalDataSource: TasksDataSource): TasksRepository {
            return INSTANCE
                    ?: TasksRepository(tasksRemoteDataSource, tasksLocalDataSource).apply { INSTANCE = this }
        }

        @JvmStatic
        fun destroyInstance() {
            INSTANCE = null
        }

    }

}