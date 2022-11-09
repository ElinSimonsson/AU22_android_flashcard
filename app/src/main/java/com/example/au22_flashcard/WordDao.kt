package com.example.au22_flashcard

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface WordDao {

    @Insert
    fun insert (word: Word)

    //delete
    @Delete
    fun delete(word: Word)

    //getAllWords
    @Query ("SELECT * FROM word_table")
    fun getAllWords () : List <Word>



}