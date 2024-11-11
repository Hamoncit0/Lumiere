package com.example.lumiere

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import androidx.core.graphics.drawable.toBitmap
import java.io.ByteArrayOutputStream

object  ImageUtilities{
    init{

    }

    fun getByteArrayFromResourse(idImage:Int, content: Context):ByteArray{
        var bitmap = BitmapFactory.decodeResource(content.resources, idImage)
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG,80, stream)
        return stream.toByteArray()
    }

    fun getByteArrayFromBitmap(bitmap: Bitmap):ByteArray{
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG,80, stream)
        return stream.toByteArray()
    }

    fun getByteArrayFromDrawable(drawable: Drawable, content: Context):ByteArray{
        var bitMap =  drawable.toBitmap(drawable.intrinsicWidth,drawable.intrinsicHeight,null)
        val stream = ByteArrayOutputStream()
        bitMap.compress(Bitmap.CompressFormat.JPEG,80, stream)
        return stream.toByteArray()
    }

    fun getBitMapFromByteArray(data:ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(data,0,data.size)
    }
}