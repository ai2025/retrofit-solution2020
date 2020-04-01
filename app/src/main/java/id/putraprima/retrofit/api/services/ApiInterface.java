package id.putraprima.retrofit.api.services;


import id.putraprima.retrofit.api.models.AppVersion;
import id.putraprima.retrofit.api.models.Envelope;
import id.putraprima.retrofit.api.models.LoginRequest;
import id.putraprima.retrofit.api.models.LoginResponse;
import id.putraprima.retrofit.api.models.PasswordRequest;
import id.putraprima.retrofit.api.models.PasswordResponse;
import id.putraprima.retrofit.api.models.ProfileRequest;
import id.putraprima.retrofit.api.models.ProfileResponse;
import id.putraprima.retrofit.api.models.RegisterRequest;
import id.putraprima.retrofit.api.models.RegisterResponse;
import id.putraprima.retrofit.api.models.UserInfo;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;

public interface ApiInterface{
    @GET("/")
    Call<AppVersion> getAppVersion();
    @POST("/api/auth/login")
    Call<LoginResponse> doLoginReq(@Body LoginRequest loginRequest);
    @POST("/api/auth/register")
    Call<Envelope<RegisterResponse>> doRegister(@Body RegisterRequest registerRequest);
    @GET("/api/auth/me")
    Call<Envelope<UserInfo>> me();
    @PATCH("/api/account/profile")
    Call<ProfileResponse> doUpdateProfile(@Header("Authorization") String token, @Body ProfileRequest req);
    @PATCH("/api/account/password")
    Call<PasswordResponse> doUpdatePassword(@Header("Authorization") String token, @Body PasswordRequest req);
}
