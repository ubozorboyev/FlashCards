package ubr.flash.flashcards.ui.fragments

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ubr.flash.flashcards.App
import ubr.flash.flashcards.R
import ubr.flash.flashcards.databinding.AllsetsFragmentBinding
import ubr.flash.flashcards.adapters.AllSetsAdapter
import ubr.flash.flashcards.models.FlashCardData
import ubr.flash.flashcards.ui.SplashActivity
import ubr.flash.flashcards.ui.viewmodel.AllSetsViewModel
import com.google.android.material.navigation.NavigationView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.toolbar_layout.view.*
import ubr.flash.flashcards.util.gotoCardPage
import javax.inject.Inject

class AllSetsFragment: BaseFragment<AllsetsFragmentBinding>
    (R.layout.allsets_fragment),NavigationView.OnNavigationItemSelectedListener{

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

        viewModel.allFlashCards.observe(viewLifecycleOwner, Observer {
            adapter.setData(it as List<FlashCardData>)
            binding.recyclview.startLayoutAnimation()
        })

        viewModel.isAddFlashCards.observe(viewLifecycleOwner, Observer {
               flashId=it
        })

        viewModel.getFlashCard.observe(viewLifecycleOwner, Observer {

        })
    }

    fun conntectionSettings(){

        binding.recyclview.layoutManager=GridLayoutManager(this.context,2,RecyclerView.VERTICAL,false)
        binding.recyclview.adapter=adapter
        binding.navigationView.setNavigationItemSelectedListener(this)

    }

    fun clickLiasteners(){

        adapter.listener=object :(FlashCardData)->Unit{

            override fun invoke(flash: FlashCardData) {
                bundle.clear()
                bundle.putInt("ID",flash.id)
                bundle.putString("NAME",flash.name)
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
                     bundle.putString("NAME",flashCardData.name)
                     gotoCardPage(bundle)

                 },{

                 })

        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.rate_us,menu)

        val searchManager=activity?.getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView=menu.findItem(R.id.search).actionView as SearchView
           searchView.setSearchableInfo(searchManager.getSearchableInfo(activity?.componentName))
           searchView.maxWidth=Int.MAX_VALUE
           searchView.queryHint="Search"

        searchView.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                adapter.filter.filter(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return false
            }

        })

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.rateUs->{}
            R.id.search->{
            }
        }

        return true
    }

    override fun onNavigationItemSelected(p0: MenuItem): Boolean {

        when(p0.itemId){

            R.id.allSets->{
              binding.recyclview.startLayoutAnimation()
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
                  val intent=Intent(context,SplashActivity::class.java)
                   intent.putExtra("ISAVIABLE",true)
                   startActivity(intent)
                   activity?.finish()
            }
            R.id.about->{
                val intent=Intent(Intent.ACTION_VIEW)
                    intent.setData(Uri.parse("https://tuit.uz"))
                    startActivity(intent)
            }
            R.id.bakupRestore->{
                findNavController().navigate(R.id.action_allSetsFragment_to_backupFragment)
            }
            R.id.mostUsed->{
                findNavController().navigate(R.id.action_allSetsFragment_to_starsFragment)
            }
        }

          binding.drawerLayout.closeDrawer(GravityCompat.START)

         return  true

    }

}
