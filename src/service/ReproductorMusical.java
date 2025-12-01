package service;

import model.*;
import utils.ArchivoHelper;

import java.io.IOException;
import java.util.*;

public class ReproductorMusical {
    private Map<Integer, Cancion> biblioteca;
    private Map<String, Playlist> playlists;
    private Set<Integer> favoritas;

    public ReproductorMusical() {
        biblioteca = new HashMap<>();
        playlists = new HashMap<>();
        favoritas = new HashSet<>();
        try {
            biblioteca = ArchivoHelper.cargarCanciones("biblioteca.txt");
            favoritas = ArchivoHelper.cargarFavoritas("favoritas.txt");
            playlists = ArchivoHelper.cargarPlaylists("playlists.txt", biblioteca);
        } catch (IOException e) {
            System.out.println("Error cargando archivos: " + e.getMessage());
        }
    }

    public void agregarCancion(Cancion c) {
        biblioteca.put(c.getId(), c);
        try {
            ArchivoHelper.guardarCanciones(biblioteca, "biblioteca.txt");
        } catch (IOException e) {
            System.out.println("Error guardando canción: " + e.getMessage());
        }
    }

    public int obtenerProximoId() {
        if (biblioteca.isEmpty()) return 1;
        return biblioteca.keySet().stream().mapToInt(Integer::intValue).max().orElse(0) + 1;
    }

    public void eliminarCancion(int id) {
        biblioteca.remove(id);
        favoritas.remove(id);
        try {
            ArchivoHelper.guardarCanciones(biblioteca, "biblioteca.txt");
            ArchivoHelper.guardarFavoritas(favoritas, "favoritas.txt");
            ArchivoHelper.guardarPlaylists(playlists, "playlists.txt");
        } catch (IOException e) {
            System.out.println("Error eliminando canción: " + e.getMessage());
        }
    }

    public boolean crearPlaylist(String name) {
        if (playlists.containsKey(name)) return false;
        playlists.put(name, new Playlist(name));
        try { ArchivoHelper.guardarPlaylists(playlists, "playlists.txt"); } catch (IOException e) { System.out.println("Error guardando playlists: " + e.getMessage()); }
        return true;
    }

    public void eliminarPlaylist(String name) {
        playlists.remove(name);
        try { ArchivoHelper.guardarPlaylists(playlists, "playlists.txt"); } catch (IOException e) { System.out.println("Error guardando playlists: " + e.getMessage()); }
    }

    public void agregarCancionAPlaylist(String name, int id) {
        Playlist p = playlists.get(name);
        Cancion c = biblioteca.get(id);
        if (p != null && c != null) {
            p.agregarCancion(c);
            try { ArchivoHelper.guardarPlaylists(playlists, "playlists.txt"); } catch (IOException e) { System.out.println("Error guardando playlists: " + e.getMessage()); }
        }
    }

    public void eliminarCancionDePlaylist(String name, int id) {
        Playlist p = playlists.get(name);
        if (p != null) {
            p.eliminarCancion(id);
            try { ArchivoHelper.guardarPlaylists(playlists, "playlists.txt"); } catch (IOException e) { System.out.println("Error guardando playlists: " + e.getMessage()); }
        }
    }

    public List<Cancion> buscar(String criterio) {
        List<Cancion> resultados = new ArrayList<>();
        for (Cancion c : biblioteca.values()) {
            if (c.getTitulo().toLowerCase().contains(criterio.toLowerCase())
                    || c.getArtista().toLowerCase().contains(criterio.toLowerCase())
                    || c.getGenero().toLowerCase().contains(criterio.toLowerCase())) {
                resultados.add(c);
            }
        }
        return resultados;
    }

    public void agregarAFavoritas(int id) {
        if (biblioteca.containsKey(id)) {
            favoritas.add(id);
            try {
                ArchivoHelper.guardarFavoritas(favoritas, "favoritas.txt");
            } catch (IOException e) {
                System.out.println("Error guardando favoritas");
            }
        } else System.out.println("Canción no encontrada");
    }

    public void mostrarEstadisticas() {
        int totalDuracion = biblioteca.values().stream().mapToInt(Cancion::getDuracion).sum();
        System.out.println("Número de canciones: " + biblioteca.size());
        System.out.println("Duración total: " + totalDuracion + "s");
    }

    public Map<Integer, Cancion> getBiblioteca() { return biblioteca; }
    public Map<String, Playlist> getPlaylists() { return playlists; }
    public Set<Integer> getFavoritas() { return favoritas; }
}
