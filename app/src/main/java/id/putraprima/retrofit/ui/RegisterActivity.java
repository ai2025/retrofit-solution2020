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
import id.putraprima.retrofit.api.models.Envelope;
import id.putraprima.retrofit.api.models.ErrorUtils;
import id.putraprima.retrofit.api.models.RegisterRequest;
import id.putraprima.retrofit.api.models.RegisterResponse;
import id.putraprima.retrofit.api.services.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    EditText mUsername, mEmail, mPassword, mPasswordConfirm;
    private RegisterRequest registerRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mUsername = findViewById(R.id.username);
        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mPasswordConfirm = findViewById(R.id.password_confirm);
    }

    public void register() {
        ApiInterface service = ServiceGenerator.createService(ApiInterface.class);
        Call<Envelope<RegisterResponse>> call = service.doRegister(registerRequest);
        call.enqueue(new Callback<Envelope<RegisterResponse>>() {

            @Override
            public void onResponse(Call<Envelope<RegisterResponse>> call, Response<Envelope<RegisterResponse>> response) {
                if (response.code() == 201) {
                    Toast.makeText(RegisterActivity.this, response.body().getData().getName(), Toast.LENGTH_SHORT).show();
                    Toast.makeText(RegisterActivity.this, response.body().getData().getEmail(), Toast.LENGTH_SHORT).show();
                    Toast.makeText(RegisterActivity.this, "Register Successfull", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    ApiError error = ErrorUtils.parseError(response);
                    int i = 0;
                    if (error.getError().getName() != null) {
                        String msgName = "";
                        while (i < error.getError().getName().size()) {
                            msgName += error.getError().getName().get(i);
                            i++;
                        }
                        if (error.getError().getEmail() != null) {
                            String msgEmail = "";
                            i = 0;
                            while (i < error.getError().getEmail().size()) {
                                msgEmail += error.getError().getEmail().get(i);
                                i++;
                            }
                            if (error.getError().getPassword() != null) {
                                String msgPass = "";
                                i = 0;
                                while (i < error.getError().getPassword().size()) {
                                    msgPass += error.getError().getPassword().get(i) + "\n";
                                    i++;
                                }
                                mPassword.setError(msgPass);
                            }
                            mEmail.setError(msgEmail);
                        }
                        mUsername.setError(msgName);
                    } else if (error.getError().getEmail() != null) {
                        String msgEmail = "";
                        while (i < error.getError().getEmail().size()) {
                            msgEmail += error.getError().getEmail().get(i);
                            i++;
                        }
                        if (error.getError().getPassword() != null) {
                            String msgPass = "";
                            i = 0;
                            while (i < error.getError().getPassword().size()) {
                                msgPass += error.getError().getPassword().get(i) + "\n";
                                i++;
                            }
                            mPassword.setError(msgPass);
                        }
                        mEmail.setError(msgEmail);
                    } else if (error.getError().getPassword() != null) {
                        String msgPass = "";
                        while (i < error.getError().getPassword().size()) {
                            msgPass += error.getError().getPassword().get(i) + "\n";
                            i++;
                        }
                        mPassword.setError(msgPass);
                    }
                }
            }

            @Override
            public void onFailure(Call<Envelope<RegisterResponse>> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, "Error Request", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void handlerRegisterProcess(View view) {
        String name = mUsername.getText().toString();
        String email = mEmail.getText().toString();
        String password = mPassword.getText().toString();
        String password_confirm = mPasswordConfirm.getText().toString();
        registerRequest = new RegisterRequest(name, email, password, password_confirm);
        register();
    }
}
