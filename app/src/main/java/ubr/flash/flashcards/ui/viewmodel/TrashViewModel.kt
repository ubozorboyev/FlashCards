package ubr.flash.flashcards.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ubr.flash.flashcards.models.CardDao
import ubr.flash.flashcards.models.FlashCardDao
import ubr.flash.flashcards.models.FlashCardData
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class TrashViewModel @Inject constructor( val dao: FlashCardDao, val cardDao: CardDao) :ViewModel(){

    private lateinit var disposable: Disposable
    private val _allDeleteCards=MutableLiveData<List<FlashCardData>>()
    val allDeleteCards:LiveData<List<FlashCardData>> =_allDeleteCards


    fun loadTrashCards(){

        disposable=dao.getDeleteCard().observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({
                _allDeleteCards.value=it
            },{

            })
    }


    fun deleteCard(flashCardData: FlashCardData){

        disposable=Completable.fromAction {

            dao.deleteFlashCardData(flashCardData)
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.d("DDDD","DELETE CARD $flashCardData")

            },{
                Log.d("DDDD","DELETE THROWABLE $it")
            })
    }

    fun updateFlashCard(flashCardData: FlashCardData){

        disposable=Completable.fromAction {

            cardDao.updateFlashCard(flashCardData)
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.d("DDDD","UPDATE CARD $flashCardData")

            },{

            })
    }


    fun onDestroy(){

        if (::disposable.isInitialized){
            disposable.dispose()
        }
    }

}