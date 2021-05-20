package com.example.currencyconverter

import android.os.Parcel
import android.os.Parcelable

class Valute(var NumCode: Int,
             var CharCode: String?,
             var Nominal: Int,
             var Name: String?,
             var Value: Double): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readDouble()
    )

    constructor() : this(0, null, 0, null, 0.0)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(NumCode)
        parcel.writeString(CharCode)
        parcel.writeInt(Nominal)
        parcel.writeString(Name)
        parcel.writeDouble(Value)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Valute> {
        override fun createFromParcel(parcel: Parcel): Valute {
            return Valute(parcel)
        }

        override fun newArray(size: Int): Array<Valute?> {
            return arrayOfNulls(size)
        }
    }
}