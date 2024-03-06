package com.example.realestatemanager.ui.slidepane

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.slidingpanelayout.widget.SlidingPaneLayout
import com.example.realestatemanager.R
import com.example.realestatemanager.databinding.SlidepanelayoutBinding
import com.example.realestatemanager.ui.estate.list.ListEstateAdapter
import com.example.realestatemanager.ui.estate.list.ListEstateViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SlidePaneFragment : Fragment() {

    private lateinit var callback: TwoPaneOnBackPressedCallback
    private val viewModel by viewModels<ListEstateViewModel>()

    companion object {
        fun newInstance() = SlidePaneFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val binding = SlidepanelayoutBinding.inflate(inflater, container, false)

        val slidingPaneLayout = binding.slidingPaneLayout
        slidingPaneLayout.lockMode = SlidingPaneLayout.LOCK_MODE_LOCKED

        callback = TwoPaneOnBackPressedCallback(slidingPaneLayout)
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            callback
        )

        val adapter = ListEstateAdapter {
            viewModel.onObjectClicked(it)
            binding.slidingPaneLayout.openPane()
        }
        binding.estateListRv.adapter = adapter

        viewModel.viewState.observe(viewLifecycleOwner) { viewState ->
            adapter.submitList(viewState)
        }

        binding.estateListAddFab.setOnClickListener {
            viewModel.onAddEstateClicked()
            Navigation.findNavController(binding.root).navigate(R.id.action_list_to_add)
        }

        return binding.root
    }
}

class TwoPaneOnBackPressedCallback(
    private val slidingPaneLayout: SlidingPaneLayout
) : OnBackPressedCallback(
    // Set the default 'enabled' state to true only if it is slidable (i.e., the panes
    // are overlapping) and open (i.e., the detail pane is visible).
    slidingPaneLayout.isSlideable && slidingPaneLayout.isOpen
), SlidingPaneLayout.PanelSlideListener {

    init {
        slidingPaneLayout.addPanelSlideListener(this)
    }

    override fun handleOnBackPressed() {
        // Return to the list pane when the system back button is pressed.
        slidingPaneLayout.closePane()

    }

    override fun onPanelSlide(panel: View, slideOffset: Float) {}

    override fun onPanelOpened(panel: View) {
        // Intercept the system back button when the detail pane becomes visible.
        isEnabled = true
    }

    override fun onPanelClosed(panel: View) {
        // Disable intercepting the system back button when the user returns to the
        // list pane.
        isEnabled = false
    }
}