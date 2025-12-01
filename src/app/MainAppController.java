package app;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import model.Cancion;
import model.Playlist;
import service.ReproductorMusical;
import utils.ArchivoHelper;

import java.io.IOException;
import java.util.Optional;

public class MainAppController {
    @FXML private ListView<Cancion> listaCanciones;
    @FXML private TextField txtSearch;
    @FXML private Button btnAddSong, btnDeleteSong, btnFavorite, btnSearch, btnShowAll;

    @FXML private ListView<String> listaPlaylists;
    @FXML private ListView<Cancion> listaCancionesPlaylist;
    @FXML private Button btnCreatePlaylist, btnDeletePlaylist, btnAddToPlaylist, btnRemoveFromPlaylist, btnPlayPlaylist;

    private ReproductorMusical rm;
    private ObservableList<Cancion> cancionesObs;
    private ObservableList<String> playlistsObs;
    private ObservableList<Cancion> cancionesPlaylistObs;

    @FXML
    public void initialize() {
        rm = new ReproductorMusical();

        cancionesObs = FXCollections.observableArrayList(rm.getBiblioteca().values());
        listaCanciones.setItems(cancionesObs);
        listaCanciones.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Cancion item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText(null);
                else {
                    String star = rm.getFavoritas().contains(item.getId()) ? " ★" : "";
                    setText(item.toString() + star);
                }
            }
        });

        playlistsObs = FXCollections.observableArrayList(rm.getPlaylists().keySet());
        listaPlaylists.setItems(playlistsObs);

        cancionesPlaylistObs = FXCollections.observableArrayList();
        listaCancionesPlaylist.setItems(cancionesPlaylistObs);

        // Wire controls
        btnAddSong.setOnAction(e -> showAddSongDialog());
        btnDeleteSong.setOnAction(e -> deleteSelectedSong());
        btnFavorite.setOnAction(e -> toggleFavoriteSelectedSong());
        btnSearch.setOnAction(e -> search());
        btnShowAll.setOnAction(e -> showAll());

        btnCreatePlaylist.setOnAction(e -> createPlaylist());
        btnDeletePlaylist.setOnAction(e -> deletePlaylist());
        btnAddToPlaylist.setOnAction(e -> addSelectedSongToPlaylist());
        btnRemoveFromPlaylist.setOnAction(e -> removeSelectedFromPlaylist());
        btnPlayPlaylist.setOnAction(e -> playSelectedPlaylist());

        listaPlaylists.getSelectionModel().selectedItemProperty().addListener((obs, old, neu) -> {
            if (neu != null) loadSelectedPlaylist(neu);
            else cancionesPlaylistObs.clear();
        });
    }

    private void showAddSongDialog() {
        Dialog<Cancion> dialog = new Dialog<>();
        dialog.setTitle("Añadir Canción");
        ButtonType addType = new ButtonType("Añadir", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addType, ButtonType.CANCEL);

        int nextId = rm.obtenerProximoId();
        Label idLabel = new Label(String.valueOf(nextId));
        TextField titF = new TextField(); titF.setPromptText("Título");
        TextField artF = new TextField(); artF.setPromptText("Artista");
        TextField durF = new TextField(); durF.setPromptText("Duración(s)");
        TextField genF = new TextField(); genF.setPromptText("Género");

        GridPane grid = new GridPane();
        grid.setHgap(8); grid.setVgap(8);
        grid.add(new Label("ID:"),0,0); grid.add(idLabel,1,0);
        grid.add(new Label("Título:"),0,1); grid.add(titF,1,1);
        grid.add(new Label("Artista:"),0,2); grid.add(artF,1,2);
        grid.add(new Label("Duración:"),0,3); grid.add(durF,1,3);
        grid.add(new Label("Género:"),0,4); grid.add(genF,1,4);
        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == addType) {
                try {
                    int dur = Integer.parseInt(durF.getText().trim());
                    return new Cancion(nextId, titF.getText().trim(), artF.getText().trim(), dur, genF.getText().trim());
                } catch (NumberFormatException ex) {
                    showAlert("Duración inválida");
                    return null;
                }
            }
            return null;
        });

        Optional<Cancion> res = dialog.showAndWait();
        res.ifPresent(c -> {
            rm.agregarCancion(c);
            cancionesObs.add(c);
        });
    }

    private void deleteSelectedSong() {
        Cancion sel = listaCanciones.getSelectionModel().getSelectedItem();
        if (sel == null) { showAlert("Selecciona una canción para eliminar"); return; }
        rm.eliminarCancion(sel.getId());
        cancionesObs.remove(sel);
        // actualizar playlists
        rm.getPlaylists().values().forEach(p -> p.eliminarCancion(sel.getId()));
        cancionesPlaylistObs.removeIf(c -> c.getId() == sel.getId());
    }

    private void toggleFavoriteSelectedSong() {
        Cancion sel = listaCanciones.getSelectionModel().getSelectedItem();
        if (sel == null) { showAlert("Selecciona una canción"); return; }
        if (rm.getFavoritas().contains(sel.getId())) {
            rm.getFavoritas().remove(sel.getId());
        } else {
            rm.agregarAFavoritas(sel.getId());
        }
        try { ArchivoHelper.guardarFavoritas(rm.getFavoritas(), "favoritas.txt"); } catch (IOException e) { showAlert("Error guardando favoritas: " + e.getMessage()); }
        listaCanciones.refresh();
    }

    private void search() {
        String crit = txtSearch.getText().trim();
        if (crit.isEmpty()) showAlert("Introduce criterio de búsqueda");
        else cancionesObs.setAll(rm.buscar(crit));
    }

    private void showAll() { cancionesObs.setAll(rm.getBiblioteca().values()); }

    private void createPlaylist() {
        TextInputDialog d = new TextInputDialog();
        d.setTitle("Crear Playlist"); d.setHeaderText("Nombre de la playlist");
        Optional<String> res = d.showAndWait();
        res.ifPresent(name -> {
            if (name.trim().isEmpty()) showAlert("Nombre vacío");
            else if (rm.getPlaylists().containsKey(name)) showAlert("La playlist ya existe");
            else {
                boolean ok = rm.crearPlaylist(name);
                if (ok) {
                    playlistsObs.add(name);
                    listaPlaylists.setItems(playlistsObs);
                } else showAlert("No se pudo crear la playlist");
            }
        });
    }

    private void deletePlaylist() {
        String sel = listaPlaylists.getSelectionModel().getSelectedItem();
        if (sel == null) { showAlert("Selecciona una playlist"); return; }
        rm.eliminarPlaylist(sel);
        playlistsObs.remove(sel);
        cancionesPlaylistObs.clear();
    }

    private void addSelectedSongToPlaylist() {
        String selPl = listaPlaylists.getSelectionModel().getSelectedItem();
        Cancion sel = listaCanciones.getSelectionModel().getSelectedItem();
        if (selPl == null || sel == null) { showAlert("Selecciona playlist y canción"); return; }
        rm.agregarCancionAPlaylist(selPl, sel.getId());
        loadSelectedPlaylist(selPl);
    }

    private void removeSelectedFromPlaylist() {
        String selPl = listaPlaylists.getSelectionModel().getSelectedItem();
        Cancion sel = listaCancionesPlaylist.getSelectionModel().getSelectedItem();
        if (selPl == null || sel == null) { showAlert("Selecciona playlist y canción en la playlist"); return; }
        rm.eliminarCancionDePlaylist(selPl, sel.getId());
        loadSelectedPlaylist(selPl);
    }

    private void playSelectedPlaylist() {
        String selPl = listaPlaylists.getSelectionModel().getSelectedItem();
        if (selPl == null) { showAlert("Selecciona una playlist"); return; }
        Playlist p = rm.getPlaylists().get(selPl);
        if (p == null) { showAlert("Playlist inexistente"); return; }
        // Reproducir en hilo para no bloquear UI
        new Thread(() -> {
            for (Cancion c : p.getCanciones()) c.reproducir();
        }).start();
    }

    private void loadSelectedPlaylist(String name) {
        Playlist p = rm.getPlaylists().get(name);
        if (p == null) { cancionesPlaylistObs.clear(); return; }
        cancionesPlaylistObs.setAll(p.getCanciones());
    }

    private void showAlert(String msg) {
        Platform.runLater(() -> {
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setTitle("Información"); a.setHeaderText(null); a.setContentText(msg); a.showAndWait();
        });
    }
}
