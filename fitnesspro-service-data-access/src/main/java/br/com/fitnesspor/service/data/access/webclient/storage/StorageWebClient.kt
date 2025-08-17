package br.com.fitnesspor.service.data.access.webclient.storage

import android.content.Context
import br.com.fitnesspor.service.data.access.extensions.getResponseBody
import br.com.fitnesspor.service.data.access.service.storage.IStorageService
import br.com.fitnesspor.service.data.access.webclient.common.FitnessProWebClient
import br.com.fitnesspro.core.extensions.defaultGSon
import br.com.fitnesspro.shared.communication.enums.storage.EnumGCBucketContentTypes
import br.com.fitnesspro.shared.communication.enums.storage.EnumGCBucketContentTypes.MP4
import br.com.fitnesspro.shared.communication.enums.storage.EnumGCBucketContentTypes.PDF
import br.com.fitnesspro.shared.communication.responses.StorageServiceResponse
import com.google.gson.GsonBuilder
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class StorageWebClient(
    context: Context,
    private val storageService: IStorageService
): FitnessProWebClient(context) {

    suspend fun uploadReports(token: String, ids: List<String>, files: List<File>): StorageServiceResponse {
        return storageServiceErrorHandlingBlock(
            codeBlock = {
                storageService.uploadReports(
                    token = formatToken(token),
                    ids = convertIdsToRequestBody(ids),
                    files = files.map { fileToMultipart(it, PDF) }
                ).getResponseBody()
            }
        )
    }

    suspend fun uploadVideos(token: String, ids: List<String>, files: List<File>): StorageServiceResponse {
        return storageServiceErrorHandlingBlock(
            codeBlock = {
                storageService.uploadVideos(
                    token = formatToken(token),
                    ids = convertIdsToRequestBody(ids),
                    files = files.map { fileToMultipart(it, MP4) }
                ).getResponseBody()
            }
        )
    }

    private fun fileToMultipart(file: File, contentType: EnumGCBucketContentTypes, partName: String = "files"): MultipartBody.Part {
        val requestFile = file.asRequestBody(contentType.value.toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(partName, file.name, requestFile)
    }

    private fun convertIdsToRequestBody(ids: List<String>): RequestBody {
        val idsJson = GsonBuilder().defaultGSon().toJson(ids)
        return idsJson.toRequestBody("application/json".toMediaTypeOrNull())
    }
}