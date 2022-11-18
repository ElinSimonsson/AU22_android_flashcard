package com.example.au22_flashcard

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class AllWordsRecycleAdapter (var listOfWords : MutableList<Word>, val clickListener: ClickListener)
    : RecyclerView.Adapter<AllWordsRecycleAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.all_words_list_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listOfWords[position])

    }

    override fun getItemCount(): Int {
        return listOfWords.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        CoroutineScope {
        private lateinit var job: Job
        private lateinit var db: AppDatabase
        override val coroutineContext: CoroutineContext
            get() = Dispatchers.Main + job

        private var englishWordTV = itemView.findViewById<TextView>(R.id.englishWordTextView)
        private val swedishWordTV = itemView.findViewById<TextView>(R.id.swedishWordTextView)
        private val deleteButton = itemView.findViewById<ImageView>(R.id.deleteImageView)
        private val context = itemView.context

        fun bind(currentWord: Word) {
            db = AppDatabase.getInstance(itemView.context)
            job = Job()
            englishWordTV.text =
                context.getString(R.string.showEnglishWord_textview, currentWord.english)
            swedishWordTV.text =
                context.getString(R.string.showSwedishWord_textview, currentWord.swedish)

            deleteButton.setOnClickListener {
                delete(currentWord)
                listOfWords.remove(currentWord)
                notifyDataSetChanged()
                if (listOfWords.isEmpty()) {
                    clickListener.listIsEmpty()
                }

//                if(listOfWords.isEmpty()) {
//                    activity.finish()
//                } else {
//                    notifyDataSetChanged()
//                }
            }

            itemView.setOnClickListener {
                Log.d("!!!", "currentId: ${currentWord.id}")
                val intent = Intent(context, AddWordActivity::class.java)
                intent.putExtra("id", currentWord.id)
                intent.putExtra("englishWord", currentWord.english)
                intent.putExtra("swedishWord", currentWord.swedish)
                context.startActivity(intent)

            }
        }

        private fun delete(word: Word) =
            launch(Dispatchers.IO) {
                db.wordDao.delete(word)
            }
    }

    interface ClickListener {
        fun listIsEmpty()
    }
}