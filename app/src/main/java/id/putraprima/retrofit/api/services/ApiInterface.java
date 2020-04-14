package id.putraprima.retrofit.api.services;


import java.util.List;
import java.util.Map;

import id.putraprima.retrofit.api.models.AppVersion;
import id.putraprima.retrofit.api.models.Envelope;
import id.putraprima.retrofit.api.models.LoginRequest;
import id.putraprima.retrofit.api.models.LoginResponse;
import id.putraprima.retrofit.api.models.PasswordRequest;
import id.putraprima.retrofit.api.models.PasswordResponse;
import id.putraprima.retrofit.api.models.ProfileRequest;
import id.putraprima.retrofit.api.models.ProfileResponse;
import id.putraprima.retrofit.api.models.RecipeResponse;
import id.putraprima.retrofit.api.models.RegisterRequest;
import id.putraprima.retrofit.api.models.RegisterResponse;
import id.putraprima.retrofit.api.models.UserInfo;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;

public interface ApiInterface{
    @GET("/")
    Call<AppVersion> getAppVersion();

    @POST("/api/auth/login")
    Call<LoginResponse> doLoginReq(@Body LoginRequest loginRequest);

    @POST("/api/auth/register")
    Call<Envelope<RegisterResponse>> doRegister(@Body RegisterRequest registerRequest);

    @GET("/api/auth/me")
    Call<Envelope<UserInfo>> me(@Header("Authorization") String token);

    @PATCH("/api/account/profile")
    Call<ProfileResponse> doUpdateProfile(@Header("Authorization") String token, @Body ProfileRequest req);

    @PATCH("/api/account/password")
    Call<PasswordResponse> doUpdatePassword(@Header("Authorization") String token, @Body PasswordRequest req);

    @GET("/api/recipe")
    Call<Envelope<List<RecipeResponse>>> getRecipeNext(@Query("page") int page);

    @GET("/api/recipe")
    Call<Envelope<List<RecipeResponse>>> getRecipe();

    @Multipart
    @POST("api/recipe")
    Call<Envelope<List<ResponseBody>>> uploadImg(@PartMap Map<String, RequestBody> text,
                                                 @Part MultipartBody.Part photo);
}
