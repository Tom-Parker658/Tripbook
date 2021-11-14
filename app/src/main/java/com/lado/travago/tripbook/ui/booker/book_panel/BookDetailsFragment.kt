package com.lado.travago.tripbook.ui.booker.book_panel

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.lado.travago.tripbook.R
import com.lado.travago.tripbook.databinding.FragmentBookDetailsBinding
import com.lado.travago.tripbook.ui.booker.book_panel.viewmodel.MyBooksViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi


@ExperimentalCoroutinesApi
class BookDetailsFragment : Fragment() {
    private lateinit var binding: FragmentBookDetailsBinding
    private lateinit var viewModel: MyBooksViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.fragment_book_details,
            container,
            false
        )
        getBookDocArgs()
        inflateBook()
        return binding.root
    }

    private fun getBookDocArgs() {
        viewModel = ViewModelProvider(requireActivity())[MyBooksViewModel::class.java]
        val bookID = BookDetailsFragmentArgs.fromBundle(requireArguments()).selectedBookID
        viewModel.getSelectedBookFromID(bookID)
    }

    private fun inflateBook() {

    }

}