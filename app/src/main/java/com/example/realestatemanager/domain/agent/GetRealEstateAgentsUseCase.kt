package com.example.realestatemanager.domain.agent

import com.example.realestatemanager.data.agent.RealEstateAgent
import com.example.realestatemanager.data.agent.RealEstateAgentRepository
import javax.inject.Inject

class GetRealEstateAgentsUseCase @Inject constructor(
    private val realEstateAgentRepository: RealEstateAgentRepository
) {

    fun invoke(): List<RealEstateAgent> = realEstateAgentRepository.getRealEstateAgents()
}