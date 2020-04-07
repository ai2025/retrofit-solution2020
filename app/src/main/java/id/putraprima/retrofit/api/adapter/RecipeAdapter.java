package id.putraprima.retrofit.api.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import id.putraprima.retrofit.R;
import id.putraprima.retrofit.api.models.Envelope;
import id.putraprima.retrofit.api.models.RecipeResponse;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {
    private Context context;
    private Envelope<List<RecipeResponse>> recipes;
//    String url;


    public RecipeAdapter(Context context, Envelope<List<RecipeResponse>> recipes) {
        this.context = context;
        this.recipes = recipes;
    }

    public static void setItems(Envelope<List<RecipeResponse>> items) {
        Envelope<List<RecipeResponse>> Items = items;
    }

    public Envelope<List<RecipeResponse>> getRecipes() {
        return recipes;
    }

    @NonNull
    @Override
    public RecipeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recipe, parent, false);
        return new ViewHolder(view);
//        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeAdapter.ViewHolder holder, int position) {
        RecipeResponse recipe = recipes.getData().get(position);
//        holder.id.setText(Integer.toString(recipe.getId()));
        holder.nama_resep.setText(recipe.getNama_resep());
        holder.deskripsi.setText(recipe.getDeskripsi());
        holder.bahan.setText(recipe.getBahan());
        holder.langkah_pembuatan.setText(recipe.getLangkah_pembuatan());
        String url = "https://mobile.putraprima.id/uploads/" + recipe.getFoto();
        Picasso.get().load(url).into(holder.foto);
    }

    @Override
    public int getItemCount() {
        return (recipes != null) ? recipes.getData().size() : 0;
//        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView foto;
        TextView nama_resep, deskripsi, bahan, langkah_pembuatan;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            foto = itemView.findViewById(R.id.foto);
//            id = itemView.findViewById(R.id.id_recipe);
            nama_resep = itemView.findViewById(R.id.nama_recipe);
            deskripsi = itemView.findViewById(R.id.deskripsi);
            bahan = itemView.findViewById(R.id.bahan);
            langkah_pembuatan = itemView.findViewById(R.id.langkah_pembuatan);
        }
    }
}
