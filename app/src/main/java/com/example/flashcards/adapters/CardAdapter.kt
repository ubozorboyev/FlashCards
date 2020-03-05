package com.example.flashcards.adapters

import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.os.Handler
import android.os.strictmode.UntaggedSocketViolation
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.annotation.ColorInt
import androidx.core.view.ViewCompat
import androidx.core.view.ViewPropertyAnimatorListener
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.flashcards.R
import com.example.flashcards.databinding.CardItemBinding
import com.example.flashcards.models.*
import com.example.flashcards.setOnFinishListener
import jp.wasabeef.recyclerview.animators.holder.AnimateViewHolder

class CardAdapter(val context: Context) :RecyclerView.Adapter<CardAdapter.ViewHolder>(){
    val cardList= arrayListOf<CardData>()
    private val duration=100L
    var isOpen=true
    @ColorInt
    var bgColor:Int=Color.parseColor("#F7E378")
    var textListener:((Int,Boolean)->Unit)?=null
    var drawListener:((Int)->Unit)?=null
    var takePhotoListener:((Int)->Unit)?=null
    var choseImageListener:((Int)->Unit)?=null
    var deleteItemListener:((CardData)->Unit)?=null

    inner class ViewHolder(val itemBinding: CardItemBinding):RecyclerView.ViewHolder(itemBinding.root),AnimateViewHolder{

        fun bind(flashCard: CardData) {

            isOpen=itemBinding.closeCard.visibility==View.VISIBLE

            Log.d("TTTT","bind isOpen $isOpen")

            itemBindingSetData(itemBinding,flashCard)

            itemBinding.plusImage.setOnClickListener{

                val menu =PopupMenu(context,it)

                menu.menuInflater.inflate(R.menu.popup_menu,menu.menu)

                menu.setOnMenuItemClickListener {
                    when(it.itemId){

                        R.id.textPop->{
                            textListener?.invoke(adapterPosition,isOpen)
                        }
                        R.id.takePhoto->{
                           takePhotoListener?.invoke(adapterPosition)
                        }
                        R.id.chooseImage->{
                            choseImageListener?.invoke(adapterPosition)
                        }
                        R.id.drawing->{
                            drawListener?.invoke(adapterPosition)
                        }
                    }
                    true
                }
                menu.show()
            }

            itemBinding.rotateImage.setOnClickListener {
               animateItem(itemBinding,flashCard)
            }

            itemBinding.closeCard.setOnClickListener {
                deleteItemListener?.invoke(flashCard)
                cardList.removeAt(adapterPosition)
                notifyItemRemoved(adapterPosition)
            }
        }

        override fun preAnimateAddImpl(holder: RecyclerView.ViewHolder?) {

            itemBinding.root.apply {
                translationY=-itemBinding.root.height*0.5f
                alpha=0f
            }
        }

        override fun preAnimateRemoveImpl(holder: RecyclerView.ViewHolder?) {
        }

        override fun animateAddImpl(holder: RecyclerView.ViewHolder?,listener: ViewPropertyAnimatorListener?) {
            ViewCompat.animate(itemView).apply {
                translationY(0f)
                alpha(1f)
                duration = 300
                setListener(listener)
            }.start()
        }

        override fun animateRemoveImpl(holder: RecyclerView.ViewHolder?,listener: ViewPropertyAnimatorListener?) {
            ViewCompat.animate(itemView).apply {
                translationY(itemView.height * 0.5f)
                alpha(0f)
                duration = 300
                setListener(listener)
            }.start()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater=LayoutInflater.from(parent.context)
        val itemBinding=CardItemBinding.inflate(inflater,parent,false)
        isOpen=true
        return ViewHolder(itemBinding)
    }

    override fun getItemCount(): Int {
        return cardList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(cardList[position])
    }

    fun addCards(currentItem:Int,cardData: CardData) {
        if (cardList.size>0){
             cardList.add(currentItem,cardData)
        }else{
            cardList.add(cardData)
        }
        notifyItemInserted(currentItem)
    }

    fun animateItem(itemBinding: CardItemBinding,flashCard: CardData) {

        if (isOpen) {

            itemBinding.root.animate().rotationY(-89f)
                .setDuration(duration)
                .setOnFinishListener {
                    itemBinding.closeCard.visibility = View.GONE

                    itemBinding.root.rotationY = -271f

                    isOpen=false
                    itemBindingSetData(itemBinding,flashCard)

                    itemBinding.root.animate().rotationY(-360f).setDuration(duration)
                        .setOnFinishListener {
                            itemBinding.root.rotationY=0f
                        }.start()
                }.start()

        } else {

            itemBinding.root.animate().rotationY(89f)
                .setDuration(duration)
                .setOnFinishListener {

                    itemBinding.closeCard.visibility = View.VISIBLE

                    itemBinding.root.rotationY = 271f

                    isOpen=true

                    itemBindingSetData(itemBinding,flashCard)

                    itemBinding.root.animate().rotationY(360f).setDuration(duration)
                        .setOnFinishListener {
                            itemBinding.root.rotationY=0f
                        }.start()
                }.start()
        }
    }

    fun itemBindingSetData(itemBinding: CardItemBinding,flashCard: CardData){

        Log.d("SSSS","itemBindingSetData  isOpen $isOpen")

        if (isOpen){

                itemBinding.flashCardText.text=flashCard.forText

            try {
                itemBinding.itemImageView.setImageURI(Uri.parse(flashCard.forImage))

            }catch (e :NullPointerException){
                itemBinding.itemImageView.setImageURI(null)
                e.printStackTrace()
            }

        }else {

            itemBinding.flashCardText.text = flashCard.backText

            try {
                itemBinding.itemImageView.setImageURI(Uri.parse(flashCard.backImage))

            }catch (e :NullPointerException){
                itemBinding.itemImageView.setImageURI(null)
                e.printStackTrace()
            }
        }

        itemBinding.cardView.setCardBackgroundColor(bgColor)
    }

    fun setItemBackround(color:Int){
         bgColor=color
         notifyDataSetChanged()
    }
}


class CardDiffuclCallback(private val oldList:List<CardData>, private val newList: List<CardData>):DiffUtil.Callback(){
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int):Boolean{
        return oldList[oldItemPosition].id==newList[newItemPosition].id
    }

    override fun getOldListSize()=oldList.size
    override fun getNewListSize()=newList.size

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition]==newList[newItemPosition]
    }

}