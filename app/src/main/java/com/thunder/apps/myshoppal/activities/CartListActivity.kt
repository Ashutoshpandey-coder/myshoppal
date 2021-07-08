package com.thunder.apps.myshoppal.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.thunder.apps.myshoppal.R
import com.thunder.apps.myshoppal.activities.ui.adapters.CartItemsListAdapter
import com.thunder.apps.myshoppal.databinding.ActivityCartListBinding

import com.thunder.apps.myshoppal.firestore.FireStoreClass
import com.thunder.apps.myshoppal.model.CartItem
import com.thunder.apps.myshoppal.model.Product
import com.thunder.apps.myshoppal.utils.Constants

class CartListActivity : BaseActivity() {
    private lateinit var binding : ActivityCartListBinding
    private lateinit var mProductList : ArrayList<Product>
    private lateinit var mCartListItem : ArrayList<CartItem>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpActionBar()

        binding.btnCheckout.setOnClickListener{
            val intent = Intent(this@CartListActivity,AddressListActivity::class.java)
            intent.putExtra(Constants.EXTRA_SELECT_ADDRESS,true)
            startActivity(intent)
        }
    }
    private fun setUpActionBar(){

        setSupportActionBar(binding.toolbarCartListActivity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_button_white_24)

        binding.toolbarCartListActivity.setNavigationOnClickListener {
            onBackPressed()
        }

    }

    override fun onResume() {
        super.onResume()
//        getCartItemList() // when getting product list success then we call get cart items list
        //we need to get the product list before cart items so that we know the quantity that user can buy
        getProductList()
    }

    private fun getCartItemList(){
//        showProgressDialog(getString(R.string.please_wait))
        FireStoreClass().getCartList(this)
    }
    fun itemUpdateSuccess(){
        hideProgressDialog()
        getCartItemList()
    }
    @SuppressLint("SetTextI18n")
    fun successCartItemList(cartList : ArrayList<CartItem>){
        hideProgressDialog()

        for(product in mProductList){
            for (cartItem in cartList){
                //make sure that the product in the cart
                if (product.product_id == cartItem.product_id){
                    //we just assign the product quantity to the cart item quantity
                    cartItem.stock_quantity = product.stock_quantity
                    if (product.stock_quantity.toInt() == 0){
                     cartItem.cart_quantity = product.stock_quantity
                    }
                }
            }
        }

        mCartListItem = cartList

        if (mCartListItem.size >0){
            binding.rvCartItemsList.visibility = View.VISIBLE
            binding.llCheckout.visibility = View.VISIBLE
            binding.tvNoCartItemFound.visibility = View.GONE

            binding.rvCartItemsList.layoutManager = LinearLayoutManager(this)
            binding.rvCartItemsList.setHasFixedSize(true)
            val adapter = CartItemsListAdapter(this, mCartListItem,true)
            binding.rvCartItemsList.adapter = adapter

            var subTotal  = 0.0
            var price = 0
            for(item in mCartListItem){
                val availableQuantity = item.stock_quantity.toInt()
                if (availableQuantity > 0) {

                    price = when {
                        item.price.contains(",") -> {
                            val index = item.price.indexOf(",")
                            val s1 = item.price.substring(0,index)
                            val s2 = item.price.substring(index+1,item.price.length)

                            (s1 + s2).toInt()
                        }
                        item.price.contains(".") -> {
                            val index = item.price.indexOf(".")
                            val s1 = item.price.substring(0,index)
                            val s2 = item.price.substring(index+1,item.price.length)

                            (s1 + s2).toInt()
                        }
                        else -> {
                            item.price.toInt()
                        }
                    }
                    val quantity = item.cart_quantity.toDouble()
                    subTotal += (price * quantity)
                }
            }
            binding.tvSubTotal.text = "Rs.${subTotal}"
            //Change the logic accordingly
            binding.tvShippingCharge.text = "Rs.${100}"

            if (subTotal >0 ){
                binding.llCheckout.visibility = View.VISIBLE

                val total = subTotal + 100

                binding.tvTotalAmount.text = "Rs.${total}"
            }else{
                binding.llCheckout.visibility = View.GONE
            }
        }else{
            binding.tvNoCartItemFound.visibility = View.VISIBLE
            binding.llCheckout.visibility = View.GONE
            binding.rvCartItemsList.visibility = View.GONE
        }
    }

    fun successProductListFromFireStore(productList : ArrayList<Product>){

        mProductList = productList
        hideProgressDialog()
        //after getting product list
        getCartItemList()

    }
    private fun getProductList(){
        showProgressDialog(getString(R.string.please_wait))
        FireStoreClass().getAllProductList(this)
    }
    fun itemRemoveSuccess(){
        hideProgressDialog()
        Toast.makeText(this, getString(R.string.msg_item_removed_successfully), Toast.LENGTH_SHORT).show()

        getCartItemList()
    }

    override fun onDestroy() {
        dismissProgressDialog()
        super.onDestroy()
    }
}