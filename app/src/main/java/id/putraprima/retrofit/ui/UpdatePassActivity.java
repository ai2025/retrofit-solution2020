package id.putraprima.retrofit.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import id.putraprima.retrofit.R;
import id.putraprima.retrofit.api.helper.ServiceGenerator;
import id.putraprima.retrofit.api.models.ApiError;
import id.putraprima.retrofit.api.models.DataApp;
import id.putraprima.retrofit.api.models.ErrorUtils;
import id.putraprima.retrofit.api.models.PasswordRequest;
import id.putraprima.retrofit.api.models.PasswordResponse;
import id.putraprima.retrofit.api.services.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdatePassActivity extends AppCompatActivity {
    private EditText etPass, etCPass;
    private DataApp sess;
    private String pass, cpass, token, exp;
    private PasswordRequest passwordRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_pass);
        etPass = findViewById(R.id.edtPass);
        etCPass = findViewById(R.id.edtCPass);
        sess = new DataApp(this);
        token = sess.getTokenType() + " " + sess.getToken();
    }

    public void handleUpdatePassProses(View view) {
        pass = etPass.getText().toString();
        cpass = etCPass.getText().toString();
        updatePass();
    }

    public void updatePass() {
        passwordRequest = new PasswordRequest(pass, cpass);
        ApiInterface service = ServiceGenerator.createService(ApiInterface.class);
        Call<PasswordResponse> call = service.doUpdatePassword(token, passwordRequest);
        call.enqueue(new Callback<PasswordResponse>() {
            @Override
            public void onResponse(Call<PasswordResponse> call, Response<PasswordResponse> response) {
                if (response.code() == 200) {
                    Toast.makeText(UpdatePassActivity.this, "Update Successfull", Toast.LENGTH_SHORT).show();
                    finishAffinity();
                    Intent i = new Intent(UpdatePassActivity.this, MainActivity.class);
                    startActivity(i);
                } else {
                    ApiError error = ErrorUtils.parseError(response);
                    int i = 0;
                    String msgPass = "";
                    while (i < error.getError().getPassword().size()) {
                        msgPass += error.getError().getPassword().get(i) + "\n";
                        i++;
                    }
                    etPass.setError(msgPass);
//                    Toast.makeText(UpdatePassActivity.this, "Update Error: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PasswordResponse> call, Throwable t) {
                Toast.makeText(UpdatePassActivity.this, "update gagal", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
