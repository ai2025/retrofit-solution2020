package id.putraprima.retrofit.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import net.khirr.android.privacypolicy.PrivacyPolicyDialog;

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

    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PrivacyPolicyDialog dialog = new PrivacyPolicyDialog(this,
                "https://localhost/terms",
                "https://localhost/privacy");

        dialog.addPoliceLine("This application uses a unique user identifier for advertising purposes, it is shared with third-party companies.");
        dialog.addPoliceLine("This application sends error reports, installation and send it to a server of the Fabric.io company to analyze and process it.");
        dialog.addPoliceLine("This application requires internet access and must collect the following information: Installed applications and history of installed applications, ip address, unique installation id, token to send notifications, version of the application, time zone and information about the language of the device.");
        dialog.addPoliceLine("All details about the use of data are available in our Privacy Policies, as well as all Terms of Service links below.");
        dialog.setTitleTextColor(Color.parseColor("#222222"));
        dialog.setAcceptButtonColor(ContextCompat.getColor(this, R.color.colorAccent));

        //  Title
        dialog.setTitle("Terms of Service");

        //  {terms}Terms of Service{/terms} is replaced by a link to your terms
        //  {privacy}Privacy Policy{/privacy} is replaced by a link to your privacy policy
        dialog.setTermsOfServiceSubtitle("If you click on {accept}, you acknowledge that it makes the content present and all the content of our {terms}Terms of Service{/terms} and implies that you have read our {privacy}Privacy Policy{privacy}.");

//        final Intent intent = new Intent(this, SecondActivity.class);

        dialog.setOnClickListener(new PrivacyPolicyDialog.OnClickListener() {
            @Override
            public void onAccept(boolean isFirstTime) {
                Log.e("MainActivity", "Policies accepted");
//                startActivity(intent);
//                finish();
            }

            @Override
            public void onCancel() {
                Log.e("MainActivity", "Policies not accepted");
                finish();
            }
        });

        dialog.show();

//        MobileAds.initialize(this, "ca-app-pub-3940256099942544/1033173712");
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
                Intent intent = new Intent(MainActivity.this, RecipeActivity.class);
//                mInterstitialAd.loadAd(new AdRequest.Builder().build());
                startActivity(intent);
//                finish();
            }
        });

//        loadAds();

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
//        loadAds();
//        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
//        Intent intent = new Intent(this, RecipeActivity.class);
//        startActivity(intent);
    }

    public void loadAds() {
        MobileAds.initialize(this, "ca-app-pub-3940256099942544/1033173712");
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        mInterstitialAd.show();

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
//                mInterstitialAd.loadAd(new AdRequest.Builder().build());
                Intent intent = new Intent(MainActivity.this, RecipeActivity.class);
                startActivity(intent);
//                finish();
            }
        });
    }
}
