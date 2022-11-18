package com.example.au22_flashcard

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class AddWordActivity : AppCompatActivity(), CoroutineScope {
    lateinit var job : Job
    lateinit var swedishWordET : EditText
    lateinit var englishWordET: EditText
    lateinit var db : AppDatabase
    var checkNewWordOrUpdateWord = "newWord" // standard

    override val coroutineContext : CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_word)
        db = AppDatabase.getInstance(this)
        job = Job()
        swedishWordET = findViewById(R.id.swedishWordEditText)
        englishWordET = findViewById(R.id.englishWordEditText)
        val saveButton = findViewById<TextView>(R.id.saveTextView)

        val actionBar: ActionBar? = supportActionBar
        actionBar?.title = "Add word"
        actionBar?.setDisplayHomeAsUpEnabled(true)

        val id = intent.getIntExtra("id", -1)
        val getEnglishWord = intent.getStringExtra("englishWord")
        val getSwedishWord = intent.getStringExtra("swedishWord")

        if (getSwedishWord != null && getEnglishWord != null) {
            actionBar?.title = "Edit the word"
            checkNewWordOrUpdateWord = "update"
            swedishWordET.setText(getSwedishWord)
            englishWordET.setText(getEnglishWord)
            saveButton.text = getString(R.string.save_button)
        }

        saveButton.setOnClickListener {
            if (swedishWordET.text.isNotEmpty() && englishWordET.text.isNotEmpty()) {
                saveWordToDatabase(id)
                finish()
            } else {
                Toast.makeText(this, "The Swedish and English words must not be empty", Toast.LENGTH_SHORT).show()
            }
        }
//        swedishWordET.setOnClickListener {
//            if(englishWordET.text.isNotEmpty()) {
//                Log.d("!!!", "engelska är inte tom")
//            }
//        }
        if(swedishWordET.hasFocus()) {
            Log.d("!!!", "Typing")
        }
    }


    fun saveWord (word : Word) {
        launch (Dispatchers.IO) {
            db.wordDao.insert(word)
        }
    }

    fun updateWord (word : Word) {
        launch (Dispatchers.IO) {
            db.wordDao.update(word)
        }
    }

    fun saveWordToDatabase (id: Int) {
        when (checkNewWordOrUpdateWord) {
            "newWord" -> {
                val swedishWord = swedishWordET.text.toString()
                val englishWord = englishWordET.text.toString()
                val newWord = Word(0, englishWord, swedishWord)
                saveWord(newWord)
            }
            "update" -> {
                val swedishWord = swedishWordET.text.toString()
                val englishWord = englishWordET.text.toString()
                val updatedWord = Word(id, englishWord, swedishWord)
                updateWord(updatedWord)
            }
        }
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()

        }
        return super.onOptionsItemSelected(item)
    }
}