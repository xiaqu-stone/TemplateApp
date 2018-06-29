package com.stone.templateapp.http

import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object Client {
    const val TAG = "Client"
    private var apiService: ApiService? = null

    fun init() {
        apiService = createClient(ApiHelper.TIME_OUT_NORMAL.toLong())
    }

    fun createClient(timeout: Long): ApiService {
        val retrofit = Retrofit.Builder()
                .baseUrl(HostConfig.API_HOST)
                .client(ApiHelper.createOkHttpClient(timeout))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        return retrofit.create(ApiService::class.java)
    }

    fun createStartPageClient(): ApiService {
        return createClient(ApiHelper.TIME_OUT_START_PAGE.toLong())
    }

    fun getApiService(): ApiService {
        if (apiService == null) {
            init()
        }
        return apiService!!
    }
}

