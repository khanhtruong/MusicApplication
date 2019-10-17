package com.truongkhanh.musicapplication.util

import android.content.ContentResolver
import android.content.Context
import android.content.res.Resources
import android.net.Uri
import androidx.annotation.AnyRes


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