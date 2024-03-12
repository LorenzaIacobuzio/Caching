package com.caching.exception

import com.caching.schema.ErrorResponse
import org.springframework.beans.TypeMismatchException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ExceptionController {
    @ExceptionHandler(BadRequestException::class)
    fun handleBadRequest(exception: BadRequestException): ResponseEntity<ErrorResponse> {
        return ResponseEntity(ErrorResponse(exception.message!!), HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(UnauthorizedException::class)
    fun handleUnauthorized(exception: UnauthorizedException): ResponseEntity<ErrorResponse> {
        return ResponseEntity(ErrorResponse(exception.message!!), HttpStatus.UNAUTHORIZED)
    }

    @ExceptionHandler(NotFoundException::class)
    fun handleNotFound(exception: NotFoundException): ResponseEntity<ErrorResponse> {
        return ResponseEntity(
            ErrorResponse("${exception.getResource()} not found"),
            HttpStatus.NOT_FOUND
        )
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(exception: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        val sb = StringBuilder()
        exception.bindingResult.allErrors.map { it.defaultMessage }
            .forEach { sb.append(it).append("; ") }
        return ResponseEntity(
            ErrorResponse(sb.toString().trimEnd(';')),
            HttpStatus.BAD_REQUEST
        )
    }

    @ExceptionHandler(TypeMismatchException::class)
    fun handleTypeMismatch(exception: TypeMismatchException): ResponseEntity<ErrorResponse> {
        return ResponseEntity(ErrorResponse("Invalid query parameter type"), HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(RuntimeException::class)
    fun handleRuntimeError(exception: RuntimeException): ResponseEntity<ErrorResponse> {
        return ResponseEntity(ErrorResponse(exception.message!!), HttpStatus.INTERNAL_SERVER_ERROR)
    }
}
