package ubr.flash.flashcards.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import ubr.flash.flashcards.models.FlashCardData
import ubr.flash.flashcards.repo.StarsRepository
import javax.inject.Inject

class StarsViewmodel @Inject constructor(private val repositoryFactory: StarsRepository) : ViewModel(){

    val starsList:LiveData<List<FlashCardData>> get() = repositoryFactory._starsList


    fun loadAllastars(){
        repositoryFactory.loadStarsFlashcards()
    }

}