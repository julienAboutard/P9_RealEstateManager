package com.example.realestatemanager.ui.estate.add

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.example.realestatemanager.BuildConfig
import com.example.realestatemanager.R
import com.example.realestatemanager.data.navigation.NavigationFragmentType
import com.example.realestatemanager.databinding.FragmentEstateAddBinding
import com.example.realestatemanager.ui.estate.add.address_predictions.PredictionListAdapter
import com.example.realestatemanager.ui.estate.add.agent.AgentSpinnerAdapter
import com.example.realestatemanager.ui.estate.add.amenity.AmenityListAdapter
import com.example.realestatemanager.ui.estate.add.media.MediaPreviewListAdapter
import com.example.realestatemanager.ui.estate.add.type.TypeSpinnerAdapter
import com.example.realestatemanager.ui.utils.Event.Companion.observeEvent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File

@AndroidEntryPoint
class AddFragment : Fragment(R.layout.fragment_estate_add) {

    companion object {
        fun newInstance() = AddFragment()

        private var currentPhotoUri: Uri? = null
    }

    private val viewModel by viewModels<AddViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentEstateAddBinding.bind(view)

        val typeAdapter = TypeSpinnerAdapter(requireContext())
        val agentAdapter = AgentSpinnerAdapter()
        val mediaPreviewAdapter = MediaPreviewListAdapter()
        val amenityAdapter = AmenityListAdapter()
        val predictionAdapter = PredictionListAdapter()

        binding.estateAddAgentActv.setAdapter(agentAdapter)
        binding.estateAddTypeActv.setAdapter(typeAdapter)
        binding.estateAddAmenitiesRecyclerView.adapter = amenityAdapter
        binding.estatePreviewMediaRecyclerView.adapter = mediaPreviewAdapter
        binding.formAddressPredictionsRecyclerView.adapter = predictionAdapter

        initTextWatchers(binding)

        binding.formSubmitButton.setOnClickListener {
            viewModel.onAddPropertyClicked()
            viewLifecycleOwner.lifecycleScope.launch {
                if (viewModel.getCurrentNavigationFragment() == NavigationFragmentType.SLIDING_FRAGMENT) {
                    Navigation.findNavController(binding.root).navigate(R.id.action_add_to_list)
                }
            }
        }

        binding.estateAddTypeActv.setOnItemClickListener { _, _, position, _ ->
            typeAdapter.getItem(position)?.let {
                viewModel.onTypeSelected(it.databaseName)
            }
        }

        binding.estateAddAgentActv.setOnItemClickListener { _, _, position, _ ->
            agentAdapter.getItem(position)?.let {
                viewModel.onAgentSelected(it.name)
            }
        }

