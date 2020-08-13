package ubr.flash.flashcards.repo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ubr.flash.flashcards.models.FlashCardDao
import ubr.flash.flashcards.models.FlashCardData
import io.reactivex.disposables.Disposable
import javax.inject.Inject

class AllSetsRepository @Inject constructor(val flashDao: FlashCardDao){
    lateinit var disposable: Disposable
    var _isAddFlashCard=MutableLiveData<Int>()
    var _flashCardData=MutableLiveData<FlashCardData>()

    fun getAllFlashCards():LiveData<List<FlashCardData>>{
        return flashDao.getAllFlashCard()
    }

    fun addFlashCard(flashCardData: FlashCardData)= flashDao.addFlashCard(flashCardData)


}