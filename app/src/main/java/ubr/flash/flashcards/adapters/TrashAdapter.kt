package ubr.flash.flashcards.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ubr.flash.flashcards.databinding.TrashItemBinding
import ubr.flash.flashcards.models.FlashCardData

class TrashAdapter :RecyclerView.Adapter<TrashAdapter.ViewHolder>(){

    private val trashCardList= arrayListOf<FlashCardData>()
    var trashItemClick:((FlashCardData)->Unit)?=null


    inner class ViewHolder(val itemBinding: TrashItemBinding):RecyclerView.ViewHolder(itemBinding.root){

        fun bind(flashCardData: FlashCardData){
            itemBinding.cardTitle.text=flashCardData.name
            itemBinding.root.setOnClickListener {
                trashItemClick?.invoke(flashCardData)
            }
            itemBinding.flashCardCount.text=flashCardData.cardCount.toString()
            itemBinding.trashItemCardView.setCardBackgroundColor(flashCardData.backgroundColor)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val inflater=LayoutInflater.from(parent.context)

        return ViewHolder(TrashItemBinding.inflate(inflater,parent,false))
    }

    override fun getItemCount(): Int {
        return trashCardList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(trashCardList[position])
    }

    fun setData(list: List<FlashCardData>){
        trashCardList.clear()
        trashCardList.addAll(list)
        notifyDataSetChanged()
    }

}