package com.example.maquinariasapp.data
import retrofit2.Call
import retrofit2.http.GET


interface ApiService {
    @GET("maestro-de-maquinarias/")
    fun getMaestroMaquinarias(): Call<List<MaestroMaquinariaResponse>>
}