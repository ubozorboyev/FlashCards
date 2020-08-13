package ubr.flash.flashcards.repo

import androidx.lifecycle.MutableLiveData
import ubr.flash.flashcards.models.FlashCardDao
import ubr.flash.flashcards.models.FlashCardData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class StarsRepository @Inject constructor(private val flashCardDao: FlashCardDao){

    private lateinit var disposable: Disposable

    val _starsList = MutableLiveData<List<FlashCardData>>()
    //val starsList: LiveData<List<FlashCardData>> get() = _starsList


    fun loadStarsFlashcards(){

        disposable=flashCardDao.getStarsFlashcards()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _starsList.value = it
            },{

            })
    }



}