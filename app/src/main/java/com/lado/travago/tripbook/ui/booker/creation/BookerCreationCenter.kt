package com.lado.travago.tripbook.ui.booker.creation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.lado.travago.tripbook.R
import com.lado.travago.tripbook.databinding.FragmentBookerCreationCenterBinding
import com.lado.travago.tripbook.model.admin.SummaryItem
import com.lado.travago.tripbook.model.enums.SignUpCaller
import com.lado.travago.tripbook.repo.firebase.FirebaseAuthRepo
import com.lado.travago.tripbook.ui.recycler_adapters.SummaryItemAdapter
import com.lado.travago.tripbook.ui.recycler_adapters.SummaryItemClickListener
import com.lado.travago.tripbook.utils.AdminUtils
import kotlinx.coroutines.ExperimentalCoroutinesApi

/**
 * A simple [Fragment] subclass.
 * Use the [BookerCreationCenter.newInstance] factory method to
 * create an instance of this fragment.
 */
@ExperimentalCoroutinesApi
class BookerCreationCenter : Fragment() {
    private lateinit var binding: FragmentBookerCreationCenterBinding
    private lateinit var adapter: SummaryItemAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.fragment_booker_creation_center,
            container,
            false
        )
        inflateRecycler()
        return binding.root
    }

    private fun inflateRecycler() {
        var list: List<SummaryItem> = if (FirebaseAuthRepo().currentUser != null)
            SummaryItem.createForBookerConfigOptions(resources)
        else {
            listOf(
                SummaryItem(
                    "3",
                    getString(R.string.text_signUp),
                    subTitle = "OK!",
                    logoResourceID = R.drawable.baseline_login_24,
                    isMainItem = false,
                    state = null,
                    logoUrl = null
                )
            )
        }
        val caller = (requireActivity().intent.extras?.get("caller")  ?: SignUpCaller.USER) as SignUpCaller

        adapter = SummaryItemAdapter(
            /**@see SummaryItem.createForBookerConfigOptions for the ids*/
            clickListener = SummaryItemClickListener {
                when (it.id) {
                    //Profile Info
                    SummaryItem.ITEM_BOOKER_PROFILE_ID -> {
                        findNavController().navigate(
                            BookerCreationCenterDirections.actionBookerCreationCenterToBookerProfileFragment(
                                caller
                            )
                        )
                    }
                    SummaryItem.ITEM_SWAP_PHONE_ID -> {
                        findNavController().navigate(
                            BookerCreationCenterDirections.actionBookerCreationCenterToChangePhoneFragment()
                        )
                    }
                    //Login in initiated by user
                    else -> {
                        findNavController().navigate(
                            BookerCreationCenterDirections.actionBookerCreationCenterToBookerSignUp(
                                caller
                            )
                        )
                    }
                }
            }
        )
        val recyclerManager = LinearLayoutManager(context)
        adapter.submitList(list)
        binding.recyclerView.layoutManager = recyclerManager
        binding.recyclerView.adapter = adapter
    }

}