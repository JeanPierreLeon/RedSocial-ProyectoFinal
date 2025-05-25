package uniquindio.com.academix.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import uniquindio.com.academix.Model.Academix;
import uniquindio.com.academix.Model.ContenidoEducativo;
import uniquindio.com.academix.Model.Estudiante;
import uniquindio.com.academix.Model.PublicacionItem;
import uniquindio.com.academix.Utils.Persistencia;
import uniquindio.com.academix.Model.ListaSimple;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import javafx.geometry.Pos;
import javafx.util.Pair;

public class InicioController {

    @FXML private TextField tituloField;
    @FXML private TextField descripcionField;
    @FXML private ChoiceBox<String> tipoChoiceBox;
    @FXML private TextField urlField;
    @FXML private TextField busquedaField;
    @FXML private ChoiceBox<String> ordenChoiceBox;
    @FXML private ListView<PublicacionItem> publicacionesListView;

    private File archivoSeleccionado;
    private Academix academix;
    private Estudiante estudianteActual;

    // Instancia estática para refresco desde PanelController
    private static InicioController instanciaActual;

    public InicioController() {
        instanciaActual = this;
    }

    public static void refrescarPublicacionesDesdePanel() {
        if (instanciaActual != null) {
            instanciaActual.refrescarPublicaciones();
        }
    }

    @FXML
    public void initialize() {
        tipoChoiceBox.getItems().addAll("Imagen", "PDF", "Video", "Otro");
        tipoChoiceBox.getSelectionModel().selectFirst();

        if (ordenChoiceBox != null) {
            ordenChoiceBox.getItems().addAll("Sin ordenar", "Tipo", "Fecha");
            ordenChoiceBox.getSelectionModel().selectFirst();

            ordenChoiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> buscarContenido());
        }

        if (busquedaField != null) {
            busquedaField.textProperty().addListener((obs, oldVal, newVal) -> buscarContenido());
        }

