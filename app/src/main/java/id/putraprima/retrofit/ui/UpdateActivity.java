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
import id.putraprima.retrofit.api.models.ProfileRequest;
import id.putraprima.retrofit.api.models.ProfileResponse;
import id.putraprima.retrofit.api.services.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateActivity extends AppCompatActivity {
    private ProfileRequest profileReq;
    private DataApp sess;
    private EditText tilNama, tilEmail;
    private String email, name, token;
//    TextInputEditText tilNama, tilEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        sess = new DataApp(this);
        tilNama = findViewById(R.id.til_nama);
        tilEmail = findViewById(R.id.til_email);
        token = sess.getTokenType() + " " + sess.getToken();
    }

    public void handleUpdateProcess(View view) {
        name = tilNama.getText().toString();
        email = tilEmail.getText().toString();
        updateProf();
    }

    public void updateProf() {
        profileReq = new ProfileRequest(email, name);
        ApiInterface service = ServiceGenerator.createService(ApiInterface.class);
        Call<ProfileResponse> call = service.doUpdateProfile(token, profileReq);
        call.enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                if (response.code() == 200) {
                    Toast.makeText(UpdateActivity.this, "Update Successfull", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(UpdateActivity.this, ProfileActivity.class);
                    startActivity(i);
                } else {
                    ApiError error = ErrorUtils.parseError(response);
                    String msgEmail = "", msgName = "";
                    int i = 0;
                    if (error.getError().getEmail() != null && error.getError().getName() != null) {
                        while (i < error.getError().getEmail().size()) {
                            msgEmail += error.getError().getEmail().get(i);
                            i++;
                        }
                        tilEmail.setError(msgEmail);
                        i = 0;
                        while (i < error.getError().getName().size()) {
                            msgName += error.getError().getName().get(i);
                            i++;
                        }
                        tilNama.setError(msgName);
                    } else if (error.getError().getName() != null) {
                        while (i < error.getError().getName().size()) {
                            msgName += error.getError().getName().get(i);
                            i++;
                        }
                        tilNama.setError(msgName);
                    } else if (error.getError().getEmail() != null) {
                        while (i < error.getError().getEmail().size()) {
                            msgEmail += error.getError().getEmail().get(i);
                            i++;
                        }
                        tilEmail.setError(msgEmail);
                    }
                }
            }

            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {
                Toast.makeText(UpdateActivity.this, "Error Req", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
