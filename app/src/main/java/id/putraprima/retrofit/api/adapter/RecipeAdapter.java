package id.putraprima.retrofit.api.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import id.putraprima.retrofit.R;
import id.putraprima.retrofit.api.models.RecipeResponse;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {
    ArrayList<RecipeResponse> items;

    public RecipeAdapter(ArrayList<RecipeResponse> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RecipeResponse item = items.get(position);
        holder.id.setText(Integer.toString(item.getId()));
        holder.nama.setText(item.getNama_resep());
        holder.deskripsi.setText(item.getDeskripsi());
        holder.bahan.setText(item.getBahan());
        holder.langkah.setText(item.getLangkah_pembuatan());
        String url = "https://mobile.putraprima.id/uploads/" + item.getFoto();
        Picasso.get().load(url).into(holder.foto);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView id, nama, deskripsi, bahan, langkah;
        ImageView foto;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            id = itemView.findViewById(R.id.id_recipe);
            nama = itemView.findViewById(R.id.nama_recipe);
            deskripsi = itemView.findViewById(R.id.deskripsi);
            bahan = itemView.findViewById(R.id.bahan);
            langkah = itemView.findViewById(R.id.langkah_pembuatan);
            foto = itemView.findViewById(R.id.foto);
        }
    }
}
