package com.example.sababukia_tbc.data.remote

import com.example.sababukia_tbc.data.remote.dto.LoginRequestDTO
import com.example.sababukia_tbc.data.remote.dto.LoginResponseDTO
import com.example.sababukia_tbc.data.remote.dto.RegisterRequestDTO
import com.example.sababukia_tbc.data.remote.dto.RegisterResponseDTO

interface IRemoteDataSource {
    suspend fun login(request: LoginRequestDTO): Result<LoginResponseDTO>
    suspend fun register(request: RegisterRequestDTO): Result<RegisterResponseDTO>
}
