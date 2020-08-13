package ubr.flash.flashcards.dialogs

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import ubr.flash.flashcards.R
import com.google.android.material.textfield.TextInputEditText

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
                hideKyboard()
            }
        })

        dialog=builder.setView(view).create()

        editText.setOnEditorActionListener { v, actionId, event ->
            when(actionId){
                EditorInfo.IME_ACTION_DONE->{
                    listener?.invoke(editText.text.toString())
                      dialog.dismiss()
                      hideKyboard()
                    true
                }
                else->false
            }
        }

        dialog.show()

        showKeyboard()
    }

    fun editTextListener(ls:(String)->Unit){
        listener=ls
    }

    fun showKeyboard(){
        val imm=context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0)
    }

    fun hideKyboard(){

        val imm=context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY,0)
    }

}