package com.example.flashcards.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.room.Index
import com.example.flashcards.models.CardDao
import com.example.flashcards.models.CardData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class PlayCardViewModel @Inject constructor(val dao: CardDao):ViewModel(){

    private val _allCard=MutableLiveData<List<CardData>>()
    val allCard:LiveData<List<CardData>> =_allCard
    private lateinit var disposable: Disposable


    fun loadAllCards(id:Int){
        disposable=dao.getFlashByCardData(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _allCard.value=it
            },{

            })
    }

    fun onDestroy(){
        disposable.dispose()
    }

}