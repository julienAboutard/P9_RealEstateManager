package com.example.realestatemanager.ui.estate.detail

import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.example.realestatemanager.R
import com.example.realestatemanager.databinding.FragmentEstateDetailsBinding
import com.example.realestatemanager.ui.estate.add.amenity.AmenityListAdapter
import com.example.realestatemanager.ui.estate.detail.medialist.MediaListAdapter
import com.example.realestatemanager.ui.utils.NativePhoto.Companion.load
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailFragment : Fragment(R.layout.fragment_estate_details) {

    private val viewModel by viewModels<DetailViewModel>()

    companion object {
        fun newInstance() = DetailFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentEstateDetailsBinding.bind(view)

        binding.estateDetailsProgressBar.isVisible = false
        binding.root.isVisible = true

        val mediaListAdapter = MediaListAdapter()
        binding.estateDetailsMediaRv.adapter = mediaListAdapter

        val amenityAdapter = AmenityListAdapter()
        binding.estateDetailsAmenitiesRv.adapter = amenityAdapter

        viewModel.viewState.observe(viewLifecycleOwner) { viewState ->


            binding.root.isVisible = true
            binding.estateDetailsProgressBar.isVisible = false

            mediaListAdapter.submitList(viewState.medias)

            amenityAdapter.submitList(viewState.amenities)

            binding.estateDetailEditFab.setOnClickListener {
                viewModel.onEditClicked()
                Navigation.findNavController(binding.root).navigate(R.id.action_list_to_add)
            }

            if (viewState.isSold) {
                binding.estateDetailsSoldDateTv.visibility = View.VISIBLE
                binding.estateDetailsSoldDateTv.text = viewState.saleDate?.toCharSequence(requireContext())
                binding.estateDetailsPriceTv.paintFlags =
                    binding.estateDetailsPriceTv.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                binding.estateDetailsSoldDateTv.visibility = View.GONE
            }

            binding.estateDetailsTypeTv.text = resources.getString(viewState.estateType)
            binding.estateDetailsPriceTv.text = viewState.price
            binding.estateDetailsDescriptionTv.text = viewState.description
            binding.estateDetailsLocationTv.text = viewState.address.toCharSequence(requireContext())
            binding.estateDetailsSurfaceTv.text = viewState.surface
            binding.estateDetailsAgentNameTv.text = viewState.agentName.toCharSequence(requireContext())
            binding.estateDetailsEntryDateTv.text = viewState.entryDate.toCharSequence(requireContext())
            binding.estateDetailsRoomTv.text = viewState.rooms.toCharSequence(requireContext())
            binding.estateDetailsBathroomTv.text = viewState.bathrooms.toCharSequence(requireContext())
            binding.estateDetailsBedroomTv.text = viewState.bedrooms.toCharSequence(requireContext())
            viewState.mapMiniature.load(binding.estateDetailsMapIv)
                .error(R.drawable.baseline_map_24)
                .into(binding.estateDetailsMapIv)
        }
    }


}
