package com.thunder.apps.myshoppal.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.thunder.apps.myshoppal.R
import com.thunder.apps.myshoppal.databinding.ActivityProductDetailsBinding
import com.thunder.apps.myshoppal.firestore.FireStoreClass
import com.thunder.apps.myshoppal.model.CartItem
import com.thunder.apps.myshoppal.model.Product
import com.thunder.apps.myshoppal.utils.Constants
import com.thunder.apps.myshoppal.utils.GlideLoader

class ProductDetailsActivity : BaseActivity() , View.OnClickListener {

    private var mProductID : String = ""
    private var mOwnerID : String = ""
    private lateinit var mProductDetails : Product
    private lateinit var binding : ActivityProductDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.hasExtra(Constants.EXTRA_PRODUCT_ID) && intent.hasExtra(Constants.EXTRA_PRODUCT_OWNER_ID)){
            mProductID = intent.getStringExtra(Constants.EXTRA_PRODUCT_ID)!!
            mOwnerID = intent.getStringExtra(Constants.EXTRA_PRODUCT_OWNER_ID)!!
            Log.i("Product id ::",mProductID)
        }

        if (mOwnerID == FireStoreClass().getCurrentUserId()){
            binding.btnAddToCart.visibility = View.GONE
            binding.btnGoToCart.visibility = View.GONE
        }else{
            binding.btnAddToCart.visibility = View.VISIBLE
        }

        setUpActionBar()

        getProductDetails()

        binding.btnAddToCart.setOnClickListener(this)
        binding.btnGoToCart.setOnClickListener(this)
    }
    private fun setUpActionBar(){
        setSupportActionBar(binding.toolbarProductDetailsActivity)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_button_white_24)

        binding.toolbarProductDetailsActivity.setNavigationOnClickListener {
            onBackPressed()
        }
    }
    private fun getProductDetails(){

        showProgressDialog(getString(R.string.please_wait))
        FireStoreClass().getProductDetails(this,mProductID)
    }
    @SuppressLint("SetTextI18n")
    fun productDetailsSuccess(product : Product){

        mProductDetails = product

//        hideProgressDialog()

        GlideLoader(this).loadProductPicture(product.image,binding.ivProductDetailImage)
        binding.tvProductDetailsTitle.text = product.title
        binding.tvProductDetailsPrice.text = "Rs.${product.price}"
        binding.tvProductDetailsAvailableQuantity.text = product.stock_quantity
        binding.tvProductDetailsDescription.text = product.description

        if (product.stock_quantity.toInt() == 0){
            hideProgressDialog()
            binding.btnAddToCart.visibility = View.GONE
            binding.tvProductDetailsAvailableQuantity.text = getString(R.string.lbl_text_out_of_stock)
            binding.tvProductDetailsAvailableQuantity.setTextColor(
                ContextCompat.getColor(this,R.color.colorSnackBarError)
            )
        }else{
            //check if his own product or not , No need to check everytime
            //we check only for those products that not created by user
            if (FireStoreClass().getCurrentUserId() == product.user_id){
                hideProgressDialog()
            }else{
                FireStoreClass().checkIfItemsExistsInCart(this,mProductID)
            }
        }


    }

    private fun addToCart(){
        val addToCart = CartItem(
            FireStoreClass().getCurrentUserId(),
            mOwnerID,
            mProductID,
            mProductDetails.title,
            mProductDetails.price,
            mProductDetails.image,
            Constants.DEFAULT_CARD_QUANTITY,
            mProductDetails.stock_quantity,
            )

        showProgressDialog(getString(R.string.please_wait))
        FireStoreClass().addCartItems(this,addToCart)

    }

    override fun onClick(v: View?) {
        if (v != null){
            when(v.id){
                R.id.btn_add_to_cart->{
                    addToCart()

                }
                R.id.btn_go_to_cart->{
                    startActivity(Intent(this@ProductDetailsActivity,CartListActivity::class.java))
                }
            }
        }
    }
    fun productExistsInCart(){
        hideProgressDialog()

        binding.btnAddToCart.visibility = View.GONE
        binding.btnGoToCart.visibility = View.VISIBLE
    }

    fun addToCartSuccess(){
        hideProgressDialog()
        Toast.makeText(this@ProductDetailsActivity, getString(R.string.success_message_item_added_in_cart), Toast.LENGTH_SHORT).show()


        binding.btnGoToCart.visibility = View.VISIBLE
        binding.btnAddToCart.visibility = View.GONE
    }
}