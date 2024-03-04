package com.app.ecarepro.utils

import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

abstract class PaginationScrollListener protected constructor(private val layoutManager: LinearLayoutManager) :
    RecyclerView.OnScrollListener() {
    /*
         Method gets callback when user scroll the search list
         */
    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        val visibleItemCount = layoutManager.childCount
        val totalItemCount = layoutManager.itemCount
        val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
        if (!isLoading && !isLastPage) {
            if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                && firstVisibleItemPosition >= 0
            ) {
                Log.i(TAG, "Loading more items")
                loadMoreItems()
            }
        }
    }

    protected abstract fun loadMoreItems()

    //optional
    abstract val totalPageCount: Int

    abstract val isLastPage: Boolean

    abstract val isLoading: Boolean

    companion object {
        private val TAG: String = PaginationScrollListener::class.java.simpleName
    }
}