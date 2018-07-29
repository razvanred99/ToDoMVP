package ro.razvan.todomvp.tasks

import ro.razvan.todomvp.data.Task
import ro.razvan.todomvp.data.source.TasksDataSource
import ro.razvan.todomvp.data.source.TasksRepository

class TasksPresenter(val tasksRepository: TasksRepository,
                     val tasksView: TasksContract.View) : TasksContract.Presenter {

    override fun loadTasks(forceUpdate: Boolean) {
        loadTasks(forceUpdate || firstLoad, true)
    }

    private fun loadTasks(forceUpdate: Boolean, showLoadingUI: Boolean) {
        if (showLoadingUI)
            tasksView.setLoadingIndicator(true)

        if (forceUpdate)
            tasksRepository.refreshTasks()

        tasksRepository.getTasks(object : TasksDataSource.LoadTasksCallback {
            override fun onTasksLoaded(tasks: List<Task>) {
                val tasksToShow = ArrayList<Task>()

                for (task in tasks) {
                    when (currentFiltering) {
                        TasksFilterType.ALL_TASKS -> tasksToShow.add(task)
                        TasksFilterType.ACTIVE_TASKS ->
                            if (task.isActive)
                                tasksToShow.add(task)
                        TasksFilterType.COMPLETED ->
                            if (task.isCompleted)
                                tasksToShow.add(task)
                    }
                }

                if (!tasksView.isActive)
                    return

                if (showLoadingUI)
                    tasksView.setLoadingIndicator(false)

                processTasks(tasksToShow)
            }

            override fun onDataNotAvailable() {
                if (!tasksView.isActive)
                    return
                tasksView.showLoadingTasksError()
            }
        })
    }

    private fun processTasks(tasks: List<Task>) {
        if (tasks.isEmpty())
            processEmptyTask()
        else {
            tasksView.showTasks(tasks)
            showFilterLabel()
        }
    }

    private fun showFilterLabel() {
        when (currentFiltering) {
            TasksFilterType.COMPLETED -> tasksView.showCompletedFilterLabel()
            TasksFilterType.ACTIVE_TASKS -> tasksView.showActiveFilterLabel()
            else -> tasksView.showAllFilterLabel()
        }
    }

    private fun processEmptyTask() {
        when (currentFiltering) {
            TasksFilterType.ACTIVE_TASKS -> tasksView.showNoActiveTasks()
            TasksFilterType.COMPLETED -> tasksView.showNoCompletedTasks()
            else -> tasksView.showNoTask()
        }
    }

    override fun addNewTask() {
        tasksView.showAddTask()
    }

    override fun openTaskDetails(requestedTask: Task) {
        tasksView.showTaskDetailsUI(requestedTask.id)
    }

    override fun completeTask(completedTask: Task) {
        tasksRepository.completeTask(completedTask)
        tasksView.showTaskMarkedComplete()
        loadTasks(false, false)
    }

    override fun activateTask(activeTask: Task) {
        tasksRepository.activateTask(activeTask)
        tasksView.showTaskMarkedActive()
        loadTasks(false, false)
    }

    override fun clearCompletedTask() {
        tasksRepository.clearCompletedTasks()
        tasksView.showCompletedTasksCleared()
        loadTasks(false, false)
    }

    override var currentFiltering = TasksFilterType.ALL_TASKS

    private var firstLoad = true

    init {
        tasksView.presenter = this
    }

    override fun start() {
        loadTasks(false)
    }

    override fun result(requestCode: Int, resultCode: Int) {

    }

}