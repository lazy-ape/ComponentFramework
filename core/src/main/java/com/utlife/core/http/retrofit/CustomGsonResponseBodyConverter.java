package com.utlife.core.http.retrofit;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.utlife.core.bean.BaseResponseData;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Converter;


/**
 * Created by lazy on 2017/3/3.
 */
final class CustomGsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {
    private final Gson gson;
    private final TypeAdapter<T> adapter;

    CustomGsonResponseBodyConverter(Gson gson, TypeAdapter<T> adapter) {
        this.gson = gson;
        this.adapter = adapter;
    }
    @Override
    public T convert(ResponseBody value) throws IOException {
            String data = value.string();
            BaseResponseData baseResponseData = gson.fromJson(data, BaseResponseData.class);
            if (!baseResponseData.isSuccess()) {
                ExceptionHandle.ResponseThrowable throwable = new ExceptionHandle.ResponseThrowable(
                        new Throwable(baseResponseData.getErrorMsg()), baseResponseData.getErrorCode());
                throwable.message = baseResponseData.getErrorMsg();
                throwable.code = baseResponseData.getErrorCode();
                throwable.originalData = data;
                throw throwable;
            }
            MediaType contentType = value.contentType();
            Charset charset = contentType != null ? contentType.charset(Charset.forName("utf-8")) : Charset.forName("utf-8");
            InputStream inputStream = new ByteArrayInputStream(data.getBytes());
            Reader reader = new InputStreamReader(inputStream, charset);
            JsonReader jsonReader = gson.newJsonReader(reader);

            try {
                return adapter.read(jsonReader);
            }catch (Exception e){
                throw new ExceptionHandle.ResponseThrowable(new Throwable("解析错误"), ExceptionHandle.ERROR.PARSE_ERROR);
            } finally {
                value.close();
            }
    }
}
