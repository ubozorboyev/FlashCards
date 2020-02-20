package com.example.flashcards

import android.app.Application
import com.example.flashcards.dagger.AppModule
import com.example.flashcards.dagger.DaggerRoomComponent
import com.example.flashcards.dagger.RoomComponent
import com.example.flashcards.models.AppDatabase

class App :Application(){

    companion object{
        private lateinit var component: RoomComponent

        fun getComponent()= component
    }

    override fun onCreate() {

        component=DaggerRoomComponent.builder().appModule(AppModule(this)).build()

        component.inject(this)

        super.onCreate()
    }

    override fun onTerminate() {
        super.onTerminate()
        AppDatabase.getDatabase().close()
    }

}