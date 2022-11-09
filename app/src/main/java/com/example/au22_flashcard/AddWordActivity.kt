package com.example.au22_flashcard

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
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

    override val coroutineContext : CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_word)
        db = AppDatabase.getInstance(this)
        job = Job()

        swedishWordET = findViewById(R.id.swedishWordEditText)
        englishWordET = findViewById(R.id.englishWordEditText)

        val saveButton = findViewById<Button>(R.id.saveButton)
        saveButton.setOnClickListener {
            val newWord = getNewWord()
            saveWord(newWord)
        }
    }

    fun saveWord (word : Word) {
        launch (Dispatchers.IO) {
            db.wordDao.insert(word)
        }
    }

    fun getNewWord (): Word {
        val swedishWord = swedishWordET.text.toString()
        val englishWord = englishWordET.text.toString()
        val newWord = Word(0, englishWord, swedishWord)
        return newWord
    }
}