package com.example.realestatemanager.data.agent

import javax.inject.Inject

class RealEstateAgentRepositoryImpl @Inject constructor(): RealEstateAgentRepository {
    override fun getRealEstateAgents(): List<RealEstateAgent> {
        return listOf(
            RealEstateAgent.SHIRO_ALMIRA,
            RealEstateAgent.KURO_ALMIRA,
            RealEstateAgent.NAECHRA_ALMIRA,
            RealEstateAgent.LENARI_ALMIRA,
            RealEstateAgent.SENERA_ALMIRA,
            RealEstateAgent.TO_DEFINE
        )
    }
}