package com.example.ervin.first_iot_login.Interfaces;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by Ipinnovatech on 30/09/16.
 */

public interface Productos {
    @FormUrlEncoded
    @POST("IOT.php/info3")
    Call<ResponseBody> repoContributors(
            @Field("ProductosSave") String ProductosSave,
            @Field("AllProductos") String AllProductos

    );


    public static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://52.32.74.24/IOT/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}
