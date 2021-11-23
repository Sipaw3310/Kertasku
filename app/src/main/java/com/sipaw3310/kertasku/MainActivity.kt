package com.sipaw3310.kertasku

import android.animation.ObjectAnimator
import android.content.Intent
import android.content.res.Resources
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.doOnCancel
import androidx.core.animation.doOnEnd
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private var notelist = mutableListOf<NoteListClass>()
    private var bookData = mutableListOf<BookClass>()
    private var pinnedBookData = mutableListOf<BookClass>()
    private var isDrawerOpened: Boolean = false

    private lateinit var noteList: RecyclerView
    private lateinit var noteListAdapter: NoteListAdapter
    private lateinit var allBookList: RecyclerView
    private lateinit var allBookListAdapter: AllBookAdapter
    private lateinit var pinnedBookTab: RecyclerView
    private lateinit var pinnedBookTabAdapter: AllBookAdapter

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var authId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Check auth
        auth = Firebase.auth
        if(auth.currentUser == null) {
            val intent: Intent = Intent(this, SignActivity::class.java)
            startActivity(intent)
            finish()
        }
        authId = auth.uid.toString()

        // Set window background color
        // Don't set background color on parent view to avoid unnecessary overdraw
        val attrColor = obtainStyledAttributes(intArrayOf(R.attr.colorPrimaryVariant))
        window.setBackgroundDrawable(ColorDrawable(attrColor.getColor(0, 0)))
        attrColor.recycle()
        // Tell the system to make the window to show content under system bars (status and navigation bar)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContentView(R.layout.activity_main)

        // Get window width and height in dpi
        val displayMetrics: DisplayMetrics = this.resources.displayMetrics
        val widthDPI: Float = displayMetrics.widthPixels / displayMetrics.density
        val heightDPI: Float = displayMetrics.heightPixels / displayMetrics.density

        // Background views
        val createBookButton: Button = findViewById(R.id.home_createBookButton) // Create book button
        val allBookTitle: TextView = findViewById(R.id.home_allBookTitle) // Title for allBook list
        val openDrawerButton: ImageView = findViewById(R.id.home_buttonOpenDrawer) // Button for opening drawer
        val titleText: TextView = findViewById(R.id.home_title) // Kertasku title
        // Foreground views
        val foregroundView: ConstraintLayout = findViewById(R.id.home_foreground) // Container for views on the foreground
        val heightInfo: TextView = findViewById(R.id.home_heightInfo) // Just for debug purpose

        // Window inset logic
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.home_parent)) { _, insets ->
            // Set padding on parent view
            findViewById<ConstraintLayout>(R.id.home_parent).setPadding(
                insets.getInsets(WindowInsetsCompat.Type.navigationBars()).left,
                insets.getInsets(WindowInsetsCompat.Type.navigationBars()).top,
                insets.getInsets(WindowInsetsCompat.Type.navigationBars()).right,
                insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
            )
            // Set padding on foreground view's container
            foregroundView.setPadding(
                0, insets.getInsets(WindowInsetsCompat.Type.statusBars()).top, 0, 0
            )

            return@setOnApplyWindowInsetsListener WindowInsetsCompat.CONSUMED
        }

        // Note list RecyclerView
        noteList = findViewById(R.id.home_noteList)
        noteListAdapter = NoteListAdapter(notelist)
        noteList.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        noteList.adapter = noteListAdapter
        noteList.addItemDecoration(NoteListDecorator((4 * displayMetrics.density).toInt()))
        // All book Recyclerview
        allBookList = findViewById(R.id.home_allBookList)
        allBookListAdapter = AllBookAdapter(bookData) { previousTab, clickedTab, position ->
            clickedTab.background = ResourcesCompat.getDrawable(resources, R.drawable.bg_book_tab_selected, theme)
            Toast.makeText(this, "clicked", Toast.LENGTH_SHORT).show()
        }
        allBookList.layoutManager = GridLayoutManager(this, 3, GridLayoutManager.HORIZONTAL, false)
        allBookList.adapter = allBookListAdapter
        // Pinned book Recyclerview
        pinnedBookTab = findViewById(R.id.home_pinnedBookTabBar)
        pinnedBookTabAdapter = AllBookAdapter(bookData) { previousTab, clickedTab, position ->
            
        }
        pinnedBookTab.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        pinnedBookTab.adapter = pinnedBookTabAdapter

        // Do the database stuff
        handleDatabase()

        //addBook("$widthDPI $heightDPI")

        // If window size is too small, don't show icon in noBook
        if(heightDPI < 340) {
            findViewById<ImageView>(R.id.home_noBookIcon).visibility = View.GONE
        }

        findViewById<FloatingActionButton>(R.id.home_createNoteButton).setOnClickListener {
            addBook("Test Book")
        }

        // On openDrawer button clicked
        var foregroundViewAnimation: ObjectAnimator? = null
        var bookTabBarAnimation: ObjectAnimator? = null
        openDrawerButton.setOnClickListener {
            if(isDrawerOpened) {
                isDrawerOpened = false
                foregroundViewAnimation?.cancel()
                foregroundViewAnimation = ObjectAnimator.ofFloat(foregroundView, "translationY", 0f).apply {
                    var canceled = false
                    duration = 300
                    interpolator = DecelerateInterpolator()
                    doOnCancel {
                        canceled = true
                    }
                    doOnEnd {
                        if(!canceled) {
                            allBookTitle.visibility = View.GONE
                            createBookButton.visibility = View.GONE
                            allBookList.visibility = View.GONE
                        }
                    }
                    start()
                }
                /*bookTabBarAnimation = ObjectAnimator.ofFloat(pinnedBookTab, "translationY", 0f).apply {
                    pinnedBookTab.layoutParams.height = (48 * displayMetrics.density).toInt()
                    pinnedBookTab.requestLayout()
                    layoutmanager.spanCount = 1
                    pinnedBookTabAdapter.notifyItemRangeChanged(0, booklist.size)
                    duration = 300
                    start()
                }
                //bookTabBar.layoutParams.height = (48 * displayMetrics.density).toInt(); bookTabBar.requestLayout()
                ValueAnimator.ofInt((144 * displayMetrics.density).toInt(), (48 * displayMetrics.density).toInt()).apply {
                    duration = 300
                    addUpdateListener {
                        pinnedBookTab.layoutParams.height = it.animatedValue as Int
                        pinnedBookTab.requestLayout()
                        heightInfo.text = it.animatedValue.toString()
                    }
                    start()
                }*/
            } else {
                isDrawerOpened = true
                allBookTitle.visibility = View.VISIBLE
                createBookButton.visibility = View.VISIBLE
                allBookList.visibility = View.VISIBLE

                foregroundViewAnimation?.cancel()
                foregroundViewAnimation = ObjectAnimator.ofFloat(foregroundView, "translationY", -340 * displayMetrics.density).apply {
                    duration = 300
                    interpolator = DecelerateInterpolator()
                    start()
                }
                /*bookTabBarAnimation?.cancel()
                bookTabBarAnimation = ObjectAnimator.ofFloat(pinnedBookTab, "translationY", -48f * displayMetrics.density).apply {
                    layoutmanager.spanCount = 3
                    pinnedBookTabAdapter.notifyItemRangeChanged(0, booklist.size)
                    duration = 300
                    start()
                }
                //bookTabBar.layoutParams.height = (144 * displayMetrics.density).toInt(); bookTabBar.requestLayout()
                ValueAnimator.ofInt((48 * displayMetrics.density).toInt(), (144 * displayMetrics.density).toInt()).apply {
                    duration = 300
                    addUpdateListener {
                        pinnedBookTab.layoutParams.height = it.animatedValue as Int
                        pinnedBookTab.requestLayout()
                        heightInfo.text = it.animatedValue.toString()
                    }
                    start()
                }*/
            }
        }
    }

    // Database stuff
    private fun handleDatabase() {
        db = Firebase.firestore
        db.collection(authId)
            .get()
            .addOnSuccessListener { collection ->
                if(!collection.isEmpty) { // If database isn't empty
                    collection.documents.forEach {
                        addBook(it.get("book").toString())
                    }
                } else { // If database is empty: Show "no book" screen
                    val noBookLayout: LinearLayout = findViewById(R.id.home_noBookLayout)
                    noBookLayout.visibility = View.VISIBLE
                    // Create new book if add book button clicked
                    findViewById<Button>(R.id.home_noBookButton).setOnClickListener {
                        val newbook = hashMapOf(
                            "book" to "Test book"
                        )
                        db.collection(authId)
                            .add(newbook)
                            .addOnSuccessListener {
                                noBookLayout.visibility = View.GONE
                                addBook(it.id)
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, it.toString(), Toast.LENGTH_LONG).show()
                            }
                    }
                }
            }
    }

    // Insert book into bookTabBar
    private fun addBook(name: String) {
        bookData.add(BookClass(name))
        notelist.add(NoteListClass("Judul", name))
        allBookListAdapter.notifyItemInserted(bookData.lastIndex)
        pinnedBookTabAdapter.notifyItemInserted(bookData.lastIndex)
        noteListAdapter.notifyItemInserted(notelist.lastIndex)
    }
}