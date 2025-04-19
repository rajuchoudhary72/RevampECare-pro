package com.app.ecarepro.data.network.service

import com.app.ecarepro.ui.language.dynamic_language.SheetsApiResponseModel
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GoogleSheetsApiService {
    @GET("v4/spreadsheets/{spreadsheetId}/values/{range}")
    suspend fun getSheetValues(
        @Path("spreadsheetId") spreadsheetId: String,
        @Path("range") range: String,
        @Query("key") apiKey: String
    ): SheetsApiResponseModel
}
