package com.github.kutyrev.rushydroinventorisation

import retrofit2.Call
import retrofit2.http.*


interface RetrofitServices {
    @GET("{db_name}/hs/RusHydroInv/getInvDocument")
    fun getInvDocument(@Path("db_name") db_name : String, @Query("DocType") docType : String, @Query("DocNum") docNum : String): Call<DocInv>

    @POST("{db_name}/hs/RusHydroInv/updateInvDocument")
    fun updateInvDocument(@Path("db_name") db_name : String, @Body docInv : DocInv): Call<Boolean>
}