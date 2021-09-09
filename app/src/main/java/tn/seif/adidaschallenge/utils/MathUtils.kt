package tn.seif.adidaschallenge.utils

import java.math.BigDecimal

fun Double.round(decimalPlace: Int): Double {
    return BigDecimal.valueOf(this).setScale(decimalPlace, BigDecimal.ROUND_HALF_UP).toDouble()
}