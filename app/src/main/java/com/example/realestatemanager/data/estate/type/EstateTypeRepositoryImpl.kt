package com.example.realestatemanager.data.estate.type

import javax.inject.Inject

class EstateTypeRepositoryImpl @Inject constructor(): EstateTypeRepository{

    override fun getEstateTypes(): List<EstateType> {
        return listOf(
            EstateType.HOUSE,
            EstateType.FLAT,
            EstateType.DUPLEX,
            EstateType.PENTHOUSE,
            EstateType.VILLA,
            EstateType.MANOR,
            EstateType.ALL,
            EstateType.OTHER
        )
    }
}