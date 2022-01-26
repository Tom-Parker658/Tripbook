package com.lado.travago.tripbook.ui.notification

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.Key
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.request.target.CustomViewTarget
import com.bumptech.glide.request.transition.Transition
import com.bumptech.glide.signature.ObjectKey
import com.lado.travago.tripbook.R
import com.lado.travago.tripbook.databinding.FragmentImageViewerBinding
import com.lado.travago.tripbook.ui.agency.config_panel.creation.AgencyCreationViewModel
import com.lado.travago.tripbook.ui.notification.ImageViewerViewModel.Companion.ImageViewerViewModelFactory
import com.lado.travago.tripbook.ui.notification.ImageViewerViewModel.FieldTags
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.util.*

/**
 * @since 20-01-2022
 *
 * An image viewer + image picker
 *
 * It is launched when ever an image is clicked upon
 * It requires for parameters
 * 1- Image URL (Blank-able String):
 * 2- Image BITMAP_IMAGE (Blank-able String):
 * 3- isEditable (Non-Nullable Boolean):
 * 4- isDeletable (Non-Nullable Boolean):
 * 5- doCompression (Non-Nullable Boolean):
 *
 */
@ExperimentalCoroutinesApi
class ImageViewerFragment : Fragment() {
    private lateinit var viewModel: ImageViewerViewModel
    private lateinit var binding: FragmentImageViewerBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        initViewModel()
        binding = DataBindingUtil.inflate(layoutInflater,
            R.layout.fragment_image_viewer,
            container,
            false
        )
        clickListeners()
        observers()

        return binding.root
    }

    private fun clickListeners() {
        //Prepare the layout
        binding.btnEditPhoto.visibility = if (viewModel.isEditable) View.VISIBLE else View.GONE
        binding.btnDeletePhoto.visibility = if (viewModel.isDeletable) View.VISIBLE else View.GONE

        binding.btnEditPhoto.setOnClickListener { edit() }
        binding.btnDeletePhoto.setOnClickListener { delete() }
    }

    private fun observers() {
        viewModel.reloadImage.observe(viewLifecycleOwner) {
            if (it) loadImage()
        }
        viewModel.onLoading.observe(viewLifecycleOwner) {
            if (it) binding.progressBarImg.visibility = View.VISIBLE
            else binding.progressBarImg.visibility = View.GONE
        }
        viewModel.toastMessage.observe(viewLifecycleOwner) {
            if (it.isNotBlank()) {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                viewModel.setField(FieldTags.TOAST_MESSAGE, "")
            }
        }
    }

    private fun initViewModel() {
        if (!this::viewModel.isInitialized) {
            val args = ImageViewerFragmentArgs.fromBundle(requireArguments())
            viewModel = ViewModelProvider(
                this,
                ImageViewerViewModelFactory(
                    args.imageUrl,
                    args.imageUri,
                    args.currentImageBitmap,
                    args.placeHolder,
                    args.isEditable,
                    args.isDeletable,
                    args.doCompression
                )
            )[ImageViewerViewModel::class.java]
        }
    }

    /**
     * A pre-built contract to pick an image from the gallery!
     * If the received photoUri is not null, we convert the uri to a bitmap and set its value to that of [AgencyCreationViewModel.logoBitmap]
     * else we re-launch the event
     */
    private val pickImageLocally: ActivityResultLauncher<String> = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            //if we have and uri, we cancel all bitmap since we can access it from the uri
            viewModel.setField(FieldTags.URI, uri)
            viewModel.setField(FieldTags.DO_RELOAD_IMAGE, true)
        }
    }

    /**
     * Launches the [pickImageLocally] event with the parameter image/
     */
    private fun edit() = pickImageLocally.launch("image/*")

    /**
     * Loads the image selected as parameter
     */
    private fun loadImage() {
        when {
            //We prioritize the image gotten from the storage nad if null before we check for the url
            viewModel.imageUri != null -> {
                Glide.with(this)
                    .load(viewModel.imageUri)
                    .timeout(120_000)
                    .placeholder(viewModel.placeholder.placeholderResID())
                    .into(binding.image)
            }

            !viewModel.imageUrl.isNullOrBlank() -> {
                val target = object : CustomViewTarget<ImageView, Drawable>(binding.image) {
                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        viewModel.setField(FieldTags.ON_LOADING, false)
                        binding.image.setImageResource(R.drawable.baseline_image_error_24)
                    }

                    override fun onResourceReady(
                        resource: Drawable,
                        transition: Transition<in Drawable>?,
                    ) {
                        viewModel.setField(FieldTags.ON_LOADING, false)
                        binding.image.setImageDrawable(resource)

                        //When we have the bitmap, we want the other null and vice-versa
                        viewModel.setField(FieldTags.URI, null)
                    }

                    override fun onResourceCleared(placeholder: Drawable?) {}

                    override fun onResourceLoading(placeholder: Drawable?) {
                        viewModel.setField(FieldTags.ON_LOADING, true)
                        super.onResourceLoading(placeholder)
                    }

                }
                Glide.with(this)
                    .asDrawable()
                    .load(viewModel.imageUrl)
                    .timeout(120_000)
                    .placeholder(viewModel.placeholder.placeholderResID())
                    .into(target)
            }
            viewModel.currentImageBitmap != null -> {
                //TODO: Mange bitmap input for image viewer
            }
            else -> {
                binding.image.setImageResource(viewModel.placeholder.placeholderResID())
            }
        }

    }

    /**
     * WHen the booker presses the delete button
     */
    private fun delete() {
        viewModel.setField(FieldTags.URL, null)
        viewModel.setField(FieldTags.URI, null)
        viewModel.setField(FieldTags.DO_RELOAD_IMAGE, true)
    }

    override fun onDestroy() {
        //We send the new image uri if any selection was made
        setFragmentResult(
            "PHOTO",
            if (viewModel.imageUri == null) Bundle.EMPTY
            else Bundle().apply {
                putParcelable("PHOTO_URI", viewModel.imageUri)
            }
        )
        super.onDestroy()
    }

}