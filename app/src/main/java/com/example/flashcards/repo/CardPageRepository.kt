package com.example.flashcards.repo

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.flashcards.models.CardDao
import com.example.flashcards.models.CardData
import com.example.flashcards.models.FlashCardData
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class CardPageRepository @Inject constructor(private val cardDao: CardDao){

     private lateinit var disposable: Disposable
     val allCards=MutableLiveData<List<CardData>>()
     val throwable=MutableLiveData<Throwable>()
     val insetCard=MutableLiveData<Int>()

    fun insetCardData(cardData: CardData):Maybe<Long> {
        return cardDao.addCardData(cardData)
    }

    fun getAllByFlashCard(cardId:Int){

         disposable=cardDao.getFlashByCardData(cardId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                allCards.value=it
            },{
                 throwable.value=it
            },{

            })
    }

    fun updateCardData( cardData: List<CardData>){
        disposable=Completable.fromAction {
            cardDao.updateCardData(cardData)
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

            },{
                it.printStackTrace()
            })

    }

    fun deleteCardData(cardData: CardData){

      disposable= Completable.fromAction{

          cardDao.deleteCardData(cardData)

       }.subscribeOn(Schedulers.io())
           .observeOn(AndroidSchedulers.mainThread()).subscribe({
               Log.d("TTTT","delete card data $cardData")
           },{
              Log.d("TTTT","trrowable $it")

              throwable.value=it
           })
    }

    fun updateFlashCard(flashCardData: FlashCardData){

        disposable=Completable.fromAction {
            cardDao.updateFlashCard(flashCardData)
           }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.d("TTTT","flashCard Update")
            },{
                Log.d("TTTT","flashCard is not  Update  $it")
                it.printStackTrace()
            })
    }

    fun onDestroy(){
        disposable.dispose()
    }

}