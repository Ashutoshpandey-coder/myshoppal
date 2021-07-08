package com.thunder.apps.myshoppal.activities.ui.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.thunder.apps.myshoppal.R
import com.thunder.apps.myshoppal.activities.AddProductActivity
import com.thunder.apps.myshoppal.activities.ui.adapters.MyProductsListAdapter
//import com.thunder.apps.myshoppal.databinding.FragmentHomeBinding
import com.thunder.apps.myshoppal.databinding.FragmentProductsBinding
import com.thunder.apps.myshoppal.firestore.FireStoreClass
import com.thunder.apps.myshoppal.model.Product

class ProductsFragment : BaseFragment() {

//    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentProductsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        homeViewModel =
//            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentProductsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun deleteProduct(productID : String){
//        Toast.makeText(requireActivity(), "You can now delete the product. $productID", Toast.LENGTH_SHORT).show()

        showAlertDialogToDeleteProduct(productID)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_products,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onResume() {
        getProductListFromFirestore()
        super.onResume()
    }

    private fun getProductListFromFirestore(){

//        showProgressDialog(getString(R.string.please_wait))

        binding.shimmerViewContainer.startShimmerAnimation()

        FireStoreClass().getProductList(this)
    }
    fun successProductListFromFirestore(productList : ArrayList<Product>){

//        hideProgressDialog()
            binding.shimmerViewContainer.visibility = View.GONE
            binding.shimmerViewContainer.stopShimmerAnimation()
        if (productList.size > 0){

            binding.rvMyProductsItems.visibility = View.VISIBLE
            binding.tvNoProductsFound.visibility = View.GONE
            binding.rvMyProductsItems.layoutManager = LinearLayoutManager(activity)
            binding.rvMyProductsItems.setHasFixedSize(true)
            val adapter = MyProductsListAdapter(requireContext(),productList,this)
            binding.rvMyProductsItems.adapter = adapter
        }else{
            binding.rvMyProductsItems.visibility = View.GONE
            binding.tvNoProductsFound.visibility = View.VISIBLE
        }
    }
    fun productDeleteSuccess(){
        hideProgressDialog()

        Toast.makeText(requireActivity(), getString(R.string.product_delete_success), Toast.LENGTH_SHORT).show()

        getProductListFromFirestore()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_add->{
                startActivity(Intent(activity, AddProductActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
    private fun showAlertDialogToDeleteProduct(productID : String){
        val builder = AlertDialog.Builder(requireActivity())

        builder.setTitle(getString(R.string.delete_dialog_title))
        builder.setMessage(getString(R.string.delete_dialog_message))
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        builder.setPositiveButton(getString(R.string.yes)){
            dialogInterface,_->

            showProgressDialog(getString(R.string.please_wait))

            FireStoreClass().deleteProduct(this,productID)

            dialogInterface.dismiss()
        }
        builder.setNegativeButton(getString(R.string.no)){
            dialogInterface,_->
            dialogInterface.dismiss()
        }

        val alertDialog : AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }
}