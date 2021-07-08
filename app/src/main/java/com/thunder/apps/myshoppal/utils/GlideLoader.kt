package com.thunder.apps.myshoppal.utils

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.thunder.apps.myshoppal.R
import java.io.IOException

class GlideLoader (val context: Context) {

    fun loadUserProfile(image : Any, imageView : ImageView){
        try {
            Glide
                .with(context)
                .load(image)
                .centerCrop()
                .placeholder(R.drawable.ic_user_placeholder)
                .into(imageView)

        }catch (e : IOException){
            e.printStackTrace()
        }
    }
    fun loadProductPicture(image : Any, imageView : ImageView){
        try {
            Glide
                .with(context)
                .load(image)
                .centerCrop()
                .into(imageView)

        }catch (e : IOException){
            e.printStackTrace()
        }
    }
}