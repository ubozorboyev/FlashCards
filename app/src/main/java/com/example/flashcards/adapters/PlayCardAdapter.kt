package com.example.flashcards.adapters

import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.flashcards.R
import com.example.flashcards.models.CardData
import com.example.flashcards.setOnFinishListener
import java.lang.Exception

class PlayCardAdapter :RecyclerView.Adapter<PlayCardAdapter.ViewHolder>(){

    private val cardList= arrayListOf<CardData>()
    private val duration=100L
    var isOpen=true
    var color:Int= Color.parseColor("#F7E378")


    inner class ViewHolder(view: View):RecyclerView.ViewHolder(view){
     val textView=view.findViewById<TextView>(R.id.textView)
     val imageView=view.findViewById<ImageView>(R.id.imageView)
     val cardView=view.findViewById<CardView>(R.id.playCardView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val infalte=LayoutInflater.from(parent.context)

        val view=infalte.inflate(R.layout.play_card_item,parent,false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return cardList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if (isOpen){
            holder.textView.text=cardList[position].forText

            try {

                holder.imageView.setImageURI(Uri.parse(cardList[position].forImage))

            }catch (e:Exception){
                holder.imageView.setImageURI(null)
                e.printStackTrace()
            }

        }else{

            holder.textView.text=cardList[position].backText

            try {

                holder.imageView.setImageURI(Uri.parse(cardList[position].backImage))

            }catch (e:Exception){
                holder.imageView.setImageURI(null)
                e.printStackTrace()
            }

        }

        holder.cardView.setCardBackgroundColor(color)

    }

    fun setDataList(list: List<CardData>){
        cardList.addAll(list)
        notifyDataSetChanged()
    }

    fun rototionItem(viewHolder: ViewHolder,position: Int){
        val itemView=viewHolder.itemView

        if (isOpen){

            itemView.animate().rotationY(-89f)
                .setDuration(duration)
                .setOnFinishListener {

                    isOpen=false
                    itemView.rotationY=-271f
                    onBindViewHolder(viewHolder,position)
                    itemView.animate().rotationY(-360f)
                        .setDuration(duration)
                        .setOnFinishListener {
                            itemView.rotationY=0f
                        }
                }

        }else{

            itemView.animate().rotationY(89f)
                .setDuration(duration)
                .setOnFinishListener {
                    isOpen=true
                    itemView.rotationY=271f
                    onBindViewHolder(viewHolder,position)
                    itemView.animate().rotationY(360f)
                        .setDuration(duration)
                        .setOnFinishListener {
                            itemView.rotationY=0f
                        }
                }
        }
    }
}