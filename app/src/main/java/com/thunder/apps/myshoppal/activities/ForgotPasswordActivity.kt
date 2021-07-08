package com.thunder.apps.myshoppal.activities

import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.thunder.apps.myshoppal.R
import com.thunder.apps.myshoppal.databinding.ActivityForgotPasswordBinding

class ForgotPasswordActivity : BaseActivity() {
    private lateinit var binding : ActivityForgotPasswordBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpActionBar()

        binding.btnSubmit.setOnClickListener{
            val email : String = binding.etEmail.text.toString().trim{ it<= ' '}

            if (email.isNotEmpty()){
                showProgressDialog(getString(R.string.please_wait))
                FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                    .addOnCompleteListener{
                        task->
                        hideProgressDialog()
                        if (task.isSuccessful){
                            Toast.makeText(
                                this@ForgotPasswordActivity,
                                getString(R.string.email_sent_successfully),
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            finish()

                        }else{
                            showErrorSnackBar(task.exception!!.message.toString(),true)
                        }
                    }
            }else{
                showErrorSnackBar("Please enter your email address", true)
            }
        }
    }
    private fun setUpActionBar(){
        setSupportActionBar(binding.toolbarForgotPasswordActivity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_button_white_24)
        binding.toolbarForgotPasswordActivity.setNavigationOnClickListener {
            onBackPressed()
        }
    }
}