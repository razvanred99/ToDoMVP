package ro.razvan.todomvp.data.source.remote

import android.os.Handler
import com.google.common.collect.Lists
import ro.razvan.todomvp.data.Task
import ro.razvan.todomvp.data.source.TasksDataSource

object TasksRemoteDataSource : TasksDataSource {

    private const val SERVICE_LATENCY_IN_MILLIS = 5000L

    private var TASKS_SERVICE_DATA = LinkedHashMap<String, Task>(2)

    init {
        addTask("Build tower in Pisa", "Ground looks good, no foundation work required.")
        addTask("Finish bridge in Tacoma", "Bla bla bla")
    }

    private fun addTask(title: String, description: String) {
        val newTask = Task(title, description)
        TASKS_SERVICE_DATA[newTask.id] = newTask
    }

    override fun getTasks(callback: TasksDataSource.LoadTasksCallback) {
        val tasks = Lists.newArrayList(TASKS_SERVICE_DATA.values)
        Handler().postDelayed({
            callback.onTasksLoaded(tasks)
        }, SERVICE_LATENCY_IN_MILLIS)
    }

    override fun getTask(taskID: String, callback: TasksDataSource.GetTaskCallback) {
        val task = TASKS_SERVICE_DATA[taskID]

        with(Handler()) {
            if (task != null)
                postDelayed({ callback.onTaskLoaded(task) }, SERVICE_LATENCY_IN_MILLIS)
            else
                postDelayed({ callback.onDataNotAvailable() }, SERVICE_LATENCY_IN_MILLIS)
        }
    }

    override fun saveTask(task: Task) {
        TASKS_SERVICE_DATA[task.id] = task
    }

    override fun completeTask(task: Task) {
        val completedTask = Task(task.title, task.description, task.id).apply {
            isCompleted = true
        }
        TASKS_SERVICE_DATA[task.id] = completedTask
    }

    override fun completeTask(taskID: String) {
        // Not required for the remote data source because the {@link TasksRepository} handles
        // converting from a {@code taskId} to a {@link task} using its cached data.
    }

    override fun activateTask(task: Task) {
        val activatedTask = Task(task.title, task.description, task.id)
        TASKS_SERVICE_DATA[task.id] = activatedTask
    }

    override fun activateTask(taskID: String) {
        // Not required for the remote data source because the {@link TasksRepository} handles
        // converting from a {@code taskId} to a {@link task} using its cached data.
    }

    override fun clearCompletedTasks() {
        TASKS_SERVICE_DATA = TASKS_SERVICE_DATA.filterValues {
            !it.isCompleted
        } as LinkedHashMap<String, Task>
    }

    override fun deleteAllTasks() {
        TASKS_SERVICE_DATA.clear()
    }

    override fun deleteTask(taskID: String) {
        TASKS_SERVICE_DATA.remove(taskID)
    }

    override fun refreshTasks() {
        // Not required because the {@link TasksRepository} handles the logic of refreshing the
        // tasks from all the available data sources.
    }
}