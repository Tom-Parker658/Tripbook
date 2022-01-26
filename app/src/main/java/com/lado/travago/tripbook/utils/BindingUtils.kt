package com.lado.travago.tripbook.utils

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomViewTarget
import com.bumptech.glide.request.transition.Transition
import com.bumptech.glide.signature.ObjectKey
import com.google.firebase.Timestamp
import com.lado.travago.tripbook.R
import com.lado.travago.tripbook.model.enums.PlaceHolder
import com.lado.travago.tripbook.ui.notification.ImageViewerViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.text.SimpleDateFormat
import java.util.*


//@BindingAdapter("imageFromUrl")
fun ImageView.imageFromUrl(
    imageUrl: String,
    placeHolder: PlaceHolder,
    progressBar: ProgressBar?,
) {
    val target = object : CustomViewTarget<ImageView, Bitmap>(this) {
        override fun onLoadFailed(errorDrawable: Drawable?) {
            progressBar?.visibility = View.GONE
            setImageResource(R.drawable.baseline_image_error_24)
        }

        override fun onResourceReady(
            resource: Bitmap,
            transition: Transition<in Bitmap>?,
        ) {
            progressBar?.visibility = View.GONE
            setImageBitmap(resource)
            //When we have the bitmap, we want the other null and vice-versa
        }

        override fun onResourceCleared(placeholder: Drawable?) {}

        override fun onResourceLoading(placeholder: Drawable?) {
            progressBar?.visibility = View.VISIBLE
            super.onResourceLoading(placeholder)
        }

    }
    Glide.with(this)
        .asBitmap()
        .load(imageUrl)
        .timeout(1200)
        .circleCrop()
        .placeholder(placeHolder.placeholderResID())
        .into(target)
}

/**
 * Loads image from local disk
 *
 * NB: If the logoUri is null, we just load the place holder
 */
fun ImageView.imageFromUri(
    imageUri: Uri?,
    placeHolder: PlaceHolder,
) {
    if (imageUri == null) {
        Glide.with(this)
            .load(placeHolder.placeholderResID())
//            .circleCrop()
            .placeholder(placeHolder.placeholderResID())
            .into(this)
    } else
        Glide.with(this)
            .load(imageUri)
            .circleCrop()
            .placeholder(placeHolder.placeholderResID())
            .into(this)
}

/**
 * Uses [Utils.getAge] to get the age of the user from their birthday in millisecond
 */
@ExperimentalCoroutinesApi
@BindingAdapter("ageFromMillis")
fun TextView.setAgeFromMillis(birthdayInMillis: Long) {
    text = Utils.getAge(birthdayInMillis).toString()
}

/**
 * Display dates in a nice form
 */
@BindingAdapter("formatDateFromMillis")
fun TextView.formatDate(dateInMillis: Long) {
    text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).toString()
}

@BindingAdapter("addedOn")
fun TextView.addedOn(date: Timestamp) {
    text = "Added on: ${date.toDate()}"
}

@BindingAdapter("loadingObserver")
fun View.visibilityObserver(onLoading: Boolean) {
    visibility = when (onLoading) {
        true -> View.VISIBLE
        false -> View.GONE
    }
}
