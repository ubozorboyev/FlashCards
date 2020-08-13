package ubr.flash.flashcards.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import ubr.flash.flashcards.models.FlashCardData
import ubr.flash.flashcards.repo.AllSetsRepository
import javax.inject.Inject

class AllSetsViewModel @Inject constructor(val repository: AllSetsRepository) :ViewModel(){

    val allFlashCards:LiveData<List<FlashCardData>> =repository.getAllFlashCards()
    val isAddFlashCards:LiveData<Int> =repository._isAddFlashCard
    val getFlashCard:LiveData<FlashCardData> =repository._flashCardData

    fun addFlashCards(flashCardData: FlashCardData)=
        repository.addFlashCard(flashCardData)


    fun getFlashCardById(id:Int){
//        repository.getFlashCardById(id)
    }


    fun onDestroy(){
        repository.disposable.dispose()
    }

}