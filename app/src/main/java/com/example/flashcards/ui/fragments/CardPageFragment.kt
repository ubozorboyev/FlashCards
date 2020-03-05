package com.example.flashcards.ui.fragments

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ThumbnailUtils
import android.os.Bundle
import android.os.Environment
import android.os.FileUtils
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Size
import android.view.*
import android.view.animation.OvershootInterpolator
import android.widget.Button
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.room.util.FileUtil
import com.divyanshu.draw.activity.DrawingActivity
import com.example.flashcards.App
import com.example.flashcards.R
import com.example.flashcards.adapters.CardAdapter
import com.example.flashcards.adapters.CardDiffuclCallback
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
    private var title = MutableLiveData<String>()
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

        adapter = CardAdapter(this.context!!)

        flashCardId = arguments!!.getInt("ID")
        title.value = arguments!!.getString("NAME", "Untitle set")
        adapter.bgColor=arguments!!.getInt("COLOR")


        viewModel.getCardsByFlashCard(flashCardId)
        viewModel.getFlashCard(flashCardId)

        viewModel.flashCard.observe(viewLifecycleOwner, Observer {
            flashCardData=it
        })

        setActionBar()
        clickListeners()
        init()
        adapterListener()
    }


    fun setActionBar(){
        toolbar = binding.appBar as Toolbar
        (activity as AppCompatActivity).setSupportActionBar(toolbar)

        toolbar.inflateMenu(R.menu.card_page_menu)

        title.observe(viewLifecycleOwner, Observer {
            binding.appBar.actionBarTitle.setText(it)
        })

        binding.appBar.actionBarTitle.visibility=View.GONE
        binding.appBar.editTextTitle.visibility=View.VISIBLE
        binding.appBar.editTextTitle.setText(title.value)

        binding.appBar.editTextTitle.addTextChangedListener(object :TextWatcher{

            override fun afterTextChanged(s: Editable?) {
                if (!s.isNullOrEmpty()){
                    title.value=s.toString()
                }else{
                    title.value="Unititel set"
                }

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                Log.d("TTTT","beforeTextChanged ${s.toString()}")
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Log.d("TTTT","onTextChanged ${s.toString()}")
            }
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
            }
        }
    }

    fun clickListeners() {

        binding.addCard.setOnClickListener(this)
        binding.playCard.setOnClickListener(this)
        binding.appBar.imageBack.setOnClickListener(this)
        binding.appBar.editTextTitle.setOnClickListener(this)


        requireActivity().onBackPressedDispatcher.addCallback(this){

            viewModel.updateCards(adapter.cardList)

            flashCardData.backgroundColor=adapter.bgColor
            flashCardData.cardCount=adapter.cardList.size
            flashCardData.name=title.value!!
            flashCardData.isDelete=isDelete
            viewModel.updateFlashCardById(flashCardData)
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
                        viewModel.updateFlashCardById(flashCardData)
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

            }
            R.id.imageBack->{
                activity?.onBackPressed()
            }
            R.id.editTextTitle->{

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

                    saveImage(bitmap,"image__${System.currentTimeMillis()}",requestCode)

                    CHOSE_IMAGE_CODE=-1
                }
                TAKE_PHOTO_CODE->{

                    val bitmap=data.extras?.get("data") as Bitmap

//                    val thumbnailUtils=ThumbnailUtils.createImageThumbnail(File(data.data!!.path!!),Size(512,384),null)
                    saveImage(bitmap,"image__${System.currentTimeMillis()}",requestCode)

                    TAKE_PHOTO_CODE=-1
                }
            }
        }
    }

    fun saveImage(bitmap: Bitmap, fileName: String,position:Int) {

        val imageDir = "${Environment.DIRECTORY_PICTURES}/FlashCard/"
        val path = Environment.getExternalStoragePublicDirectory(imageDir)

        val file = File(path, "$fileName.png")
        path.mkdirs()
        file.createNewFile()
        val outputStream = FileOutputStream(file)

        val resize=Bitmap.createScaledBitmap(bitmap,512,384,true)

        resize.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
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

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onDestroy()
    }

}

interface CheckLabelInterface{
    fun setLabelCardData(flashCardData: FlashCardData)
}