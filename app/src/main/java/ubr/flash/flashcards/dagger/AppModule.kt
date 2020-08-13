package ubr.flash.flashcards.dagger

import android.app.Application
import ubr.flash.flashcards.models.AppDatabase
import ubr.flash.flashcards.ui.viewmodel.CardPageViewModel
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