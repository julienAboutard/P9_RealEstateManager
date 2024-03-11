package com.example.realestatemanager.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.realestatemanager.data.navigation.NavigationFragmentType
import com.example.realestatemanager.data.utils.CoroutineDispatcherProvider
import com.example.realestatemanager.domain.estate.current.ResetCurrentEstateIdUseCase
import com.example.realestatemanager.domain.filter.GetIsFilteredUseCase
import com.example.realestatemanager.domain.filter.ResetEstatesFilterUseCase
import com.example.realestatemanager.domain.filter.SetIsFilteredUseCase
import com.example.realestatemanager.domain.navigation.GetNavigationTypeUseCase
import com.example.realestatemanager.domain.navigation.SetNavigationTypeUseCase
import com.example.realestatemanager.domain.permission.SetLocationPermissionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val setNavigationTypeUseCase: SetNavigationTypeUseCase,
    private val getNavigationTypeUseCase: GetNavigationTypeUseCase,
    private val resetEstatesFilterUseCase: ResetEstatesFilterUseCase,
    private val getIsFilteredUseCase: GetIsFilteredUseCase,
    private val setIsFilteredUseCase: SetIsFilteredUseCase,
    private val setLocationPermissionUseCase: SetLocationPermissionUseCase,
    coroutineDispatcherProvider: CoroutineDispatcherProvider,
) : ViewModel() {

    val mainViewState: LiveData<MainViewState> = liveData(coroutineDispatcherProvider.main) {
        combine(
            getNavigationTypeUseCase.invoke(),
            getIsFilteredUseCase.invoke(),
        ) { navigationType, isFiltered ->
            when (navigationType) {
                NavigationFragmentType.SLIDING_FRAGMENT ->
                    emit(
                        MainViewState(
                            isFilterAppBarButtonVisible = if (isFiltered == null) true else !isFiltered,
                            isResetFilterAppBarButtonVisible = isFiltered ?: false,
                            isMapAppBarButtonVisible = true,
                        )
                    )

                NavigationFragmentType.ADD_FRAGMENT ->
                    emit(
                        MainViewState(
                            isFilterAppBarButtonVisible = false,
                            isResetFilterAppBarButtonVisible = false,
                            isMapAppBarButtonVisible = false,
                        )
                    )

                NavigationFragmentType.EDIT_FRAGMENT ->
                    emit(
                        MainViewState(
                            isFilterAppBarButtonVisible = false,
                            isResetFilterAppBarButtonVisible = false,
                            isMapAppBarButtonVisible = false,
                        )
                    )

                NavigationFragmentType.MAP_FRAGMENT ->
                    emit(
                        MainViewState(
                            isFilterAppBarButtonVisible = false,
                            isResetFilterAppBarButtonVisible = false,
                            isMapAppBarButtonVisible = false,
                        )
                    )

                NavigationFragmentType.FILTER_DIALOG_FRAGMENT -> Unit
            }
        }.collect()
    }


    fun onResetFiltersClicked() {
        resetEstatesFilterUseCase.invoke()
        setIsFilteredUseCase.invoke(false)
    }

    fun onMapClicked() {
        setNavigationTypeUseCase.invoke(NavigationFragmentType.MAP_FRAGMENT)
    }

    fun hasPermissionBeenGranted(isPermissionGranted: Boolean?) {
        setLocationPermissionUseCase.invoke(isPermissionGranted ?: false)
    }

    fun onBackClicked() {
        setNavigationTypeUseCase.invoke(NavigationFragmentType.SLIDING_FRAGMENT)
    }
}