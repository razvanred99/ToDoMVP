package ro.razvan.todomvp.tasks

import ro.razvan.todomvp.BasePresenter
import ro.razvan.todomvp.BaseView
import ro.razvan.todomvp.data.Task

interface TasksContract {

    interface View : BaseView<Presenter> {

        var isActive:Boolean

        fun setLoadingIndicator(active:Boolean)

        fun showTasks(tasks:List<Task>)

        fun showAddTask()

        fun showTaskDetailsUI(taskID:String)

        fun showTaskMarkedActive()

        fun showTaskMarkedComplete()

        fun showCompletedTasksCleared()

        fun showLoadingTasksError()

        fun showNoTask()

        fun showActiveFilterLabel()

        fun showCompletedFilterLabel()

        fun showAllFilterLabel()

        fun showNoActiveTasks()

        fun showNoCompletedTasks()

        fun showSuccessfullySavedMessage()

        fun showFilteringPopUpMenu()

    }

    interface Presenter : BasePresenter {

        var currentFiltering: TasksFilterType

        fun result(requestCode: Int, resultCode: Int)

        fun loadTasks(forceUpdate: Boolean)

        fun addNewTask()

        fun openTaskDetails(requestedTask: Task)

        fun completeTask(completedTask: Task)

        fun activateTask(activeTask: Task)

        fun clearCompletedTask()

    }

}