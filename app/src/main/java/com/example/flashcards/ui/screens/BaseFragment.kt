package com.example.flashcards.ui.screens

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import javax.inject.Singleton


abstract class BaseFragment<DB:ViewDataBinding>(@LayoutRes val layoutRes:Int) :Fragment(){
     lateinit var binding:DB

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
      binding= DataBindingUtil.inflate(inflater,layoutRes,container,false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
/*

        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {

            Log.d("BBBB","ONBACKPRESSERT")
            // Handle the back button event
        }
*/

    }


}