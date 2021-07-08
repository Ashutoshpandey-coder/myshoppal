package com.thunder.apps.myshoppal.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * A data model class for Sold Product with required fields.
 */
@kotlinx.parcelize.Parcelize
data class SoldProduct(
    val user_id: String = "",
    val title: String = "",
    val price: String = "",
    val sold_quantity: String = "",
    val image: String = "",
    val order_id: String = "",
    val order_date: Long = 0L,
    val sub_total_amount: String = "",
    val shipping_charge: String = "",
    val total_amount: String = "",
    val address: Address = Address(),
    var id: String = "",
) : Parcelable
