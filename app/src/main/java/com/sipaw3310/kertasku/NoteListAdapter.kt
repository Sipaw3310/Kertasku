package com.sipaw3310.kertasku

import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NoteListAdapter(private val notes: MutableList<NoteListClass>): RecyclerView.Adapter<NoteListAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = View.inflate(parent.context, R.layout.note_list, null)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.title.text = notes[position].title
        holder.summary.text = notes[position].summary
    }

    override fun getItemCount(): Int = notes.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.noteList_title)
        val summary: TextView = itemView.findViewById(R.id.noteList_summary)
    }
}

class NoteListClass(var title: String, var summary: String)

class NoteListDecorator(val marginSize: Int): RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.top = marginSize
        outRect.left = marginSize
        outRect.bottom = marginSize
        outRect.right = marginSize
    }
}