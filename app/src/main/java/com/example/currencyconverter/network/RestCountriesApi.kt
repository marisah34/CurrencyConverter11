package com.example.currencyconverter.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

data class Country(
    val name: String,
    val alpha2Code: String,
    val flag: String
)

interface RestCountriesApi {
    @GET("all")
    suspend fun getAllCountries(): List<Country>

    companion object {
        private const val BASE_URL = "https://restcountries.com/v2/"

        fun create(): RestCountriesApi {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(RestCountriesApi::class.java)
        }
    }
}
