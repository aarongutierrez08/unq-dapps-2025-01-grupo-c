package com.example.unq_dapps_2025_01_grupo_c.exceptions

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException::class)
    fun handleUserAlreadyExists(
        ex: UserAlreadyExistsException,
        request: HttpServletRequest
    ): ResponseEntity<ApiErrorResponse> {
        return buildErrorResponse(ex.message, HttpStatus.CONFLICT, request)
    }

    @ExceptionHandler(InvalidCredentialsException::class)
    fun handleInvalidCredentials(
        ex: InvalidCredentialsException,
        request: HttpServletRequest
    ): ResponseEntity<ApiErrorResponse> {
        return buildErrorResponse(ex.message, HttpStatus.UNAUTHORIZED, request)
    }

    @ExceptionHandler(TeamNotFoundException::class)
    fun handleTeamNotFound(
        ex: TeamNotFoundException,
        request: HttpServletRequest
    ): ResponseEntity<ApiErrorResponse> {
        return buildErrorResponse(ex.message, HttpStatus.NOT_FOUND, request)
    }

    @ExceptionHandler(PlayerNotFoundException::class)
    fun handleTeamNotFound(
        ex: PlayerNotFoundException,
        request: HttpServletRequest
    ): ResponseEntity<ApiErrorResponse> {
        return buildErrorResponse(ex.message, HttpStatus.NOT_FOUND, request)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(
        ex: MethodArgumentNotValidException,
        request: HttpServletRequest
    ): ResponseEntity<ApiErrorResponse> {
        val errors = ex.bindingResult.fieldErrors
            .joinToString("; ") { "${it.field}: ${it.defaultMessage}" }

        return buildErrorResponse(errors, HttpStatus.BAD_REQUEST, request)
    }

    @ExceptionHandler(UserNotFoundException::class)
    fun handleUserNotFound(
        ex: UserNotFoundException,
        request: HttpServletRequest
    ): ResponseEntity<ApiErrorResponse> {
        return buildErrorResponse(ex.message, HttpStatus.NOT_FOUND, request)
    }


    @ExceptionHandler(Exception::class)
    fun handleAll(
        ex: Exception,
        request: HttpServletRequest
    ): ResponseEntity<ApiErrorResponse> {
        return buildErrorResponse(ex.message, HttpStatus.INTERNAL_SERVER_ERROR, request)
    }

    private fun buildErrorResponse(
        message: String?,
        status: HttpStatus,
        request: HttpServletRequest
    ): ResponseEntity<ApiErrorResponse> {
        val body = ApiErrorResponse(
            status = status.value(),
            error = status.reasonPhrase,
            message = message,
            path = request.requestURI
        )
        return ResponseEntity.status(status).body(body)
    }
}
