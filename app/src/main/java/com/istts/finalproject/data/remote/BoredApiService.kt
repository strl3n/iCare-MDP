package com.istts.finalproject.data.remote

import com.istts.finalproject.data.remote.model.BoredActivityResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface BoredApiService {
    // Endpoint publik, gratis, tanpa API key.
    // type: "social" | "relaxation" | "recreational" | "charity" | "cooking" | "music" | "busywork"
    @GET("api/activity")
    suspend fun getActivity(
        @Query("type") type: String? = null
    ): Response<BoredActivityResponse>
}
