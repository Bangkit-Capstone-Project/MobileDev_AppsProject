package com.example.tanamin.ui.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.example.tanamin.R

class dark_green_button: AppCompatButton {
    private lateinit var enabledBackground: Drawable

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        background =  enabledBackground
        textSize = 15f
    }

    private fun init() {
        enabledBackground = ContextCompat.getDrawable(context, R.drawable.round_button_green) as Drawable

    }
}