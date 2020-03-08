package com.duyin.bookshelf.todo.widget.image

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.text.TextPaint
import android.util.AttributeSet
import com.duyin.bookshelf.R
import com.duyin.bookshelf.todo.help.ImageLoader
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.lang.Exception


class CoverImageView : androidx.appcompat.widget.AppCompatImageView {
    internal var width: Float = 0.toFloat()
    internal var height: Float = 0.toFloat()
    private var nameHeight = 0f
    private var authorHeight = 0f
    private val namePaint = TextPaint()
    private val authorPaint = TextPaint()
    private var name: String? = null
    private var author: String? = null
    private var loadFailed = false

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
            context,
            attrs,
            defStyleAttr
    )

    init {
        namePaint.typeface = Typeface.DEFAULT_BOLD
        namePaint.isAntiAlias = true
        namePaint.textAlign = Paint.Align.CENTER
        namePaint.textSkewX = -0.2f
        authorPaint.typeface = Typeface.DEFAULT
        authorPaint.isAntiAlias = true
        authorPaint.textAlign = Paint.Align.CENTER
        authorPaint.textSkewX = -0.1f
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val measuredWidth = MeasureSpec.getSize(widthMeasureSpec)
        val measuredHeight = measuredWidth * 7 / 5
        super.onMeasure(
                widthMeasureSpec,
                MeasureSpec.makeMeasureSpec(measuredHeight, MeasureSpec.EXACTLY)
        )
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        width = getWidth().toFloat()
        height = getHeight().toFloat()
        namePaint.textSize = width / 6
        namePaint.strokeWidth = namePaint.textSize / 10
        authorPaint.textSize = width / 9
        authorPaint.strokeWidth = authorPaint.textSize / 10
        nameHeight = height / 2
        authorHeight = nameHeight + authorPaint.fontSpacing
    }

    override fun onDraw(canvas: Canvas) {
        if (width >= 10 && height > 10) {
            @SuppressLint("DrawAllocation")
            val path = Path()
            //四个圆角
            path.moveTo(10f, 0f)
            path.lineTo(width - 10, 0f)
            path.quadTo(width, 0f, width, 10f)
            path.lineTo(width, height - 10)
            path.quadTo(width, height, width - 10, height)
            path.lineTo(10f, height)
            path.quadTo(0f, height, 0f, height - 10)
            path.lineTo(0f, 10f)
            path.quadTo(0f, 0f, 10f, 0f)

            canvas.clipPath(path)
        }
        super.onDraw(canvas)
        if (!loadFailed) return
        name?.let {
            namePaint.color = Color.WHITE
            namePaint.style = Paint.Style.STROKE
            canvas.drawText(it, width / 2, nameHeight, namePaint)
            namePaint.color = Color.RED
            namePaint.style = Paint.Style.FILL
            canvas.drawText(it, width / 2, nameHeight, namePaint)
        }
        author?.let {
            authorPaint.color = Color.WHITE
            authorPaint.style = Paint.Style.STROKE
            canvas.drawText(it, width / 2, authorHeight, authorPaint)
            authorPaint.color = Color.RED
            authorPaint.style = Paint.Style.FILL
            canvas.drawText(it, width / 2, authorHeight, authorPaint)
        }
    }

    fun setHeight(height: Int) {
        val width = height * 5 / 7
        minimumWidth = width
    }

    private fun setText(name: String?, author: String?) {
        this.name =
                when {
                    name == null -> null
                    name.length > 5 -> name.substring(0, 4) + "…"
                    else -> name
                }
        this.author =
                when {
                    author == null -> null
                    author.length > 8 -> author.substring(0, 7) + "…"
                    else -> author
                }
    }

    private val picassoCallback = object :Callback{
        override fun onSuccess() {
            loadFailed = false
        }

        override fun onError(e: Exception?) {
            loadFailed = true
        }
    }

    fun load(path: String?, name: String?, author: String?) {
        setText(name, author)
        ImageLoader.load(path)
                .placeholder(R.drawable.image_cover_default)
                .error(R.drawable.image_cover_default)
                .into(this,picassoCallback)
    }
}
