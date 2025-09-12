package br.com.fitnesspro.service.data.access.service.storage

import br.com.fitnesspro.shared.communication.constants.EndPointsV1.STORAGE
import br.com.fitnesspro.shared.communication.constants.EndPointsV1.UPLOAD_REPORTS
import br.com.fitnesspro.shared.communication.constants.EndPointsV1.UPLOAD_VIDEOS
import br.com.fitnesspro.shared.communication.responses.StorageServiceResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface IStorageService {

    @Multipart
    @POST("$STORAGE/$UPLOAD_REPORTS")
    suspend fun uploadReports(
        @Header("Authorization") token: String,
        @Part("ids") ids: RequestBody,
        @Part files: List<MultipartBody.Part>
    ): Response<StorageServiceResponse>

    @Multipart
    @POST("$STORAGE/$UPLOAD_VIDEOS")
    suspend fun uploadVideos(
        @Header("Authorization") token: String,
        @Part("ids") ids: RequestBody,
        @Part files: List<MultipartBody.Part>
    ): Response<StorageServiceResponse>
}