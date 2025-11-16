package com.example.sababukia_tbc.data.remote

import com.example.sababukia_tbc.data.remote.dto.LoginRequestDTO
import com.example.sababukia_tbc.data.remote.dto.LoginResponseDTO
import com.example.sababukia_tbc.data.remote.dto.RegisterRequestDTO
import com.example.sababukia_tbc.data.remote.dto.RegisterResponseDTO
import com.example.sababukia_tbc.data.remote.dto.UsersResponseDTO

interface IAuthRemoteDataSource {
    suspend fun login(request: LoginRequestDTO): Result<LoginResponseDTO>
    suspend fun register(request: RegisterRequestDTO): Result<RegisterResponseDTO>
    suspend fun getUsers(page: Int): Result<UsersResponseDTO>
}
