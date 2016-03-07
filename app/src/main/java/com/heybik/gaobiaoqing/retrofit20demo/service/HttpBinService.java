package com.heybik.gaobiaoqing.retrofit20demo.service;

import com.heybik.gaobiaoqing.retrofit20demo.model.HttpBinResponse;
import com.heybik.gaobiaoqing.retrofit20demo.model.JsonData;
import com.squareup.okhttp.ResponseBody;

import java.io.File;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Streaming;

/**
 * Created by gaobiaoqing on 16-2-25.
 */
public interface HttpBinService {

    //post encoded form
    @FormUrlEncoded
    @POST("post")
    Call<HttpBinResponse> httpbinPost(@Field("filed1") String filed1,@Field("filed2") String filed2);

    @Streaming
    @GET("image/png")
    Call<ResponseBody> httpbinImage();

    @POST("post")
    Call<HttpBinResponse> httpbinPostJson(@Body JsonData jsonData);

}
