package com.github.kutyrev.rushydroinventorisation

import retrofit2.Retrofit
import okhttp3.OkHttpClient.Builder
import okhttp3.*
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private var retrofit: Retrofit? = null
    val wslogger = WsLoggerInterceptor()

    /*  var proxyAuthenticator: Authenticator = object : Authenticator {
          @Throws(IOException::class)
          override fun authenticate(route: Route?, response: Response): Request? {
              val credential: String = Credentials.basic("CORP\\KutyrevDD", "Alpha2000")
              return response.request.newBuilder()
                  .header("Proxy-Authorization", credential)
                  .build()
          }
      }*/

    /*val proxy : Proxy = Proxy(Proxy.Type.HTTP, InetSocketAddress("inetm9.corp.gidroogk.com", 8080))*/

    var client: OkHttpClient = Builder()
        .addInterceptor(BasicAuthInterceptor("", ""))
        .addInterceptor(wslogger.logging)
        // .proxy(proxy)
        // .proxyAuthenticator(proxyAuthenticator)
        //.connectTimeout(15, TimeUnit.SECONDS)
        .build()

    fun getClient(baseUrl: String): Retrofit {
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit!!
    }

    fun initSettings(login : String, password : String) {
        client = Builder()
            .addInterceptor(BasicAuthInterceptor(login, password))
            .addInterceptor(wslogger.logging)
            // .proxy(proxy)
            // .proxyAuthenticator(proxyAuthenticator)
            .build()

        retrofit = null
    }

    fun resetRetrofitClient(){
        retrofit = null
    }
}

object Common {
    //http://10.101.104.124/
    var base_url = ""
        set(value){
            field = value
            RetrofitClient.resetRetrofitClient()
        }
    var db_name = ""
    val retrofitService: RetrofitServices
        get() = RetrofitClient.getClient(base_url).create(RetrofitServices::class.java)


    val GOODS_DOC_TYPE = "Goods"
    val FIXED_ASSETS_DOC_TYPE = "Fixed assets"
}