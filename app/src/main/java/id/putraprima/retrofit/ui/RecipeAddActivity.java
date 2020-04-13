package id.putraprima.retrofit.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import id.putraprima.retrofit.R;
import id.putraprima.retrofit.api.models.DataApp;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class RecipeAddActivity extends AppCompatActivity {

    EditText edtnama, edtdeskripsi, edtbahan, edtlangkah;
    private DataApp sess;
    private String token;
    private int idUsers;
    private ProfileActivity prof;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_add);
        sess = new DataApp(this);
        token = sess.getTokenType() + " " + sess.getToken();
        idUsers = prof.idUser;

        edtnama = findViewById(R.id.edt_nama_recipe);
        edtdeskripsi = findViewById(R.id.edt_deskripsi);
        edtbahan = findViewById(R.id.edt_bahan);
        edtlangkah = findViewById(R.id.edt_langkah_pembuatan);
    }

    public void handleTakePhoto(View view) {
        Intent camIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(camIntent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Bitmap foto = (Bitmap) data.getExtras().get("data");
            uploadImg(foto);
        }
    }

    private File createTempFile(Bitmap bitmap) {
        File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                System.currentTimeMillis() + "_image.webp");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.WEBP, 0, bos);
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

    public void uploadImg(Bitmap img) {
        String nama = edtnama.getText().toString();
        String deskripsi = edtdeskripsi.getText().toString();
        String bahan = edtbahan.getText().toString();
        String langkah = edtlangkah.getText().toString();

        HashMap<String, RequestBody> map = new HashMap<>();
        map.put("fk_user", RequestBody.create(MultipartBody.FORM, String.valueOf(idUsers)));
        map.put("nama_resep", RequestBody.create(MultipartBody.FORM, nama));
        map.put("deskripsi", RequestBody.create(MultipartBody.FORM, deskripsi));
        map.put("bahan", RequestBody.create(MultipartBody.FORM, bahan));
        map.put("langkah_pembuatan", RequestBody.create(MultipartBody.FORM, langkah));

        File file = createTempFile(img);
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("foto", file.getName(), requestBody);
//      butuh yaaa
//        Call<Envelope<ResponseBody>> call = service.upload
    }
}
