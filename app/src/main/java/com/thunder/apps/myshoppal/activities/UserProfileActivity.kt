package com.thunder.apps.myshoppal.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.thunder.apps.myshoppal.R
import com.thunder.apps.myshoppal.databinding.ActivityUserProfileBinding
import com.thunder.apps.myshoppal.firestore.FireStoreClass
import com.thunder.apps.myshoppal.model.User
import com.thunder.apps.myshoppal.utils.Constants
import com.thunder.apps.myshoppal.utils.GlideLoader
import java.io.IOException

class UserProfileActivity : BaseActivity(), View.OnClickListener {
    private lateinit var binding: ActivityUserProfileBinding
    private lateinit var mUserDetails: User
    private var mSelectedProfileImageFileUri: Uri? = null
    private var mUserProfileImageUrl: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)


        if (intent.hasExtra(Constants.EXTRA_USER_DETAILS)) {
            mUserDetails = intent.getParcelableExtra(Constants.EXTRA_USER_DETAILS)!!

            binding.etFirstName.setText(mUserDetails.firstName)
            binding.etLastName.setText(mUserDetails.lastName)

            binding.etEmail.isEnabled = false
            binding.etEmail.setText(mUserDetails.email)

            if (mUserDetails.profileCompleted == 0) {
                binding.toolbarUserProfileActivity.title = getString(R.string.title_complete_profile)

                binding.etFirstName.isEnabled = false
                binding.etLastName.isEnabled = false

            }else{
                setUpActionBar()
                binding.toolbarUserProfileActivity.title = getString(R.string.edit_profile)

                GlideLoader(this).loadUserProfile(mUserDetails.image,binding.ivUserPhoto)

                if (mUserDetails.mobile != 0L){
                    binding.etMobileNumber.setText(mUserDetails.mobile.toString())
                }

                if (mUserDetails.gender == Constants.MALE){
                    binding.rbMale.isChecked = true
                }else{
                    binding.rbFemale.isChecked = true
                }

            }
        }
        binding.ivUserPhoto.setOnClickListener(this)
        binding.btnSubmit.setOnClickListener(this)
    }
    private fun setUpActionBar(){
        setSupportActionBar(binding.toolbarUserProfileActivity)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_button_white_24)

        binding.toolbarUserProfileActivity.setNavigationOnClickListener {
            onBackPressed()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == Constants.PICK_IMAGE_REQUEST_CODE) {
            if (data!!.data != null) {
                try {
                    mSelectedProfileImageFileUri = data.data
                    GlideLoader(this).loadUserProfile(
                        mSelectedProfileImageFileUri!!,
                        binding.ivUserPhoto
                    )

                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(
                        this,
                        getString(R.string.image_selection_failed),
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Constants.showImageChooser(this)
        } else {
            Toast.makeText(
                this,
                getString(R.string.read_storage_permission_denied),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_user_photo -> {
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    Constants.showImageChooser(this)
                } else {
                    ActivityCompat.requestPermissions(
                        this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        Constants.READ_STORAGE_PERMISSION_CODE
                    )
                }

            }
            R.id.btn_submit -> {
                if (validateUserProfileData()) {

                    showProgressDialog(getString(R.string.please_wait))

                    if (mSelectedProfileImageFileUri != null) {

                        FireStoreClass().uploadImageToCloudStorage(
                            this,
                            mSelectedProfileImageFileUri,
                            Constants.USER_PROFILE_IMAGE
                        )
                    } else {
                        updateUserProfileDetails()
                    }
                }

            }
        }
    }

    private fun updateEditedUserProfileDetails(){

        val userHashMap = HashMap<String,Any>()



        if (binding.etMobileNumber.text.toString() != mUserDetails.mobile.toString() && binding.etMobileNumber.text.toString() != "0L"){
            userHashMap[Constants.MOBILE] = binding.etMobileNumber.text.toString().toLong()
        }
        userHashMap[Constants.GENDER] = if (binding.rbMale.isChecked) Constants.MALE else Constants.FEMALE


    }

    private fun updateUserProfileDetails() {

        val userHashMap: HashMap<String, Any> = HashMap()

        if (mUserDetails.firstName != binding.etFirstName.text.toString().trim{ it<=' '}){
            userHashMap[Constants.FIRST_NAME] = binding.etFirstName.text.toString().trim{ it<= ' '}
        }
        if (mUserDetails.lastName != binding.etLastName.text.toString().trim{ it<= ' '}){
            userHashMap[Constants.LAST_NAME] = binding.etLastName.text.toString().trim{ it<= ' '}
        }

        val mobileNumber = binding.etMobileNumber.text.toString().trim { it <= ' ' }

        val gender = if (binding.rbMale.isChecked) {
            Constants.MALE
        } else {
            Constants.FEMALE
        }
        if (mUserProfileImageUrl.isNotEmpty()) {
            userHashMap[Constants.IMAGE] = mUserProfileImageUrl
        }

        if (mobileNumber.isNotEmpty() && mobileNumber != mUserDetails.mobile.toString()) {
            userHashMap[Constants.MOBILE] = mobileNumber.toLong()
        }

        if(gender.isNotEmpty() && gender != mUserDetails.gender){
            userHashMap[Constants.GENDER] = gender
        }
        userHashMap[Constants.PROFILE_COMPLETED] = 1
        userHashMap[Constants.GENDER] = gender

        FireStoreClass().updateUserProfileData(this, userHashMap)

    }

    private fun validateUserProfileData(): Boolean {
        return when {
            TextUtils.isEmpty(binding.etMobileNumber.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(getString(R.string.error_mobile_number), true)
                false
            }
            else -> {
                true
            }
        }
    }

    fun userProfileUpdateSuccess() {
        hideProgressDialog()
        Toast.makeText(this, getString(R.string.profile_update_success_message), Toast.LENGTH_SHORT)
            .show()

        startActivity(Intent(this, DashboardActivity::class.java))
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        dismissProgressDialog()
    }

    fun imageUploadSuccess(imageURL: String) {

        mUserProfileImageUrl = imageURL

        updateUserProfileDetails()
    }
}