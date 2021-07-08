package com.thunder.apps.myshoppal.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.thunder.apps.myshoppal.R
import com.thunder.apps.myshoppal.databinding.ActivityRegisterBinding
import com.thunder.apps.myshoppal.firestore.FireStoreClass
import com.thunder.apps.myshoppal.model.User
import com.thunder.apps.myshoppal.utils.Constants

class RegisterActivity : BaseActivity() {
    private lateinit var binding: ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpActionBar()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            @Suppress("DEPRECATION")
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        binding.tvLogin.setOnClickListener {
            onBackPressed()
        }
        binding.btnRegister.setOnClickListener {
            registerUser()
        }
    }

    private fun setUpActionBar() {
        setSupportActionBar(binding.toolbarRegisterActivity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_black_back_24)
        binding.toolbarRegisterActivity.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun registerUser() {
        if (validateRegisterDetails()) {
            showProgressDialog(getString(R.string.please_wait))

            val email: String = binding.etEmail.text.toString().trim { it <= ' ' }
            val password: String = binding.etPassword.text.toString().trim { it <= ' ' }

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val firebaseUser: FirebaseUser = task.result!!.user!!
                        val userId = firebaseUser.uid

                        val user = User(userId,
                            binding.etFirstName.text.toString().trim { it <= ' ' },
                            binding.etLastName.text.toString().trim { it <= ' ' },
                            binding.etEmail.text.toString().trim { it <= ' ' })

                        //Register the user to the FireStore firebase
                        FireStoreClass().registerUser(this, user)
                        //getting details and logged in the user
                        FireStoreClass().getUserDetails(this@RegisterActivity)
                    } else {
                        hideProgressDialog()
                        Toast.makeText(
                            this@RegisterActivity,
                            task.exception!!.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }.addOnFailureListener { exception ->
                    hideProgressDialog()
                    Toast.makeText(this@RegisterActivity, exception.message, Toast.LENGTH_SHORT)
                        .show()
                }
        }
    }

    private fun validateRegisterDetails(): Boolean {
        return when {
            TextUtils.isEmpty(binding.etFirstName.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar("Please enter first name.", true)
                false
            }
            TextUtils.isEmpty(binding.etLastName.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar("Please enter last name.", true)
                false
            }
            TextUtils.isEmpty(binding.etEmail.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar("Please enter an email id.", true)
                false
            }
            TextUtils.isEmpty(binding.etPassword.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar("Please enter a password.", true)
                false
            }
            TextUtils.isEmpty(binding.etConfirmPassword.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar("Please enter confirm password.", true)
                false
            }
            binding.etPassword.text.toString()
                .trim { it <= ' ' } != binding.etConfirmPassword.text.toString()
                .trim { it <= ' ' } -> {
                showErrorSnackBar("Password and confirm password does not match", true)
                false
            }
            !binding.cbTermsAndCondition.isChecked -> {
                showErrorSnackBar("Please agree terms and conditions.", true)
                false
            }
            else -> {
                true
            }

        }
    }

    fun userRegistrationSuccess() {
        hideProgressDialog()
        Toast.makeText(this@RegisterActivity, "You are registered successfully", Toast.LENGTH_SHORT)
            .show()
    }

    fun userLoggedInSuccess(user: User) {
        hideProgressDialog()
        val intent = Intent(this@RegisterActivity, UserProfileActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        intent.putExtra(Constants.EXTRA_USER_DETAILS, user)
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        dismissProgressDialog()
        super.onDestroy()

    }

}