package id.putraprima.retrofit.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import id.putraprima.retrofit.R;
import id.putraprima.retrofit.api.helper.ServiceGenerator;
import id.putraprima.retrofit.api.models.PasswordRequest;
import id.putraprima.retrofit.api.models.PasswordResponse;
import id.putraprima.retrofit.api.models.Session;
import id.putraprima.retrofit.api.services.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdatePassActivity extends AppCompatActivity {
    private EditText etPass, etCPass;
    private Session sess;
    private String pass, cpass, token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_pass);
        etPass = findViewById(R.id.edtPass);
        etCPass = findViewById(R.id.edtCPass);
        sess = new Session(this);
        token = sess.getTokenType() + " " + sess.getToken();
    }

    public void handleUpdatePassProses(View view) {
        pass = etPass.getText().toString();
        cpass = etCPass.getText().toString();
        boolean cek = pass.equals("") && cpass.equals("");
        boolean confirm = pass.equals(cpass);
        boolean dataLength = pass.length() < 8 && cpass.length() < 8;

        if (cek) {
            etPass.setError("diisi ya");
            etCPass.setError("diisi ya");
            if (confirm) {
                etPass.setError("ngga sama");
                etCPass.setError("ngga sama");
            }
            if (dataLength) {
                etPass.setError("minim 8");
                etCPass.setError("minim 8");
            }
        } else {
            updatePass();
        }
    }

    public void updatePass() {
        ApiInterface service = ServiceGenerator.createService(ApiInterface.class);
        Call<PasswordResponse> call = service.doUpdatePassword(token, new PasswordRequest(pass, cpass));
        call.enqueue(new Callback<PasswordResponse>() {
            @Override
            public void onResponse(Call<PasswordResponse> call, Response<PasswordResponse> response) {
                if (response.body() != null) {
                    Toast.makeText(UpdatePassActivity.this, "Update Successfull", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(UpdatePassActivity.this, MainActivity.class);
                    startActivity(i);
                } else {
                    Toast.makeText(UpdatePassActivity.this, "Update Error: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PasswordResponse> call, Throwable t) {
                Toast.makeText(UpdatePassActivity.this, "Data gagal", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
