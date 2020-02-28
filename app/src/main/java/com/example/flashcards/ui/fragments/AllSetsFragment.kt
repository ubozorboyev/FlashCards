package com.example.flashcards.ui.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.flashcards.App
import com.example.flashcards.R
import com.example.flashcards.databinding.AllsetsFragmentBinding
import com.example.flashcards.adapters.AllSetsAdapter
import com.example.flashcards.gotoCardPage
import com.example.flashcards.models.FlashCardData
import com.example.flashcards.ui.viewmodel.AllSetsViewModel
import com.google.android.material.navigation.NavigationView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.toolbar_layout.view.*
import java.net.URL
import javax.inject.Inject

class AllSetsFragment: BaseFragment< AllsetsFragmentBinding>
    (R.layout.allsets_fragment),NavigationView.OnNavigationItemSelectedListener,View.OnClickListener{

    private val adapter= AllSetsAdapter()
    private  var flashId:Int=0
    private val bundle=Bundle()
    @Inject lateinit var viewModel: AllSetsViewModel


    override fun onAttach(context: Context) {
        super.onAttach(context)
        App.getComponent().inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        conntectionSettings()
        clickLiasteners()

         val toolbar=binding.appBar as Toolbar
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        binding.appBar.imageBack.visibility=View.GONE

        binding.appBar.actionBarTitle.setText("All Sets")

        toolbar.setNavigationIcon(R.drawable.ic_menu)
        toolbar.setNavigationOnClickListener { binding.drawerLayout.openDrawer(GravityCompat.START) }

        toolbar.inflateMenu(R.menu.rate_us)
        obseravbleAndLiveData()

    }

    fun obseravbleAndLiveData(){

        viewModel.allFlashCards.observe(this, Observer {
            adapter.list.clear()
            adapter.list.addAll(it)
            adapter.notifyDataSetChanged()
            binding.recyclview.startLayoutAnimation()
        })

        viewModel.isAddFlashCards.observe(this, Observer {
               flashId=it
        })

        viewModel.getFlashCard.observe(this, Observer {

        })
    }

    fun conntectionSettings(){

        binding.recyclview.layoutManager=LinearLayoutManager(this.context)
        binding.recyclview.adapter=adapter
        binding.stickyIndex.bindRecyclerView(binding.recyclview)
        binding.navigationView.setNavigationItemSelectedListener(this)
        binding.stickyIndex.refresh(converttoIndexList(adapter.list))
        binding.fastScroller.bindRecyclerView(binding.recyclview)

    }

    fun clickLiasteners(){

        adapter.listener=object :(FlashCardData)->Unit{

            override fun invoke(flash: FlashCardData) {
                bundle.clear()
                bundle.putInt("ID",flash.id)
                bundle.putString("NAME",flash.name)
                bundle.putInt("COLOR",flash.backgroundColor)
                gotoCardPage(bundle)
            }
        }

        binding.fabButton.setOnClickListener {
            val flashCardData=FlashCardData(0,"Unititel set",0)
             viewModel.addFlashCards(flashCardData)
                 .observeOn(AndroidSchedulers.mainThread())
                 .subscribeOn(Schedulers.io())
                 .subscribe({
                     flashId=it.toInt()
                     bundle.clear()
                     bundle.putInt("ID",flashId)
                     bundle.putInt("COLOR",flashCardData.backgroundColor)
                     gotoCardPage(bundle)

                 },{

                 })

        }
    }

    @SuppressLint("DefaultLocale")
    fun converttoIndexList(list: List<FlashCardData>)=list.map { card->card.name.toUpperCase()[0] }
        .toCollection(ArrayList())
        .toCharArray()

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.rate_us,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return true
    }

    override fun onNavigationItemSelected(p0: MenuItem): Boolean {

        when(p0.itemId){

            R.id.title1, R.id.title2->{

            }

            R.id.trash->{
                findNavController().navigate(R.id.action_allSetsFragment_to_trashFragment)
            }

            R.id.important->{
                bundle.clear()
                bundle.putInt("LABEL",1)
                findNavController().navigate(R.id.action_allSetsFragment_to_labelFragment,bundle)
            }
            R.id.todo->{
                bundle.clear()
                bundle.putInt("LABEL",2)
                findNavController().navigate(R.id.action_allSetsFragment_to_labelFragment,bundle)
            }
            R.id.dictionary->{
                bundle.clear()
                bundle.putInt("LABEL",3)
                findNavController().navigate(R.id.action_allSetsFragment_to_labelFragment,bundle)
            }
            R.id.hepl->{

            }
            R.id.about->{
//                activity?.startService(Intent("https://tuit.uz"))
            }
        }

        binding.drawerLayout.closeDrawer(GravityCompat.START)

         return  true
    }

    override fun onClick(v: View?) {
        when(v?.id){

        }
    }

}