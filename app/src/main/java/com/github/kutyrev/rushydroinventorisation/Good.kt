package com.github.kutyrev.rushydroinventorisation

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Good(
    @SerializedName("name") val name: String?,
    @SerializedName("accountNumber") val accountNumber: Int?,
    @SerializedName("factNumber") var factNumber: Int?,
    @SerializedName("barCode") val barCode: String?,
) : Parcelable