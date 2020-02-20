package com.example.flashcards.dagger

import com.example.flashcards.App
import com.example.flashcards.ui.screens.AllSetsFragment
import com.example.flashcards.ui.screens.CardPageFragment
import dagger.Component


@Component(modules = arrayOf(AppModule::class))
interface RoomComponent {

    fun inject(application: App)

    fun inject(allSetsFragment: AllSetsFragment)

    fun inject(cardPageFragment: CardPageFragment)

}