package com.example.flashcards.ui.viewmodel

import android.support.v4.app.INotificationSideChannel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.flashcards.models.CardData
import com.example.flashcards.models.FlashCardData
import com.example.flashcards.repo.CardPageRepository
import io.reactivex.Maybe
import javax.inject.Inject

class CardPageViewModel @Inject constructor(private val repo:CardPageRepository) :ViewModel(){

    private val _allCard=repo.allCards
    var allCards:LiveData<List<CardData>> =_allCard
    var insetCard:LiveData<Int> =repo.insetCard

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


    fun onDestroy(){
        repo.onDestroy()
    }

}