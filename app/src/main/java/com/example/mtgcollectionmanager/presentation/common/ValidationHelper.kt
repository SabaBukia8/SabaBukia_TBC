package com.example.mtgcollectionmanager.presentation.common

import android.content.Context
import com.example.mtgcollectionmanager.R

object ValidationHelper {
    fun getCollectionNameRequiredError(context: Context): String {
        return context.getString(R.string.error_empty_collection_name)
    }
}