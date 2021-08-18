package com.lado.travago.tripbook.utils

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.lado.travago.tripbook.R
import java.text.SimpleDateFormat
import java.util.*

///**
// * Formats the date in a nice format from milliseconds and sets as teh text of the view
// */
//@BindingAdapter("dateFormatted")
//fun TextView.setDateFormatted(dateInMillis: Long, pattern: String){
//    text = Utils.formatDate(dateInMillis, pattern)
//}

/**
 * Uses [Glide]
 * This binding adapter uses a provided url, download the image, and load it into the image view
 * on which it was called.
 * We use the [.placeHolder()] to set a pending image while the image is loading
 */
@BindingAdapter("imageFromUrl")
fun ImageView.loadImageFromUrl(imageUrl: String){
    Glide.with(this)
        .load(imageUrl)
        .timeout(1200)
        .placeholder(R.drawable.baseline_insert_photo_24)
        .into(this)
}

//@BindingAdapter("statusText")
//fun TextView.setStatusTextIfScanner(userBasicInfo: User.UserBasicInfo){
//    text = if(userBasicInfo is Scanner.ScannerBasicInfo)
//        if (userBasicInfo.isAdmin) "Admin scanner" else "scanner"
//    else
//        "Booker"
//}

/**
 * Uses [Utils.getAge] to get the age of the user from their birthday in millisecond
 */
@BindingAdapter("ageFromMillis")
fun TextView.setAgeFromMillis(birthdayInMillis: Long){
    text = Utils.getAge(birthdayInMillis).toString()
}

/**
 * Display dates in a nice form
 */
@BindingAdapter("formatDateFromMillis")
fun TextView.formatDate(dateInMillis: Long){
    //TODO:YYYY
    text = SimpleDateFormat("dd/MM", Locale.getDefault()).toString()
}
