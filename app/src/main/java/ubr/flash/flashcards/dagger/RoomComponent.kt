package ubr.flash.flashcards.dagger

import ubr.flash.flashcards.App
import ubr.flash.flashcards.ui.fragments.*
import dagger.Component


@Component(modules = arrayOf(AppModule::class))
interface RoomComponent {

    fun inject(application: App)

    fun inject(allSetsFragment: AllSetsFragment)

    fun inject(cardPageFragment: CardPageFragment)

    fun inject(trashFragment: TrashFragment)

    fun inject(trashFragment: LabelFragment)

    fun inject(playCardFragment: PlayCardFragment)

    fun inject(playCardFragment: StarsFragment)

}