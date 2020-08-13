package ubr.flash.flashcards.ui.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import ubr.flash.flashcards.R
import ubr.flash.flashcards.databinding.BackupFragmentBinding
import kotlinx.android.synthetic.main.toolbar_layout.view.*

class BackupFragment :BaseFragment<BackupFragmentBinding>(R.layout.backup_fragment){


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.appBar.actionBarTitle.setText("Backup and restore")
        binding.appBar.imageBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.signUp.setOnClickListener {
            val intent=Intent(Intent.ACTION_VIEW)
            intent.setData(Uri.parse("https://www.dropbox.com/"))
            startActivity(intent)

        }

    }



}