package com.github.kutyrev.rushydroinventorisation

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


@Parcelize
data class DocInv(

    @SerializedName("docType")
    var docType : String,

    @SerializedName("docNum")
    var docNumber : String,

    @SerializedName("goods")
    var goods: List<Good> = mutableListOf()

):Parcelable