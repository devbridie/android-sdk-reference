package com.devbridie.androidsdkreference.models

data class SourceFileEntry(val fullFileName: String) {
    val fileName = fullFileName.split("/").last()
    val className = fileName.split(".").dropLast(1).joinToString(".")
    val packageName = fullFileName.split("/").dropLast(1).joinToString(".")
    val fullyQualifiedClassName = packageName + "." + className
}