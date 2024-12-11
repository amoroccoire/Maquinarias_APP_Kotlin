package com.example.maquinariasapp.utils

import kotlinx.coroutines.runBlocking

fun generateNextCodigo(prefix: String, getLastCodigo: suspend () -> String?): String {
    val lastCodigo = runBlocking { getLastCodigo() }
    val nextNumber = if (lastCodigo != null) {
        lastCodigo.removePrefix(prefix).toInt() + 1
    } else {
        1
    }
    return "$prefix${String.format("%04d", nextNumber)}"
}