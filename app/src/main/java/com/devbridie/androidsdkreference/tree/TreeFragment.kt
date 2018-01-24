package com.devbridie.androidsdkreference.tree

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.devbridie.androidsdkreference.MainActivity
import com.devbridie.androidsdkreference.R
import kotlinx.android.synthetic.main.tree_fragment.*
import org.jetbrains.anko.AnkoLogger

class TreeFragment : Fragment(), AnkoLogger {
    val activity get() = context as MainActivity

    val path = mutableListOf<String>()
    val adapter = TreeAdapter().apply {
        resultClickListener = { item ->
            if (item is FileResult) {
                activity.showFile(item.file.fullFileName)
            } else if (item is PackageSegmentResult) {
                path.add(item.packageSegment)
                filter(path)
                updateTitle()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.tree_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        adapter.setFiles(activity.entries)
        updateTitle()
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        treeList.adapter = adapter
        treeList.layoutManager = LinearLayoutManager(context)
    }

    fun canPopTree(): Boolean {
        return path.isNotEmpty()
    }

    fun popTree() {
        path.removeAt(path.lastIndex)
        adapter.filter(path)
        updateTitle()
    }

    fun updateTitle() {
        if (path.isEmpty()) {
            activity.supportActionBar?.title = "Packages"
        } else {
            activity.supportActionBar?.title = path.joinToString(".")
        }
    }
}