        viewModel.viewEventLiveData.observeEvent(viewLifecycleOwner) { event ->
            when (event) {
                is AddEvent.Loading -> {
                    binding.root.visibility = View.GONE
                    binding.formProgressBar.visibility = View.VISIBLE
                }

                is AddEvent.Form -> {
                    binding.root.visibility = View.VISIBLE
                    binding.formProgressBar.visibility = View.GONE
                }

                is AddEvent.Toast -> Toast.makeText(
                    requireContext(),
                    event.text.toCharSequence(requireContext()),
                    Toast.LENGTH_SHORT
                ).show()

                is AddEvent.Error -> Toast.makeText(
                    requireContext(),
                    event.errorMessage,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        viewModel.viewStateLiveData.observe(viewLifecycleOwner) { viewState ->
            typeAdapter.setData(viewState.estateTypes)
            agentAdapter.setData(viewState.agents)

            mediaPreviewAdapter.submitList(viewState.medias)
            amenityAdapter.submitList(viewState.amenities)
            predictionAdapter.submitList(viewState.addressPredictions)

            binding.formSubmitButton.text = viewState.submitButtonText.toCharSequence(requireContext())

            binding.estateAddSoldStatusSwitch.isVisible = viewState.areEditItemsVisible
            binding.estateAddSoldStatusSwitch.isChecked = viewState.isSold
            binding.estateAddSoldStatusSwitch.setOnCheckedChangeListener { _, isChecked ->
                viewModel.onSoldStatusChanged(isChecked)
            }

            binding.estateAddSoldStatusTv.isVisible = viewState.areEditItemsVisible
            binding.estateAddSoldStatusTv.text = viewState.soldDate
            binding.estateAddDateCreationTv.isVisible = viewState.areEditItemsVisible
            binding.estateAddDateCreationTv.text = viewState.entryDate

            binding.formProgressBar.isVisible = viewState.isProgressBarVisible

            val currentDescription = binding.estateAddDescriptionTextInputEditText.text?.toString() ?: ""
            if (currentDescription != viewState.description) {
                binding.estateAddDescriptionTextInputEditText.setText(viewState.description)
            }

            val currentSurface = binding.estateAddSurfaceTextInputEditText.text?.toString() ?: ""
            if (currentSurface != viewState.surface) {
                binding.estateAddSurfaceTextInputEditText.setText(viewState.surface)
            }

            val currentPrice = binding.estateAddPriceTextInputEditText.text?.toString() ?: ""
            if (currentPrice != viewState.price) {
                binding.estateAddPriceTextInputEditText.setText(viewState.price)
            }

            val currentAddress = binding.estateAddAddressTextInputEditText.text?.toString() ?: ""

            if (currentAddress != viewState.address) {
                binding.estateAddAddressTextInputEditText.setText(viewState.address)
            }

            binding.estateAddAddressTextInputEditText.doAfterTextChanged {
                if (!viewState.isInternetEnabled) {
                    viewModel.onAddressChanged(it.toString())
                } else {
                    binding.estateAddAddressTextInputEditText.setSelection(it.toString().length)
                    viewModel.onAddressChanged(it?.toString() ?: "")
                }
            }

            binding.estateAddAddressTextInputEditText.setOnFocusChangeListener { _, hasFocus ->
                if (viewState.isInternetEnabled) {
                    viewModel.onAddressEditTextFocused(hasFocus)
                    if (!hasFocus) {
                        hideKeyboard(binding.estateAddAddressTextInputEditText)
                        binding.estateAddAddressTextInputLayout.isHelperTextEnabled = false
                    }
                    if (hasFocus) binding.estateAddAddressTextInputLayout.helperText =
                        getString(R.string.form_address_helper_text)
                }
            }

            binding.estateAddAddressIsValidHelperTv.isVisible = viewState.isAddressValid

            val currentRooms = binding.estateAddRoomTextInputEditText.text?.toString() ?: ""
            if (currentRooms != viewState.nbRooms.toString()) {
                binding.estateAddRoomTextInputEditText  .setText(viewState.nbRooms)
            }

            val currentBedrooms = binding.estateAddBedroomTextInputEditText.text?.toString() ?: ""
            if (currentBedrooms != viewState.nbBedrooms.toString()) {
                binding.estateAddBedroomTextInputEditText.setText(viewState.nbBedrooms)
            }

            val currentBathrooms = binding.estateAddBathroomTextInputEditText.text?.toString() ?: ""
            if (currentBathrooms != viewState.nbBathrooms.toString()) {
                binding.estateAddBathroomTextInputEditText.setText(viewState.nbBathrooms)
            }

            binding.estateAddPriceTextInputEditText.hint =
                viewState.priceCurrencyHint.toCharSequence(requireContext())
            binding.estateAddPriceTextInputLayout.startIconDrawable = ContextCompat.getDrawable(
                requireContext(),
                viewState.currencyDrawableRes
            )

            binding.estateAddSurfaceTextInputLayout.hint = viewState.surfaceUnit.toCharSequence(requireContext())

            binding.estateAddTypeActv.setText(viewState.type?.let { getString(it) }, false)
            binding.estateAddAgentActv.setText(viewState.selectedAgent, false)

        }

        // region Import pictures
        val importPictureCallbackImage = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                Log.d("PhotoPicker", "Selected URI: $uri")
                viewModel.onPictureSelected(uri.toString())
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }

        binding.estateAddPictureFromStorageButton.setOnClickListener {
            importPictureCallbackImage.launch(
                PickVisualMediaRequest(
                    ActivityResultContracts.PickVisualMedia.ImageOnly
                )
            )
        }

        binding.estateAddPictureFromCameraButton.setOnClickListener {
            currentPhotoUri = FileProvider.getUriForFile(
                requireContext(),
                BuildConfig.APPLICATION_ID + ".provider",
                File.createTempFile(
                    "JPEG_",
                    ".jpg",
                    requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                )
            )

            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                .putExtra(MediaStore.EXTRA_OUTPUT, currentPhotoUri)

            @Suppress("DEPRECATION")
            startActivityForResult(intent, 0)
            // endregion Import pictures
        }

        // region Video pictures
        val importPictureCallbackVideo = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                Log.d("VideoPicker", "Selected URI: $uri")
                viewModel.onVideoSelected(uri.toString())
            } else {
                Log.d("VideoPicker", "No media selected")
            }
        }

        binding.estateAddVideoFromStorageButton.setOnClickListener {
            importPictureCallbackVideo.launch(
                PickVisualMediaRequest(
                    ActivityResultContracts.PickVisualMedia.VideoOnly
                )
            )
        }

        binding.estateAddVideoFromCameraButton.setOnClickListener {
            currentPhotoUri = FileProvider.getUriForFile(
                requireContext(),
                BuildConfig.APPLICATION_ID + ".provider",
                File.createTempFile(
                    "MP4_",
                    ".mp4",
                    requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                )
            )
            val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
                .putExtra(MediaStore.EXTRA_OUTPUT, currentPhotoUri)

            @Suppress("DEPRECATION")
            startActivityForResult(intent, 0)
            // endregion Video pictures
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        @Suppress("DEPRECATION")
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (currentPhotoUri.toString().endsWith("mp4")) {
                viewModel.onVideoSelected(currentPhotoUri.toString())
            } else {
                viewModel.onPictureSelected(currentPhotoUri.toString())
            }
        }
    }

    private fun initTextWatchers(binding: FragmentEstateAddBinding) {

        binding.estateAddPriceTextInputEditText.doAfterTextChanged {
            viewModel.onPriceChanged(it?.toString())
        }

        binding.estateAddAddressTextInputEditText.doAfterTextChanged {
            viewModel.onAddressChanged(it.toString())
        }

        binding.estateAddSurfaceTextInputEditText.doAfterTextChanged {
            viewModel.onSurfaceChanged(it?.toString())
        }

        binding.estateAddRoomTextInputEditText.doAfterTextChanged {
            viewModel.onRoomsNumberChanged(it?.toString())
        }

        binding.estateAddBathroomTextInputEditText.doAfterTextChanged {
            viewModel.onBathroomsNumberChanged(it?.toString())
        }

        binding.estateAddBedroomTextInputEditText.doAfterTextChanged {
            viewModel.onBedroomsNumberChanged(it?.toString())
        }

        binding.estateAddDescriptionTextInputEditText.doAfterTextChanged {
            viewModel.onDescriptionChanged(it.toString())
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