        // Configurar el cell factory para las publicaciones
        publicacionesListView.setCellFactory(lv -> new ListCell<PublicacionItem>() {
            private final VBox contenido = new VBox(10);
            private final HBox encabezado = new HBox(10);
            private final ImageView fotoPerfil = new ImageView();
            private final VBox infoUsuario = new VBox(5);
            private final Label nombreUsuario = new Label();
            private final Label tiempoPublicacion = new Label();
            private final Label texto = new Label();
            private final HBox interacciones = new HBox(10);
            private final Label estrellas = new Label();
            private final Button btnValorar = new Button("Valorar");

            {
                // Configurar foto de perfil
                fotoPerfil.setFitHeight(40);
                fotoPerfil.setFitWidth(40);
                fotoPerfil.setStyle("-fx-background-radius: 20;");

                // Estilos
                nombreUsuario.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
                tiempoPublicacion.setStyle("-fx-text-fill: #65676b; -fx-font-size: 12;");
                texto.setWrapText(true);
                texto.setStyle("-fx-font-size: 14;");
                estrellas.setStyle("-fx-text-fill: #FFD700; -fx-font-size: 14;");
                btnValorar.setStyle("-fx-background-color: #1e88e5; -fx-text-fill: white;");

                // Estructura
                infoUsuario.getChildren().addAll(nombreUsuario, tiempoPublicacion);
                encabezado.getChildren().addAll(fotoPerfil, infoUsuario);
                interacciones.getChildren().addAll(estrellas, btnValorar);
                contenido.getChildren().addAll(encabezado, texto, interacciones);
                contenido.setStyle("-fx-padding: 10; -fx-background-color: white; -fx-background-radius: 10;");
            }

            @Override
            protected void updateItem(PublicacionItem item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    nombreUsuario.setText(item.getAutorNombre());
                    tiempoPublicacion.setText(item.getTiempoTranscurrido());
                    texto.setText(item.getContenido());

                    // Mostrar imagen si existe
                    contenido.getChildren().removeIf(node -> node instanceof ImageView && node != fotoPerfil);
                    if (item.getRutaImagen() != null) {
                        try {
                            ImageView imagenPublicacion = new ImageView(new Image(new File(item.getRutaImagen()).toURI().toString()));
                            imagenPublicacion.setFitWidth(400);
                            imagenPublicacion.setPreserveRatio(true);
                            imagenPublicacion.setSmooth(true);
                            contenido.getChildren().add(2, imagenPublicacion); // después del encabezado y texto
                        } catch (Exception e) {
                            // Si hay error, no mostrar imagen
                        }
                    }

                    // Cargar imagen de perfil
                    if (item.getImagenPerfil() != null) {
                        try {
                            Image imagen = new Image(new File(item.getImagenPerfil()).toURI().toString());
                            fotoPerfil.setImage(imagen);
                        } catch (Exception e) {
                            // Usar imagen por defecto si hay error
                        }
                    }

                    int promedio = item.getPromedioValoraciones();
                    estrellas.setText("★".repeat(promedio) + "☆".repeat(5 - promedio));
                    
                    btnValorar.setOnAction(e -> mostrarDialogoValoracion(item));
                    setGraphic(contenido);
                }
            }
        });
    }

    public void setEstudianteActual(Estudiante estudiante) {
        this.estudianteActual = estudiante;
        academix = Persistencia.cargarRecursoBancoBinario();
        buscarContenido();
        cargarPublicaciones();
    }

    @FXML
    public void seleccionarArchivo() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar archivo");
        String tipo = tipoChoiceBox.getValue();
        if ("Imagen".equalsIgnoreCase(tipo)) {
            fileChooser.getExtensionFilters().setAll(
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp")
            );
        } else if ("PDF".equalsIgnoreCase(tipo)) {
            fileChooser.getExtensionFilters().setAll(
                new FileChooser.ExtensionFilter("PDF", "*.pdf")
            );
        } else if ("Video".equalsIgnoreCase(tipo)) {
            fileChooser.getExtensionFilters().setAll(
                new FileChooser.ExtensionFilter("Videos", "*.mp4", "*.avi", "*.mov", "*.mkv")
            );
        } else {
            fileChooser.getExtensionFilters().clear();
        }
        archivoSeleccionado = fileChooser.showOpenDialog(null);
        if (archivoSeleccionado != null) {
            urlField.setText(archivoSeleccionado.getAbsolutePath());
        }
    }

    @FXML
    public void publicarContenido() {
        String titulo = tituloField.getText();
        String descripcion = descripcionField.getText();
        String tipo = tipoChoiceBox.getValue();
        String url = urlField.getText();

        if (estudianteActual == null) {
            mostrarAlerta("Error", "No hay sesión activa.");
            return;
        }

        if (titulo.isEmpty() || descripcion.isEmpty() || tipo == null || url.isEmpty()) {
            mostrarAlerta("Campos incompletos", "Por favor, completa todos los campos.");
            return;
        }

        // Crear la publicación
        PublicacionItem nuevaPublicacion = new PublicacionItem(
            titulo + "\n" + descripcion,
            estudianteActual.getUsuario(),
            estudianteActual.getNombre(),
            estudianteActual.getFotoPerfil()
        );
        // Guardar la ruta del archivo (imagen/pdf/video) en la publicación
        if (archivoSeleccionado != null && archivoSeleccionado.exists()) {
            nuevaPublicacion.setRutaImagen(archivoSeleccionado.getAbsolutePath());
        }

        // Persistir en el estudiante y en la base
        estudianteActual.agregarPublicacion(nuevaPublicacion);
        Academix academix = Persistencia.cargarRecursoBancoBinario();
        Estudiante estudiantePersistente = academix.buscarEstudiante(estudianteActual.getUsuario());
        if (estudiantePersistente != null) {
            estudiantePersistente.agregarPublicacion(nuevaPublicacion);
            Persistencia.guardarRecursoBancoBinario(academix);
        }

        // Limpiar formulario y selección de imagen
        tituloField.clear();
        descripcionField.clear();
        tipoChoiceBox.getSelectionModel().selectFirst();
        urlField.clear();
        archivoSeleccionado = null;

        // Refrescar publicaciones
        buscarContenido();
    }

    public void buscarContenido() {
        String filtro = busquedaField != null ? busquedaField.getText().toLowerCase() : "";
        String criterioOrden = ordenChoiceBox != null ? ordenChoiceBox.getValue() : "Sin ordenar";

        publicacionesListView.getItems().clear();
        if (academix != null) {
            // Recorrer todas las publicaciones de todos los estudiantes
            for (Estudiante est : academix.getListaEstudiantes()) {
                if (est.getPublicaciones() != null) {
                    for (PublicacionItem pub : est.getPublicaciones()) {
                        // Filtrar por contenido, autor o tipo (tipo no está en PublicacionItem, así que solo por contenido y autor)
                        if (
                            pub.getContenido().toLowerCase().contains(filtro) ||
                            pub.getAutorNombre().toLowerCase().contains(filtro)
                        ) {
                            publicacionesListView.getItems().add(pub);
                        }
                    }
                }
            }
            // Ordenar si corresponde
            if ("Fecha".equals(criterioOrden)) {
                publicacionesListView.getItems().sort((p1, p2) -> 
                    p2.getFechaPublicacion().compareTo(p1.getFechaPublicacion()));
            }
            // Si quieres ordenar por autor o contenido, puedes agregar más criterios aquí
        }
    }

    private ListaSimple<ContenidoEducativo> ordenarPorTipo(ListaSimple<ContenidoEducativo> lista) {
        ListaSimple<ContenidoEducativo> ordenada = new ListaSimple<>();

        for (int i = 0; i < lista.size(); i++) {
            ContenidoEducativo actual = lista.get(i);
            int j = 0;
            while (j < ordenada.size() && actual.getTipo().compareToIgnoreCase(ordenada.get(j).getTipo()) > 0) {
                j++;
            }
            insertarEnLista(ordenada, j, actual);
        }
        return ordenada;
    }

    private ListaSimple<ContenidoEducativo> ordenarPorFecha(ListaSimple<ContenidoEducativo> lista) {
        ListaSimple<ContenidoEducativo> ordenada = new ListaSimple<>();

        // Orden descendente: fecha más reciente primero
        for (int i = 0; i < lista.size(); i++) {
            ContenidoEducativo actual = lista.get(i);
            int j = 0;
            while (j < ordenada.size() && actual.getFechaPublicacion().isBefore(ordenada.get(j).getFechaPublicacion())) {
                j++;
            }
            insertarEnLista(ordenada, j, actual);
        }
        return ordenada;
    }

    private void insertarEnLista(ListaSimple<ContenidoEducativo> lista, int index, ContenidoEducativo dato) {
        lista.insertarEn(index, dato);
    }

    private void agregarPublicacionVista(ContenidoEducativo contenido) {
        VBox tarjeta = new VBox(5);
        tarjeta.setStyle("-fx-background-color: #ffffff; -fx-padding: 10; -fx-background-radius: 10; -fx-border-color: #cccccc;");

        Label titulo = new Label(contenido.getTitulo());
        titulo.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        Label descripcion = new Label(contenido.getDescripcion());
        Label autor = new Label("Publicado por: " + contenido.getAutor());

        // Mostrar promedio de valoraciones
        double promedio = contenido.getPromedioValoracion();
        Label promedioLabel = new Label("Valoración: " + (promedio > 0 ? String.format("%.1f", promedio) + " / 5 (" + contenido.getCantidadValoraciones() + ")" : "Sin valorar"));
        promedioLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #888;");

        tarjeta.getChildren().addAll(titulo, descripcion, promedioLabel);

        if ("Imagen".equalsIgnoreCase(contenido.getTipo()) &&
                contenido.getUrl().toLowerCase().matches(".*\\.(png|jpg|jpeg|gif|bmp)")) {
            try {
                Image imagen = new Image(new File(contenido.getUrl()).toURI().toString(), 300, 200, true, true);
                ImageView imageView = new ImageView(imagen);
                tarjeta.getChildren().add(imageView);
            } catch (Exception e) {
                tarjeta.getChildren().add(new Label("No se pudo cargar la imagen."));
            }
        }

        HBox botones = new HBox(10);
        botones.setAlignment(Pos.CENTER_LEFT);
        Button abrirBtn = new Button("Abrir");
        abrirBtn.setStyle("-fx-background-color: #007bff; -fx-text-fill: white;");
        abrirBtn.setOnAction(e -> abrirArchivo(contenido.getUrl()));
        botones.getChildren().add(abrirBtn);

        // Comprobar que estudianteActual no sea null antes de usarlo
        if (estudianteActual != null) {
            if (contenido.getAutor().equals(estudianteActual.getUsuario())) {
                Button eliminarBtn = new Button("Eliminar");
                eliminarBtn.setStyle("-fx-background-color: #007bff; -fx-text-fill: white;");
                eliminarBtn.setOnAction(e -> {
                    publicacionesListView.getItems().remove(contenido);
                    academix.eliminarContenido(contenido);
                    Persistencia.guardarRecursoBancoBinario(academix);
                    buscarContenido(); // actualizar vista tras eliminar
                });
                botones.getChildren().add(eliminarBtn);
            } else {
                // Control de valoración solo si NO es el autor
                int valoracionActual = contenido.getValoracionDe(estudianteActual.getUsuario());
                Label tuValoracion = new Label(valoracionActual > 0 ? "Tu valoración: " + valoracionActual : "Valorar:");
                ComboBox<Integer> valoracionCombo = new ComboBox<>();
                valoracionCombo.getItems().addAll(1, 2, 3, 4, 5);
                valoracionCombo.setValue(valoracionActual > 0 ? valoracionActual : 5);
                valoracionCombo.setDisable(valoracionActual > 0); // No permitir valorar dos veces

                Button valorarBtn = new Button("Enviar");
                valorarBtn.setDisable(valoracionActual > 0);
                valorarBtn.setOnAction(e -> {
                    int val = valoracionCombo.getValue();
                    contenido.valorar(estudianteActual.getUsuario(), val);
                    Persistencia.guardarRecursoBancoBinario(academix);
                    buscarContenido(); // refrescar vista
                });

                HBox valorarBox = new HBox(5, tuValoracion, valoracionCombo, valorarBtn);
                valorarBox.setAlignment(Pos.CENTER_LEFT);
                botones.getChildren().add(valorarBox);
            }
        }

        tarjeta.getChildren().addAll(botones, autor);
    }

    private void abrirArchivo(String ruta) {
        try {
            File archivo = new File(ruta);
            if (archivo.exists()) {
                Desktop.getDesktop().open(archivo);
            } else {
                mostrarAlerta("Archivo no encontrado", "La ruta no es válida.");
            }
        } catch (IOException e) {
            mostrarAlerta("Error al abrir", "No se pudo abrir el archivo.");
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    private void cargarPublicaciones() {
        publicacionesListView.getItems().clear();
        // Cargar todas las publicaciones de todos los estudiantes
        for (Estudiante est : academix.getListaEstudiantes()) {
            if (est.getPublicaciones() != null) {
                for (PublicacionItem pub : est.getPublicaciones()) {
                    publicacionesListView.getItems().add(pub);
                }
            }
        }
        // Ordenar por fecha más reciente
        publicacionesListView.getItems().sort((p1, p2) -> 
            p2.getFechaPublicacion().compareTo(p1.getFechaPublicacion()));
    }

    private void mostrarDialogoValoracion(PublicacionItem publicacion) {
        Dialog<Pair<Integer, String>> dialog = new Dialog<>();
        dialog.setTitle("Valorar Publicación");
        dialog.setHeaderText("¿Qué te pareció esta publicación?");

        // Crear los controles de valoración
        VBox contenidoDialog = new VBox(15);
        contenidoDialog.setAlignment(javafx.geometry.Pos.CENTER);

        // Estrellas
        HBox contenedorEstrellas = new HBox(5);
        contenedorEstrellas.setAlignment(javafx.geometry.Pos.CENTER);
        ToggleGroup grupo = new ToggleGroup();
        Label previewEstrellas = new Label("☆☆☆☆☆");
        previewEstrellas.setStyle("-fx-text-fill: #FFD700; -fx-font-size: 24px;");

        for (int i = 1; i <= 5; i++) {
            ToggleButton btn = new ToggleButton(String.valueOf(i));
            btn.setToggleGroup(grupo);
            btn.setUserData(i);
            btn.setStyle("-fx-background-radius: 15; -fx-pref-width: 30;");
            int estrellasFinal = i;
            btn.setOnAction(e -> previewEstrellas.setText("★".repeat(estrellasFinal) + "☆".repeat(5 - estrellasFinal)));
            contenedorEstrellas.getChildren().add(btn);
        }

        // Campo para comentario/sugerencia
        TextArea comentarioArea = new TextArea();
        comentarioArea.setPromptText("Escribe una sugerencia o comentario (opcional)");
        comentarioArea.setPrefRowCount(3);
        comentarioArea.setWrapText(true);

        contenidoDialog.getChildren().addAll(
            previewEstrellas, 
            contenedorEstrellas,
            new Separator(),
            new Label("Sugerencia:"),
            comentarioArea
        );
        
        dialog.getDialogPane().setContent(contenidoDialog);

        ButtonType valorarButtonType = new ButtonType("Valorar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(valorarButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == valorarButtonType) {
                ToggleButton seleccionado = (ToggleButton) grupo.getSelectedToggle();
                if (seleccionado != null) {
                    return new Pair<>((Integer) seleccionado.getUserData(), comentarioArea.getText().trim());
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(resultado -> {
            publicacion.agregarValoracion(
                estudianteActual.getUsuario(),
                estudianteActual.getNombre(),
                resultado.getKey(),
                resultado.getValue()
            );
            
            // Guardar en persistencia
            Academix academix = Persistencia.cargarRecursoBancoBinario();
            for (Estudiante est : academix.getListaEstudiantes()) {
                for (PublicacionItem pub : est.getPublicaciones()) {
                    if (pub.getContenido().equals(publicacion.getContenido()) && 
                        pub.getAutorId().equals(publicacion.getAutorId())) {
                        pub.agregarValoracion(
                            estudianteActual.getUsuario(),
                            estudianteActual.getNombre(),
                            resultado.getKey(),
                            resultado.getValue()
                        );
                        break;
                    }
                }
            }
            Persistencia.guardarRecursoBancoBinario(academix);
            
            // Actualizar ambas vistas
            cargarPublicaciones();
        });
    }

    private void mostrarDialogoComentario(PublicacionItem publicacion) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Agregar Comentario");
        dialog.setHeaderText("Escribe tu comentario");

        // Crear el área de texto para el comentario
        TextArea comentarioArea = new TextArea();
        comentarioArea.setPromptText("Escribe tu comentario aquí...");
        comentarioArea.setWrapText(true);
        
        dialog.getDialogPane().setContent(comentarioArea);

        ButtonType comentarButtonType = new ButtonType("Comentar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(comentarButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == comentarButtonType) {
                return comentarioArea.getText();
            }
            return null;
        });

        dialog.showAndWait().ifPresent(comentario -> {
            if (!comentario.trim().isEmpty()) {
                publicacion.agregarComentario(
                    estudianteActual.getUsuario(),
                    estudianteActual.getNombre(),
                    comentario
                );
                
                // Guardar en persistencia
                Academix academix = Persistencia.cargarRecursoBancoBinario();
                for (Estudiante est : academix.getListaEstudiantes()) {
                    for (PublicacionItem pub : est.getPublicaciones()) {
                        if (pub.getContenido().equals(publicacion.getContenido()) && 
                            pub.getAutorId().equals(publicacion.getAutorId())) {
                            pub.agregarComentario(
                                estudianteActual.getUsuario(),
                                estudianteActual.getNombre(),
                                comentario
                            );
                            break;
                        }
                    }
                }
                Persistencia.guardarRecursoBancoBinario(academix);
                
                // Actualizar la vista
                cargarPublicaciones();
            }
        });
    }

    public void refrescarPublicaciones() {
        academix = Persistencia.cargarRecursoBancoBinario();
        publicacionesListView.getItems().clear();
        for (Estudiante est : academix.getListaEstudiantes()) {
            if (est.getPublicaciones() != null && !est.getPublicaciones().isEmpty()) {
                publicacionesListView.getItems().addAll(est.getPublicaciones());
            }
        }
    }
}
