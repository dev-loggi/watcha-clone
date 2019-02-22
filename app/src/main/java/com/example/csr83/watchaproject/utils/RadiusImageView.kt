package com.example.csr83.watchaproject.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.widget.ImageView
import com.example.csr83.watchaproject.R

class RadiusImageView : ImageView {

    var topLeftRadius : Float = 0f
    var topRightRadius : Float = 0f
    var bottomLeftRadius : Float = 0f
    var bottomRightRadius : Float = 0f

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        topLeftRadius = context.obtainStyledAttributes(attrs, R.styleable.NewAttr)
            .getDimensionPixelSize(R.styleable.NewAttr_topLeftRadius, 0).toFloat()
        topRightRadius = context.obtainStyledAttributes(attrs, R.styleable.NewAttr)
            .getDimensionPixelSize(R.styleable.NewAttr_topRightRadius, 0).toFloat()
        bottomLeftRadius = context.obtainStyledAttributes(attrs, R.styleable.NewAttr)
            .getDimensionPixelSize(R.styleable.NewAttr_bottomLeftRadius, 0).toFloat()
        bottomRightRadius = context.obtainStyledAttributes(attrs, R.styleable.NewAttr)
            .getDimensionPixelSize(R.styleable.NewAttr_bottomRightRadius, 0).toFloat()
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {}

    override fun onDraw(canvas: Canvas) {
        val clipPath = Path()
        val rect = RectF(0f, 0f, this.width.toFloat(), this.height.toFloat())
//        clipPath.addRoundRect(rect, radius, radius, Path.Direction.CW)
        val radii = floatArrayOf(topLeftRadius, topLeftRadius, topRightRadius, topRightRadius, bottomLeftRadius, bottomLeftRadius, bottomRightRadius, bottomRightRadius)
        clipPath.addRoundRect(rect, radii, Path.Direction.CW)
        canvas.clipPath(clipPath)
        super.onDraw(canvas)
    }

    companion object {
        // 라운드처리 강도 값을 크게하면 라운드 범위가 커짐
        var radius = 18.0f
    }
}