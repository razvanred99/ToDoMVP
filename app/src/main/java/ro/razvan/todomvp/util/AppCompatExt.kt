package ro.razvan.todomvp.util

import androidx.annotation.IdRes
import androidx.appcompat.app.ActionBar
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.transaction

fun AppCompatActivity.replaceFragmentInActivity(fragment: Fragment, @IdRes frameID: Int) {
    supportFragmentManager.transact {
        replace(frameID, fragment)
    }
}

fun AppCompatActivity.addFragmentToActivity(fragment: Fragment, tag: String) {
    supportFragmentManager.transact {
        add(fragment, tag)
    }
}

fun AppCompatActivity.setupActionBar(@IdRes toolbarID: Int, action: ActionBar.() -> Unit) {
    setSupportActionBar(findViewById(toolbarID))
    supportActionBar?.run {
        action()
    }
}

private inline fun FragmentManager.transact(action: FragmentTransaction.() -> Unit) {
    beginTransaction().apply {
        action()
    }.commit()
}