package model;

import java.util.ArrayList;
import java.util.List;

public class Playlist {
    private final String nombre;
    private final List<Cancion> canciones;

    public Playlist(String nombre) {
        this.nombre = nombre;
        this.canciones = new ArrayList<>();
    }

    public void agregarCancion(Cancion c) { canciones.add(c); }
    public void eliminarCancion(int id) { canciones.removeIf(c -> c.getId() == id); }
    public List<Cancion> getCanciones() { return canciones; }
    public String getNombre() { return nombre; }
    public int duracionTotal() { return canciones.stream().mapToInt(Cancion::getDuracion).sum(); }

    public void mostrar() {
        System.out.println("Playlist: " + nombre);
        for (Cancion c : canciones) System.out.println(c);
    }
}
