package com.thunder.apps.myshoppal.activities.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.thunder.apps.myshoppal.R
import com.thunder.apps.myshoppal.activities.ui.adapters.CartItemsListAdapter
import com.thunder.apps.myshoppal.activities.ui.adapters.SoldProductsListAdapter
import com.thunder.apps.myshoppal.databinding.FragmentProductsBinding
import com.thunder.apps.myshoppal.databinding.FragmentSoldProductsBinding
import com.thunder.apps.myshoppal.firestore.FireStoreClass
import com.thunder.apps.myshoppal.model.SoldProduct


/**
 * A simple [Fragment] subclass.
 * Use the [SoldProductsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SoldProductsFragment : BaseFragment() {
    private var _binding: FragmentSoldProductsBinding? = null

    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSoldProductsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onResume() {
        super.onResume()
        getSoldProductsList()
    }

    private fun getSoldProductsList(){
//        showProgressDialog(getString(R.string.please_wait))
        binding.shimmerViewContainer.startShimmerAnimation()
        FireStoreClass().getSoldProductsList(this)
    }
    fun successSoldProductsList(soldProductList : ArrayList<SoldProduct>){
//        hideProgressDialog()

        binding.shimmerViewContainer.visibility = View.GONE
        binding.shimmerViewContainer.stopShimmerAnimation()

        if (soldProductList.size > 0){
            binding.rvSoldProductItems.visibility = View.VISIBLE
            binding.tvNoSoldProductsFound.visibility = View.GONE

            binding.rvSoldProductItems.layoutManager = LinearLayoutManager(requireActivity())
            binding.rvSoldProductItems.setHasFixedSize(true)

            val adapter = SoldProductsListAdapter(requireActivity(),soldProductList)
            binding.rvSoldProductItems.adapter = adapter

        }else{
            binding.rvSoldProductItems.visibility = View.GONE
            binding.tvNoSoldProductsFound.visibility = View.VISIBLE
        }
    }

}