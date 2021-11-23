package com.sipaw3310.kertasku

import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView

class AllBookAdapter(
    private val books: MutableList<BookClass>,
    private val clickListener: (previousTab: View?, clickedTab: View, position: Int) -> Unit
): RecyclerView.Adapter<AllBookAdapter.ViewHolder>() {

    val tabs = mutableListOf<FrameLayout>()
    var selectedTab: View? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = View.inflate(parent.context, R.layout.all_book, null)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.parent.setOnClickListener {
            clickListener(selectedTab, it, position)
        }
        holder.button.text = books[position].name

        // Insert parent view to tabs list
        tabs.add(holder.parent)
    }

    override fun getItemCount(): Int = books.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val parent: FrameLayout = itemView.findViewById(R.id.allBook_parent)
        val button: Button = itemView.findViewById(R.id.allBook_title)
    }
}

class BookClass(var name: String) {
    var state: Boolean = false
    lateinit var button: Button
}

