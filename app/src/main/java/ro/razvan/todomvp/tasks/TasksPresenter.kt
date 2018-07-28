package ro.razvan.todomvp.tasks

import ro.razvan.todomvp.data.Task
import ro.razvan.todomvp.data.source.TasksRepository

class TasksPresenter(val tasksRepository: TasksRepository,
                     val tasksView: TasksContract.View) : TasksContract.Presenter {
    override fun loadTasks(forceUpdate: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun addNewTask() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun openTaskDetails(requestedTask: Task) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun completeTask(completedTask: Task) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun activateTask(activeTask: Task) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun clearCompletedTask() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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