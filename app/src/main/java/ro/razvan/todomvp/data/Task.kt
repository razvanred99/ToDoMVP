package ro.razvan.todomvp.data

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.util.*

@Entity(tableName = "Tasks")
data class Task @JvmOverloads constructor(
        var title: String = "",
        var description: String = "",
        @PrimaryKey
        var id: String = UUID.randomUUID().toString()
) {

    @ColumnInfo(name = "completed")
    var isCompleted = false

    val titleForList: String
        get() = if (title.isNotBlank()) title else description

    val isActive
        get() = !isCompleted

    val isEmpty
        get() = title.isBlank() && description.isBlank()
}