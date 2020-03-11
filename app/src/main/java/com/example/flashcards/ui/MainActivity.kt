package com.example.flashcards.ui

import android.content.DialogInterface
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.flashcards.R
import com.google.android.material.navigation.NavigationView
import dagger.internal.InjectedFieldSignature
import java.util.jar.Manifest

class MainActivity : AppCompatActivity(){

    private val PERMISSION_CODE=100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkPermissons()
    }

    fun checkPermissons(){
        if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.CAMERA,
                        android.Manifest.permission.INTERNET
                    ), PERMISSION_CODE
                )
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.CAMERA,
                        android.Manifest.permission.INTERNET
                    ), PERMISSION_CODE
                )
            }
        }
        else{
            Toast.makeText(this,"Permissions granted!",Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        when(requestCode){
            PERMISSION_CODE->{

                if (grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this,"Permission was granted, yay! ",Toast.LENGTH_SHORT).show()
                }else{
                     showAlert()
                }
            }
        }

    }


    fun showAlert(){

        val dialog=AlertDialog.Builder(this)

        dialog.setMessage("Ruxsatlar berilmasa ilovani ishga tushirib bolmaydi. Qaytadan urinib koring!")

        dialog.setPositiveButton("OK",DialogInterface.OnClickListener { dialog, which ->
            dialog.dismiss()
            checkPermissons()
        })
        dialog.setNegativeButton("Cancel",DialogInterface.OnClickListener { dialog, which ->
            dialog.dismiss()
            finish()
        })
        dialog.create().show()
    }

}
