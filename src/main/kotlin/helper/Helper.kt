package shiroi_plugin.huya.helper

import okhttp3.*
import java.util.concurrent.TimeUnit

class Helper {

    //虎牙地址
    val url:String = "https://www.huya.com/"

    companion object {
        val okHttpClient: OkHttpClient by lazy {
            OkHttpClient
                .Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(3, TimeUnit.SECONDS)
                .writeTimeout(3, TimeUnit.SECONDS)
                .build()
        }
    }

    fun add(id: String): Call {
        return okHttpClient.newCall(Request.Builder().get().url(this.url + id).build())
    }

    fun get(id: String): String? {
        try {
            okHttpClient.newCall(Request.Builder().get().url(this.url + id).build()).execute().use { response -> return response.body!!.string() }
        } catch (_:Exception){}
        return null
    }
}