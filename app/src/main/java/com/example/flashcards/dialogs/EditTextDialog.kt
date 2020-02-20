package com.example.flashcards.dialogs

import android.content.Context
import com.example.flashcards.R
import kotlinx.android.synthetic.main.edit_text_dialog.view.*

class EditTextDialog(context: Context,var oldText:String) :BaseDialog(context, R.layout.edit_text_dialog){
    var listener:((String)->Unit)?=null

    init{

        view.apply {

            editText.setText(oldText)

            editText.setHint("Set title")

            okButton.setOnClickListener {

                if (!editText.text.toString().isEmpty()){
                    listener?.invoke(editText.text.toString())
                    dissmis()

                } else {
                    editText.error="is not empty"
                }
            }

            cancelButton.setOnClickListener {
                dissmis()
            }
        }
    }

    fun editTextListener(ls:(String)->Unit){
        listener=ls
    }
}