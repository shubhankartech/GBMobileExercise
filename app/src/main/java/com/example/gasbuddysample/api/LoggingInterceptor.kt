package com.example.gasbuddysample.api

import okhttp3.Interceptor
import java.io.IOException
import java.util.logging.Logger

// this class is ued only for debugging purpose
class LoggingInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain):okhttp3.Response {
        val request = chain.request()

        val t1 = System.nanoTime()
        Logger.getLogger("interceptor").info(
            String.format(
                "Sending request %s on %s%n%s",
                request.url(), chain.connection(), request.headers()
            )
        )

        val response = chain.proceed(request)

        val t2 = System.nanoTime()
        Logger.getLogger("interceptor").info(
            String.format(
                "Received response for %s in %.1fms%n%s",
                response.request().url(), (t2 - t1) / 1e6, response.headers()
            )
        )

        return response
    }
}