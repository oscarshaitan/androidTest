package com.example.ervin.first_iot_login.Interfaces;

//import java.util.List;

//import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by Ipinnovatech on 19/09/16.
 */
public interface conexionService {
    @FormUrlEncoded
    @POST("IOT.php/info1")
    Call<ResponseBody> repoContributors(
    @Field("user") String user,
    @Field("Passwd") String Passwd,
    @Field("Token") String Token
    );

    public static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://52.32.74.24/IOT/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

        }