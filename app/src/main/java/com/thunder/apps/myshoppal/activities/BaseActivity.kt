package com.thunder.apps.myshoppal.activities

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.thunder.apps.myshoppal.R

open class BaseActivity : AppCompatActivity() {
    private  var mProgressDialog : Dialog? = null
    private var doubleBackToExitPressedOnce : Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
    }

    fun showErrorSnackBar(message : String, errorMessage : Boolean){
        val snackBar = Snackbar.make(findViewById(android.R.id.content),message,Snackbar.LENGTH_LONG)

        val snackBarView = snackBar.view

        if (errorMessage){
            snackBarView.setBackgroundColor(
                ContextCompat.getColor(this@BaseActivity,R.color.colorSnackBarError)
            )
        }else{
            snackBarView.setBackgroundColor(
                ContextCompat.getColor(this@BaseActivity,R.color.colorSnackBarSuccess)
            )
        }
        snackBar.show()
    }
    fun showProgressDialog(message : String){
        mProgressDialog = Dialog(this)
        mProgressDialog!!.setContentView(R.layout.dialog_progress)
        mProgressDialog!!.findViewById<TextView>(R.id.tv_progress_text).text = message
        mProgressDialog!!.setCancelable(false)
        mProgressDialog!!.setCanceledOnTouchOutside(false)
        mProgressDialog?.show()
    }
    fun hideProgressDialog(){
        mProgressDialog!!.hide()
    }
    fun dismissProgressDialog(){
        mProgressDialog?.dismiss()
    }
    fun doubleBackToExit(){

        if (doubleBackToExitPressedOnce){
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true

        Toast.makeText(this, getString(R.string.please_press_back_again_to_exit), Toast.LENGTH_SHORT).show()

        Handler(Looper.myLooper()!!).postDelayed({
            doubleBackToExitPressedOnce = false
        },2000)

    }
}