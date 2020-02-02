package com.bwillocean.jiveQuizTalk.view

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SpaceItemDecoration(private val pxSpace: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        outRect.left += pxSpace
        outRect.right += pxSpace
        outRect.bottom += pxSpace

        // Add top margin only for the first item to avoid double space between items

        if (parent.getChildLayoutPosition(view) < ((parent.layoutManager as? GridLayoutManager)?.spanCount ?: 1)) {
            outRect.top += pxSpace
        } else {
            outRect.top += pxSpace
        }
    }
}