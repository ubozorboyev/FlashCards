package com.example.flashcards.models

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = arrayOf(FlashCardData::class,CardData::class),version = 2,exportSchema = false)
abstract class AppDatabase :RoomDatabase(){

    abstract fun flashCardDao():FlashCardDao

    abstract fun cardDao():CardDao

    companion object{
        @Volatile
        private lateinit var database:AppDatabase

        fun initDataBase(context: Context){
            if (!::database.isInitialized){
                synchronized(this){
                    database=Room.databaseBuilder(context.applicationContext,AppDatabase::class.java,"cardDatebase").build()
                }
            }
        }

        fun getDatabase()= database
    }

}