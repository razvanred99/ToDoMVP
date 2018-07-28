package ro.razvan.todomvp.util

import com.google.android.material.snackbar.Snackbar
import android.view.View

fun View.showSnackbar(message:String,duration: Int){
    Snackbar.make(this,message,duration).show()
}