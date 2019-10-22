package com.truongkhanh.musicapplication.util

import android.content.ContentResolver
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.annotation.AnyRes
import com.truongkhanh.musicapplication.R

@Throws(Resources.NotFoundException::class)
 fun getUriToResource(context: Context,
                      @AnyRes resId:Int): Uri {
/** Return a Resources instance for your application's package.  */
    val res = context.resources
    /** return uri  */
    return Uri.parse(
        ContentResolver.SCHEME_ANDROID_RESOURCE +
    "://" + res.getResourcePackageName(resId)
    + '/'.toString() + res.getResourceTypeName(resId)
    + '/'.toString() + res.getResourceEntryName(resId)
    )
}

fun getButtonColor(enable: Boolean): Int {
    return when(enable) {
        true -> getColor(COLOR_ACCENT)
        false -> getColor(COLOR_LIGHT_GRAY)
    }
}

fun getShuffleColor(shuffleMode: Int): Int {
    return when(shuffleMode) {
        PlaybackStateCompat.SHUFFLE_MODE_ALL -> {
            getColor(COLOR_ACCENT)
        }
        else -> {
            getColor(COLOR_LIGHT_GRAY)
        }
    }
}

fun getRepeatDrawable(repeatMode: Int, context: Context): Drawable {
    return when(repeatMode) {
        PlaybackStateCompat.REPEAT_MODE_ALL -> {
            context.getDrawable(R.drawable.ic_repeat_black_24dp)!!
        }
        PlaybackStateCompat.REPEAT_MODE_ONE -> {
            context.getDrawable(R.drawable.ic_repeat_one_black_24dp)!!
        }
        else -> {
            context.getDrawable(R.drawable.ic_repeat_black_none_24dp)!!
        }
    }
}

fun getColor(colorString: String): Int {
    return Color.parseColor(colorString)
}

fun getSongBitmap(child: MediaBrowserCompat.MediaItem, context: Context): Bitmap? {
    return if (child.description.iconBitmap != null) {
        child.description.iconBitmap
    } else {
        BitmapFactory.decodeResource(context.resources, R.drawable.ic_launcher_foreground)
    }
}