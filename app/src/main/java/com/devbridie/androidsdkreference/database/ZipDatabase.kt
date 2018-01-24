package com.devbridie.androidsdkreference.database

import android.content.Context
import com.devbridie.androidsdkreference.R
import com.devbridie.androidsdkreference.models.SourceFileEntry
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

operator fun ZipInputStream.iterator(): Iterator<ZipEntry> {
    return object : Iterator<ZipEntry> {
        var next: ZipEntry? = null
        override fun hasNext(): Boolean {
            next = this@iterator.nextEntry
            return next != null
        }

        override fun next(): ZipEntry {
            return next!!
        }
    }
}

class ZipDatabase(val context: Context) : Database() {
    private fun getEntries(): List<ZipEntry> {
        createZipInputStream().use {
            return it.iterator().asSequence().toList()
        }
    }

    private fun createZipInputStream(): ZipInputStream {
        return ZipInputStream(context.resources.openRawResource(R.raw.android26))
    }

    override fun getFileContents(fileName: String): String {
        createZipInputStream().use { stream ->
            stream.iterator().asSequence().find { it.name == fileName }!!
            return stream.bufferedReader().readText()
        }
    }

    override fun getFiles(): List<SourceFileEntry> {
        return getEntries().filter { !it.isDirectory }.map { SourceFileEntry(it.name) }
    }
}