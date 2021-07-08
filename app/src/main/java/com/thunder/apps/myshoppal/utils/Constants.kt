package com.thunder.apps.myshoppal.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap

object Constants {

    /*Collections*/
    const val USERS: String = "users"
    const val PRODUCTS : String = "products"
    const val CART_ITEMS : String = "card_items"
    const val ORDERS : String = "orders"
    const val SOLD_PRODUCT :String = "sold_products"


    const val MY_SHOP_PAL_PREFERENCES: String = "myShopPalPreferences"
    const val LOGGED_IN_USERNAME: String = "logged_in_user_name"
    const val EXTRA_USER_DETAILS: String = "extra_user_details"
    const val EXTRA_ADDRESS_DETAILS: String = "extra_address_details"

    const val MALE: String = "male"
    const val FEMALE: String = "female"

    const val FIRST_NAME : String = "firstName"
    const val LAST_NAME : String = "lastName"

    const val GENDER: String = "gender"
    const val MOBILE: String = "mobile"
    const val IMAGE: String = "image"

    const val PRODUCT_IMAGE : String = "product_image"

    const val USER_ID : String = "user_id"

    const val EXTRA_PRODUCT_ID  : String = "extra_product_id"
    const val PRODUCT_ID : String = "product_id"

    const val EXTRA_PRODUCT_OWNER_ID : String = "extra_product_owner_id"
    const val DEFAULT_CARD_QUANTITY : String = "1"

    const val CART_QUANTITY : String = "cart_quantity"

    const val USER_PROFILE_IMAGE : String = "user_profile_image"
    const val PROFILE_COMPLETED : String = "profileCompleted"
    const val READ_STORAGE_PERMISSION_CODE = 2
    const val PICK_IMAGE_REQUEST_CODE = 1


    const val HOME : String = "Home"
    const val OFFICE : String = "Office"
    const val OTHER : String = "Others"

    const val ADDRESSES : String = "addresses"

    const val EXTRA_SELECT_ADDRESS : String = "extra_select_address"
    const val ADD_ADDRESS_REQUEST_CODE : Int = 121

    const val EXTRA_SELECTED_ADDRESS : String = "extra_selected_address"

    const val STOCK_QUANTITY : String = "stock_quantity"

    const val EXTRA_MY_ORDER_DETAILS : String = "extra_my_order_details"


    const val EXTRA_SOLD_PRODUCT_DETAILS : String = "extra_sold_product_details"


    fun showImageChooser(activity: Activity) {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        activity.startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE)
    }
    fun getFileExtension(activity: Activity,uri : Uri?) : String?{

        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(activity.contentResolver.getType(uri!!))

    }
}