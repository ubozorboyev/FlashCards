package ubr.flash.flashcards.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import ubr.flash.flashcards.App
import ubr.flash.flashcards.R
import ubr.flash.flashcards.adapters.TrashAdapter
import ubr.flash.flashcards.databinding.TrashFragmentBinding
import ubr.flash.flashcards.dialogs.DeleteDialog
import ubr.flash.flashcards.models.FlashCardData
import ubr.flash.flashcards.ui.viewmodel.TrashViewModel
import kotlinx.android.synthetic.main.toolbar_layout.view.*
import javax.inject.Inject

class TrashFragment :BaseFragment<TrashFragmentBinding>(R.layout.trash_fragment){

    private val adapter=TrashAdapter()
    @Inject lateinit var viewModel: TrashViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.getComponent().inject(this)
        viewModel.loadTrashCards()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.appBar.actionBarTitle.text="Trash"
        init()
        clickListeners()
    }


    fun init(){

        binding.recyclview.layoutManager=GridLayoutManager(context!!,GridLayoutManager.VERTICAL)
        binding.recyclview.adapter=adapter


        val observer=Observer<List<FlashCardData>>{
            adapter.setData(it)
            binding.recyclview.startLayoutAnimation()
        }

        viewModel.allDeleteCards.observe(this,observer)

    }


    fun clickListeners(){

        adapter.trashItemClick=object :(FlashCardData)->Unit{

            override fun invoke(p1: FlashCardData) {

                val dialog=DeleteDialog(context!!)

                dialog.setDeleteListener {

                    Log.d("DDD","int $it")

                    if (it==1){
                        p1.isDelete=false
                        viewModel.updateFlashCard(p1)
                    }
                    else if (it==0){
                        Log.d("DDD","delete  $p1")

                        viewModel.deleteCard(p1)
                    }
                }
            }
        }

        binding.appBar.imageBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        viewModel.onDestroy()
    }

}