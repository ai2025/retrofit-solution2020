package id.putraprima.retrofit.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import id.putraprima.retrofit.R;
import id.putraprima.retrofit.api.helper.ServiceGenerator;
import id.putraprima.retrofit.api.models.Envelope;
import id.putraprima.retrofit.api.models.UserInfo;
import id.putraprima.retrofit.api.services.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {
    public Context context;
    private TextView tvName, tvEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        context = getApplicationContext();
        tvName = findViewById(R.id.Name);
        tvEmail = findViewById(R.id.Email);
        getDatas();
    }

    public void getDatas() {
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(context);
        ApiInterface service = ServiceGenerator.createService(ApiInterface.class, "Bearer " + preference.getString("token", null));
        Call<Envelope<UserInfo>> call = service.me();
        call.enqueue(new Callback<Envelope<UserInfo>>() {
            @Override
            public void onResponse(Call<Envelope<UserInfo>> call, Response<Envelope<UserInfo>> response) {
                tvName.setText(response.body().getData().getName());
                tvEmail.setText(response.body().getData().getEmail());
            }

            @Override
            public void onFailure(Call<Envelope<UserInfo>> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Gagal Koneksi Server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void handleUpdateClick(View view) {
        Intent i = new Intent(ProfileActivity.this, UpdateActivity.class);
        startActivity(i);
    }

    public void handleUpdatePassClick(View view) {
        Intent i = new Intent(ProfileActivity.this, UpdatePassActivity.class);
        startActivity(i);
    }

    public void handleGoToRV(View view) {
        Intent i = new Intent(ProfileActivity.this, RecipeActivity.class);
        startActivity(i);
    }
}
