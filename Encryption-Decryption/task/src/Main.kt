package encryptdecrypt

import java.io.File
import kotlin.math.abs

fun main(args: Array<String>) {
    val params = mutableMapOf<String, String>()
    if (args.size % 2 != 0) throw Exception("Error. Wrong parameters")

    for (i in args.indices step 2) {
        when (args[i]) {
            "-mode" -> params["-mode"] = args[i + 1]
            "-key" -> params["-key"] = args[i + 1]
            "-data" -> params["-data"] = args[i + 1]
            "-in" -> params["-in"] = args[i + 1]
            "-out" -> params["-out"] = args[i + 1]
            "-alg" -> params["-alg"] = args[i + 1]
        }
    }
    val operation = params["-mode"] ?: "enc"
    var message = params["-data"]?.toByteArray() ?: "".toByteArray()
    val key = params["-key"]?.toInt() ?: 0
    val algorithm = params["-alg"] ?: "shift"

    if (message.isEmpty() && params["-in"] != null) {
        if (File(params["-in"] ?: "").exists())
            message = File(params["-in"] ?: "").readText().toByteArray()
        else throw Exception("Error. Input file doesn't exist")
    }

    val str = if (algorithm == "shift") cesarAlgorithm(message, operation, key)
    else unicodeAlgorithm(message, operation, key)

    if (params["-out"] != null) {
        File(params["-out"] ?: "").writeText(str)
    } else {
        println(str)
    }
}

private fun cesarAlgorithm(message: ByteArray, operation: String, shift: Int): String {
    val sign = if (operation == "dec") -1 else 1

    return buildString {
        for (i in message.decodeToString()) {
            if (i.isLetter()) {
                var max = 0
                var min = 0
                if (i.code in 97..122) {
                    max = 122
                    min = 97
                } else {
                    max = 90
                    min = 65
                }
                val letter = i.code + shift * sign
                val shiftedLetter = if (letter > max) {
                    min + abs(max - letter) - 1
                } else if (sign == 1) letter else {
                    if (letter < min)
                        max - abs(min - letter) + 1
                    else letter
                }
                append(shiftedLetter.toChar())
            } else append(i)
        }
    }
}

private fun unicodeAlgorithm(message: ByteArray, operation: String, shift: Int): String {
    val str = buildString {
        for (i in message.indices) {
            append(
                (message[i] + if (operation == "dec") {
                    shift * -1
                } else {
                    shift
                }).toChar()
            )
        }
    }
    return str
}