package ro.razvan.todomvp.data.source.local

import androidx.room.*
import ro.razvan.todomvp.data.Task

@Dao
interface TasksDao {

    @Query("SELECT * FROM Tasks")
    fun getTasks(): List<Task>

    @Query("SELECT * FROM Tasks WHERE id=:taskID")
    fun getTask(taskID: String): Task?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTask(task: Task)

    @Update
    fun updateTask(task: Task): Int

    @Query("UPDATE Tasks SET completed=:completed WHERE id=:taskID")
    fun updateCompleted(taskID: String, completed: Boolean)

    @Query("DELETE FROM Tasks where id=:taskID")
    fun deleteTask(taskID: String): Int

    @Query("DELETE FROM Tasks")
    fun deleteAll()

    @Query("DELETE FROM Tasks WHERE completed=1")
    fun deleteCompletedTasks():Int

}