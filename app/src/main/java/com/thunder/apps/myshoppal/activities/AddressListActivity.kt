package com.thunder.apps.myshoppal.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.thunder.apps.myshoppal.R
import com.thunder.apps.myshoppal.activities.ui.adapters.AddressListAdapter
import com.thunder.apps.myshoppal.databinding.ActivityAddressListBinding
import com.thunder.apps.myshoppal.firestore.FireStoreClass
import com.thunder.apps.myshoppal.model.Address
import com.thunder.apps.myshoppal.utils.Constants
import com.thunder.apps.myshoppal.utils.SwipeToDeleteCallback
import com.thunder.apps.myshoppal.utils.SwipeToEditCallback

class AddressListActivity : BaseActivity() {
    private lateinit var binding: ActivityAddressListBinding
    private var mSelectAddress: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddressListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpActionBar()
        @Suppress("DEPRECATION")
        binding.tvAddAddress.setOnClickListener {
            val intent = Intent(this, AddEditAddressActivity::class.java)
            startActivityForResult(intent,Constants.ADD_ADDRESS_REQUEST_CODE)
        }

        if (intent.hasExtra(Constants.EXTRA_SELECT_ADDRESS)) {
            mSelectAddress = intent.getBooleanExtra(Constants.EXTRA_SELECT_ADDRESS, false)
        }
        getAddressList()


    }
    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == Constants.ADD_ADDRESS_REQUEST_CODE){
            getAddressList()
        }
    }

    private fun setUpActionBar() {
        setSupportActionBar(binding.toolbarAddressListActivity)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_button_white_24)

        binding.toolbarAddressListActivity.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    fun setUpAddressInUI(addressList: ArrayList<Address>) {
//        hideProgressDialog()

        binding.shimmerViewContainer.visibility = View.GONE
        binding.shimmerViewContainer.stopShimmerAnimation()

        if (mSelectAddress) {
            binding.tvTitle.text = getString(R.string.select_address)
            if (addressList.size > 0) {
                binding.tvAddAddress.visibility = View.GONE
            }else{
                binding.tvAddAddress.visibility = View.VISIBLE
            }

        }

        if (addressList.size > 0) {
            binding.rvAddressList.visibility = View.VISIBLE
            binding.tvNoAddressFound.visibility = View.GONE

            binding.rvAddressList.layoutManager = LinearLayoutManager(this)
            binding.rvAddressList.setHasFixedSize(true)
            val adapter = AddressListAdapter(this, addressList,mSelectAddress)
            binding.rvAddressList.adapter = adapter

            if (!mSelectAddress) {

                val editSwipeHandler = object : SwipeToEditCallback(this) {
                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        val adapters = binding.rvAddressList.adapter as AddressListAdapter
                        adapters.notifyEditItem(
                            this@AddressListActivity,
                            viewHolder.adapterPosition
                        )
                    }
                }
                val editItemTouchHelper = ItemTouchHelper(editSwipeHandler)
                editItemTouchHelper.attachToRecyclerView(binding.rvAddressList)


                val deleteSwipeHandler = object : SwipeToDeleteCallback(this) {
                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                        showProgressDialog(getString(R.string.please_wait))
                        FireStoreClass().deleteAddress(
                            this@AddressListActivity,
                            addressList[viewHolder.adapterPosition].id
                        )

                    }

                }
                val deleteItemTouchHelper = ItemTouchHelper(deleteSwipeHandler)
                deleteItemTouchHelper.attachToRecyclerView(binding.rvAddressList)
            }
        } else {
            binding.rvAddressList.visibility = View.VISIBLE
            binding.tvNoAddressFound.visibility = View.GONE
        }
    }

   private fun getAddressList(){
//       showProgressDialog(getString(R.string.please_wait))

       binding.shimmerViewContainer.startShimmerAnimation()

       FireStoreClass().getAddressList(this)
   }

    fun deleteAddressSuccess() {
        hideProgressDialog()
        Toast.makeText(
            this@AddressListActivity,
            getString(R.string.error_message_address_deleted_successfully),
            Toast.LENGTH_SHORT
        ).show()
        getAddressList()

    }
}