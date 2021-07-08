package com.thunder.apps.myshoppal.activities.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.thunder.apps.myshoppal.R
import com.thunder.apps.myshoppal.activities.ui.adapters.MyOrdersListAdapter
import com.thunder.apps.myshoppal.databinding.FragmentOrdersBinding
import com.thunder.apps.myshoppal.firestore.FireStoreClass
import com.thunder.apps.myshoppal.model.Order


class OrdersFragment : BaseFragment() {

//    private lateinit var notificationsViewModel: NotificationsViewModel
    private var _binding: FragmentOrdersBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        notificationsViewModel =
//            ViewModelProvider(this).get(NotificationsViewModel::class.java)

        _binding = FragmentOrdersBinding.inflate(inflater, container, false)
        val root: View = binding.root


        return root
    }

    fun populateOrdersListInUI(orderList : ArrayList<Order>){
//        hideProgressDialog()

        binding.shimmerViewContainer.visibility = View.GONE
        binding.shimmerViewContainer.stopShimmerAnimation()
        if (orderList.size >0){
            binding.rvMyOrderItems.visibility = View.VISIBLE
            binding.tvNoOrdersFound.visibility = View.GONE

            binding.rvMyOrderItems.layoutManager = LinearLayoutManager(activity)
            binding.rvMyOrderItems.setHasFixedSize(true)
            val adapter = MyOrdersListAdapter(requireActivity(),orderList)
            binding.rvMyOrderItems.adapter = adapter
        }else{
            binding.rvMyOrderItems.visibility = View.GONE
            binding.tvNoOrdersFound.visibility = View.VISIBLE
        }

    }
    private fun getMyOrdersList(){
//        showProgressDialog(getString(R.string.please_wait))

        binding.shimmerViewContainer.startShimmerAnimation()
        FireStoreClass().getMyOrderList(this)
    }

    override fun onResume() {
        getMyOrdersList()
        super.onResume()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}