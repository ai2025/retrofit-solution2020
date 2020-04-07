package id.putraprima.retrofit.ui;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
    ArrayList<RecipeResponse> recipes;
    RecipeAdapter adapter;
    ProgressDialog progressDialog;
    Button btnLoadMore, btnReload;

    int page;

    int id;
    String nama, deskripsi, bahan, langkah, foto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        btnLoadMore = findViewById(R.id.btnLoadMore);
        btnReload = findViewById(R.id.btnReload);

        recipes = new ArrayList<>();

        RecyclerView recipeView = findViewById(R.id.rv_recipe);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recipeView.setLayoutManager(layoutManager);

        adapter = new RecipeAdapter(recipes);
        recipeView.setAdapter(adapter);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait..");
        progressDialog.show();

        page = 1;
        doReload();
    }

    public void doRecipe() {
        ApiInterface service = ServiceGenerator.createService(ApiInterface.class);
        Call<Envelope<List<RecipeResponse>>> call = service.getRecipe();
        call.enqueue(new Callback<Envelope<List<RecipeResponse>>>() {
            @Override
            public void onResponse(Call<Envelope<List<RecipeResponse>>> call, Response<Envelope<List<RecipeResponse>>> response) {
                if (response.isSuccessful()) {
                    progressDialog.dismiss();
                    btnReload.setVisibility(View.GONE);
                    for (int i = 0; i < response.body().getData().size(); i++) {
                        id = response.body().getData().get(i).getId();
                        nama = response.body().getData().get(i).getNama_resep();
                        deskripsi = response.body().getData().get(i).getDeskripsi();
                        bahan = response.body().getData().get(i).getBahan();
                        langkah = response.body().getData().get(i).getLangkah_pembuatan();
                        foto = response.body().getData().get(i).getFoto();
                        recipes.add(new RecipeResponse(id, nama, deskripsi, bahan, langkah, foto));
                        adapter.notifyDataSetChanged();
                    }

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
                    page++;
                } else {
                    final AlertDialog.Builder alert = new AlertDialog.Builder(RecipeActivity.this);
                    alert.setTitle("Notification")
                            .setMessage("Your data cant be loaded")
                            .setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    doRecipe();
                                    alert.setCancelable(true);
                                }
                            })
                            .setCancelable(false);
                    alert.show();
                }
            }

            @Override
            public void onFailure(Call<Envelope<List<RecipeResponse>>> call, Throwable t) {
                final AlertDialog.Builder alert = new AlertDialog.Builder(RecipeActivity.this);
                alert.setTitle("Notification")
                        .setMessage("Check your internet connection!")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                alert.setCancelable(true);
                            }
                        })
                        .setCancelable(false);
                alert.show();
            }
        });
//
    }

    public void doLoadMore() {
        ApiInterface service = ServiceGenerator.createService(ApiInterface.class);
        Call<Envelope<List<RecipeResponse>>> call = service.getRecipeNext(page);
        call.enqueue(new Callback<Envelope<List<RecipeResponse>>>() {
            @Override
            public void onResponse(Call<Envelope<List<RecipeResponse>>> call, Response<Envelope<List<RecipeResponse>>> response) {
                if (response.isSuccessful()) {
                    btnReload.setVisibility(View.VISIBLE);
                    if (response.body().getData().size() != 0) {
                        for (int i = 0; i < response.body().getData().size(); i++) {
                            int id = response.body().getData().get(i).getId();
                            String namaResep = response.body().getData().get(i).getNama_resep();
                            String deskripsi = response.body().getData().get(i).getDeskripsi();
                            String bahan = response.body().getData().get(i).getBahan();
                            String langkahPembuatan = response.body().getData().get(i).getLangkah_pembuatan();
                            String foto = response.body().getData().get(i).getFoto();
                            recipes.add(new RecipeResponse(id, namaResep, deskripsi, bahan, langkahPembuatan, foto));
                            adapter.notifyDataSetChanged();
                        }
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
                        page++;
                    } else {
                        final AlertDialog.Builder alert = new AlertDialog.Builder(RecipeActivity.this);
                        alert.setTitle("Notification")
                                .setMessage("No more to load")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        btnLoadMore.setVisibility(View.GONE);
                                        alert.setCancelable(true);
                                    }
                                })
                                .setCancelable(false);
                        alert.show();
                    }
                } else {
                    final AlertDialog.Builder alert = new AlertDialog.Builder(RecipeActivity.this);
                    alert.setTitle("Notification")
                            .setMessage("Check your internet connection!")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    btnLoadMore.setVisibility(View.GONE);
                                    alert.setCancelable(true);
                                }
                            })
                            .setCancelable(false);
                    alert.show();
                }
            }

            @Override
            public void onFailure(Call<Envelope<List<RecipeResponse>>> call, Throwable t) {
                Toast.makeText(RecipeActivity.this, "koneksinyaa", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void doReload() {
        recipes.clear();
        page = 1;
        doRecipe();
    }

    public void handleReload(View view) {
        btnLoadMore.setVisibility(View.VISIBLE);
        doReload();
    }

    public void handleLoadMore(View view) {
        doLoadMore();
    }
}
