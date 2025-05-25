package com.example.unq_dapps_2025_01_grupo_c.exceptions

class UserNotFoundException(username: String) :
    RuntimeException("User not found: $username")
