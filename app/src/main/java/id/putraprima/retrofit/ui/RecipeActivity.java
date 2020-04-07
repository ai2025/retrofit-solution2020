package id.putraprima.retrofit.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import id.putraprima.retrofit.R;
import id.putraprima.retrofit.api.adapter.RecipeAdapter;
import id.putraprima.retrofit.api.helper.ServiceGenerator;
import id.putraprima.retrofit.api.models.Envelope;
import id.putraprima.retrofit.api.models.RecipeResponse;
import id.putraprima.retrofit.api.services.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecipeActivity extends AppCompatActivity {

    //    int id;
//    String nama, deskripsi, bahan, langkah, foto;
    public Context context;
    RecyclerView recipeView;
    List<RecipeResponse> recipes = new ArrayList<>();
    private ProgressBar loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
        recipeView = findViewById(R.id.rv_recipe);
        loading = findViewById(R.id.loading);
        doLoadResep();
    }

    public void doLoadResep() {
        loading.setVisibility(View.VISIBLE);
        ApiInterface service = ServiceGenerator.createService(ApiInterface.class);
        Call<Envelope<List<RecipeResponse>>> call = service.getRecipe();
        call.enqueue(new Callback<Envelope<List<RecipeResponse>>>() {
            @Override
            public void onResponse(Call<Envelope<List<RecipeResponse>>> call, Response<Envelope<List<RecipeResponse>>> response) {
                if (response.isSuccessful()) {
                    final AlertDialog.Builder alert = new AlertDialog.Builder(RecipeActivity.this);
                    alert.setTitle("Notification")
                            .setMessage("Your data have been succesfully loaded")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    alert.setCancelable(true);
                                }
                            })
                            .setCancelable(false);
                    alert.show();
                    loading.setVisibility(View.GONE);
                    Envelope<List<RecipeResponse>> resep = response.body();
                    RecipeAdapter.setItems(resep);

                    RecipeAdapter adp = new RecipeAdapter(RecipeActivity.this, resep);
                    recipeView.setAdapter(adp);

                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(RecipeActivity.this);
                    recipeView.setLayoutManager(layoutManager);
//                    int i = 0;
//                    while (i < response.body().getData().size()){
//                        id = response.body().getData().get(i).getId();
//                        nama = response.body().getData().get(i).getNama_resep();
//                        deskripsi = response.body().getData().get(i).getDeskripsi();
//                        bahan = response.body().getData().get(i).getBahan();
//                        langkah = response.body().getData().get(i).getLangkah_pembuatan();
//                        recipes.add(new RecipeResponse(id, nama, deskripsi, bahan, langkah, foto));
//                        i++;
//                    }
                } else {
                    AlertDialog.Builder batal = new AlertDialog.Builder(RecipeActivity.this);
                    batal.setTitle("Notification")
                            .setMessage("Your data cant be loaded")
                            .setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    doLoadResep();
                                }
                            })
                            .setCancelable(false);
                    batal.show();
                    loading.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<Envelope<List<RecipeResponse>>> call, Throwable t) {
                Toast.makeText(RecipeActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

//    public void handleLoadResep(View view) {
//
//    }
}
