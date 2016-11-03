package com.example.ervin.first_iot_login.Interfaces;

        import okhttp3.ResponseBody;
        import retrofit2.Call;
        import retrofit2.Retrofit;
        import retrofit2.converter.gson.GsonConverterFactory;
        import retrofit2.http.Field;
        import retrofit2.http.FormUrlEncoded;
        import retrofit2.http.POST;

/**
 * Created by Ipinnovatech on 20/09/16.
 */
public interface registerService {
        @FormUrlEncoded
        @POST("IOT.php/info2")
        Call<ResponseBody> repoContributors(
                @Field("user") String user,
                @Field("Passwd") String Passwd,
                @Field("Nombre") String Nombre,
                @Field("Edad") String Edad
        );


        public static final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://52.32.74.24/IOT/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

}