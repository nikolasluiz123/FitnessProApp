package br.com.fitnesspor.service.data.access.webclient.common

import android.content.Context
import br.com.fitnesspro.service.data.access.R
import br.com.fitnesspro.shared.communication.responses.AuthenticationServiceResponse
import br.com.fitnesspro.shared.communication.responses.FitnessProServiceResponse
import br.com.fitnesspro.shared.communication.responses.PersistenceServiceResponse
import br.com.fitnesspro.shared.communication.responses.ReadServiceResponse
import java.net.ConnectException
import java.net.HttpURLConnection
import java.net.SocketTimeoutException

abstract class FitnessProWebClient(private val context: Context) {

    protected fun formatToken(token: String) = "Bearer $token"

    suspend fun persistenceServiceErrorHandlingBlock(
        codeBlock: suspend () -> PersistenceServiceResponse,
        customExceptions: (e: Exception) -> PersistenceServiceResponse = { throw it }
    ): PersistenceServiceResponse {
        return try {
            codeBlock()
        } catch (exception: Exception) {
            when (exception) {
                is ConnectException -> {
                    PersistenceServiceResponse(
                        success = false,
                        error = context.getString(R.string.message_connect_exception)
                    )
                }
                is SocketTimeoutException -> {
                    PersistenceServiceResponse(
                        success = false,
                        error = context.getString(R.string.message_socket_timeout_exception)
                    )
                }
                else -> customExceptions(exception)
            }
        }
    }

    suspend fun authenticationServiceErrorHandlingBlock(
        codeBlock: suspend () -> AuthenticationServiceResponse,
        customExceptions: (e: Exception) -> AuthenticationServiceResponse =  { throw it }
    ): AuthenticationServiceResponse {
        return try {
            codeBlock()
        } catch (exception: Exception) {
            when (exception) {
                is ConnectException -> {
                    AuthenticationServiceResponse(
                        success = false,
                        error = context.getString(R.string.message_connect_exception),
                        code = HttpURLConnection.HTTP_BAD_REQUEST
                    )
                }
                is SocketTimeoutException -> {
                    AuthenticationServiceResponse(
                        success = false,
                        error = context.getString(R.string.message_socket_timeout_exception),
                        code = HttpURLConnection.HTTP_UNAVAILABLE
                    )
                }
                else -> customExceptions(exception)
            }
        }
    }

    suspend fun <SDO> readServiceErrorHandlingBlock(
        codeBlock: suspend () -> ReadServiceResponse<SDO>,
        customExceptions: (e: Exception) -> ReadServiceResponse<SDO> = { throw it }
    ): ReadServiceResponse<SDO> {
        return try {
            codeBlock()
        } catch (exception: Exception) {
            when (exception) {
                is ConnectException -> {
                    ReadServiceResponse(
                        values = emptyList(),
                        success = false,
                        error = context.getString(R.string.message_connect_exception),
                        code = HttpURLConnection.HTTP_BAD_REQUEST
                    )
                }
                is SocketTimeoutException -> {
                    ReadServiceResponse(
                        values = emptyList(),
                        success = false,
                        error = context.getString(R.string.message_socket_timeout_exception),
                        code = HttpURLConnection.HTTP_UNAVAILABLE
                    )
                }
                else -> customExceptions(exception)
            }
        }
    }

    suspend fun serviceErrorHandlingBlock(
        codeBlock: suspend () -> FitnessProServiceResponse,
        customExceptions: (e: Exception) -> FitnessProServiceResponse = { throw it }
    ): FitnessProServiceResponse {
        return try {
            codeBlock()
        } catch (exception: Exception) {
            when (exception) {
                is ConnectException -> {
                    FitnessProServiceResponse(
                        success = false,
                        error = context.getString(R.string.message_connect_exception),
                        code = HttpURLConnection.HTTP_BAD_REQUEST
                    )
                }
                is SocketTimeoutException -> {
                    FitnessProServiceResponse(
                        success = false,
                        error = context.getString(R.string.message_socket_timeout_exception),
                        code = HttpURLConnection.HTTP_UNAVAILABLE
                    )
                }
                else -> customExceptions(exception)
            }
        }
    }
}