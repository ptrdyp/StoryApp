package com.dicoding.storyapp.ui.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import androidx.core.content.ContextCompat
import com.dicoding.storyapp.R
import com.google.android.material.textfield.TextInputEditText

class EmailEditText : TextInputEditText {

    private var backgroundDefault: Drawable? = null
    private var backgroundError: Drawable? = null
    private var isError: Boolean = false

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs){
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        background = if (isError) backgroundError else backgroundDefault
        invalidate()
    }

    private fun init(){
        backgroundDefault = ContextCompat.getDrawable(context, R.drawable.bg_et_default)
        backgroundError = ContextCompat.getDrawable(context, R.drawable.bg_et_error)

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Do nothing
            }

            override fun onTextChanged(
                s: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ) {
                if (!Patterns.EMAIL_ADDRESS.matcher(s.toString()).matches()) {
                    error = context.getString(R.string.email_error)
                    isError = true
                } else {
                    isError = false
                }
            }

            override fun afterTextChanged(s: Editable) {
                // Do nothing
            }
        })
    }
}