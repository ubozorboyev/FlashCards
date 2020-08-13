package ubr.flash.flashcards

import android.app.Application
import ubr.flash.flashcards.dagger.AppModule
import ubr.flash.flashcards.dagger.DaggerRoomComponent
import ubr.flash.flashcards.dagger.RoomComponent
import ubr.flash.flashcards.models.AppDatabase

class App :Application(){

    companion object{
        private lateinit var component: RoomComponent

        fun getComponent()= component
    }

    override fun onCreate() {

        component = DaggerRoomComponent.builder().appModule(AppModule(this)).build()

        component.inject(this)

        super.onCreate()
    }

    override fun onTerminate() {
        super.onTerminate()
        AppDatabase.getDatabase().close()
    }

}