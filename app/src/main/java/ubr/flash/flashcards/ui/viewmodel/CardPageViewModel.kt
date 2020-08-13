package ubr.flash.flashcards.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import ubr.flash.flashcards.models.CardData
import ubr.flash.flashcards.models.FlashCardData
import ubr.flash.flashcards.repo.CardPageRepository
import io.reactivex.Maybe
import javax.inject.Inject

class CardPageViewModel @Inject constructor(private val repo:CardPageRepository) :ViewModel(){

    private val _allCard=repo.allCards
    var allCards:LiveData<List<CardData>> =_allCard
    var insetCard:LiveData<Int> =repo.insetCard
    var flashCard:LiveData<FlashCardData> =repo._flashCard

    fun getCardsByFlashCard(id:Int){
        repo.getAllByFlashCard(id)
    }

    fun inserCard(cardData: CardData):Maybe<Long>{
       return repo.insetCardData(cardData)
    }

    fun updateCards( cardData: List<CardData>){
        repo.updateCardData(cardData)
    }

    fun deleteCard(cardData: CardData){
        repo.deleteCardData(cardData)
    }

    fun updateFlashCardById(flashCardData: FlashCardData){
        repo.updateFlashCard(flashCardData)
    }

    fun getFlashCard(id: Int){
        repo.getFlashCard(id)
    }


    fun onDestroy(){
        repo.onDestroy()
    }

}