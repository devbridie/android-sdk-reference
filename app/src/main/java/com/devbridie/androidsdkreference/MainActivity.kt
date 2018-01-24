package com.devbridie.androidsdkreference

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.devbridie.androidsdkreference.database.Database
import com.devbridie.androidsdkreference.database.ZipDatabase
import com.devbridie.androidsdkreference.models.SourceFileEntry
import com.devbridie.androidsdkreference.search.SearchFragment
import com.devbridie.androidsdkreference.sourceview.SourceFileActivity
import com.devbridie.androidsdkreference.tree.TreeFragment
import kotlinx.android.synthetic.main.main_activity.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.coroutines.experimental.bg
import org.jetbrains.anko.info

class MainActivity : AppCompatActivity(), AnkoLogger {
    var currentView = ViewState.LOADING

    lateinit var database: Database
    lateinit var entries: List<SourceFileEntry>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.main_activity)
        switchView(ViewState.LOADING)
        setSupportActionBar(toolbar)

        database = ZipDatabase(this)
        async(UI) {
            info { "LoadingFileContents entries... " }
            entries = bg { database.getFiles() }.await()
            info { "Loaded ${entries.size} entries." }
            switchView(ViewState.TREE)
        }
    }

    fun switchView(newViewState: ViewState) {
        currentView = newViewState
        info { "Switched view state to $newViewState." }

        progress.visibility = if (newViewState == ViewState.LOADING) View.VISIBLE else View.GONE
        when (currentView) {
            ViewState.TREE -> replaceMainFragment(TreeFragment())
            ViewState.SEARCH -> replaceMainFragment(SearchFragment())
            ViewState.LOADING -> { }
        }
    }

    fun replaceMainFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.push_down_in, android.R.anim.slide_out_right)
            .replace(R.id.mainFrame, fragment)
            .commit()
        invalidateOptionsMenu()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        when (currentView) {
            ViewState.TREE -> menuInflater.inflate(R.menu.searchview, menu)
            ViewState.SEARCH -> menuInflater.inflate(R.menu.treeview, menu)
            ViewState.LOADING -> {
            }
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.switchSearchView -> switchView(ViewState.SEARCH)
            R.id.switchTreeView -> switchView(ViewState.TREE)
            android.R.id.home -> onBackPressed()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun onBackPressed() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.mainFrame)
        if (currentFragment is TreeFragment && currentFragment.canPopTree()) {
            currentFragment.popTree()
        } else {
            super.onBackPressed()
        }
    }

    fun showFile(filePath: String) {
        startActivity(Intent(this, SourceFileActivity::class.java).putExtra("file", filePath))
        overridePendingTransition(R.anim.slide_in_right, R.anim.hold)
    }

    enum class ViewState {
        LOADING, TREE, SEARCH
    }
}
