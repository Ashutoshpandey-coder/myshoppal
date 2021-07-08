package com.thunder.apps.myshoppal.utils

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton

class MSPButton(context: Context, attrs : AttributeSet) : AppCompatButton(context, attrs) {

    init {
        applyFonts()
    }
    private fun applyFonts(){
        val typeFace : Typeface = Typeface.createFromAsset(context.assets,"Montserrat-Regular.ttf")
        typeface = typeFace
    }
}