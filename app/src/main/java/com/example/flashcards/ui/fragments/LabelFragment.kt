package com.example.flashcards.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.flashcards.App
import com.example.flashcards.R
import com.example.flashcards.adapters.LabelAdapter
import com.example.flashcards.databinding.LabelFragmentBinding
import com.example.flashcards.ui.viewmodel.LabelViewModel
import kotlinx.android.synthetic.main.toolbar_layout.view.*
import javax.inject.Inject
import kotlin.math.log

class LabelFragment :BaseFragment<LabelFragmentBinding>(R.layout.label_fragment){


    @Inject lateinit var viewModel: LabelViewModel
    private val adapter=LabelAdapter()

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
            }
            2->{
                binding.appBar.actionBarTitle.text="Todo"
                viewModel.loadAllTodo()
            }
            3->{
                binding.appBar.actionBarTitle.text="Dictionary"
                viewModel.loadAllDictionary()
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
            val bundle=Bundle()
            bundle.clear()
            bundle.putInt("ID",it.id)
            bundle.putString("NAME",it.name)
            bundle.putInt("COLOR",it.backgroundColor)

            findNavController().navigate(R.id.action_labelFragment_to_cardPageFragment,bundle)
        }

    }

}