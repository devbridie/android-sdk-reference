package com.devbridie.androidsdkreference.tree

import com.devbridie.androidsdkreference.models.SourceFileEntry

sealed class TreeSearchResult
data class FileResult(val file: SourceFileEntry) : TreeSearchResult()
data class PackageSegmentResult(val packageSegment: String) : TreeSearchResult()