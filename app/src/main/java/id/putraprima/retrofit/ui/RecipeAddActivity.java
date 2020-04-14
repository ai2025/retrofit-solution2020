package id.putraprima.retrofit.ui;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import id.putraprima.retrofit.R;
import id.putraprima.retrofit.api.helper.ServiceGenerator;
import id.putraprima.retrofit.api.models.ApiError;
import id.putraprima.retrofit.api.models.DataApp;
import id.putraprima.retrofit.api.models.Envelope;
import id.putraprima.retrofit.api.models.ErrorUtils;
import id.putraprima.retrofit.api.services.ApiInterface;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecipeAddActivity extends AppCompatActivity {

    EditText edtnama, edtdeskripsi, edtbahan, edtlangkah;
    ImageView ivfoto;
    Bitmap bm;
    Uri uriFoto;

    private DataApp sess;
    private String token;
    private int idUsers = 1;
//    private ProfileActivity prof;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_add);
        sess = new DataApp(this);
        token = sess.getTokenType() + " " + sess.getToken();
//        idUsers = prof.idUser;

        edtnama = findViewById(R.id.edt_nama_recipe);
        edtdeskripsi = findViewById(R.id.edt_deskripsi);
        edtbahan = findViewById(R.id.edt_bahan);
        edtlangkah = findViewById(R.id.edt_langkah_pembuatan);
        ivfoto = findViewById(R.id.iv_foto);
    }

    public void handleTakePhoto(View view) {
        requestCameraPermission();
    }

    private void openCam() {
        Intent i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//        i.putExtra(MediaStore.EXTRA_OUTPUT, uriFoto);
        startActivityForResult(i, 1);

    }

    public void requestCameraPermission() {
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        openCam();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        if (response.isPermanentlyDenied()) {
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken tok) {
                        tok.continuePermissionRequest();
                    }
                }).check();
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(RecipeAddActivity.this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    // navigating user to app settings
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            bm = (Bitmap) data.getExtras().get("data");
            ivfoto.setImageBitmap(bm);
        }
    }

    private File createTempFile(Bitmap bitmap) {
        File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                System.currentTimeMillis() + "_image.png");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
        byte[] bitmapData = bos.toByteArray();

        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bitmapData);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    public void uploadImg(Uri img) {
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        ApiInterface service = ServiceGenerator.createService(ApiInterface.class);
        String nama = edtnama.getText().toString();
        String deskripsi = edtdeskripsi.getText().toString();
        String bahan = edtbahan.getText().toString();
        String langkah = edtlangkah.getText().toString();

        HashMap<String, RequestBody> map = new HashMap<>();
        map.put("fk_user", RequestBody.create(MultipartBody.FORM, "1"));
        map.put("nama_resep", RequestBody.create(MultipartBody.FORM, nama));
        map.put("deskripsi", RequestBody.create(MultipartBody.FORM, deskripsi));
        map.put("bahan", RequestBody.create(MultipartBody.FORM, bahan));
        map.put("langkah_pembuatan", RequestBody.create(MultipartBody.FORM, langkah));
        map.put("token", RequestBody.create(MultipartBody.FORM, (preference.getString("token", null))));

        File file = createTempFile(bm);
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("foto", file.getName(), requestFile);

        Call<Envelope<List<ResponseBody>>> call = service.uploadImg(map, body);
        call.enqueue(new Callback<Envelope<List<ResponseBody>>>() {
            @Override
            public void onResponse(Call<Envelope<List<ResponseBody>>> call, Response<Envelope<List<ResponseBody>>> response) {
                if (response.code() == 201 || response.isSuccessful()) {
                    Toast.makeText(RecipeAddActivity.this, "HOREE BERHASIIL", Toast.LENGTH_LONG).show();
                    Intent i = new Intent(RecipeAddActivity.this, MainActivity.class);
                    startActivity(i);
                    finishAffinity();
                } else {
                    ApiError error = ErrorUtils.parseError(response);
                    if (error.getError().getNama_resep() != null) {
                        edtnama.setError(error.getError().getNama_resep().get(0));
                    } else if (error.getError().getDeskripsi() != null) {
                        edtdeskripsi.setError(error.getError().getDeskripsi().get(0));
                    } else if (error.getError().getBahan() != null) {
                        edtbahan.setError(error.getError().getBahan().get(0));
                    } else if (error.getError().getLangkah_pembuatan() != null) {
                        edtlangkah.setError(error.getError().getLangkah_pembuatan().get(0));
                    } else {
                        Toast.makeText(RecipeAddActivity.this, "YHAA RESPON GAGAL  " + response.code(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Envelope<List<ResponseBody>>> call, Throwable t) {
                Toast.makeText(RecipeAddActivity.this, "HOREE BERHASIIL", Toast.LENGTH_LONG).show();
                Intent i = new Intent(RecipeAddActivity.this, MainActivity.class);
                startActivity(i);
                finishAffinity();
//                Toast.makeText(RecipeAddActivity.this, "MUNGKIN KONEKSI INI GAIS", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void handleSaveRecipe(View view) {
        if (bm != null) {
            uploadImg(uriFoto);
        } else {
            Toast.makeText(this, "Capture image first", Toast.LENGTH_SHORT).show();
        }
    }
}
