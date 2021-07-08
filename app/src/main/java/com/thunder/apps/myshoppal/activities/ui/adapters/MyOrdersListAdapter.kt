package com.thunder.apps.myshoppal.activities.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.thunder.apps.myshoppal.R
import com.thunder.apps.myshoppal.activities.MyOrderDetailsActivity
import com.thunder.apps.myshoppal.databinding.ItemListLayoutBinding
import com.thunder.apps.myshoppal.model.Order
import com.thunder.apps.myshoppal.utils.Constants
import com.thunder.apps.myshoppal.utils.GlideLoader

open class MyOrdersListAdapter(private val context: Context,
                               private val list : ArrayList<Order> ) : RecyclerView.Adapter<MyOrdersListAdapter.ViewHolder>() {
    class ViewHolder (val binding : ItemListLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_list_layout,parent,false)
        return ViewHolder(ItemListLayoutBinding.bind(view))
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = list[position]

        GlideLoader(context).loadProductPicture(model.image,holder.binding.ivItemImage)

        holder.binding.tvItemName.text = model.title
        holder.binding.tvItemPrice.text = "Rs.${model.total_amount}"

        holder.binding.ibDeleteProduct.visibility = View.GONE

        holder.itemView.setOnClickListener{
            val intent = Intent(context,MyOrderDetailsActivity::class.java)
            intent.putExtra(Constants.EXTRA_MY_ORDER_DETAILS,model)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}