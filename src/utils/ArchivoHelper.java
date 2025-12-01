package utils;

import model.Cancion;
import model.Playlist;

import java.io.*;
import java.util.*;

public class ArchivoHelper {

    public static void guardarCanciones(Map<Integer, Cancion> biblioteca, String filename) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            for (Cancion c : biblioteca.values()) {
                bw.write(c.getId() + ";" + c.getTitulo() + ";" + c.getArtista() + ";" + c.getDuracion() + ";" + c.getGenero());
                bw.newLine();
            }
        }
    }

    public static Map<Integer, Cancion> cargarCanciones(String filename) throws IOException {
        Map<Integer, Cancion> biblioteca = new HashMap<>();
        File f = new File(filename);
        if(!f.exists()) return biblioteca;

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] datos = line.split(";");
                try {
                    int id = Integer.parseInt(datos[0]);
                    String titulo = datos[1];
                    String artista = datos[2];
                    int dur = Integer.parseInt(datos[3]);
                    String gen = datos[4];
                    biblioteca.put(id, new Cancion(id, titulo, artista, dur, gen));
                } catch (NumberFormatException e) {
                    System.out.println("Error leyendo línea: " + line);
                }
            }
        }
        return biblioteca;
    }

    public static void guardarFavoritas(Set<Integer> favoritas, String filename) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            for (int id : favoritas) {
                bw.write(String.valueOf(id));
                bw.newLine();
            }
        }
    }

    public static Set<Integer> cargarFavoritas(String filename) throws IOException {
        Set<Integer> favoritas = new HashSet<>();
        File f = new File(filename);
        if (!f.exists()) return favoritas;

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                try {
                    favoritas.add(Integer.valueOf(line));
                } catch (NumberFormatException e) {
                    System.out.println("ID inválido en favoritas: " + line);
                }
            }
        }
        return favoritas;
    }

    public static void guardarPlaylists(Map<String, Playlist> playlists, String filename) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            for (Map.Entry<String, Playlist> e : playlists.entrySet()) {
                String name = e.getKey();
                Playlist p = e.getValue();
                StringBuilder sb = new StringBuilder();
                sb.append(name).append(";");
                boolean first = true;
                for (Cancion c : p.getCanciones()) {
                    if (!first) sb.append(",");
                    sb.append(c.getId());
                    first = false;
                }
                bw.write(sb.toString());
                bw.newLine();
            }
        }
    }

    public static Map<String, Playlist> cargarPlaylists(String filename, Map<Integer, Cancion> biblioteca) throws IOException {
        Map<String, Playlist> playlists = new HashMap<>();
        File f = new File(filename);
        if (!f.exists()) return playlists;

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";", 2);
                if (parts.length == 0) continue;
                String name = parts[0];
                Playlist p = new Playlist(name);
                if (parts.length == 2 && !parts[1].trim().isEmpty()) {
                    String[] ids = parts[1].split(",");
                    for (String sid : ids) {
                        try {
                            int id = Integer.parseInt(sid.trim());
                            Cancion c = biblioteca.get(id);
                            if (c != null) p.agregarCancion(c);
                        } catch (NumberFormatException ex) {
                            System.out.println("ID inválido en playlists: " + sid);
                        }
                    }
                }
                playlists.put(name, p);
            }
        }
        return playlists;
    }
}
