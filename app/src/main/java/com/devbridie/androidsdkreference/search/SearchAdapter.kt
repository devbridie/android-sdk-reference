package com.devbridie.androidsdkreference.search

import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.text.Spannable
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.binaryfork.spanny.Spanny
import com.devbridie.androidsdkreference.R
import com.devbridie.androidsdkreference.models.SourceFileEntry
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class SearchAdapter : RecyclerView.Adapter<SearchAdapter.ViewHolder>(), AnkoLogger {
    private var displayList = listOf<SourceFileEntry>()
    private var totalList: List<SourceFileEntry> = listOf()
    private var filterTerm: String? = null
    var itemClickListener: ((SourceFileEntry) -> Unit)? = null

    fun setItems(items: List<SourceFileEntry>) {
        totalList = items
        displayList = totalList
        notifyDataSetChanged()
    }

    fun setFilter(filter: String?) {
        filterTerm = filter
        info { "New filter: $filter" }

        displayList = if (filter == null) {
            totalList
        } else {
            totalList.filter {
                it.fullyQualifiedClassName.toLowerCase().contains(filter.toLowerCase())
            }
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.listing_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = displayList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = displayList[position]

        holder.view.setOnClickListener {
            itemClickListener?.invoke(item)
        }

        val context = holder.view.context as AppCompatActivity
        val colorSpan = ForegroundColorSpan(ContextCompat.getColor(context, R.color.colorAccent))

        holder.titleView.text = spanSearchTerm(filterTerm, item.className, colorSpan)
        holder.subtitleView.text = spanSearchTerm(filterTerm, item.packageName, colorSpan)
        holder.icon.setImageResource(R.drawable.ic_insert_drive_file_black_24dp)
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val titleView = view.findViewById<TextView>(R.id.text)
        val subtitleView = view.findViewById<TextView>(R.id.text2)
        val icon = view.findViewById<ImageView>(R.id.icon)
    }
}

fun Spanny.findAndSpanCaseInsensitive(textToSpan: CharSequence, getSpan: () -> Any): Spanny {
    var lastIndex = 0
    while (lastIndex != -1) {
        lastIndex = toString().toLowerCase().indexOf(textToSpan.toString().toLowerCase(), lastIndex)
        if (lastIndex != -1) {
            setSpan(
                getSpan(),
                lastIndex,
                lastIndex + textToSpan.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            lastIndex += textToSpan.length
        }
    }
    return this
}

fun spanSearchTerm(term: String?, text: String, span: Any): CharSequence {
    return if (term == null || term.isEmpty()) {
        text
    } else {
        Spanny(text).findAndSpanCaseInsensitive(term, { span })
    }
}