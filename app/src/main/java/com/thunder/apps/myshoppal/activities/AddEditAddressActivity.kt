package com.thunder.apps.myshoppal.activities

import android.app.Activity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.thunder.apps.myshoppal.R
import com.thunder.apps.myshoppal.databinding.ActivityAddEditAddressBinding

import com.thunder.apps.myshoppal.firestore.FireStoreClass
import com.thunder.apps.myshoppal.model.Address
import com.thunder.apps.myshoppal.utils.Constants

class AddEditAddressActivity : BaseActivity() {
    private lateinit var binding : ActivityAddEditAddressBinding
    private var mAddressDetails : Address? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpActionBar()

        if (intent.hasExtra(Constants.EXTRA_ADDRESS_DETAILS)){
            mAddressDetails = intent.getParcelableExtra(Constants.EXTRA_ADDRESS_DETAILS)!!
        }
        if(mAddressDetails != null){
            if (mAddressDetails!!.id.isNotEmpty()){
                binding.tvTitle.text = getString(R.string.title_edit_address)
                binding.btnSubmitAddress.text = getString(R.string.btn_lbl_update)

                binding.etFullName.setText(mAddressDetails?.name)
                binding.etPhoneNumber.setText(mAddressDetails?.mobileNumber)
                binding.etZipCode.setText(mAddressDetails?.zipcode)
                binding.etAdditionalNote.setText(mAddressDetails?.additionalNote)
                binding.etAddress.setText(mAddressDetails?.address)

                when(mAddressDetails?.type){
                    Constants.HOME->{
                        binding.rbHome.isChecked = true
                    }
                    Constants.OFFICE ->{
                        binding.rbOffice.isChecked = true
                    }else->{
                        binding.rbOther.isChecked = true
                        binding.tilOtherDetails.visibility = View.VISIBLE
                        binding.etOtherDetails.setText(mAddressDetails?.otherDetails)
                    }
                }
            }
        }

        binding.btnSubmitAddress.setOnClickListener{
            saveAddressToFireStore()
        }

        binding.rgType.setOnCheckedChangeListener{
            _,checkedId ->
            if (checkedId ==  R.id.rb_other){
                binding.tilOtherDetails.visibility = View.VISIBLE
            }else{
                binding.tilOtherDetails.visibility = View.GONE
            }
        }
    }
    private fun setUpActionBar(){
        setSupportActionBar(binding.toolbarAddEditAddressActivity)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_button_white_24)

        binding.toolbarAddEditAddressActivity.setNavigationOnClickListener {
            onBackPressed()
        }
    }
    private fun validateData() : Boolean{

        return when{
            TextUtils.isEmpty(binding.etFullName.text.toString().trim{ it <= ' '})->{
                showErrorSnackBar("Please enter your full name.",true)
                false
            }
            TextUtils.isEmpty(binding.etPhoneNumber.text.toString().trim{ it <= ' '})->{
                showErrorSnackBar("Please enter your phone number.",true)
                false
            }
            TextUtils.isEmpty(binding.etAddress.text.toString().trim{ it <= ' '})->{
                showErrorSnackBar("Please enter your Address.",true)
                false
            }
            TextUtils.isEmpty(binding.etZipCode.text.toString().trim{ it <= ' '})->{
                showErrorSnackBar("Please enter area Zipcode.",true)
                false
            }
            binding.rbOther.isChecked && TextUtils.isEmpty(
                binding.etZipCode.text.toString().trim{ it <= ' '}
            ) -> {
                showErrorSnackBar("Please enter other details.",true)
                false
            }
            else->{
                true
            }
        }

    }
     private fun saveAddressToFireStore(){
        val fullName : String = binding.etFullName.text.toString().trim{ it <= ' '}
        val phoneNumber : String = binding.etPhoneNumber.text.toString().trim{ it <= ' '}
        val address : String = binding.etAddress.text.toString().trim{ it <= ' '}
        val zipCode : String = binding.etZipCode.text.toString().trim{ it <= ' '}
        val additionalNote : String = binding.etAdditionalNote.text.toString().trim{ it <= ' '}
        val otherDetails : String = binding.etOtherDetails.text.toString().trim{ it <= ' '}

        if (validateData()){

            showProgressDialog(getString(R.string.please_wait))

            val addressType : String = when{
                binding.rbHome.isChecked-> Constants.HOME
                binding.rbOffice.isChecked-> Constants.OFFICE
                else-> Constants.OTHER
            }

            val addressModel = Address(
                FireStoreClass().getCurrentUserId(),
                fullName,
                phoneNumber,
                address,
                zipCode,
                additionalNote,
                addressType,
                otherDetails
            )

            if(mAddressDetails != null && mAddressDetails!!.id.isNotEmpty()){
                FireStoreClass().updateAddressSuccess(this,addressModel,mAddressDetails!!.id)
            }else {

                FireStoreClass().addAddress(this, addressModel)
            }
        }
    }

    fun addUpdateAddressSuccess(){
        hideProgressDialog()
        if(mAddressDetails != null && mAddressDetails!!.id.isNotEmpty()){
            Toast.makeText(this, getString(R.string.msg_your_address_updated_successfully), Toast.LENGTH_SHORT).show()
        }else {
            Toast.makeText(this, getString(R.string.address_added_success_msg), Toast.LENGTH_SHORT)
                .show()
        }
        setResult(Activity.RESULT_OK)
        finish()
    }
}