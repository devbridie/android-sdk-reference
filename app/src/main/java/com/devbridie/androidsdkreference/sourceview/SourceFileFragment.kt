package com.devbridie.androidsdkreference.sourceview

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.devbridie.androidsdkreference.R
import com.devbridie.androidsdkreference.database.ZipDatabase
import com.devbridie.androidsdkreference.models.SourceFileEntry
import com.pddstudio.highlightjs.models.Language
import com.pddstudio.highlightjs.models.Theme
import kotlinx.android.synthetic.main.file_fragment.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.coroutines.experimental.bg
import org.jetbrains.anko.info

sealed class ViewState {
    data class LoadingFileContents(val sourceFileEntry: SourceFileEntry) : ViewState()
    data class Completed(val fileContents: String) : ViewState()
}

class SourceFileFragment : Fragment(), AnkoLogger {
    lateinit var state: ViewState

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val entry = SourceFileEntry(arguments.getString("file"))
        state = ViewState.LoadingFileContents(entry)

        val database = ZipDatabase(context)
        async(UI) {
            info { "Loading file $entry." }
            val fileContents = bg {
                database.getFileContents(entry.fullFileName)
            }.await()
            updateState(ViewState.Completed(fileContents))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.file_fragment, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateState(state)
    }

    fun updateState(viewState: ViewState) {
        info { "Updating state to $viewState." }
        when (viewState) {
            is ViewState.LoadingFileContents -> {
                (this.activity as AppCompatActivity).supportActionBar?.title = viewState.sourceFileEntry.fileName
                highlightView.visibility = View.GONE
                progress.visibility = View.VISIBLE
            }
            is ViewState.Completed -> {
                highlightView.apply {
                    theme = Theme.GOOGLECODE
                    highlightLanguage = Language.JAVA
                    setZoomSupportEnabled(true)
                    setShowLineNumbers(true)
                    setSource(viewState.fileContents)
                }

                progress.visibility = View.GONE
                highlightView.visibility = View.VISIBLE
            }
        }
    }
}