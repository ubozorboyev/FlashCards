package ubr.flash.flashcards.ui.fragments
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ubr.flash.flashcards.App
import ubr.flash.flashcards.R
import ubr.flash.flashcards.adapters.AllSetsAdapter
import ubr.flash.flashcards.databinding.StarsFragmentBinding
import ubr.flash.flashcards.models.FlashCardData
import ubr.flash.flashcards.ui.viewmodel.StarsViewmodel
import kotlinx.android.synthetic.main.toolbar_layout.view.*
import javax.inject.Inject

class StarsFragment :BaseFragment<StarsFragmentBinding>(R.layout.stars_fragment){

    @Inject lateinit var starsViewmodel: StarsViewmodel

    private val adapter = AllSetsAdapter()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        App.getComponent().inject(this)

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        starsViewmodel.loadAllastars()

        starsViewmodel.starsList.observe(viewLifecycleOwner, Observer {
            adapter.setData(ls = it)
            binding.recyclview.startLayoutAnimation()
        })

        binding.recyclview.layoutManager = GridLayoutManager(this.context,2, RecyclerView.VERTICAL,false)
        binding.recyclview.adapter = adapter

        init()


    }


    fun init(){

        binding.appBar.actionBarTitle.setText("Most used")

        binding.appBar.imageBack.setOnClickListener {
            findNavController().popBackStack()
        }


        adapter.listener = object : (FlashCardData) -> Unit{

            override fun invoke(p1: FlashCardData) {

                val bundle = Bundle()
                bundle.putInt("ID",p1.id)
                bundle.putString("NAME",p1.name)
                bundle.putBoolean("ISSELECTED",true)
                findNavController().navigate(R.id.action_starsFragment_to_cardPageFragment,bundle)

            }
        }
    }

}


