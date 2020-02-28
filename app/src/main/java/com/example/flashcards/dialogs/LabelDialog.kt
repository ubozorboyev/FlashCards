package com.example.flashcards.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.CompoundButton
import com.example.flashcards.R
import com.example.flashcards.databinding.LevelLayoutBinding
import com.example.flashcards.models.FlashCardData
import com.example.flashcards.ui.fragments.CheckLabelInterface
import kotlinx.android.synthetic.main.toolbar_layout.view.*

class LabelDialog(context: Context,
                  val checkLabelInterface: CheckLabelInterface,
                  var cardData: FlashCardData):Dialog(context,R.style.DialogAnimationFromBottom
                  ), CompoundButton.OnCheckedChangeListener{

    private lateinit var binding:LevelLayoutBinding
    private var isTodo=cardData.isTodo
    private var isDictionary=cardData.isDictionary
    private var isImportant=cardData.isImportant

    override fun onCreate(savedInstanceState: Bundle?) {

        binding=LevelLayoutBinding.inflate(LayoutInflater.from(context),null,false)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        checked()
        init()
    }

    fun init(){

        binding.appBar.imageBack.visibility=View.VISIBLE
        binding.appBar.actionBarTitle.visibility=View.VISIBLE
        binding.appBar.actionBarTitle.setText("Select label")
        binding.importantCheck.setOnCheckedChangeListener(this)
        binding.dictionaryCheck.setOnCheckedChangeListener(this)
        binding.todoCheck.setOnCheckedChangeListener(this)

        binding.appBar.imageBack.setOnClickListener {

            cardData.isDictionary=isDictionary
            cardData.isTodo=isTodo
            cardData.isImportant=isImportant
            checkLabelInterface.setLabelCardData(cardData)
            dismiss()
        }
    }


    fun checked(){
        binding.dictionaryCheck.isChecked=isDictionary
        binding.importantCheck.isChecked=isImportant
        binding.todoCheck.isChecked=isTodo
        replaceImage()
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {

        when(buttonView?.id){
            R.id.importantCheck->{
                isImportant=isChecked
            }
            R.id.todoCheck->{
                isTodo=isChecked
            }
            R.id.dictionaryCheck->{
                isDictionary=isChecked
            }
        }
        replaceImage()

    }

    fun replaceImage(){
        if (isDictionary || isImportant || isTodo){
            binding.appBar.imageBack.setImageResource(R.drawable.ic_check)
        }else{
            binding.appBar.imageBack.setImageResource(R.drawable.ic_arrow_back)
        }
    }

}

