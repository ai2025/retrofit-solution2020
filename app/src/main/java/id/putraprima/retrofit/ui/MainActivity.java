package id.putraprima.retrofit.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import id.putraprima.retrofit.R;
import id.putraprima.retrofit.api.helper.ServiceGenerator;
import id.putraprima.retrofit.api.models.ApiError;
import id.putraprima.retrofit.api.models.ErrorUtils;
import id.putraprima.retrofit.api.models.LoginRequest;
import id.putraprima.retrofit.api.models.LoginResponse;
import id.putraprima.retrofit.api.services.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    //    SplashActivity appNameVer;
    Context context;
    TextView txtApp, txtVer;
    Button loginButton, registerButton;
    EditText edtEmail, edtPassword;
    String email, password, msgEmail, msgPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();
        loginButton = findViewById(R.id.btnLogin);
        registerButton = findViewById(R.id.bntToRegister);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        txtApp = findViewById(R.id.mainTxtAppName);
        txtVer = findViewById(R.id.mainTxtAppVersion);
        getAppNameVer();
    }

//    indikator atau feedback kepada user bahwa ada kondisi yang salah di dalam program

    public void getAppNameVer() {
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(context);
        txtApp.setText(preference.getString("appName", "default"));
        txtVer.setText(preference.getString("appVersion", "default"));
    }

    public void handleLoginClick(View view) {
        email = edtEmail.getText().toString();
        password = edtPassword.getText().toString();
        doLogin();
    }

    private void doLogin() {
        ApiInterface service = ServiceGenerator.createService(ApiInterface.class);
        LoginRequest loginRequest = new LoginRequest(email, password);
        Call<LoginResponse> call = service.doLoginReq(loginRequest);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.code() == 200) {
                    Toast.makeText(MainActivity.this, "Connected :)", Toast.LENGTH_SHORT).show();
                    SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = preference.edit();
                    editor.putString("token", response.body().getToken());
                    editor.apply();
                    Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
                    startActivity(i);
                } else {
                    ApiError error = ErrorUtils.parseError(response);
                    msgEmail = " ";
                    msgPass = " ";
                    int i = 0;
                    if (error.getError().getEmail() != null && error.getError().getPassword() != null) {
                        while (i < error.getError().getEmail().size()) {
                            msgEmail += error.getError().getEmail().get(i);
                            i++;
                        }
                        edtEmail.setError(msgEmail);
                        i = 0;
                        while (i < error.getError().getPassword().size()) {
                            msgPass += error.getError().getPassword().get(i);
                            i++;
                        }
                        edtPassword.setError(msgPass);
                    } else if (error.getError().getEmail() != null) {
                        if (error.getError().getEmail().get(0).equals("These credentials do not match our records.")) {
                            Toast.makeText(MainActivity.this, error.getError().getEmail().get(0), Toast.LENGTH_SHORT).show();
                        } else {
                            i = 0;
                            while (i < error.getError().getEmail().size()) {
                                msgEmail += error.getError().getEmail().get(i);
                                i++;
                            }
                            edtEmail.setError(msgEmail);
                        }
                    } else if (error.getError().getPassword() != null) {
                        i = 0;
                        while (i < error.getError().getPassword().size()) {
                            msgPass += error.getError().getPassword().get(i);
                            i++;
                        }
                        edtPassword.setError(msgPass);
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Connection failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void handleRegisterClick(View view) {
        Intent i = new Intent(this, RegisterActivity.class);
        startActivity(i);
    }

    public void handleRecipe(View view) {
        Intent intent = new Intent(this, RecipeActivity.class);
        startActivity(intent);
    }
}
