package com.example.flashcards.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.flashcards.databinding.TrashItemBinding
import com.example.flashcards.models.FlashCardData

class LabelAdapter :RecyclerView.Adapter<LabelAdapter.ViewHolder>(){

    private val labelList=ArrayList<FlashCardData>()
    private var onClickListener:((FlashCardData)->Unit)?=null

   inner class ViewHolder(trashItemBinding: TrashItemBinding):RecyclerView.ViewHolder(trashItemBinding.root){
       val title=trashItemBinding.cardTitle
       val count=trashItemBinding.flashCardCount
       val backgroundColor=trashItemBinding.trashItemCardView
       val item=trashItemBinding.root
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val inflater=LayoutInflater.from(parent.context)
        val itemBinding=TrashItemBinding.inflate(inflater,parent,false)
        return ViewHolder(itemBinding)
    }

    override fun getItemCount(): Int {
       return labelList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val data=labelList[position]

        holder.backgroundColor.setCardBackgroundColor(data.backgroundColor)
        holder.count.text=data.cardCount.toString()
        holder.title.text=data.name
        holder.item.setOnClickListener {
            onClickListener?.invoke(labelList[position])
        }

    }

    fun setData(list: List<FlashCardData>){
        labelList.clear()
        labelList.addAll(list)
        notifyDataSetChanged()
    }

    fun onClickItemListener(ls:(FlashCardData)->Unit){
        onClickListener=ls
    }


}
