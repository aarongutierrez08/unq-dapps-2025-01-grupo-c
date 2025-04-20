package com.example.unq_dapps_2025_01_grupo_c.utils

import org.springframework.stereotype.Component

@Component
class Validator {

    fun validateRegister(username: String?, password: String?): String {
        if (username.isNullOrBlank()) {
            return "El nombre de usuario no puede estar vacío."
        }
        if (password.isNullOrBlank()) {
            return "La contraseña no puede estar vacía."
        }
        if (password.length < 6) {
            return "La contraseña debe tener al menos 6 caracteres."
        }
        return "OK"
    }

    fun validateLogin(username: String?, password: String?): String {
        if (username.isNullOrBlank()) {
            return "El nombre de usuario no puede estar vacío."
        }
        if (password.isNullOrBlank()) {
            return "La contraseña no puede estar vacía."
        }
        if (password.length < 6) {
            return "La contraseña debe tener al menos 6 caracteres."
        }
        return "OK"
    }
}