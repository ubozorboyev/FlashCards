package com.example.flashcards.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.flashcards.R
import com.example.flashcards.databinding.AllsetItemBinding
import com.example.flashcards.models.FlashCardData

class AllSetAdapter :RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    var list= arrayListOf<FlashCardData>()
    var listener:((FlashCardData)->Unit)?= null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val inflater=LayoutInflater.from(parent.context)

        when(viewType){

            1 ->{
                val view=inflater.inflate(R.layout.main_item,parent,false)
                return StickyViewHolder(view)
            }
            else->{
                val itemBinding=AllsetItemBinding.inflate(inflater)
                return ViewHolder(itemBinding)
            }
        }
    }

    override fun getItemCount(): Int {
      return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when(holder){
            is StickyViewHolder ->{
                holder.bind(list[position])
            }
            is ViewHolder->{
                holder.bind(list[position])
            }
        }
    }

    override fun getItemViewType(position: Int): Int {

        if (position==0) return 0
        val oldNmae=list[position-1].name[0].toUpperCase()
        val newName=list[position].name[0].toUpperCase()

        return if (oldNmae!=newName) 1 else 0
    }

    inner class ViewHolder(val itemBinding: AllsetItemBinding):RecyclerView.ViewHolder(itemBinding.root){

        fun bind(flashCard: FlashCardData){
            itemBinding.cardData=flashCard
            itemBinding.itemCardView.setCardBackgroundColor(flashCard.backgroundColor)
            itemBinding.flashCardCount.text=flashCard.cardCount.toString()
            itemBinding.root.setOnClickListener { listener?.invoke(flashCard) }
        }
    }

    inner class StickyViewHolder(val view: View):RecyclerView.ViewHolder(view){

        fun bind(flashCard: FlashCardData){
            Log.d("FFFF","flashcard $flashCard")
            view.findViewById<TextView>(R.id.stickyHeader)
                .text=flashCard.name[0].toUpperCase().toString()
        }
    }

    fun setData(ls: List<FlashCardData>){
        list.clear()
        val sortList=ls.sortedWith(compareBy { it.name.toUpperCase() })
        list.addAll(sortList)
        notifyDataSetChanged()
    }


}