package com.example.realestatemanager.ui.filter

import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.DatePicker
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.example.realestatemanager.R
import com.example.realestatemanager.data.navigation.NavigationFragmentType
import com.example.realestatemanager.databinding.FilterEstatesFragmentBinding
import com.example.realestatemanager.ui.estate.add.amenity.AmenityListAdapter
import com.example.realestatemanager.ui.estate.add.type.TypeSpinnerAdapter
import com.example.realestatemanager.ui.utils.Event.Companion.observeEvent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Calendar

@AndroidEntryPoint
class FilterEstatesFragment : DialogFragment(R.layout.filter_estates_fragment) {

    companion object {
        fun newInstance() = FilterEstatesFragment()
    }

    private val viewModel by viewModels<FilterPropertiesViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FilterEstatesFragmentBinding.bind(view)
        val width = (Resources.getSystem().displayMetrics.widthPixels * 0.98).toInt()
        val height = ViewGroup.LayoutParams.MATCH_PARENT
        dialog?.window?.setLayout(width, height)

        val propertyTypeAdapter = TypeSpinnerAdapter(requireContext())
        binding.fragmentFilterTypeActv.setAdapter(propertyTypeAdapter)

        val amenityAdapter = AmenityListAdapter()
        binding.fragmentFilterAmenitiesRecyclerView.adapter = amenityAdapter

        binding.fragmentFilterCancelBtn.setOnClickListener {
            dismiss()
        }

        binding.fragmentFilterResetBtn.setOnClickListener {
            viewModel.onResetFilters()
            hideKeyboard(it)
        }

        viewModel.viewEvent.observeEvent(viewLifecycleOwner) {
            dismiss()
        }

        viewModel.viewState.observe(viewLifecycleOwner) { viewState ->
            propertyTypeAdapter.setData(viewState.estateTypes)
            amenityAdapter.submitList(viewState.amenities)

            binding.fragmentFilterTypeActv.setOnItemClickListener { _, _, position, _ ->
                propertyTypeAdapter.getItem(position)?.let {
                    viewModel.onPropertyTypeSelected(it.databaseName)
                }
            }
            binding.fragmentFilterTypeActv.setText(viewState.estateType?.let { getString(it) }, false)

            if (binding.fragmentFilterAddressEditText.text.toString() != viewState.address) {
                binding.fragmentFilterAddressEditText.setText(viewState.address)
            }
            binding.fragmentFilterAddressEditText.doAfterTextChanged {
                viewModel.onAddressChanged(it?.toString() ?: "")
            }

            if (binding.fragmentFilterMinPriceEditText.text.toString() != viewState.minPrice) {
                binding.fragmentFilterMinPriceEditText.setText(viewState.minPrice)
            }
            binding.fragmentFilterMinPriceEditText.doAfterTextChanged {
                viewModel.onMinPriceChanged(it?.toString() ?: "")
            }

            if (binding.fragmentFilterMaxPriceEditText.text.toString() != viewState.maxPrice) {
                binding.fragmentFilterMaxPriceEditText.setText(viewState.maxPrice)
            }

            binding.fragmentFilterMaxPriceEditText.doAfterTextChanged {
                viewModel.onMaxPriceChanged(it?.toString() ?: "")
            }

            if (binding.fragmentFilterMinSurfaceEditText.text.toString() != viewState.minSurface) {
                binding.fragmentFilterMinSurfaceEditText.setText(viewState.minSurface)
            }
            binding.fragmentFilterMinSurfaceEditText.doAfterTextChanged {
                viewModel.onMinSurfaceChanged(it?.toString())
            }

            if (binding.fragmentFilterMaxSurfaceEditText.text.toString() != viewState.maxSurface) {
                binding.fragmentFilterMaxSurfaceEditText.setText(viewState.maxSurface)
            }
            binding.fragmentFilterMaxSurfaceEditText.doAfterTextChanged {
                viewModel.onMaxSurfaceChanged(it?.toString() ?: "")
            }

            if (viewState.availableForSale == null) {
                binding.fragmentFilterEntrySaleStateToggleGroup.clearChecked()
            }

            if (binding.fragmentFilterMediaMinEditText.text.toString() != viewState.minMedia) {
                binding.fragmentFilterMediaMinEditText.setText(viewState.minMedia)
            }
            binding.fragmentFilterMediaMinEditText.doAfterTextChanged {
                viewModel.onMediaMinChanged(it?.toString() ?: "")
            }

            setDatePicker(binding)

            binding.fragmentFilterFilterBtn.setOnClickListener {
                viewState.onFilterClicked()
                viewLifecycleOwner.lifecycleScope.launch {
                    if (viewModel.getCurrentNavigationFragment() == NavigationFragmentType.SLIDING_FRAGMENT) {
                        dismiss()
                    }
                }
            }
        }

        binding.fragmentFilterEntrySaleStateToggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.fragment_filter_for_sale_btn -> {
                        viewModel.onPropertySaleStateChanged(EstateSaleState.FOR_SALE)
                        binding.fragmentFilterForSaleBtn.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.purple_700))
                        binding.fragmentFilterForSaleBtn.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                        binding.fragmentFilterSoldBtn.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
                        binding.fragmentFilterSoldBtn.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                        binding.fragmentFilterAllBtn.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
                        binding.fragmentFilterAllBtn.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                    }

                    R.id.fragment_filter_sold_btn -> {
                        viewModel.onPropertySaleStateChanged(EstateSaleState.SOLD)
                        binding.fragmentFilterForSaleBtn.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
                        binding.fragmentFilterForSaleBtn.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                        binding.fragmentFilterSoldBtn.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.purple_700))
                        binding.fragmentFilterSoldBtn.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                        binding.fragmentFilterAllBtn.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
                        binding.fragmentFilterAllBtn.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                    }

                    R.id.fragment_filter_all_btn -> {
                        viewModel.onPropertySaleStateChanged(EstateSaleState.ALL)
                        binding.fragmentFilterForSaleBtn.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
                        binding.fragmentFilterForSaleBtn.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                        binding.fragmentFilterSoldBtn.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
                        binding.fragmentFilterSoldBtn.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                        binding.fragmentFilterAllBtn.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.purple_700))
                        binding.fragmentFilterAllBtn.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                    }

                    else -> viewModel.onPropertySaleStateChanged(EstateSaleState.ALL)
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        viewModel.onStop()
    }

    private fun setDatePicker(binding: FilterEstatesFragmentBinding) {
        val today = Calendar.getInstance()
        binding.fragmentFilterEntrydatePicker.init(
            today.get(Calendar.YEAR),
            today.get(Calendar.MONTH),
            today.get(Calendar.DAY_OF_MONTH)
        ) { _, year, month, day ->
            viewModel.onEntryDateRangeStatusChanged(LocalDate.of(year, month, day))
        }
    }

    private fun hideKeyboard(view: View?) {
        if (view != null) {
            val inputMethodManager =
                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}