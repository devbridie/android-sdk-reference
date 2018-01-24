package com.devbridie.androidsdkreference.sourceview

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.devbridie.androidsdkreference.R
import com.devbridie.androidsdkreference.models.SourceFileEntry
import kotlinx.android.synthetic.main.main_activity.*

class SourceFileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        progress.visibility = View.GONE

        val sourceFileEntry = intent.getStringExtra("file").let { SourceFileEntry(it) }

        setSupportActionBar(toolbar)
        supportActionBar?.title = sourceFileEntry.className
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val fragment = SourceFileFragment().apply {
            arguments = Bundle().apply {
                putString("file", sourceFileEntry.fullFileName)
            }
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.mainFrame, fragment)
            .commit()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}