package com.example.rickandmorty.presentation.extensions

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.rickandmorty.R

fun RecyclerView.space(context: Context) {
    addItemDecoration(
        object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                outRect.bottom =
                    context.resources.getDimension(R.dimen.distance_between_items_recycler_view)
                        .toInt()
                outRect.right =
                    context.resources.getDimension(R.dimen.distance_between_items_recycler_view)
                        .toInt()
            }
        }
    )
}

fun RecyclerView.addPaginationListener(
    layoutManager: GridLayoutManager,
    itemsToLoad: Int,
    onLoadMore: () -> Unit
) {
    addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            val lastVisiblePosition = layoutManager.findLastVisibleItemPosition()
            val itemCount = layoutManager.itemCount
            if (dy != 0 && itemsToLoad + lastVisiblePosition >= itemCount) {
                onLoadMore()
            }
        }
    })
}

fun <T> RecyclerView.setupGridLayoutManager(
    itemsToLoad: Int,
    context: Context,
    adapter: ListAdapter<T, RecyclerView.ViewHolder>,
    onLoadMore: () -> Unit
) {
    val layoutManager = GridLayoutManager(context, 2)
    this.layoutManager = layoutManager
    this.adapter = adapter
    this.space(context)
    this.addPaginationListener(
        layoutManager,
        itemsToLoad,
        onLoadMore
    )
}