package id.putraprima.retrofit.ui;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

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

    Button btnLoadMore, btnReload, showbtn;
    ConstraintLayout constraintLayout;
    ProgressDialog progressDialog;
    ProgressBar loading;

    boolean status, statusR, statusM;
    int page, id;
    String nama, deskripsi, bahan, langkah, foto;

//    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

//        MobileAds.initialize(this, "ca-app-pub-3940256099942544/1033173712");
//        mInterstitialAd = new InterstitialAd(this);
//        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");

        constraintLayout = findViewById(R.id.recipeLayout);
        btnLoadMore = findViewById(R.id.btnLoadMore);
        btnReload = findViewById(R.id.btnReload);
        showbtn = findViewById(R.id.showBtn);
        loading = findViewById(R.id.loading);

        recipes = new ArrayList<>();

        RecyclerView recipeView = findViewById(R.id.rv_recipe);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recipeView.setLayoutManager(layoutManager);

        adapter = new RecipeAdapter(recipes);
        recipeView.setAdapter(adapter);

        loading.setVisibility(View.VISIBLE);

        status = false;
        statusR = false;
        statusM = false;
        statusBtn(statusR, statusM);

        page = 1;
        doReload();
    }

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        mInterstitialAd.loadAd(new AdRequest.Builder().build());
//    }

    private void statusBtn(boolean statusR, boolean statusM) {
        if (!status) {
            btnLoadMore.setVisibility(View.GONE);
            btnReload.setVisibility(View.GONE);
            status = true;
        } else {
            if (!statusR) {
                btnLoadMore.setVisibility(View.VISIBLE);
                btnReload.setVisibility(View.GONE);
            } else if (!statusM) {
                btnLoadMore.setVisibility(View.GONE);
                btnReload.setVisibility(View.VISIBLE);
            } else {
                btnLoadMore.setVisibility(View.VISIBLE);
                btnReload.setVisibility(View.VISIBLE);
            }
            status = false;
        }
    }

    public void doRecipe() {
        ApiInterface service = ServiceGenerator.createService(ApiInterface.class);
        Call<Envelope<List<RecipeResponse>>> call = service.getRecipe();
        call.enqueue(new Callback<Envelope<List<RecipeResponse>>>() {
            @Override
            public void onResponse(Call<Envelope<List<RecipeResponse>>> call, Response<Envelope<List<RecipeResponse>>> response) {
                if (response.isSuccessful()) {
                    statusR = false;
                    statusM = true;
                    statusBtn(statusR, statusM);
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
                                    loading.setVisibility(View.GONE);
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
                    statusM = false;
                    statusR = true;
                    statusBtn(statusR, statusM);
                    progressDialog = new ProgressDialog(RecipeActivity.this);
                    progressDialog.setMessage("Please Wait..");
                    progressDialog.show();
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
                        progressDialog.dismiss();
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                statusM = true;
                                statusBtn(statusR, statusM);
                            }
                        }, 1590);
                        Snackbar snackbar = Snackbar.make(constraintLayout, "Your data have been succesfully loaded", Snackbar.LENGTH_SHORT);
                        snackbar.show();
                        page++;
                    } else {
                        final AlertDialog.Builder alert = new AlertDialog.Builder(RecipeActivity.this);
                        alert.setTitle("Notification")
                                .setMessage("No more to load")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        statusM = false;
                                        statusBtn(statusR, statusM);
                                        alert.setCancelable(true);
                                    }
                                })
                                .setCancelable(false);
                        alert.show();
                    }
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
    }

    public void doReload() {
        recipes.clear();
        page = 1;
        doRecipe();
    }

    public void handleReload(View view) {
        statusM = true;
        statusR = true;
        statusBtn(statusR, statusM);
        doReload();
    }

    public void handleLoadMore(View view) {
        doLoadMore();
    }

    public void handleBtn(View view) {
        statusBtn(statusR, statusM);
    }
}
