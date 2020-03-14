package com.example.flashcards.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.flashcards.App
import com.example.flashcards.R
import com.example.flashcards.adapters.LabelAdapter
import com.example.flashcards.databinding.LabelFragmentBinding
import com.example.flashcards.models.FlashCardData
import com.example.flashcards.ui.viewmodel.LabelViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.toolbar_layout.view.*
import javax.inject.Inject
import kotlin.math.log

class LabelFragment :BaseFragment<LabelFragmentBinding>(R.layout.label_fragment){


    @Inject lateinit var viewModel: LabelViewModel
    private val adapter=LabelAdapter()
    private var isImportant=false
    private var isTodo=false
    private var isDictionary=false
    private val bundle=Bundle()
    override fun onCreate(savedInstanceState: Bundle?) {
        App.getComponent().inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        loadItems()
        listeners()

        binding.appBar.imageBack.visibility=View.VISIBLE

    }

    fun loadItems(){
        val label=arguments?.getInt("LABEL",1)

        when(label){
            1->{
                binding.appBar.actionBarTitle.text="Important"
                viewModel.loadImpotrant()
                isImportant=true
            }
            2->{
                binding.appBar.actionBarTitle.text="Todo"
                viewModel.loadAllTodo()
                isTodo=true
            }
            3->{
                binding.appBar.actionBarTitle.text="Dictionary"
                viewModel.loadAllDictionary()
                isDictionary=true
            }
        }

        viewModel.allLabel.observe(viewLifecycleOwner, Observer {
           adapter.setData(it)
           binding.recyclview.startLayoutAnimation()
        })

    }


    fun listeners(){

        binding.appBar.imageBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.recyclview.adapter=adapter

        adapter.onClickItemListener {
            gotoCardPage(it.id,it.name)
        }

        binding.fabButton.setOnClickListener {

         var flashCardData=FlashCardData(0,"Untitel set",0,
             Color.parseColor("#F7E378"),
             false,
             isImportant,
             isTodo,
             isDictionary)

            viewModel.dao.addFlashCard(flashCardData).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                     gotoCardPage(it.toInt(),flashCardData.name)
                },{

                })
        }
    }

    fun gotoCardPage(id:Int,name:String){
        bundle.clear()
        bundle.putInt("ID",id)
        bundle.putString("NAME",name)
        findNavController().navigate(R.id.action_labelFragment_to_cardPageFragment,bundle)
    }
}