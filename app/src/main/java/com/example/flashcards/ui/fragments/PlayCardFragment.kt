package com.example.flashcards.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.flashcards.App
import com.example.flashcards.R
import com.example.flashcards.adapters.PlayCardAdapter
import com.example.flashcards.databinding.PalyCardFragmentBinding
import com.example.flashcards.getSnapPosition
import com.example.flashcards.ui.viewmodel.PlayCardViewModel
import com.example.flashcards.util.CustomLayoutManager
import javax.inject.Inject
import kotlin.random.Random

class PlayCardFragment :BaseFragment<PalyCardFragmentBinding>(R.layout.paly_card_fragment),View.OnClickListener{

    private val adapter=PlayCardAdapter()
    private var SIZE:Int=0

    @Inject lateinit var viewModel: PlayCardViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.getComponent().inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.recyclview.layoutManager=CustomLayoutManager(context!!,LinearLayoutManager.HORIZONTAL)
        binding.recyclview.adapter=adapter
        binding.fabRototion.setOnClickListener(this)
        binding.fabShuffle.setOnClickListener(this)
        init()
        binding.cardName.text=arguments?.getString("NAME")
        adapter.color=arguments?.getInt("COLOR")!!

        val id=arguments?.getInt("ID")!!
        viewModel.loadAllCards(id)

    }


    fun init(){
        val snapHelper=LinearSnapHelper()
        snapHelper.attachToRecyclerView(binding.recyclview)
        val snapVIew=snapHelper.findSnapView(binding.recyclview.layoutManager)

        viewModel.allCard.observe(viewLifecycleOwner, Observer {
            adapter.setDataList(it)
            binding.currentCard.text="1 / ${it.size}"
            SIZE=it.size
        })

        binding.recyclview.addOnScrollListener(object :RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                val position=LinearSnapHelper().getSnapPosition(recyclerView)+1

                binding.currentCard.text="$position / $SIZE"

                super.onScrollStateChanged(recyclerView, newState)
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val position=LinearSnapHelper().getSnapPosition(recyclerView)+1

                binding.currentCard.text="$position / $SIZE"

                super.onScrolled(recyclerView, dx, dy)
            }

        })

    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.fabRototion->{

                val position=LinearSnapHelper().getSnapPosition(binding.recyclview)

                val holder=binding.recyclview.findViewHolderForAdapterPosition(position) as PlayCardAdapter.ViewHolder
                adapter.rototionItem(holder,position)
            }
            R.id.fabShuffle->{

                val position= Random.nextInt(SIZE)
                binding.recyclview.smoothScrollToPosition(position)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onDestroy()
    }
}