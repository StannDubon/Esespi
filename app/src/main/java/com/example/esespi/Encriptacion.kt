package com.example.esespi

import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class Encriptacion {
    fun convertirSHA256(password: String): String {
        var md: MessageDigest? = null

        try {
            md = MessageDigest.getInstance("SHA-256")
        } catch (e: NoSuchAlgorithmException) {
            println(e.toString())
            return ""
        }

        val hash: ByteArray = md.digest(password.toByteArray())
        val sb = StringBuilder()

        for (b in hash) {
            sb.append(String.format("%02x", b))
        }

        return sb.toString()
    }
}