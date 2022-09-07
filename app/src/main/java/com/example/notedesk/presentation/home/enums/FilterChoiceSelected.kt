package com.example.notedesk.presentation.home.enums

import android.os.Parcel
import android.os.Parcelable


data class FilterChoiceSelected(val isFavorite:Boolean,val isPriority_red: Boolean,val isPriority_yellow: Boolean,val isPriority_green:Boolean) :
    Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),


    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeByte(if (isFavorite) 1 else 0)
        parcel.writeByte(if (isPriority_red) 1 else 0)
        parcel.writeByte(if (isPriority_yellow) 1 else 0)
        parcel.writeByte(if (isPriority_green) 1 else 0)

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FilterChoiceSelected> {
        override fun createFromParcel(parcel: Parcel): FilterChoiceSelected {
            return FilterChoiceSelected(parcel)
        }

        override fun newArray(size: Int): Array<FilterChoiceSelected?> {
            return arrayOfNulls(size)
        }
    }
}

