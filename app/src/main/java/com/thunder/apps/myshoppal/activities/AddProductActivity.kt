package com.thunder.apps.myshoppal.activities

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.thunder.apps.myshoppal.R
import com.thunder.apps.myshoppal.databinding.ActivityAddProductBinding
import com.thunder.apps.myshoppal.firestore.FireStoreClass
import com.thunder.apps.myshoppal.model.Product
import com.thunder.apps.myshoppal.utils.Constants
import com.thunder.apps.myshoppal.utils.GlideLoader

class AddProductActivity : BaseActivity(), View.OnClickListener {

    private var mSelectedImageFileUri : Uri? = null
    private var mProductImageURL : String = ""
    private lateinit var binding : ActivityAddProductBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProductBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setUpActionBar()
        binding.btnSubmit.setOnClickListener(this)
        binding.ivAddUpdateProduct.setOnClickListener(this)
    }

    private fun setUpActionBar(){

        setSupportActionBar(binding.toolbarAddProductActivity)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_button_white_24)

        binding.toolbarAddProductActivity.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onClick(v: View?) {
        if (v != null){
            when(v.id){
                R.id.iv_add_update_product->{
                    if (ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                        Constants.showImageChooser(this)
                    }else{
                        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),Constants.READ_STORAGE_PERMISSION_CODE)
                    }
                }
                R.id.btn_submit->{
                    if (validateProductDetails()){
                        uploadProductImage()
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK){
            if (requestCode == Constants.PICK_IMAGE_REQUEST_CODE){
                if (data != null) {
                    binding.ivAddUpdateProduct.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_vector_edit_24))

                    mSelectedImageFileUri = data.data
                   GlideLoader(this).loadUserProfile(data.data!!,binding.ivProductImage)
                }
            }
        }else{
            Log.e("Cancelled ::", "Cancelled by user")
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE){
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Constants.showImageChooser(this)
            }else{
                Toast.makeText(this, getString(R.string.read_storage_permission_denied), Toast.LENGTH_SHORT).show()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
    private fun uploadProductImage(){
        showProgressDialog(getString(R.string.please_wait))

        FireStoreClass().uploadImageToCloudStorage(this,mSelectedImageFileUri,Constants.PRODUCT_IMAGE)
    }
    private fun validateProductDetails() : Boolean{
        return when{

            mSelectedImageFileUri == null ->{
                showErrorSnackBar(getString(R.string.error_message_product_image),true)
                false
            }
            TextUtils.isEmpty(binding.etProductTitle.text.toString().trim{ it <= ' '})->{
                showErrorSnackBar(getString(R.string.error_message_product_title),true)
                false
            }
            TextUtils.isEmpty(binding.etProductPrice.text.toString().trim{ it <= ' '})->{
                showErrorSnackBar(getString(R.string.error_message_product_price),true)
                false
            }
            TextUtils.isEmpty(binding.etProductDescription.text.toString().trim{ it <= ' '})->{
                showErrorSnackBar(getString(R.string.error_message_product_description),true)
                false
            }
            TextUtils.isEmpty(binding.etProductQuantity.text.toString().trim{ it <= ' '})->{
                showErrorSnackBar(getString(R.string.error_message_product_quantity),true)
                false
            }
            else ->{
                true
            }
        }
    }
    fun imageUploadSuccess(imageURL : String){

//        hideProgressDialog()

        mProductImageURL = imageURL
//        showErrorSnackBar("Image upload successfully $imageURL",false)

        //Upload product details to the storage

        uploadProductDetails()

    }
     fun productUploadSuccess(){
        hideProgressDialog()
         Toast.makeText(this, getString(R.string.product_upload_success_message), Toast.LENGTH_SHORT).show()

         finish()

    }

    private fun uploadProductDetails(){

        val shared = getSharedPreferences(Constants.MY_SHOP_PAL_PREFERENCES,Context.MODE_PRIVATE)
        val userName = shared.getString(Constants.LOGGED_IN_USERNAME,"")


        val productDetails = Product(
            FireStoreClass().getCurrentUserId(),
            userName!!,
            binding.etProductTitle.text.toString().trim{ it <= ' '},
            binding.etProductPrice.text.toString().trim{ it<= ' '},
            binding.etProductDescription.text.toString().trim{it <= ' '},
            binding.etProductQuantity.text.toString().trim{ it <= ' '},
            mProductImageURL
            )

        FireStoreClass().uploadProductDetails(this,productDetails)

    }

    override fun onDestroy() {
        dismissProgressDialog()
        super.onDestroy()
    }
}