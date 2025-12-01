package model;

public class Cancion extends Media {

    public Cancion(int id, String titulo, String artista, int duracion, String genero) {
        super(id, titulo, artista, duracion, genero);
    }

    @Override
    public void reproducir() {
        System.out.println("Reproduciendo canción: " + titulo + " de " + artista);
        try {
            Thread.sleep(Math.min(duracion * 1000, 3000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return id + " | " + titulo + " | " + artista + " | " + duracion + "s | " + genero;
    }
}
