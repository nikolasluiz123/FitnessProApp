package br.com.fitnesspor.service.data.access.webclient.common

import android.content.Context
import br.com.fitnesspro.core.exceptions.ServiceException
import br.com.fitnesspro.service.data.access.R
import br.com.fitnesspro.shared.communication.dtos.common.BaseDTO
import br.com.fitnesspro.shared.communication.enums.serviceauth.EnumErrorType.EXPIRED_TOKEN
import br.com.fitnesspro.shared.communication.enums.serviceauth.EnumErrorType.INVALID_TOKEN
import br.com.fitnesspro.shared.communication.enums.serviceauth.EnumErrorType.NETWORK
import br.com.fitnesspro.shared.communication.exception.ExpiredTokenException
import br.com.fitnesspro.shared.communication.exception.NotFoundTokenException
import br.com.fitnesspro.shared.communication.responses.AuthenticationServiceResponse
import br.com.fitnesspro.shared.communication.responses.ExportationServiceResponse
import br.com.fitnesspro.shared.communication.responses.FitnessProServiceResponse
import br.com.fitnesspro.shared.communication.responses.IFitnessProServiceResponse
import br.com.fitnesspro.shared.communication.responses.ImportationServiceResponse
import br.com.fitnesspro.shared.communication.responses.PersistenceServiceResponse
import br.com.fitnesspro.shared.communication.responses.ReadServiceResponse
import br.com.fitnesspro.shared.communication.responses.SingleValueServiceResponse
import java.io.InterruptedIOException
import java.net.ConnectException
import java.net.HttpURLConnection

abstract class FitnessProWebClient(private val context: Context) {

    protected fun formatToken(token: String) = "Bearer $token"

    protected suspend fun <DTO: BaseDTO> persistenceServiceErrorHandlingBlock(
        codeBlock: suspend () -> PersistenceServiceResponse<DTO>,
        customExceptions: (e: Exception) -> PersistenceServiceResponse<DTO> = { throw it }
    ): PersistenceServiceResponse<DTO> {
        return try {
            val response = codeBlock()
            executeResponseValidations(response)
            response
        } catch (exception: Exception) {
            when (exception) {
                is ConnectException -> {
                    PersistenceServiceResponse<DTO>(
                        success = false,
                        error = context.getString(R.string.message_connect_exception),
                        errorType = NETWORK
                    )
                }
                is InterruptedIOException -> {
                    PersistenceServiceResponse<DTO>(
                        success = false,
                        error = context.getString(R.string.message_connect_exception),
                        errorType = NETWORK
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
            val response = codeBlock()
            executeResponseValidations(response)
            response
        } catch (exception: Exception) {
            when (exception) {
                is ConnectException -> {
                    ExportationServiceResponse(
                        executionLogId = "",
                        executionLogPackageId = "",
                        success = false,
                        error = context.getString(R.string.message_connect_exception),
                        errorType = NETWORK
                    )
                }
                is InterruptedIOException -> {
                    ExportationServiceResponse(
                        executionLogId = "",
                        executionLogPackageId = "",
                        success = false,
                        error = context.getString(R.string.message_connect_exception),
                        errorType = NETWORK
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
            val response = codeBlock()
            executeResponseValidations(response)
            response
        } catch (exception: Exception) {
            when (exception) {
                is ConnectException -> {
                    AuthenticationServiceResponse(
                        success = false,
                        error = context.getString(R.string.message_connect_exception),
                        code = HttpURLConnection.HTTP_BAD_REQUEST,
                        errorType = NETWORK
                    )
                }
                is InterruptedIOException -> {
                    AuthenticationServiceResponse(
                        success = false,
                        error = context.getString(R.string.message_connect_exception),
                        code = HttpURLConnection.HTTP_UNAVAILABLE,
                        errorType = NETWORK
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
            val response = codeBlock()
            executeResponseValidations(response)
            response
        } catch (exception: Exception) {
            when (exception) {
                is ConnectException -> {
                    ReadServiceResponse(
                        values = emptyList(),
                        success = false,
                        error = context.getString(R.string.message_connect_exception),
                        code = HttpURLConnection.HTTP_BAD_REQUEST,
                        errorType = NETWORK
                    )
                }
                is InterruptedIOException -> {
                    ReadServiceResponse(
                        values = emptyList(),
                        success = false,
                        error = context.getString(R.string.message_connect_exception),
                        code = HttpURLConnection.HTTP_UNAVAILABLE,
                        errorType = NETWORK
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
            val response = codeBlock()
            executeResponseValidations(response)
            response
        } catch (exception: Exception) {
            when (exception) {
                is ConnectException -> {
                    ImportationServiceResponse(
                        executionLogId = "",
                        executionLogPackageId = "",
                        values = emptyList(),
                        success = false,
                        error = context.getString(R.string.message_connect_exception),
                        code = HttpURLConnection.HTTP_BAD_REQUEST,
                        errorType = NETWORK
                    )
                }
                is InterruptedIOException -> {
                    ImportationServiceResponse(
                        executionLogId = "",
                        executionLogPackageId = "",
                        values = emptyList(),
                        success = false,
                        error = context.getString(R.string.message_connect_exception),
                        code = HttpURLConnection.HTTP_UNAVAILABLE,
                        errorType = NETWORK
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
            val response = codeBlock()
            executeResponseValidations(response)
            response
        } catch (exception: Exception) {
            when (exception) {
                is ConnectException -> {
                    SingleValueServiceResponse<DTO>(
                        success = false,
                        error = context.getString(R.string.message_connect_exception),
                        errorType = NETWORK
                    )
                }
                is InterruptedIOException -> {
                    SingleValueServiceResponse<DTO>(
                        success = false,
                        error = context.getString(R.string.message_connect_exception),
                        errorType = NETWORK
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
            val response = codeBlock()
            executeResponseValidations(response)
            response
        } catch (exception: Exception) {
            when (exception) {
                is ConnectException -> {
                    FitnessProServiceResponse(
                        success = false,
                        error = context.getString(R.string.message_connect_exception),
                        code = HttpURLConnection.HTTP_BAD_REQUEST,
                        errorType = NETWORK
                    )
                }
                is InterruptedIOException -> {
                    FitnessProServiceResponse(
                        success = false,
                        error = context.getString(R.string.message_connect_exception),
                        code = HttpURLConnection.HTTP_UNAVAILABLE,
                        errorType = NETWORK
                    )
                }
                else -> customExceptions(exception)
            }
        }
    }

    private fun executeResponseValidations(response: IFitnessProServiceResponse) {
        if (!response.success) {
            when (response.errorType) {
                EXPIRED_TOKEN -> throw ExpiredTokenException(context.getString(R.string.message_expired_token))
                INVALID_TOKEN -> throw NotFoundTokenException(context.getString(R.string.message_not_foud_token))
                NETWORK -> { }
                else -> {
                    throw ServiceException(response.error!!)
                }
            }
        }
    }
}
