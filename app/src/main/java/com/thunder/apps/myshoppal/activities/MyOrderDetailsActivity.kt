package com.thunder.apps.myshoppal.activities

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.thunder.apps.myshoppal.R
import com.thunder.apps.myshoppal.activities.ui.adapters.CartItemsListAdapter
import com.thunder.apps.myshoppal.databinding.ActivityMyOrderDetailsBinding
import com.thunder.apps.myshoppal.model.Order
import com.thunder.apps.myshoppal.utils.Constants
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class MyOrderDetailsActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMyOrderDetailsBinding
    private var mMyOrderDetails : Order? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyOrderDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpActionBar()

        if (intent.hasExtra(Constants.EXTRA_MY_ORDER_DETAILS)){
            mMyOrderDetails = intent.getParcelableExtra(Constants.EXTRA_MY_ORDER_DETAILS)
            setUpUI(mMyOrderDetails!!)
        }
    }

    private fun setUpActionBar(){
        setSupportActionBar(binding.toolbarMyOrderDetailsActivity)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_button_white_24)

        binding.toolbarMyOrderDetailsActivity.setNavigationOnClickListener {
            onBackPressed()
        }
    }
    @SuppressLint("SetTextI18n")
    private fun setUpUI(orderDetails : Order){

        binding.tvOrderDetailsId.text = orderDetails.title

        val dateFormat = "dd MMM yyyy HH:mm"
        val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = orderDetails.order_dateTime
        val orderDateTime = formatter.format(calendar.time)

        binding.tvOrderDetailsDate.text = orderDateTime

        //for status processing placed and delivered,
        //we find the time difference btw in booking time and passed time
        val diffInMilliSeconds : Long = System.currentTimeMillis() - orderDetails.order_dateTime
        //we convert the milli to hours
        val diffInHours : Long = TimeUnit.MICROSECONDS.toHours(diffInMilliSeconds)

        when{
            //if time is less than 1 hour
            diffInHours < 1->{
                binding.tvOrderStatus.text = getString(R.string.order_status_pending)
                binding.tvOrderStatus.setTextColor(
                    ContextCompat.getColor(
                        this@MyOrderDetailsActivity,
                        R.color.colorAccent)
                )

            }
            //if time is less than 2 hour
            diffInHours < 2->{
                binding.tvOrderStatus.text = getString(R.string.order_status_in_process)
                binding.tvOrderStatus.setTextColor(
                    ContextCompat.getColor(
                        this@MyOrderDetailsActivity,
                        R.color.colorOrderStatusInProcess)
                )

            }
            //if time is greater than 2 hour
            else->{
                binding.tvOrderStatus.text = getString(R.string.order_status_delivered)
                binding.tvOrderStatus.setTextColor(
                    ContextCompat.getColor(
                        this@MyOrderDetailsActivity,
                        R.color.colorOrderStatusDelivered)
                )

            }
        }

        binding.rvMyOrderItemsList.layoutManager = LinearLayoutManager(this)
        binding.rvMyOrderItemsList.setHasFixedSize(true)

        val adapter = CartItemsListAdapter(this,orderDetails.items,false)
        binding.rvMyOrderItemsList.adapter = adapter


        binding.tvMyOrderDetailsAddressType.text = orderDetails.address.type
        binding.tvMyOrderDetailsFullName.text = orderDetails.address.name
        binding.tvMyOrderDetailsAddress.text = "${orderDetails.address.address} , ${orderDetails.address.zipcode}"
        binding.tvMyOrderDetailsAdditionalNote.text = orderDetails.address.additionalNote

        if (orderDetails.address.otherDetails.isNotEmpty()){
            binding.tvMyOrderDetailsOtherDetails.visibility = View.VISIBLE
            binding.tvMyOrderDetailsOtherDetails.text = orderDetails.address.otherDetails
        }else{
            binding.tvMyOrderDetailsOtherDetails.visibility = View.GONE
        }
        binding.tvMyOrderDetailsMobileNumber.text = orderDetails.address.mobileNumber

        binding.tvOrderDetailsSubTotal.text = orderDetails.sub_total_amount
        binding.tvOrderDetailsShippingCharge.text = orderDetails.shipping_charge
        binding.tvOrderDetailsTotalAmount.text = orderDetails.total_amount
    }
}