package com.duyin.bookshelf.helper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.annotation.DrawableRes
import com.squareup.picasso.Picasso
import com.squareup.picasso.RequestCreator
import java.io.File

object ImageLoader {

    fun load(path: String?): RequestCreator {
        return Picasso.get().load(path)
    }

}
