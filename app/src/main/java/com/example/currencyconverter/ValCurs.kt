package com.example.currencyconverter

import java.util.*

class ValCurs {
    var Valute: List<Valute>? = null
    var Date: String? = null
    var name: String? = null
    var text: String? = null
    fun getByCharCode(charCode: String): Valute? {
        for (i in Valute!!)
            if (i.CharCode!!.toUpperCase(Locale.ROOT) == charCode.toUpperCase(Locale.ROOT)) return i
        return null
    }
}