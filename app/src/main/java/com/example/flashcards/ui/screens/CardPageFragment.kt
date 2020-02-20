package com.example.flashcards.ui.screens

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.Button
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.divyanshu.draw.activity.DrawingActivity
import com.example.flashcards.App
import com.example.flashcards.R
import com.example.flashcards.adapters.CardAdapter
import com.example.flashcards.databinding.CardPageFragmentBinding
import com.example.flashcards.dialogs.ColorPicerDialog
import com.example.flashcards.dialogs.EditTextDialog
import com.example.flashcards.dialogs.SetTextItemDialog
import com.example.flashcards.getSnapPosition
import com.example.flashcards.models.CardData
import com.example.flashcards.models.FlashCardData
import com.example.flashcards.ui.viewmodel.CardPageViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator
import kotlinx.android.synthetic.main.toolbar_layout.view.*
import petrov.kristiyan.colorpicker.ColorPicker
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class CardPageFragment :BaseFragment<CardPageFragmentBinding>
    (R.layout.card_page_fragment),View.OnClickListener {

    private var REQUEST_CODE_DRAW = 0
    private lateinit var toolbar: Toolbar
    private lateinit var adapter: CardAdapter
    private var snapView: View? = null
    private var title = MutableLiveData<String>()
    private var flashCardId: Int = 0
    private var isDelete:Boolean=false
    @Inject
    lateinit var viewModel: CardPageViewModel


    override fun onAttach(context: Context) {
        super.onAttach(context)
        App.getComponent().inject(this)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        adapter = CardAdapter(this.context!!)

        flashCardId = arguments!!.getInt("ID")
        title.value = arguments!!.getString("NAME", "Untitle set")
        adapter.bgColor=arguments!!.getInt("COLOR")
        toolbar = binding.appBar as Toolbar

        (activity as AppCompatActivity).setSupportActionBar(toolbar)

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        toolbar.inflateMenu(R.menu.card_page_menu)

        viewModel.getCardsByFlashCard(flashCardId)

        clickListeners()
        init()
        adapterListener()
    }

    fun init() {


        val aplhaadapter = AlphaInAnimationAdapter(adapter)

        binding.recyclview.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL,false)

        binding.recyclview.itemAnimator = SlideInLeftAnimator(OvershootInterpolator(1f)) as RecyclerView.ItemAnimator?

        binding.recyclview.adapter = ScaleInAnimationAdapter(aplhaadapter) as RecyclerView.Adapter<*>?

        val snapHelper = LinearSnapHelper()

        snapHelper.attachToRecyclerView(binding.recyclview)

        snapView = snapHelper.findSnapView(binding.recyclview.layoutManager)

        viewModel.allCards.observe(this, androidx.lifecycle.Observer {

            if (adapter.cardList.isEmpty()){
                adapter.cardList.addAll(it)
                adapter.notifyDataSetChanged()
            }

            if (!it.isNullOrEmpty()){
                binding.imageView.visibility=View.GONE
            }else{
                binding.imageView.visibility=View.VISIBLE
            }

        })
    }

    fun adapterListener(){

        adapter.textListener=object :(Int,Boolean)->Unit{

            override fun invoke(positon: Int,isOpen:Boolean) {
                val dialog=SetTextItemDialog(context!!)
                dialog.show()
                dialog.setTextListener {
                    if (isOpen){
                        adapter.cardList[positon].forText= it
                        adapter.cardList[positon].forImage= null
                    }else{
                        adapter.cardList[positon].backImage= null
                        adapter.cardList[positon].backText= it
                    }
                    adapter.notifyItemChanged(positon)
                }
            }
        }

        adapter.drawListener=object :(Int)->Unit{

            override fun invoke(positon: Int) {
                val intent = Intent(activity, DrawingActivity::class.java)
                REQUEST_CODE_DRAW=positon
                startActivityForResult(intent, REQUEST_CODE_DRAW)
            }
        }

        adapter.deleteItemListener=object :(CardData)->Unit{

            override fun invoke(p1: CardData) {
                viewModel.deleteCard(p1)
            }
        }
    }

    fun clickListeners() {

        binding.addCard.setOnClickListener(this)

        binding.playCard.setOnClickListener(this)

        toolbar.setNavigationOnClickListener{
            activity?.onBackPressed()
        }

        requireActivity().onBackPressedDispatcher.addCallback(this){

            viewModel.updateCards(adapter.cardList)

            viewModel.updateFlashCardById(FlashCardData(flashCardId,title.value!!,adapter.cardList.size,adapter.bgColor,isDelete))
            findNavController().popBackStack()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){

            R.id.editTextItem->{
                val dialog=EditTextDialog(context!!,title.value!!)

                dialog.editTextListener { title.value=it }
                dialog.show()
            }
            R.id.changeColor->{
                choseColorItemColor()
            }
            R.id.trash->{
                isDelete=true
                activity?.onBackPressed()
            }
            R.id.saveBack->{
                activity?.onBackPressed()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    fun choseColorItemColor(){

        val colorPicker=ColorPicker(activity)
        val button=Button(context)
        button.background= null

        val buttonCancel=Button(context)
        buttonCancel.background= null

        val filterButton=Button(context)
        filterButton.background= null

        colorPicker
            .setColors(R.array.colorsList)
            .addListenerButton("cancel",buttonCancel,object :ColorPicker.OnButtonListener{
                override fun onClick(v: View?, position: Int, color: Int) {
                    colorPicker.dismissDialog()
                }
            })
            .addListenerButton("filter",filterButton,object :ColorPicker.OnButtonListener{
                override fun onClick(v: View?, position: Int, color: Int) {
                    colorPicker.dismissDialog()
                    val dialog=ColorPicerDialog(context!!)
                    dialog.setColorListener {
                        adapter.setItemBackround(it)
                    }
                    dialog.show()
                }
            })
            .addListenerButton("ok",button,object :ColorPicker.OnButtonListener{

                override fun onClick(v: View?, position: Int, color: Int) {

                    if (color!=0){
                        adapter.setItemBackround(color)
                    }
                 colorPicker.dismissDialog()
            }
          }).disableDefaultButtons(true)
            .setColumns(4)
            .setColorButtonSize(60,60)
            .setRoundColorButton(true)
            .setDismissOnButtonListenerClick(true)
            .show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.card_page_menu,menu)
    }

    override fun onClick(v: View?) {

        when (v?.id) {

            R.id.addCard -> {

                val snapPosition = LinearSnapHelper().getSnapPosition(binding.recyclview)

                 val cardData=CardData(0,flashCardId,"Foreground Text",null,"Backgrount Text", null)

                    viewModel.inserCard(cardData).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            cardData.id=it.toInt()
                            adapter.addCards(snapPosition,cardData)
                            if (snapPosition >= 0) binding.recyclview.scrollToPosition(snapPosition)
                        },{

                        })
            }

            R.id.playCard -> {

            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (data != null && resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_DRAW -> {
                    val result = data.getByteArrayExtra("bitmap")

                    val bitmap = BitmapFactory.decodeByteArray(result, 0, result.size)
                    saveImage(bitmap,"draw__${System.currentTimeMillis()}")
                }
            }
        }
    }

    fun saveImage(bitmap: Bitmap, fileName: String) {

        val imageDir = "${Environment.DIRECTORY_PICTURES}/FlashCard/"
        val path = Environment.getExternalStoragePublicDirectory(imageDir)

        val file = File(path, "$fileName.png")
        path.mkdirs()
        file.createNewFile()
        val outputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        outputStream.flush()
        outputStream.close()

        if (adapter.isOpen){

            adapter.cardList[REQUEST_CODE_DRAW].forImage=file.absolutePath
            adapter.cardList[REQUEST_CODE_DRAW].forText=""
        } else {

            adapter.cardList[REQUEST_CODE_DRAW].backImage=file.absolutePath
            adapter.cardList[REQUEST_CODE_DRAW].backText=""
        }

        adapter.notifyItemChanged(REQUEST_CODE_DRAW)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onDestroy()
    }

}