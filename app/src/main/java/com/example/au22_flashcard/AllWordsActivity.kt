package com.example.au22_flashcard

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class AllWordsActivity : AppCompatActivity(), CoroutineScope, AllWordsRecycleAdapter.ClickListener {
    lateinit var job: Job
    lateinit var recyclerView: RecyclerView
    lateinit var noWordsAdded: TextView
    lateinit var deleteAllWordsTV: TextView
    lateinit var wordList: List<Word>
    lateinit var db: AppDatabase
    lateinit var list: Deferred<List<Word>>


    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_words)
        recyclerView = findViewById(R.id.recyclerView)
        noWordsAdded = findViewById(R.id.noWordsAddedTextView1)
        deleteAllWordsTV = findViewById(R.id.deleteAllWordsTextView)
        job = Job()
        db = AppDatabase.getInstance(this)

        val actionBar: ActionBar? = supportActionBar
        actionBar?.title = "All words"
        actionBar?.setDisplayHomeAsUpEnabled(true)

        val fabAdd = findViewById<FloatingActionButton>(R.id.floatingActionButton)
            .setOnClickListener {
                val intent = Intent(this, AddWordActivity::class.java)
                startActivity(intent)
            }

        deleteAllWordsTV.setOnClickListener {
            deleteAllWords(wordList as MutableList<Word>)
            setLayoutIfListIsEmpty()
        }
    }

    override fun onResume() {
        super.onResume()

        // Vi behöver läsa data från databas på onResume då användaren eventuellt
        // matat in ny data på AddWordActivity
        readData {
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = AllWordsRecycleAdapter( it, this)
        }
    }

    fun readData(myCallback: (MutableList<Word>) -> Unit) {
        list = loadAllWords()
        launch {
            wordList = list.await()
            if (wordList.isEmpty()) {
                recyclerView.visibility = View.GONE
                noWordsAdded.visibility = View.VISIBLE
                deleteAllWordsTV.visibility = View.GONE
            } else {
                recyclerView.visibility = View.VISIBLE
                noWordsAdded.visibility = View.GONE
                deleteAllWordsTV.visibility = View.VISIBLE
                myCallback(wordList as MutableList<Word>)
            }
        }
    }

    fun loadAllWords(): Deferred<List<Word>> =
        async(Dispatchers.IO) {
            db.wordDao.getAllWords()
        }


    fun deleteAllWords (wordList : MutableList<Word>) {
            launch(Dispatchers.IO) {
                for (word in wordList) {
                    db.wordDao.delete(word)
                }
            }
        }

    fun setLayoutIfListIsEmpty() {
        recyclerView.visibility = View.GONE
        noWordsAdded.visibility = View.VISIBLE
        deleteAllWordsTV.visibility = View.GONE
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun listIsEmpty() {
        setLayoutIfListIsEmpty()
    }
}