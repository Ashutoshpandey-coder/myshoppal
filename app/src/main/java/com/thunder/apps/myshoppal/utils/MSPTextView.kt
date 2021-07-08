package com.thunder.apps.myshoppal.utils

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class MSPTextView (context : Context, attrs : AttributeSet) : AppCompatTextView(context,attrs) {

    init {
        applyFonts()
    }

    private fun applyFonts() {
        //this is use to get the file from asset folder and set it to the title
        val typeFace: Typeface = Typeface.createFromAsset(context.assets, "Montserrat-Regular.ttf")
        typeface = typeFace
    }
}