package com.sipaw3310.kertasku

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView

class ItemClickListener(val context: Context): RecyclerView.OnItemTouchListener {
    //var gestureDetector: GestureDetector
    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        TODO("Not yet implemented")
    }

    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
        TODO("Not yet implemented")
    }

    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
        TODO("Not yet implemented")
    }

}

interface ItemClickInterface {
    fun onClick(position: Int)
}