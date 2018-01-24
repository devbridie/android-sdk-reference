package com.devbridie.androidsdkreference.tree

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.devbridie.androidsdkreference.R
import com.devbridie.androidsdkreference.models.SourceFileEntry
import org.jetbrains.anko.AnkoLogger

class TreeAdapter : RecyclerView.Adapter<TreeAdapter.ViewHolder>(), AnkoLogger {
    var resultClickListener: ((TreeSearchResult) -> Unit)? = null

    private var displayList = listOf<TreeSearchResult>()
    private var packages: Set<String> = setOf()
    private var files: List<SourceFileEntry> = listOf()
    private var filterPackage: List<String> = listOf("")

    fun setFiles(files: List<SourceFileEntry>) {
        this.files = files
        packages = files.map { it.packageName }.toSet()
        filter(listOf())
    }

    fun filter(term: List<String>) {
        filterPackage = term
        val termName = term.joinToString(".")
        val filteredPackages =
            (if (termName == "") packages else packages.filter { it.startsWith(termName + ".") }) - ""
        val results = filteredPackages.map { it.split(".").drop(term.size).first() }.distinct()
            .map { PackageSegmentResult(it) }
        val filteredFiles = files.filter { it.packageName == termName }.map { FileResult(it) }
        displayList = results + filteredFiles
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.listing_item, parent, false)
        return ViewHolder(view as ViewGroup)
    }

    override fun getItemCount() = displayList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = displayList[position]

        holder.view.setOnClickListener {
            resultClickListener?.invoke(item)
        }

        holder.textView.text = when (item) {
            is FileResult -> item.file.fileName
            is PackageSegmentResult -> item.packageSegment
        }

        holder.iconView.setImageResource(
            when (item) {
                is FileResult -> R.drawable.ic_insert_drive_file_black_24dp
                is PackageSegmentResult -> R.drawable.ic_folder_black_24dp
            }
        )
    }

    class ViewHolder(val view: ViewGroup) : RecyclerView.ViewHolder(view) {
        val textView = view.findViewById<TextView>(R.id.text)
        val iconView = view.findViewById<ImageView>(R.id.icon)
        init {
            view.findViewById<TextView>(R.id.text2).visibility = View.GONE
        }
    }
}