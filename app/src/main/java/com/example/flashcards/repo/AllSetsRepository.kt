package com.example.flashcards.repo

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.flashcards.models.FlashCardDao
import com.example.flashcards.models.FlashCardData
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class AllSetsRepository @Inject constructor(val flashDao: FlashCardDao){
    lateinit var disposable: Disposable
    var _isAddFlashCard=MutableLiveData<Int>()
    var _flashCardData=MutableLiveData<FlashCardData>()

    fun getAllFlashCards():LiveData<List<FlashCardData>>{
        return flashDao.getAllFlashCard()
    }

    fun addFlashCard(flashCardData: FlashCardData){

        disposable=flashDao.addFlashCard(flashCardData)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _isAddFlashCard.value=it.toInt()
            },{
               it.printStackTrace()
            })

    }



}