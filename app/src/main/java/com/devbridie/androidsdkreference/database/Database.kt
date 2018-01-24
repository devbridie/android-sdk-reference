package com.devbridie.androidsdkreference.database

import com.devbridie.androidsdkreference.models.SourceFileEntry

abstract class Database {
    abstract fun getFileContents(fileName: String): String
    abstract fun getFiles(): List<SourceFileEntry>
}