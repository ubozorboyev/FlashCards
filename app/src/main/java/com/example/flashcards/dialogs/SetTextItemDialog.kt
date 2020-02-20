package com.example.flashcards.dialogs

import android.content.Context
import com.example.flashcards.R
import kotlinx.android.synthetic.main.edit_text_dialog.view.*

class SetTextItemDialog(context: Context) :BaseDialog(context,R.layout.edit_text_dialog){

    private var textListener:((String)->Unit)?=null


    init {

        view.cancelButton.setOnClickListener {
            dissmis()
        }

        view.okButton.setOnClickListener {

            textListener?.invoke(view.editText.text.toString())

            dissmis()
        }
    }

    fun setTextListener(ls:(String)->Unit){
        textListener=ls
    }

}