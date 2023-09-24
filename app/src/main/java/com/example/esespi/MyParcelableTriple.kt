package com.example.esespi

import android.os.Parcel
import android.os.Parcelable

data class MyParcelableTriple(val first: String?, val second: String?, val third: ByteArray?) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.createByteArray()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(first)
        parcel.writeString(second)
        parcel.writeByteArray(third)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MyParcelableTriple> {
        override fun createFromParcel(parcel: Parcel): MyParcelableTriple {
            return MyParcelableTriple(parcel)
        }

        override fun newArray(size: Int): Array<MyParcelableTriple?> {
            return arrayOfNulls(size)
        }
    }
}