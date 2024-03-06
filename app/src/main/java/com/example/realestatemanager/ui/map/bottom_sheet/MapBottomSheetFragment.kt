package com.example.realestatemanager.ui.map.bottom_sheet

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.example.realestatemanager.R
import com.example.realestatemanager.data.navigation.NavigationFragmentType
import com.example.realestatemanager.databinding.FragmentMapBottomSheetBinding
import com.example.realestatemanager.ui.main.MainActivity
import com.example.realestatemanager.ui.utils.Event.Companion.observeEvent
import com.example.realestatemanager.ui.utils.NativePhoto.Companion.load
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MapBottomSheetFragment : BottomSheetDialogFragment(R.layout.fragment_map_bottom_sheet) {
    companion object {
        const val TAG = "MapBottomSheetFragment"
        const val EDIT_PROPERTY_TAG = "EDIT_PROPERTY_TAG"
        const val DETAIL_PROPERTY_TAG = "DETAIL_PROPERTY_TAG"

        fun newInstance() = MapBottomSheetFragment()
    }

    private val viewModel: MapBottomSheetViewModel by viewModels()



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentMapBottomSheetBinding.bind(view)
        val standardBottomSheetBehavior = BottomSheetBehavior.from(binding.mapBottomSheetPropertyDetailLayout as View)
        standardBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        viewModel.viewState.observe(viewLifecycleOwner) { viewState ->
            binding.mapBottomSheetPropertyTypeTv.text = viewState.type
            binding.mapBottomSheetPropertyPriceTv.text = viewState.price

            viewState.featuredPicture
                .load(binding.mapBottomSheetPropertyImageView)
                .transform(CenterCrop())
                .error(R.drawable.baseline_add_home_24)
                .into(binding.mapBottomSheetPropertyImageView)

            binding.mapBottomSheetPropertySoldLinearLayout.isVisible = viewState.isSold

            binding.mapBottomSheetPropertyImageView.contentDescription =
                "Picture of ${viewState.type}. Status: ${viewState.isSold}"

            binding.mapBottomSheetPropertyEditBtn.setOnClickListener {
                viewState.onEditClick.invoke(EDIT_PROPERTY_TAG)
            }
            binding.mapBottomSheetPropertyDetailBtn.setOnClickListener {
                viewState.onDetailClick.invoke(DETAIL_PROPERTY_TAG)
            }
            binding.mapBottomSheetPropertyDescriptionTv.text = viewState.description
            binding.mapBottomSheetPropertySurfaceTv.text = viewState.surface
            binding.mapBottomSheetPropertyRoomsTv.text = viewState.rooms.toCharSequence(requireContext())
            binding.mapBottomSheetPropertyBedroomsTv.text = viewState.bedrooms.toCharSequence(requireContext())
            binding.mapBottomSheetPropertyBathroomsTv.text = viewState.bathrooms.toCharSequence(requireContext())
            binding.mapBottomSheetPropertyProgressBar.isVisible = viewState.isProgressBarVisible
            binding.root.isVisible = !viewState.isProgressBarVisible
        }

        viewModel.viewEvent.observeEvent(viewLifecycleOwner) { viewEvent ->
            when (viewEvent) {
                is MapBottomSheetEvent.Edit -> {
                    dismiss()
                }

                is MapBottomSheetEvent.Detail -> {
                    dismiss()
                }
            }
        }
    }
}

