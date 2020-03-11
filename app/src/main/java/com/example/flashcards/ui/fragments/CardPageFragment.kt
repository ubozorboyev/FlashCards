package com.example.flashcards.ui.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.view.animation.OvershootInterpolator
import android.widget.Button
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
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
import com.example.flashcards.dialogs.LabelDialog
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

    private var REQUEST_CODE_DRAW = -1
    private var TAKE_PHOTO_CODE=-1
    private var CHOSE_IMAGE_CODE=-1
    private lateinit var toolbar: Toolbar
    private lateinit var adapter: CardAdapter
    private var snapView: View? = null
    private var title = ""
    private var flashCardId: Int = 0
    private var isDelete:Boolean=false
    @Inject
    lateinit var viewModel: CardPageViewModel
    private lateinit var flashCardData:FlashCardData


    override fun onAttach(context: Context) {
        super.onAttach(context)
        App.getComponent().inject(this)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        flashCardId = arguments!!.getInt("ID")
        title=arguments!!.getString("NAME","Unititel set")
        viewModel.getCardsByFlashCard(flashCardId)
        viewModel.getFlashCard(flashCardId)
        adapter = CardAdapter(this.context!!)


        viewModel.flashCard.observe(viewLifecycleOwner, Observer {
            adapter.setItemBackround(it.backgroundColor)
            flashCardData=it
        })

        setActionBar()
        clickListeners()
        init()
        adapterListener()
    }


    fun setActionBar(){
        binding.appBar.actionBarTitle.visibility=View.GONE
        binding.appBar.editTextTitle.visibility=View.VISIBLE
        binding.appBar.editTextTitle.setText(title)

        toolbar = binding.appBar as Toolbar
        (activity as AppCompatActivity).setSupportActionBar(toolbar)

        toolbar.inflateMenu(R.menu.card_page_menu)

        binding.appBar.editTextTitle.addTextChangedListener(object :TextWatcher{

            override fun afterTextChanged(s: Editable?) {
                title=if (!s.isNullOrEmpty()) s.toString() else "Untitel set"
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
        })

    }

    fun init() {

        val aplhaadapter = AlphaInAnimationAdapter(adapter)

        binding.recyclview.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL,false)

        binding.recyclview.itemAnimator = SlideInLeftAnimator(OvershootInterpolator(1f)) as RecyclerView.ItemAnimator?

        binding.recyclview.adapter = ScaleInAnimationAdapter(aplhaadapter) as RecyclerView.Adapter<*>?

        val snapHelper = LinearSnapHelper()

        snapHelper.attachToRecyclerView(binding.recyclview)

        snapView = snapHelper.findSnapView(binding.recyclview.layoutManager)

        viewModel.allCards.observe(viewLifecycleOwner, androidx.lifecycle.Observer {

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
                val dialog=EditTextDialog(context!!)
                dialog.editTextListener {
                    if (isOpen){
                        adapter.cardList[positon].forText= it
                        adapter.cardList[positon].forImage= null
                    }else{
                        adapter.cardList[positon].backImage= null
                        adapter.cardList[positon].backText= it
                    }
                    adapter.notifyItemRangeChanged(positon,1)
                }
            }
        }

        adapter.takePhotoListener=object :(Int)->Unit{
            override fun invoke(p1: Int) {
                val intent=Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                TAKE_PHOTO_CODE=p1
                startActivityForResult(intent,TAKE_PHOTO_CODE)
            }
        }

        adapter.choseImageListener=object :(Int)->Unit{

            override fun invoke(p1: Int) {

                val intent=Intent(Intent.ACTION_PICK)
                intent.type="image/*"
                CHOSE_IMAGE_CODE=p1
                startActivityForResult(intent,CHOSE_IMAGE_CODE)
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
                Toast.makeText(context,"card delete",Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun clickListeners() {

        binding.addCard.setOnClickListener(this)
        binding.playCard.setOnClickListener(this)
        binding.appBar.imageBack.setOnClickListener(this)

        requireActivity().onBackPressedDispatcher.addCallback(this){

            viewModel.updateCards(adapter.cardList)
            upateFlashCard()
            findNavController().popBackStack()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){

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
            R.id.addLabel->{

                val dialog=LabelDialog(context!!,object :CheckLabelInterface{
                    override fun setLabelCardData(flashCardData: FlashCardData) {
                        this@CardPageFragment.flashCardData.apply {
                            isDictionary=flashCardData.isDictionary
                            isImportant=flashCardData.isImportant
                            isImportant=flashCardData.isImportant
                        }
                        upateFlashCard()
                    }

                },flashCardData)

                dialog.show()

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

                 val cardData=CardData(
                     0,
                     flashCardId,
                     "Example front side of a flashcard, you can add text image or drawing",
                     null,
                     "Example back side of a flashcard, you can add text image or drawing",
                     null)

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
                upateFlashCard()
                viewModel.updateCards(adapter.cardList)
                val bunde=Bundle()
                bunde.putInt("ID",flashCardData.id)
                bunde.putString("NAME",flashCardData.name)
                bunde.putInt("COLOR",flashCardData.backgroundColor)

                findNavController().navigate(R.id.action_cardPageFragment_to_playCardFragment,bunde)
            }
            R.id.imageBack->{
                activity?.onBackPressed()
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (data != null && resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_DRAW -> {
                    val result = data.getByteArrayExtra("bitmap")

                    val bitmap = BitmapFactory.decodeByteArray(result, 0, result.size)
                    saveImage(bitmap,"draw__${System.currentTimeMillis()}",requestCode)
                 REQUEST_CODE_DRAW=-1
                }
                CHOSE_IMAGE_CODE->{

                    val bitmap=MediaStore.Images.Media.getBitmap(activity?.contentResolver,data.data)

                    saveImage(resizeBitmap(bitmap),"image__${System.currentTimeMillis()}",requestCode)

                    CHOSE_IMAGE_CODE=-1
                }
                TAKE_PHOTO_CODE->{

                    val bitmap=data.extras?.get("data") as Bitmap

                    saveImage(resizeBitmap(bitmap),"image__${System.currentTimeMillis()}",requestCode)

                    TAKE_PHOTO_CODE=-1
                }
            }
        }
    }

    fun saveImage(bitmap: Bitmap, fileName: String,position:Int) {

        val imageDir =File("${Environment.getExternalStorageDirectory()}/FlashCard/")

        val file = File(imageDir, "$fileName.jpg")
        imageDir.mkdirs()
        file.createNewFile()
        val outputStream = FileOutputStream(file)

        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        outputStream.flush()
        outputStream.close()

        if (adapter.isOpen){

            adapter.cardList[position].forImage=file.absolutePath
            adapter.cardList[position].forText=""
        } else {
            adapter.cardList[position].backImage=file.absolutePath
            adapter.cardList[position].backText=""
        }
        adapter.notifyItemChanged(position)
    }

    fun resizeBitmap(bitmap: Bitmap):Bitmap{

        var heigt=bitmap.height.toFloat()
        var width=bitmap.width.toFloat()
    /*    var process=bitmap.width.toFloat()/bitmap.height

        if (process==1f || process==0f){
            process=1.1f
        }*/


            while (heigt>1024 && width>1024){
                Log.d("tttt","heigt $heigt")
                Log.d("tttt","with $width")
                heigt *= 0.9f
                width *= 0.9f
            }

        val resize=Bitmap.createScaledBitmap(bitmap,width.toInt(),heigt.toInt(),true)

        return resize
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onDestroy()
    }

    fun upateFlashCard(){
        flashCardData.backgroundColor=adapter.bgColor
        flashCardData.cardCount=adapter.cardList.size
        flashCardData.name=title
        flashCardData.isDelete=isDelete
        viewModel.updateFlashCardById(flashCardData)
    }
}

interface CheckLabelInterface{
    fun setLabelCardData(flashCardData: FlashCardData)
}