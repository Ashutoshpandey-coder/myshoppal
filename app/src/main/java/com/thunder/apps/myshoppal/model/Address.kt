package com.thunder.apps.myshoppal.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Address (
    val user_id : String = "",
    val name : String = "",
    val mobileNumber : String = "",

    val address : String = "",
    val zipcode : String = "",
    val additionalNote : String = "",

    val type : String = "",
    val otherDetails : String = "",
    var id : String = "",
        ) : Parcelable