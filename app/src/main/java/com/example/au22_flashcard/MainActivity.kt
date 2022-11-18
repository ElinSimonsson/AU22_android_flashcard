package com.example.au22_flashcard

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.cardview.widget.CardView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(), CoroutineScope {

    private lateinit var job : Job
    private lateinit var wordView : TextView
    private lateinit var noWordsTextView : TextView
    private lateinit var flagImageView: ImageView
    private lateinit var changeFAB : FloatingActionButton
    private lateinit var seeAllWordsFAB : FloatingActionButton
    private lateinit var cardView : CardView
    private var currentWord : Word? = null
    private lateinit var db : AppDatabase
    private lateinit var list : Deferred<List<Word>>
    private lateinit var wordList : List<Word>
    private lateinit var usedWords : MutableList<Word>
    private var checkLanguage = "english"

    override val coroutineContext : CoroutineContext
    get() = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        job = Job()

        db = AppDatabase.getInstance(this)

        wordView = findViewById(R.id.wordTextView)
        noWordsTextView = findViewById(R.id.noWordsAddedTextView)
        flagImageView = findViewById(R.id.flagImageView)
        cardView = findViewById(R.id.cardView)
        changeFAB = findViewById(R.id.changeFAB)
        seeAllWordsFAB = findViewById(R.id.seeAllWordsFAB)
        wordList = mutableListOf()
        usedWords = mutableListOf()

        initializeLayout()

        cardView.setOnClickListener {
            revealTranslation()
        }

        changeFAB.setOnClickListener {
            if (checkLanguage == "english") {
                if (wordList.isNotEmpty()) {
                    checkLanguage = "swedish"
                    changeLanguageAndLayout()
                }
            } else {
                if (wordList.isNotEmpty()) {
                    checkLanguage = "english"
                    changeLanguageAndLayout()
                }
            }
        }

        seeAllWordsFAB.setOnClickListener {
            val intent = Intent(this, AllWordsActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        loadAllWordsAndShowWord()
    }

    private fun changeLanguageAndLayout () {
        if(checkLanguage == "swedish" && changeFAB.backgroundTintList == ColorStateList.valueOf(Color.LTGRAY)) {

            changeFAB.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#db30bf"))
            wordView.text = getString(R.string.showSwedishWord_textview, currentWord?.swedish)
            flagImageView.setImageResource(R.drawable.sveriges_flagga)

        } else if (checkLanguage == "swedish" && changeFAB.backgroundTintList ==
                ColorStateList.valueOf(Color.parseColor("#db30bf"))) {

            wordView.text = getString(R.string.showSwedishWord_textview, currentWord?.swedish)
            flagImageView.setImageResource(R.drawable.sveriges_flagga)
            changeFAB.backgroundTintList = ColorStateList.valueOf(Color.LTGRAY)

        } else if (checkLanguage == "english" && changeFAB.backgroundTintList == ColorStateList.valueOf(Color.LTGRAY)) {

            changeFAB.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#db30bf"))
            wordView.text = getString(R.string.word_textView, currentWord?.english)
            flagImageView.setImageResource(R.drawable.englands_flagga)

        } else {

            wordView.text = getString(R.string.word_textView, currentWord?.english)
            flagImageView.setImageResource(R.drawable.englands_flagga)
            changeFAB.backgroundTintList = ColorStateList.valueOf(Color.LTGRAY)
        }
    }

    private fun loadAllWordsAndShowWord() {
        list = loadAllWords()
        launch {
            wordList = list.await()
            if (wordList.isEmpty()) {
                setLayoutIfListIsEmpty()
            } else {
                setLayoutIfListIsNotEmpty()
                showNewWord()
            }
        }
    }

    private fun setLayoutIfListIsEmpty() {
        cardView.visibility = View.GONE
        noWordsTextView.visibility = View.VISIBLE

    }

    private fun setLayoutIfListIsNotEmpty() {
        cardView.visibility = View.VISIBLE
        noWordsTextView.visibility = View.GONE

    }

    private fun initializeLayout () {
        val actionBar: ActionBar? = supportActionBar
        actionBar?.title = "Flashcards"
        changeFAB.backgroundTintList = ColorStateList.valueOf(Color.LTGRAY)
        seeAllWordsFAB.backgroundTintList =
            ColorStateList.valueOf(Color.parseColor("#db30bf"))
    }

    private fun revealTranslation() {
        when (checkLanguage) {
            "swedish" -> {
                wordView.text = currentWord?.english
                flagImageView.setImageResource(R.drawable.englands_flagga)
                checkLanguage = "english"
            }
            "english" -> {
                wordView.text = currentWord?.swedish
                flagImageView.setImageResource(R.drawable.sveriges_flagga)
                checkLanguage = "swedish"
            }
        }
    }

    private fun getNewWord() : Word {
        if (wordList.size == usedWords.size) {
            usedWords.clear()
        }
        var word : Word?

        do {
            val rnd = (wordList.indices).random()
            word = wordList[rnd]
        } while(usedWords.contains(word))

        usedWords.add(word!!)

        return word
    }

  private fun showNewWord () {
      currentWord = getNewWord()
      when (checkLanguage) {
          "swedish" -> {
              wordView.text = currentWord?.swedish
              flagImageView.setImageResource(R.drawable.sveriges_flagga)
          }
          "english" -> {
              wordView.text = currentWord?.english
              flagImageView.setImageResource(R.drawable.englands_flagga)
          }
      }
    }

   private fun loadAllWords () : Deferred<List<Word>> =
        async (Dispatchers.IO) {
            db.wordDao.getAllWords()
        }


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_UP) {
            if (wordList.isNotEmpty()) {
                showNewWord()
            }
        }
        return true
    }
}


// 1. skapa ny aktivitet där man lägger in ny ord

// 2. spara det nya ordet i databas

// 3. i main läs in alla ord från databas

// (anväd coroutiner när ni läser och skriver till databasen se tidigare exempel)