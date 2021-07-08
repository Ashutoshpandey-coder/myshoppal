package com.thunder.apps.myshoppal.activities.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.thunder.apps.myshoppal.R
import com.thunder.apps.myshoppal.activities.CartListActivity
import com.thunder.apps.myshoppal.databinding.ItemCartLayoutBinding
import com.thunder.apps.myshoppal.firestore.FireStoreClass
import com.thunder.apps.myshoppal.model.CartItem
import com.thunder.apps.myshoppal.utils.Constants
import com.thunder.apps.myshoppal.utils.GlideLoader

class CartItemsListAdapter(
    private val context: Context,
    private val list: ArrayList<CartItem>,
    private val updateCartItems : Boolean
) : RecyclerView.Adapter<CartItemsListAdapter.ViewHolder>() {
    class ViewHolder(val binding: ItemCartLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_cart_layout, parent, false)
        return ViewHolder(ItemCartLayoutBinding.bind(view))
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = list[position]

        GlideLoader(context).loadProductPicture(model.image, holder.binding.ivCartItemImage)
        holder.binding.tvCartItemTitle.text = model.title
        holder.binding.tvCartItemPrice.text = "Rs.${model.price}"
        holder.binding.tvCartQuantity.text = model.cart_quantity

        if (model.cart_quantity == "0") {
            holder.binding.ibAddCartItem.visibility = View.GONE
            holder.binding.ibRemoveCartItem.visibility = View.GONE

            if (updateCartItems){
                holder.binding.ibDeleteCartItem.visibility = View.VISIBLE
            }else{
                holder.binding.ibDeleteCartItem.visibility = View.GONE
            }

            holder.binding.tvCartQuantity.text =
                context.resources.getString(R.string.lbl_text_out_of_stock)

            holder.binding.tvCartQuantity.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.colorSnackBarError
                )
            )
        } else {

            if (updateCartItems){
                holder.binding.ibDeleteCartItem.visibility = View.VISIBLE
                holder.binding.ibAddCartItem.visibility = View.VISIBLE
                holder.binding.ibRemoveCartItem.visibility = View.VISIBLE
            }else{
                holder.binding.ibDeleteCartItem.visibility = View.GONE
                holder.binding.ibAddCartItem.visibility = View.GONE
                holder.binding.ibRemoveCartItem.visibility = View.GONE
            }


            holder.binding.tvCartQuantity.setTextColor(
                ContextCompat.getColor(context, R.color.colorSecondaryText)
            )
        }

        holder.binding.ibDeleteCartItem.setOnClickListener {
            if (context is CartListActivity) {
                context.showProgressDialog(context.getString(R.string.please_wait))
                FireStoreClass().removeItemFromCart(context, model.id)
            }
        }
        holder.binding.ibRemoveCartItem.setOnClickListener {
            if (model.cart_quantity == "1") {
                FireStoreClass().removeItemFromCart(context, model.id)
            } else {
                val cartQuantity: Int = model.cart_quantity.toInt()

                val hashMap = HashMap<String, Any>()

                hashMap[Constants.CART_QUANTITY] = (cartQuantity - 1).toString()
                if (context is CartListActivity) {
                    context.showProgressDialog(context.getString(R.string.please_wait))
                }

                FireStoreClass().updateMyCart(context, model.id, hashMap)
            }

        }
        holder.binding.ibAddCartItem.setOnClickListener {
            val cartQuantity: Int = model.cart_quantity.toInt()

            if (cartQuantity < model.stock_quantity.toInt()) {
                val hashMap = HashMap<String, Any>()

                hashMap[Constants.CART_QUANTITY] = (cartQuantity + 1).toString()
                if (context is CartListActivity) {
                    context.showProgressDialog(context.getString(R.string.please_wait))
                }

                FireStoreClass().updateMyCart(context, model.id, hashMap)
            } else {
                if (context is CartListActivity) {
                    context.showErrorSnackBar(
                        "Available stock is ${model.stock_quantity.toInt()}. You cannot add more than stock quantity.",
                        true
                    )
                }
            }
        }
//        holder.itemView.setOnClickListener{
//            if (context is CartListActivity){
//                val intent = Intent(context,ProductDetailsActivity::class.java)
//                context.startActivity(intent)
//            }
//        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}