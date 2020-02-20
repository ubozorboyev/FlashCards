package com.example.flashcards.dagger

import android.app.Application
import androidx.lifecycle.ViewModel
import com.example.flashcards.models.AppDatabase
import com.example.flashcards.ui.viewmodel.CardPageViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule (val application: Application){

    init {
        AppDatabase.initDataBase(application)
    }


    @Singleton
    @Provides
    fun providesApplecation():Application{
        return application
    }

    @Provides
    fun getCardDao()=AppDatabase.getDatabase().cardDao()

    @Provides
    fun getFlashCardDao()=AppDatabase.getDatabase().flashCardDao()

}