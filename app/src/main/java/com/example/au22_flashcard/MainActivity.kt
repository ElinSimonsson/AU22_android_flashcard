package com.example.au22_flashcard

import android.content.Intent
import android.graphics.Color
import android.graphics.DashPathEffect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.widget.TextView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.*
import org.w3c.dom.Text
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(), CoroutineScope {
    lateinit var job : Job
    lateinit var wordView : TextView
    lateinit var floatingActionButton: FloatingActionButton
    lateinit var englishToSwedishButton : TextView
    lateinit var swedishToEnglishButton : TextView
    var currentWord : Word? = null
    lateinit var db : AppDatabase
    lateinit var list : Deferred<List<Word>>
    lateinit var wordList : List<Word>
    lateinit var usedWords : MutableList<Word>
    var checkLanguage = "swedish"

    override val coroutineContext : CoroutineContext
    get() = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        job = Job()

        db = AppDatabase.getInstance(this)

        wordView = findViewById(R.id.wordTextView)
        englishToSwedishButton = findViewById(R.id.englishToSwedishTextView)
        swedishToEnglishButton = findViewById(R.id.swedishToEnglishTextView)
        wordList = mutableListOf()
        usedWords = mutableListOf()
        floatingActionButton = findViewById<FloatingActionButton?>(R.id.floatingActionButton)

        englishToSwedishButton.setOnClickListener {
            englishButtonPressedSetButtonsColor()
            checkLanguage = "english"
            changeLanguage()
        }

        swedishToEnglishButton.setOnClickListener {
            swedishButtonPressedSetButtonsColor()
            checkLanguage = "swedish"
            changeLanguage()
        }

        floatingActionButton.setOnClickListener {
            val intent = Intent(this, AddWordActivity::class.java)
            startActivity(intent)
        }

        wordView.setOnClickListener {
            revealTranslation()
        }
    }

    private fun changeLanguage() {
        when (checkLanguage) {
            "swedish" -> wordView.text = currentWord?.swedish
            "english" -> wordView.text = currentWord?.english
        }
    }

    override fun onResume() {
        super.onResume()
        loadAllWordsAndShowWord()
    }

    fun loadAllWordsAndShowWord() {
        list = loadAllWords()
        launch {
            wordList = list.await()
            if (wordList.isEmpty()) {
                val messageToUser ="The list is empty, add new word by clicking addButton"
                wordView.text = messageToUser
            } else {
                showNewWord()
            }
        }
    }

    fun revealTranslation() {
        val english = currentWord?.english
        Log.d("!!!", "engelska : $english")
        when (checkLanguage) {
            "swedish" -> wordView.text = currentWord?.english
            "english" -> wordView.text = currentWord?.swedish
        }
    }

    fun getNewWord() : Word {
        if (wordList.size == usedWords.size) {
            usedWords.clear()
        }
        var word : Word? = null

        do {
            val rnd = (0 until wordList.size).random()
            word = wordList[rnd]
        } while(usedWords.contains(word))

        usedWords.add(word!!)

        return word
    }

  fun showNewWord () {
      currentWord = getNewWord()
      when (checkLanguage) {
          "swedish" -> wordView.text = currentWord?.swedish
          "english" -> wordView.text = currentWord?.english
      }
    }

    fun loadAllWords () : Deferred<List<Word>> =
        async (Dispatchers.IO) {
            db.wordDao.getAllWords()
        }

    fun englishButtonPressedSetButtonsColor () {
        englishToSwedishButton.setBackgroundResource(R.drawable.rounded_button)
        englishToSwedishButton.setTextColor(Color.WHITE)
        swedishToEnglishButton.setBackgroundResource(R.drawable.list_divider)
        swedishToEnglishButton.setTextColor(Color.parseColor("#D50000"))
    }

    fun swedishButtonPressedSetButtonsColor () {
        swedishToEnglishButton.setBackgroundResource(R.drawable.rounded_button)
        swedishToEnglishButton.setTextColor(Color.WHITE)
        englishToSwedishButton.setBackgroundResource(R.drawable.list_divider)
        englishToSwedishButton.setTextColor(Color.parseColor("#D50000"))
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_UP) {
            showNewWord()
        }
        return true
    }
}


// 1. skapa ny aktivitet där man lägger in ny ord

// 2. spara det nya ordet i databas

// 3. i main läs in alla ord från databas

// (anväd coroutiner när ni läser och skriver till databasen se tidigare exempel)