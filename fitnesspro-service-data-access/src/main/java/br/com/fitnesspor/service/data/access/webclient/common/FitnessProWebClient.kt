package br.com.fitnesspor.service.data.access.webclient.common

import android.content.Context
import br.com.fitnesspro.service.data.access.R
import br.com.fitnesspro.shared.communication.responses.AuthenticationServiceResponse
import br.com.fitnesspro.shared.communication.responses.ExportationServiceResponse
import br.com.fitnesspro.shared.communication.responses.FitnessProServiceResponse
import br.com.fitnesspro.shared.communication.responses.ImportationServiceResponse
import br.com.fitnesspro.shared.communication.responses.PersistenceServiceResponse
import br.com.fitnesspro.shared.communication.responses.ReadServiceResponse
import br.com.fitnesspro.shared.communication.responses.SingleValueServiceResponse
import java.net.ConnectException
import java.net.HttpURLConnection
import java.net.SocketTimeoutException

abstract class FitnessProWebClient(private val context: Context) {

    protected fun formatToken(token: String) = "Bearer $token"

    protected suspend fun persistenceServiceErrorHandlingBlock(
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

    protected suspend fun exportationServiceErrorHandlingBlock(
        codeBlock: suspend () -> ExportationServiceResponse,
        customExceptions: (e: Exception) -> ExportationServiceResponse = { throw it }
    ): ExportationServiceResponse {
        return try {
            codeBlock()
        } catch (exception: Exception) {
            when (exception) {
                is ConnectException -> {
                    ExportationServiceResponse(
                        executionLogId = "",
                        executionLogPackageId = "",
                        success = false,
                        error = context.getString(R.string.message_connect_exception)
                    )
                }
                is SocketTimeoutException -> {
                    ExportationServiceResponse(
                        executionLogId = "",
                        executionLogPackageId = "",
                        success = false,
                        error = context.getString(R.string.message_socket_timeout_exception)
                    )
                }
                else -> customExceptions(exception)
            }
        }
    }

    protected suspend fun authenticationServiceErrorHandlingBlock(
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

    protected suspend fun <DTO> readServiceErrorHandlingBlock(
        codeBlock: suspend () -> ReadServiceResponse<DTO>,
        customExceptions: (e: Exception) -> ReadServiceResponse<DTO> = { throw it }
    ): ReadServiceResponse<DTO> {
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

    protected suspend fun <DTO> importationServiceErrorHandlingBlock(
        codeBlock: suspend () -> ImportationServiceResponse<DTO>,
        customExceptions: (e: Exception) -> ImportationServiceResponse<DTO> = { throw it }
    ): ImportationServiceResponse<DTO> {
        return try {
            codeBlock()
        } catch (exception: Exception) {
            when (exception) {
                is ConnectException -> {
                    ImportationServiceResponse(
                        executionLogId = "",
                        executionLogPackageId = "",
                        values = emptyList(),
                        success = false,
                        error = context.getString(R.string.message_connect_exception),
                        code = HttpURLConnection.HTTP_BAD_REQUEST
                    )
                }
                is SocketTimeoutException -> {
                    ImportationServiceResponse(
                        executionLogId = "",
                        executionLogPackageId = "",
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

    protected suspend fun <DTO> singleValueErrorHandlingBlock(
        codeBlock: suspend () -> SingleValueServiceResponse<DTO>,
        customExceptions: (e: Exception) -> SingleValueServiceResponse<DTO> = { throw it }
    ): SingleValueServiceResponse<DTO> {
        return try {
            codeBlock()
        } catch (exception: Exception) {
            when (exception) {
                is ConnectException -> {
                    SingleValueServiceResponse<DTO>(
                        success = false,
                        error = context.getString(R.string.message_connect_exception)
                    )
                }
                is SocketTimeoutException -> {
                    SingleValueServiceResponse<DTO>(
                        success = false,
                        error = context.getString(R.string.message_socket_timeout_exception)
                    )
                }
                else -> customExceptions(exception)
            }
        }
    }

    protected suspend fun serviceErrorHandlingBlock(
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