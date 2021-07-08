package com.thunder.apps.myshoppal.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.thunder.apps.myshoppal.R
import com.thunder.apps.myshoppal.databinding.ActivityLoginBinding
import com.thunder.apps.myshoppal.firestore.FireStoreClass
import com.thunder.apps.myshoppal.model.User
import com.thunder.apps.myshoppal.utils.Constants

class LoginActivity : BaseActivity() , View.OnClickListener {
    private lateinit var binding : ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        }else{
            @Suppress("DEPRECATION")
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        binding.tvRegister.setOnClickListener (this)
        binding.btnLogin.setOnClickListener(this)
        binding.tvForgotPassword.setOnClickListener(this)

    }

    private fun loginUser() {

        if (validateLoginDetails()) {

            showProgressDialog(getString(R.string.please_wait))

            val email: String = binding.etEmail.text.toString().trim { it <= ' ' }
            val password: String = binding.etPassword.text.toString().trim { it <= ' ' }

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        FireStoreClass().getUserDetails(this@LoginActivity)
                    
                    } else {
                        hideProgressDialog()
                        Toast.makeText(
                            this@LoginActivity,
                            task.exception!!.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }.addOnFailureListener { exception ->
                    hideProgressDialog()
                    Toast.makeText(this@LoginActivity, exception.message, Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun onClick(v: View?) {
        if(v != null){
            when(v.id){
                R.id.tv_forgot_password->{
                    val intent = Intent(this@LoginActivity,ForgotPasswordActivity::class.java)
                    startActivity(intent)
                }
                R.id.btn_login->{
                    loginUser()
                }
                R.id.tv_register->{
                    val intent = Intent(this@LoginActivity,RegisterActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }
    private fun validateLoginDetails() : Boolean{
        return when{
            TextUtils.isEmpty(binding.etEmail.text.toString().trim{ it <= ' '}) ->{
                showErrorSnackBar("Please enter an email id.",true)
                false
            }
            TextUtils.isEmpty(binding.etPassword.text.toString().trim{ it <= ' '}) ->{
                showErrorSnackBar("Please enter a password.",true)
                false
            }
            else->{
                true
            }

        }
    }
    fun userLoggedInSuccess(user : User){
        hideProgressDialog()

        if(user.profileCompleted == 0){
            val intent = Intent(this@LoginActivity, UserProfileActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            intent.putExtra(Constants.EXTRA_USER_DETAILS,user)
            startActivity(intent)
        }else{
            val intent = Intent(this@LoginActivity, DashboardActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
        finish()
    }
    override fun onDestroy() {
        dismissProgressDialog()
        super.onDestroy()

    }

}