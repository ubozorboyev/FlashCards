package ubr.flash.flashcards.ui.fragments

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
import android.view.*
import android.view.animation.OvershootInterpolator
import android.widget.Button
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.divyanshu.draw.activity.DrawingActivity
import ubr.flash.flashcards.App
import ubr.flash.flashcards.R
import ubr.flash.flashcards.adapters.CardAdapter
import ubr.flash.flashcards.databinding.CardPageFragmentBinding
import ubr.flash.flashcards.dialogs.ColorPicerDialog
import ubr.flash.flashcards.dialogs.EditTextDialog
import ubr.flash.flashcards.dialogs.LabelDialog
import ubr.flash.flashcards.util.getSnapPosition
import ubr.flash.flashcards.models.CardData
import ubr.flash.flashcards.models.FlashCardData
import ubr.flash.flashcards.ui.viewmodel.CardPageViewModel
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
    private var TAKE_PHOTO_CODE = -1
    private var CHOSE_IMAGE_CODE = -1
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

        flashCardId = requireArguments().getInt("ID")
        title=requireArguments().getString("NAME","Unititel set")
        viewModel.getCardsByFlashCard(flashCardId)
        viewModel.getFlashCard(flashCardId)
        adapter = CardAdapter(this.requireContext())


        viewModel.flashCard.observe(viewLifecycleOwner, Observer {
            adapter.setItemBackground(it.backgroundColor)
            flashCardData = it

            binding.appBar.starImage.setImageResource(if (it.isSelected) R.drawable.ic_star else R.drawable.ic_baseline_star_border_24)
        })

        setActionBar()
        clickListeners()
        init()
        adapterListener()
    }


    private fun setActionBar(){

        binding.appBar.actionBarTitle.visibility=View.GONE
        binding.appBar.editTextTitle.visibility=View.VISIBLE
        binding.appBar.editTextTitle.setText(title)
        binding.appBar.starImage.setOnClickListener(this)
        binding.appBar.starImage.visibility = View.VISIBLE

        toolbar = binding.appBar as Toolbar
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        (activity as AppCompatActivity).supportActionBar?.title = ""
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

        val alphaAdapter = AlphaInAnimationAdapter(adapter)

        binding.recyclview.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL,false)

        binding.recyclview.itemAnimator = SlideInLeftAnimator(OvershootInterpolator(1f)) as RecyclerView.ItemAnimator?

        binding.recyclview.adapter = ScaleInAnimationAdapter(alphaAdapter) as RecyclerView.Adapter<*>?

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

    private fun adapterListener(){

        adapter.textListener = object :(Int,CardData)->Unit{

            override fun invoke(positon: Int,cardData: CardData) {
                val text = if (cardData.isOpen) cardData.forText else cardData.backText
                val dialog=EditTextDialog(context!!,text)

                dialog.editTextListener {
                    if (cardData.isOpen){
                        adapter.cardList[positon].forText = it
                        adapter.cardList[positon].forImage = null
                    }else{
                        adapter.cardList[positon].backImage = null
                        adapter.cardList[positon].backText = it
                    }
                    adapter.notifyItemRangeChanged(positon,1)
                }
            }
        }

        adapter.takePhotoListener = object :(Int)->Unit{
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
            updateFlashCard()
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

                val dialog=LabelDialog(requireContext(),object :CheckLabelInterface{
                    override fun setLabelCardData(flashCardData: FlashCardData) {
                        this@CardPageFragment.flashCardData.apply {
                            isDictionary=flashCardData.isDictionary
                            isImportant=flashCardData.isImportant
                            isImportant=flashCardData.isImportant
                        }
                        updateFlashCard()
                    }

                },flashCardData)

                dialog.show()

            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun choseColorItemColor(){

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
                        adapter.setItemBackground(it)
                    }
                    dialog.show()
                }
            })
            .addListenerButton("ok",button,object :ColorPicker.OnButtonListener{

                override fun onClick(v: View?, position: Int, color: Int) {

                    if (color!=0){
                        adapter.setItemBackground(color)
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
                            cardData.id = it.toInt()
                            adapter.addCards(snapPosition,cardData)
                            if (snapPosition >= 0) binding.recyclview.scrollToPosition(snapPosition)
                        },{

                        })
            }

            R.id.playCard -> {
                updateFlashCard()
                viewModel.updateCards(adapter.cardList)
                val bundle = Bundle()
                bundle.putInt("ID",flashCardData.id)
                bundle.putString("NAME",flashCardData.name)
                bundle.putInt("COLOR",flashCardData.backgroundColor)

                findNavController().navigate(R.id.action_cardPageFragment_to_playCardFragment,bundle)
            }
            R.id.imageBack->{
                activity?.onBackPressed()
            }
            R.id.starImage -> {
                if (flashCardData.isSelected) {
                    flashCardData.isSelected = false
                    binding.appBar.starImage.setImageResource(R.drawable.ic_baseline_star_border_24)
                }else{
                    flashCardData.isSelected = true
                    binding.appBar.starImage.setImageResource(R.drawable.ic_star)
                }
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (data != null && resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_DRAW -> {

                    val result = data.getByteArrayExtra("bitmap")

                    val bitmap = BitmapFactory.decodeByteArray(result, 0, result!!.size)
                    saveImage(bitmap, "draw__${System.currentTimeMillis()}", requestCode)
                    REQUEST_CODE_DRAW = -1
                }
                CHOSE_IMAGE_CODE->{

                    val bitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver,data.data)

                    saveImage(resizeBitmap(bitmap),"image__${System.currentTimeMillis()}",requestCode)

                    CHOSE_IMAGE_CODE=-1
                }
                TAKE_PHOTO_CODE->{

                    val bitmap = data.extras?.get("data") as Bitmap

                    saveImage(resizeBitmap(bitmap),"image__${System.currentTimeMillis()}",requestCode)

                    TAKE_PHOTO_CODE=-1
                }
            }
        }
    }

    private fun saveImage(bitmap: Bitmap, fileName: String, position:Int) {

        val imageDir =File("${Environment.getExternalStorageDirectory()}/FlashCard/")

        val file = File(imageDir, "$fileName.jpg")
        imageDir.mkdirs()
        file.createNewFile()
        val outputStream = FileOutputStream(file)

        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        outputStream.flush()
        outputStream.close()

        if (adapter.isOpen){

            adapter.cardList[position].forImage = file.absolutePath
            adapter.cardList[position].forText = ""
        } else {
            adapter.cardList[position].backImage = file.absolutePath
            adapter.cardList[position].backText = ""
        }
        adapter.notifyItemChanged(position)
    }

    private fun resizeBitmap(bitmap: Bitmap):Bitmap{

        var heigt = bitmap.height.toFloat()
        var width = bitmap.width.toFloat()
        while (heigt>1024 && width>1024){
                heigt *= 0.9f
                width *= 0.9f
            }

        return Bitmap.createScaledBitmap(bitmap,width.toInt(),heigt.toInt(),true)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onDestroy()
    }

    fun updateFlashCard(){
        flashCardData.backgroundColor = adapter.bgColor
        flashCardData.cardCount = adapter.cardList.size
        flashCardData.name = title
        flashCardData.isDelete = isDelete
        viewModel.updateFlashCardById(flashCardData)
    }
}

interface CheckLabelInterface{
    fun setLabelCardData(flashCardData: FlashCardData)
}