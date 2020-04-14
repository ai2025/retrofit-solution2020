package id.putraprima.retrofit.api.models;

public class RecipeRequest {

//    @NonNull
//    private RequestBody createPartFromString(String descriptionString) {
//        return RequestBody.create(
//                okhttp3.MultipartBody.FORM, descriptionString);
//    }

    private int fk_user;
    private String nama_resep;
    private String deskripsi;
    private String bahan;
    private String langkah_pembuatan;
//    private String foto;

    public RecipeRequest(int fk_user, String nama_resep, String deskripsi, String bahan, String langkah_pembuatan) {
        this.fk_user = fk_user;
        this.nama_resep = nama_resep;
        this.deskripsi = deskripsi;
        this.bahan = bahan;
        this.langkah_pembuatan = langkah_pembuatan;
    }

    public int getFk_user() {
        return fk_user;
    }

    public void setFk_user(int fk_user) {
        this.fk_user = fk_user;
    }

    public String getNama_resep() {
        return nama_resep;
    }

    public void setNama_resep(String nama_resep) {
        this.nama_resep = nama_resep;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public String getBahan() {
        return bahan;
    }

    public void setBahan(String bahan) {
        this.bahan = bahan;
    }

    public String getLangkah_pembuatan() {
        return langkah_pembuatan;
    }

    public void setLangkah_pembuatan(String langkah_pembuatan) {
        this.langkah_pembuatan = langkah_pembuatan;
    }
}
