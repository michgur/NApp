package com.klmn.napp.util

import okhttp3.Interceptor

/*
* creates an interceptor that adds query parameters
* */
fun parameterInterceptor(vararg parameters: Pair<String, String?>) = { chain: Interceptor.Chain ->
    val url = chain.request().url().newBuilder()
    for (p in parameters) url.addQueryParameter(p.first, p.second)
    chain.proceed(
        chain.request().newBuilder().url(url.build()).build()
    )
}