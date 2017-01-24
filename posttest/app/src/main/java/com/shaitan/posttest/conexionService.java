package com.shaitan.posttest;

        import okhttp3.ResponseBody;
        import retrofit2.Call;
        import retrofit2.Retrofit;
        import retrofit2.converter.gson.GsonConverterFactory;
        import retrofit2.http.Field;
        import retrofit2.http.FormUrlEncoded;
        import retrofit2.http.POST;

public interface conexionService {
    String login_url = "https://192.168.0.15:8080/gss/login2.php/";
    @FormUrlEncoded
    @POST("IOT.php/info1")
    Call<ResponseBody> repoContributors(
            @Field("user") String user,
            @Field("Passwd") String Passwd
            //,
            //@Field("Token") String Token
    );

    @FormUrlEncoded
    @POST("IOT.php/info1")
    Call<ResponseBody> getIMEI(
            @Field("user") String user,
            @Field("Passwd") String Passwd
            //,
            //@Field("Token") String Token
    );

    public static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(login_url)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

}
