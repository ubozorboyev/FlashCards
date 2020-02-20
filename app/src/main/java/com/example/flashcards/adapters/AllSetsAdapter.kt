package com.example.flashcards.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.flashcards.databinding.AllsetItemBinding
import com.example.flashcards.models.FlashCardData

class AllSetsAdapter() :RecyclerView.Adapter<AllSetsAdapter.ViewHolder>(){

    val list= arrayListOf<FlashCardData>()
    var listener:((FlashCardData)->Unit)?= null

    inner class ViewHolder(val itemBinding: AllsetItemBinding):RecyclerView.ViewHolder(itemBinding.root){

        fun bind(flashCard: FlashCardData){
           itemBinding.cardData=flashCard

           itemBinding.itemCardView.setCardBackgroundColor(flashCard.backgroundColor)
           itemBinding.flashCardCount.text=flashCard.cardCount.toString()
           itemBinding.root.setOnClickListener { listener?.invoke(flashCard) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
      val inflater=LayoutInflater.from(parent.context)
        val binding=AllsetItemBinding.inflate(inflater,parent,false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
      return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
      holder.bind(list[position])
    }

}