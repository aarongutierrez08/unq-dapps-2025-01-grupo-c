package com.example.unq_dapps_2025_01_grupo_c.exceptions

class UserAlreadyExistsException(message: String) : RuntimeException(message)
class InvalidCredentialsException() : RuntimeException("User or password incorrect.")
