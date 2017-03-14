package com.utlife.core.http.okhttp;

import android.text.TextUtils;


import java.io.IOException;
import java.net.URLEncoder;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by sll on 2016/2/23.
 */
public class CookieInterceptor implements Interceptor {


  private String token;

  public CookieInterceptor(String token) {
    this.token = token;
  }

  @Override public Response intercept(Chain chain) throws IOException {
    Request original = chain.request();
    if (!TextUtils.isEmpty(token)) {
      Request request = original.newBuilder()
          .addHeader("token", token)
          .build();
      return chain.proceed(request);
    }
    return chain.proceed(original);
  }
}
