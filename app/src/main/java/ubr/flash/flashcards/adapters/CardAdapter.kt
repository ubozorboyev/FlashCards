package ubr.flash.flashcards.adapters

import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.annotation.ColorInt
import androidx.core.view.ViewCompat
import androidx.core.view.ViewPropertyAnimatorListener
import androidx.recyclerview.widget.RecyclerView
import ubr.flash.flashcards.R
import ubr.flash.flashcards.databinding.CardItemBinding
import ubr.flash.flashcards.models.*
import ubr.flash.flashcards.util.setOnFinishListener
import jp.wasabeef.recyclerview.animators.holder.AnimateViewHolder
import java.lang.IndexOutOfBoundsException

class CardAdapter(val context: Context) :RecyclerView.Adapter<CardAdapter.ViewHolder>(){
    val cardList= arrayListOf<CardData>()
    private val duration = 100L
    var isOpen = true
    @ColorInt
    var bgColor:Int = Color.parseColor("#F7E378")
    var textListener:((Int,CardData)->Unit)? = null
    var drawListener:((Int)->Unit)? = null
    var takePhotoListener:((Int)->Unit)? = null
    var choseImageListener:((Int)->Unit)? = null
    var deleteItemListener:((CardData)->Unit)? = null

    inner class ViewHolder(private val itemBinding: CardItemBinding):RecyclerView.ViewHolder(itemBinding.root),AnimateViewHolder{

        fun bind(flashCard: CardData) {

            isOpen = itemBinding.closeCard.visibility == View.VISIBLE

            itemBindingSetData(itemBinding, flashCard)

            itemBinding.flashCardText.setOnClickListener {
                textListener?.invoke(adapterPosition, flashCard)
            }

            itemBinding.plusImage.setOnClickListener{ image ->

                val menu = PopupMenu(context,image)

                menu.menuInflater.inflate(R.menu.popup_menu,menu.menu)

                menu.setOnMenuItemClickListener {
                    when(it.itemId){

                        R.id.textPop->{
                            textListener?.invoke(adapterPosition,flashCard)
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
                translationY = -itemBinding.root.height*0.5f
                alpha = 0f
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

        try {

            if (cardList.size>0){
                cardList.add(currentItem,cardData)
            }else{
                cardList.add(cardData)
            }
        }catch (e:IndexOutOfBoundsException){
            cardList.add(cardData)
            e.printStackTrace()
        }

        notifyItemInserted(currentItem)
    }

    fun animateItem(itemBinding: CardItemBinding,flashCard: CardData) {

        if (flashCard.isOpen) {

            itemBinding.root.animate().rotationY(-89f)
                .setDuration(duration)
                .setOnFinishListener {
                    itemBinding.closeCard.visibility = View.GONE

                    itemBinding.root.rotationY = -271f

                    flashCard.isOpen = false
                    itemBindingSetData(itemBinding,flashCard)

                    itemBinding.root.animate().rotationY(-360f).setDuration(duration)
                        .setOnFinishListener {
                            itemBinding.root.rotationY = 0f
                        }.start()
                }.start()

        } else {

            itemBinding.root.animate().rotationY(89f)
                .setDuration(duration)
                .setOnFinishListener {

                    itemBinding.closeCard.visibility = View.VISIBLE

                    itemBinding.root.rotationY = 271f

                    flashCard.isOpen = true

                    itemBindingSetData(itemBinding,flashCard)

                    itemBinding.root.animate().rotationY(360f).setDuration(duration)
                        .setOnFinishListener {
                            itemBinding.root.rotationY=0f
                        }.start()
                }.start()
        }
    }

    fun itemBindingSetData(itemBinding: CardItemBinding,flashCard: CardData){

        if (flashCard.isOpen){

                itemBinding.flashCardText.text = flashCard.forText

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

    fun setItemBackground(color:Int){
         bgColor = color
         notifyDataSetChanged()
    }
}