package model;

public abstract class Media {
    protected int id;
    protected String titulo;
    protected String artista;
    protected int duracion;
    protected String genero;

    public Media(int id, String titulo, String artista, int duracion, String genero) {
        this.id = id;
        this.titulo = titulo;
        this.artista = artista;
        this.duracion = duracion;
        this.genero = genero;
    }

    public abstract void reproducir();

    public int getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getArtista() { return artista; }
    public int getDuracion() { return duracion; }
    public String getGenero() { return genero; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public void setArtista(String artista) { this.artista = artista; }
    public void setDuracion(int duracion) { this.duracion = duracion; }
    public void setGenero(String genero) { this.genero = genero; }
}
