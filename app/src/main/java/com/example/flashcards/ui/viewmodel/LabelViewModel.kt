package com.example.flashcards.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.flashcards.models.FlashCardDao
import com.example.flashcards.models.FlashCardData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class LabelViewModel @Inject constructor(val dao: FlashCardDao):ViewModel(){

    private lateinit var disposable: Disposable
    private val _allLabel=MutableLiveData<List<FlashCardData>>()
    val allLabel:LiveData<List<FlashCardData>> =_allLabel


    fun loadImpotrant(){
        disposable=dao.getAllImportant()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.d("LLLL","flash $it")
                _allLabel.value=it
            },{
                Log.d("LLLL","throwable $it")
                it.printStackTrace()
            })
    }

    fun loadAllDictionary(){
        disposable=dao.getAllDictionary()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.d("LLLL","flash $it")
                _allLabel.value=it
            },{
                Log.d("LLLL","throwable $it")
                it.printStackTrace()
            })
    }

    fun loadAllTodo(){
        disposable=dao.getAllTodo()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.d("LLLL","flash $it")
                _allLabel.value=it
            },{
                Log.d("LLLL","throwable $it")
                it.printStackTrace()
            })
    }
}