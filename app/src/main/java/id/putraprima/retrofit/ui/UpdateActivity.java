package id.putraprima.retrofit.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import id.putraprima.retrofit.R;
import id.putraprima.retrofit.api.helper.ServiceGenerator;
import id.putraprima.retrofit.api.models.ProfileRequest;
import id.putraprima.retrofit.api.models.ProfileResponse;
import id.putraprima.retrofit.api.models.Session;
import id.putraprima.retrofit.api.services.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateActivity extends AppCompatActivity {
    //    public ProfileRequest profileReq;
    private Session sess;
    private EditText tilNama, tilEmail;
    private String email, name, token;
//    TextInputEditText tilNama, tilEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        sess = new Session(this);
        tilNama = findViewById(R.id.til_nama);
        tilEmail = findViewById(R.id.til_email);
        token = sess.getTokenType() + " " + sess.getToken();
    }

    public void handleUpdateProcess(View view) {
        name = tilNama.getText().toString();
        email = tilEmail.getText().toString();
        boolean cek = name.equals("") && email.equals("");
        boolean cekEmail = !Patterns.EMAIL_ADDRESS.matcher(email).matches();
//        if (cek || cekEmail){
        if (cek || cekEmail) {
            tilNama.setError("Namanya diisi dong");
            tilEmail.setError("Emailnya salah");
        } else {
            updateProf();
        }
//            if (cekEmail){
//                tilEmail.setError("Emailnya salah");
//            }
    }

    public void updateProf() {
//        profileReq = new ProfileRequest(name, email);
        ApiInterface service = ServiceGenerator.createService(ApiInterface.class);
        System.out.println("email : " + email);
        System.out.println("name : " + name);
        Call<ProfileResponse> call = service.doUpdateProfile(token, new ProfileRequest(email, name));
        call.enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                if (response.body() != null) {
//                    final AlertDialog.Builder builder = new AlertDialog.Builder(UpdateActivity.this);
//                    Toast.makeText(UpdateActivity.this, response.body().getName(), Toast.LENGTH_SHORT).show();
//                    Toast.makeText(UpdateActivity.this, response.body().getEmail(), Toast.LENGTH_SHORT).show();
                    Toast.makeText(UpdateActivity.this, "Update Successfull", Toast.LENGTH_SHORT).show();

                    Intent i = new Intent(UpdateActivity.this, ProfileActivity.class);
                    startActivity(i);
                } else {
                    Toast.makeText(UpdateActivity.this, "Update Error: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {
                Toast.makeText(UpdateActivity.this, "Error Req", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
