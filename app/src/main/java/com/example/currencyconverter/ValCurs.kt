package com.example.currencyconverter

class ValCurs {
    var Valute: List<Valute>? = null
    var Date: String? = null
    var name: String? = null
    var text: String? = null
    fun getByCharCode(charCode: String): Valute? {
        for (i in Valute!!)
            if (i.CharCode!!.equals(charCode, ignoreCase = true)) return i
        return null
    }
}