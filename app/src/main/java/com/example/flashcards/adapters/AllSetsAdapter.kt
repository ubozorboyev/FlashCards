package com.example.flashcards.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.example.flashcards.databinding.AllsetItemBinding
import com.example.flashcards.models.FlashCardData

class AllSetsAdapter() :RecyclerView.Adapter<AllSetsAdapter.ViewHolder>(),Filterable{

    var list= arrayListOf<FlashCardData>()
    var listener:((FlashCardData)->Unit)?= null
    var filterList= arrayListOf<FlashCardData>()
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
      return filterList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
      holder.bind(filterList[position])
    }

    fun setData(ls: List<FlashCardData>){
        list.clear()
        val sortList=ls.sortedWith(compareBy { it.name.toUpperCase() })
        list.addAll(sortList)
        filterList= list
        notifyDataSetChanged()
    }

    override fun getFilter(): Filter {

        return object :Filter(){
            override fun performFiltering(constraint: CharSequence?): FilterResults {

                val query=constraint.toString()

                var resultList= arrayListOf<FlashCardData>()

                if (!query.isNullOrEmpty()){
                    list.forEach {
                        if (it.name.toLowerCase().contains(query)){
                            resultList.add(it)
                        }
                    }

                }else{
                    resultList=list
                }

                val fiResult=FilterResults()
                fiResult.values=resultList

                return fiResult
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filterList=results!!.values as ArrayList<FlashCardData>
                notifyDataSetChanged()
            }
        }
    }
}