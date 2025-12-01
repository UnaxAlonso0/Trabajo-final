package app;

import java.io.IOException;
import model.Cancion;
import model.Playlist;
import service.ReproductorMusical;

import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class main {
    public static void main(String[] args) {
        ReproductorMusical rm = new ReproductorMusical();
        try (Scanner sc = new Scanner(System.in)) {
            int opcion;

            do {
                System.out.println("\n--- Menú Principal ---");
                System.out.println("1. Gestión de Biblioteca");
                System.out.println("2. Gestión de Playlists");
                System.out.println("3. Estadísticas");
                System.out.println("4. Guardar y Salir");
                System.out.println("-----------------------");
                System.out.print("Elige opción: ");
                opcion = sc.nextInt(); sc.nextLine();

                switch (opcion) {
                    case 1 -> {
                        System.out.println("1. Añadir canción\n2. Eliminar canción\n3. Buscar canción");
                        int sub = sc.nextInt(); sc.nextLine();
                        switch(sub){
                            case 1 -> {
                                System.out.print("ID: "); int id = sc.nextInt(); sc.nextLine();
                                System.out.print("Título: "); String t = sc.nextLine();
                                System.out.print("Artista: "); String a = sc.nextLine();
                                System.out.print("Duración(s): "); int d = sc.nextInt(); sc.nextLine();
                                System.out.print("Género: "); String g = sc.nextLine();
                                rm.agregarCancion(new Cancion(id,t,a,d,g));
                            }
                            case 2 -> {
                                System.out.print("ID a eliminar: "); int eid = sc.nextInt(); sc.nextLine();
                                rm.eliminarCancion(eid);
                            }
                            case 3 -> {
                                System.out.print("Criterio búsqueda: "); String crit = sc.nextLine();
                                List<Cancion> res = rm.buscar(crit);
                                res.forEach(System.out::println);
                            }
                        }
                    }
                    case 2 -> {
                        boolean salirPlaylist = false;
                        do {
                            System.out.println("\n--- Sub-Menú Playlists ---");
                            System.out.println("1. Crear Playlist");
                            System.out.println("2. Seleccionar Playlist");
                            System.out.println("3. Volver");
                            System.out.println("-----------------------");
                            System.out.print("Elige opción: ");
                            int ps = sc.nextInt(); sc.nextLine();

                            switch (ps) {
                                case 1 -> {
                                    System.out.print("Nombre playlist: ");
                                    String name = sc.nextLine();

                                    if(!rm.getPlaylists().containsKey(name)) {
                                        rm.getPlaylists().put(name, new Playlist(name));
                                        System.out.println("Playlist creada: " + name);
                                        System.out.println("-----------------------");
                                    } else {
                                        System.out.println("La playlist ya existe.");
                                        System.out.println("-----------------------");
                                    }
                                }
                                
                                case 2 -> {
                                    System.out.print("Nombre playlist a seleccionar: ");
                                    System.out.println("-----------------------");
                                    String sel = sc.nextLine();
                                    Playlist playlist = rm.getPlaylists().get(sel);
                                    if (playlist == null) {
                                        System.out.println("Playlist no encontrada.");
                                        System.out.println("-----------------------");
                                        break;
                                    }

                                    boolean subSalir = false;
                                    do {
                                        System.out.println("\n--- Manejo Playlist: " + sel + " ---");
                                        System.out.println("1. Añadir canción por ID");
                                        System.out.println("2. Eliminar canción por ID");
                                        System.out.println("3. Ordenar por título");
                                        System.out.println("4. Ordenar por duración");
                                        System.out.println("5. Mostrar playlist");
                                        System.out.println("6. Reproducir playlist");
                                        System.out.println("7. Volver");
                                        System.out.println("-----------------------");
                                        System.out.print("Elige opción: ");
                                        int opPlaylist = sc.nextInt(); sc.nextLine();
                                        switch(opPlaylist) {
                                            case 1 -> {
                                                System.out.print("ID de canción a añadir: ");
                                                int idAdd = sc.nextInt(); sc.nextLine();
                                                Cancion cAdd = rm.getBiblioteca().get(idAdd);
                                                if(cAdd != null) {
                                                    playlist.agregarCancion(cAdd);
                                                    System.out.println("Canción añadida.");
                                                } else System.out.println("Canción no encontrada en biblioteca.");
                                            }
                                            case 2 -> {
                                                System.out.print("ID de canción a eliminar: ");
                                                System.out.println("-----------------------");
                                                int idDel = sc.nextInt(); sc.nextLine();
                                                playlist.eliminarCancion(idDel);
                                                rm.getFavoritas().remove(idDel); // si estaba en favoritas
                                                System.out.println("Canción eliminada (si existía).");
                                                System.out.println("-----------------------");
                                            }
                                            case 3 -> {
                                                playlist.getCanciones().sort(Comparator.comparing(Cancion::getTitulo));
                                                System.out.println("Playlist ordenada por título.");
                                                System.out.println("-----------------------");
                                            }
                                            case 4 -> {
                                                playlist.getCanciones().sort(Comparator.comparingInt(Cancion::getDuracion));
                                                System.out.println("Playlist ordenada por duración.");
                                                System.out.println("-----------------------");
                                            }
                                            case 5 -> {
                                                System.out.println("Playlist: " + playlist.getNombre());
                                                for(Cancion c : playlist.getCanciones()) {
                                                    String fav = rm.getFavoritas().contains(c.getId()) ? "[FAVORITA]" : "";
                                                    System.out.println(c + " " + fav);
                                                }
                                            }
                                            case 6 -> {
                                                System.out.print("ID de canción para marcar/desmarcar favorita: ");
                                                System.out.println("-----------------------");
                                                int idFav = sc.nextInt(); sc.nextLine();
                                                if(rm.getBiblioteca().containsKey(idFav)) {
                                                    if(rm.getFavoritas().contains(idFav)) {
                                                        rm.getFavoritas().remove(idFav);
                                                        System.out.println("Canción desmarcada como favorita.");
                                                        System.out.println("-----------------------");
                                                    } else {
                                                        rm.getFavoritas().add(idFav);
                                                        System.out.println("Canción marcada como favorita.");
                                                        System.out.println("-----------------------");
                                                    }
                                                    // Guardar en archivo
                                                    try {
                                                        utils.ArchivoHelper.guardarFavoritas(rm.getFavoritas(), "favoritas.txt");
                                                    } catch (IOException e) {
                                                        System.out.println("Error guardando favoritas: " + e.getMessage());
                                                    }
                                                } else {
                                                    System.out.println("Canción no encontrada en biblioteca.");
                                                }
                                            }
                                            case 7 -> {
                                                System.out.println("Reproducción simulada:");
                                                for(Cancion c : playlist.getCanciones()) c.reproducir();
                                            }
                                            case 8 -> subSalir = true;
                                        }
                                    } while(!subSalir);
                                }
                                
                                case 3 -> salirPlaylist = true;
                            }

                        } while(!salirPlaylist);
                    }

                    case 3 -> rm.mostrarEstadisticas();
                }
            } while(opcion != 4);
        }
        System.out.println("¡Hasta luego!");
    }
}
