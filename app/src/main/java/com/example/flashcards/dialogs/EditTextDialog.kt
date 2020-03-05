package com.example.flashcards.dialogs

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.ActionMode
import android.view.LayoutInflater
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.content.getSystemService
import com.example.flashcards.R
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.edit_text_dialog.view.*

class EditTextDialog(val context: Context) {
    var listener:((String)->Unit)?=null
    private val builder=AlertDialog.Builder(context)
    private val dialog:AlertDialog
    init{

        val view=LayoutInflater.from(context).inflate(R.layout.edit_text_dialog,null,false)

        val editText=view.findViewById<TextInputEditText>(R.id.editText)

        builder.setPositiveButton("Ok",object :DialogInterface.OnClickListener{

            override fun onClick(dialog: DialogInterface?, which: Int) {
                listener?.invoke(editText.text.toString())
                dialog?.dismiss()
            }
        })

        editText.setOnEditorActionListener { v, actionId, event ->
            when(actionId){
                EditorInfo.IME_ACTION_DONE->{
                    listener?.invoke(editText.text.toString())
                    true

                }
                else->false
            }
        }

        dialog=builder.setView(view).create()

        showKeyboard()

        dialog.show()
    }

    fun editTextListener(ls:(String)->Unit){
        listener=ls
    }

    fun showKeyboard(){
        val imm=context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,0)
    }

    fun hideKyboard(){

        val imm=context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY,0)
    }

}