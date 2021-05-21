package com.example.currencyconverter
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

class SpaceItemDecoration(
    private val top: Int,
    private val bottom: Int
) : ItemDecoration() {
    constructor(offset: Int) : this(offset, offset)

    override fun getItemOffsets(
        outRect: Rect, view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.set(0, top, 0, bottom)
    }
}