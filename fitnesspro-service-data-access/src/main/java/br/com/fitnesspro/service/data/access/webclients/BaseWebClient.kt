package br.com.fitnesspro.service.data.access.webclients

import android.content.Context
import br.com.fitnesspro.service.data.access.R
import br.com.fitnesspro.service.data.access.dto.interfaces.IEnumDTOValidationFields
import br.com.fitnesspro.service.data.access.response.ErrorDetails
import br.com.fitnesspro.service.data.access.webclients.result.ResultList
import br.com.fitnesspro.service.data.access.webclients.result.SingleResult
import br.com.fitnesspro.service.data.access.webclients.result.ValidationResult
import java.io.InterruptedIOException
import java.net.ConnectException
import java.net.HttpURLConnection
import java.net.SocketTimeoutException

abstract class BaseWebClient(val context: Context) {

    /**
     * Função para tratamento de erros do serviço, deve ser usada nos processamentos solicitados
     * ao serviço que são validados lá sem nenhum tipo de retorno além do resultado da validação
     * realizada.
     */
    suspend fun <ENUM> executeValidatedProcessErrorHandlerBlock(
        codeBlock: suspend () -> ValidationResult,
        customExceptions: (e: Exception) -> ValidationResult = {
            it.printStackTrace()

            ValidationResult.Error<ENUM>(
                fieldErrors = emptyList(),
                message = context.getString(R.string.message_unexpected_error),
                details = null
            )
        }
    ): ValidationResult where ENUM : Enum<ENUM>, ENUM : IEnumDTOValidationFields {
        return try {
            codeBlock()
        } catch (e: Exception) {
            when (e) {
                is ConnectException -> {
                    ValidationResult.Error<ENUM>(
                        fieldErrors = emptyList(),
                        message = context.getString(R.string.message_connect_exception),
                    )
                }

                is SocketTimeoutException, is InterruptedIOException -> {
                    ValidationResult.Error<ENUM>(
                        fieldErrors = emptyList(),
                        message = context.getString(R.string.message_socket_timeout_exception)
                    )
                }

                else -> customExceptions(e)
            }
        }
    }

    /**
     * Função para tratamento de erros do serviço, deve ser usada nos processamentos solicitados
     * ao serviço que podem retornar um objeto especifico como resultado.
     */
    suspend fun <T, ENUM> executeSingleResultProcessErrorHandlerBlock(
        codeBlock: suspend () -> SingleResult<T>,
        customExceptions: (e: Exception) -> SingleResult<T> = {
            it.printStackTrace()

            SingleResult(
                data = null,
                validationResult = ValidationResult.Error<ENUM>(
                    fieldErrors = emptyList(),
                    message = context.getString(R.string.message_unexpected_error),
                    details = null
                )
            )
        }
    ): SingleResult<T> where ENUM : Enum<ENUM>, ENUM : IEnumDTOValidationFields {
        return try {
            codeBlock()
        } catch (e: Exception) {
            when (e) {
                is ConnectException -> {
                    SingleResult(
                        data = null,
                        validationResult = ValidationResult.Error<ENUM>(
                            fieldErrors = emptyList(),
                            message = context.getString(R.string.message_connect_exception),
                            details = null
                        )
                    )
                }

                is SocketTimeoutException, is InterruptedIOException -> {
                    SingleResult(
                        data = null,
                        validationResult = ValidationResult.Error<ENUM>(
                            fieldErrors = emptyList(),
                            message = context.getString(R.string.message_socket_timeout_exception),
                            details = null
                        )
                    )
                }

                else -> customExceptions(e)
            }
        }
    }

    /**
     * Função para tratamento de erros do serviço, deve ser usada nos contextos de recebimento
     * de uma lista de dados do serviço.
     */
    suspend fun <T> executeResultListProcessErrorHandlerBlock(
        codeBlock: suspend () -> ResultList<T>,
        customExceptions: (e: Exception) -> ResultList<T> = {
            it.printStackTrace()

            ResultList(
                data = emptyList(),
                error = ErrorDetails(
                    code = HttpURLConnection.HTTP_INTERNAL_ERROR,
                    message = context.getString(R.string.message_unexpected_error),
                )
            )
        }
    ): ResultList<T> {
        return try {
            codeBlock()
        } catch (e: Exception) {
            when (e) {
                is ConnectException -> {
                    ResultList(
                        data = emptyList(),
                        error = ErrorDetails(
                            code = HttpURLConnection.HTTP_UNAVAILABLE,
                            message = context.getString(R.string.message_connect_exception),
                        )
                    )
                }

                is SocketTimeoutException, is InterruptedIOException -> {
                    ResultList(
                        data = emptyList(),
                        error = ErrorDetails(
                            code = HttpURLConnection.HTTP_UNAVAILABLE,
                            message = context.getString(R.string.message_socket_timeout_exception),
                        )
                    )
                }

                else -> customExceptions(e)
            }
        }
    }
}