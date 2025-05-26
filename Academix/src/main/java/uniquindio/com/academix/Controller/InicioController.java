package uniquindio.com.academix.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import uniquindio.com.academix.Model.*;
import uniquindio.com.academix.Utils.Persistencia;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

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
    @FXML private VBox sugerenciasContainer;
    @FXML private VBox solicitudesPendientesContainer;
    @FXML private VBox amigosContainer;
    @FXML private ImageView perfilImageView;
    @FXML private Label nombreUsuarioLabel;
    @FXML private ChoiceBox<String> criterioBusquedaChoiceBox;

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

        // Agregar ChoiceBox para criterio de búsqueda
        if (criterioBusquedaChoiceBox != null) {
            criterioBusquedaChoiceBox.getItems().addAll("Tema", "Autor");
            criterioBusquedaChoiceBox.getSelectionModel().selectFirst();
            criterioBusquedaChoiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> buscarContenido());
        }

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
            private final ImageView imagenPublicacion = new ImageView();
            private final HBox interacciones = new HBox(10);
            private final Label estrellas = new Label();
            private final Button btnValorar = new Button("Valorar");
            private final Button btnComentar = new Button("Comentar");

            {
                // Configurar foto de perfil circular
                fotoPerfil.setFitHeight(40);
                fotoPerfil.setFitWidth(40);
                
                // Crear clip circular para la foto de perfil
                javafx.scene.shape.Circle clip = new javafx.scene.shape.Circle(20);
                clip.setCenterX(20);
                clip.setCenterY(20);
                fotoPerfil.setClip(clip);
                
                fotoPerfil.setStyle(
                    "-fx-background-radius: 20; " +
                    "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 2, 0, 0, 0); " +
                    "-fx-background-color: white; " +
                    "-fx-border-radius: 20; " +
                    "-fx-border-color: #e0e0e0; " +
                    "-fx-border-width: 1;"
                );

                // Configurar imagen de la publicación
                imagenPublicacion.setFitWidth(400);
                imagenPublicacion.setPreserveRatio(true);
                imagenPublicacion.setSmooth(true);

                // Estilos
                nombreUsuario.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-font-family: 'Segoe UI';");
                tiempoPublicacion.setStyle("-fx-text-fill: #65676b; -fx-font-size: 12px; -fx-font-family: 'Segoe UI';");
                texto.setWrapText(true);
                texto.setStyle("-fx-font-size: 14px; -fx-font-family: 'Segoe UI';");
                estrellas.setStyle("-fx-text-fill: #FFD700; -fx-font-size: 14px;");
                
                btnValorar.setStyle(
                    "-fx-background-color: #1a73e8; " +
                    "-fx-text-fill: white; " +
                    "-fx-font-size: 12px; " +
                    "-fx-font-family: 'Segoe UI'; " +
                    "-fx-cursor: hand;"
                );
                
                btnComentar.setStyle(
                    "-fx-background-color: #1a73e8; " +
                    "-fx-text-fill: white; " +
                    "-fx-font-size: 12px; " +
                    "-fx-font-family: 'Segoe UI'; " +
                    "-fx-cursor: hand;"
                );

                // Estructura
                infoUsuario.getChildren().addAll(nombreUsuario, tiempoPublicacion);
                encabezado.getChildren().addAll(fotoPerfil, infoUsuario);
                encabezado.setAlignment(Pos.CENTER_LEFT);
                interacciones.getChildren().addAll(estrellas, btnValorar, btnComentar);
                interacciones.setAlignment(Pos.CENTER_LEFT);
                
                contenido.getChildren().addAll(encabezado, texto);
                contenido.setStyle(
                    "-fx-padding: 15; " +
                    "-fx-background-color: white; " +
                    "-fx-background-radius: 10; " +
                    "-fx-border-color: #e4e6eb; " +
                    "-fx-border-radius: 10; " +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 1);"
                );
            }

            @Override
            protected void updateItem(PublicacionItem item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    nombreUsuario.setText(item.getAutorNombre());
                    tiempoPublicacion.setText(item.getTiempoTranscurrido());
                    String contenidoTexto = item.getContenido();
                    if (contenidoTexto.startsWith("Imagen seleccionada: ")) {
                        contenidoTexto = contenidoTexto.substring("Imagen seleccionada: ".length());
                    }
                    texto.setText(contenidoTexto);

                    // Imagen de perfil
                    try {
                        if (item.getImagenPerfil() != null) {
                            File archivoFoto = new File(item.getImagenPerfil());
                            if (archivoFoto.exists()) {
                                Image imagen = new Image(archivoFoto.toURI().toString());
                                fotoPerfil.setImage(imagen);
                            } else {
                                fotoPerfil.setImage(new Image(getClass().getResource("/images/img_1.png").toExternalForm()));
                            }
                        } else {
                            fotoPerfil.setImage(new Image(getClass().getResource("/images/img_1.png").toExternalForm()));
                        }
                    } catch (Exception e) {
                        try {
                            fotoPerfil.setImage(new Image(getClass().getResource("/images/img_1.png").toExternalForm()));
                        } catch (Exception ex) {}
                    }

                    // Limpiar posibles previos
                    contenido.getChildren().remove(imagenPublicacion);
                    contenido.getChildren().removeIf(node -> node instanceof HBox && "archivoBox".equals(node.getId()));

                    // Mostrar imagen, PDF, video o archivo con ícono
                    boolean archivoMostrado = false;
                    if (item.getRutaImagen() != null) {
                        String ruta = item.getRutaImagen();
                        String rutaLower = ruta.toLowerCase();
                        if (rutaLower.endsWith(".png") || rutaLower.endsWith(".jpg") || rutaLower.endsWith(".jpeg") || rutaLower.endsWith(".gif") || rutaLower.endsWith(".bmp")) {
                            try {
                                File archivoImagen = new File(ruta);
                                if (archivoImagen.exists()) {
                                    Image imagen = new Image(archivoImagen.toURI().toString());
                                    imagenPublicacion.setImage(imagen);
                                    if (!contenido.getChildren().contains(imagenPublicacion)) {
                                        int idx = contenido.getChildren().indexOf(interacciones);
                                        if (idx >= 0) {
                                            contenido.getChildren().add(idx, imagenPublicacion);
                                        } else {
                                            contenido.getChildren().add(imagenPublicacion);
                                        }
                                    }
                                    archivoMostrado = true;
                                }
                            } catch (Exception e) {
                                contenido.getChildren().remove(imagenPublicacion);
                            }
                        } else {
                            // Mostrar botón con ícono para abrir PDF, video u otro archivo
                            String tipoBoton = "Abrir Archivo";
                            if (rutaLower.endsWith(".pdf")) {
                                tipoBoton = "Abrir PDF";
                            } else if (rutaLower.endsWith(".mp4") || rutaLower.endsWith(".avi") || rutaLower.endsWith(".mov") || rutaLower.endsWith(".mkv")) {
                                tipoBoton = "Abrir Video";
                            }
                            Button abrirArchivoBtn = new Button(tipoBoton);
                            abrirArchivoBtn.setStyle("-fx-background-color: #1a73e8; -fx-text-fill: white; -fx-font-size: 12px; -fx-font-family: 'Segoe UI'; -fx-cursor: hand;");
                            abrirArchivoBtn.setOnAction(e -> abrirArchivo(ruta)); // <--- CORREGIDO: ahora abre el archivo
                            HBox archivoBox = new HBox(abrirArchivoBtn);
                            archivoBox.setId("archivoBox");
                            archivoBox.setAlignment(Pos.CENTER_LEFT);
                            archivoBox.setSpacing(10);
                            int idx = contenido.getChildren().indexOf(interacciones);
                            if (idx >= 0) {
                                contenido.getChildren().add(idx, archivoBox);
                            } else {
                                contenido.getChildren().add(archivoBox);
                            }
                            archivoMostrado = true;
                        }
                    }
                    if (!archivoMostrado) {
                        contenido.getChildren().remove(imagenPublicacion);
                    }

                    if (!contenido.getChildren().contains(interacciones)) {
                        contenido.getChildren().add(interacciones);
                    }

                    // Mostrar estrellas según la valoración del usuario actual si existe, si no el promedio
                    int estrellasUsuario = 0;
                    if (estudianteActual != null) {
                        for (int i = 0; i < item.getValoraciones().tamano(); i++) {
                            PublicacionItem.Valoracion v = item.getValoraciones().get(i);
                            if (v.getAutorId().equals(estudianteActual.getUsuario())) {
                                estrellasUsuario = v.getPuntuacion();
                                break;
                            }
                        }
                    }
                    int estrellasMostrar = estrellasUsuario > 0 ? estrellasUsuario : item.getPromedioValoraciones();
                    estrellas.setText("★".repeat(estrellasMostrar) + "☆".repeat(5 - estrellasMostrar));
                    btnValorar.setDisable(estudianteActual != null && item.getAutorId().equals(estudianteActual.getUsuario()));
                    btnValorar.setOnAction(e -> mostrarDialogoValoracion(item));
                    btnComentar.setOnAction(e -> mostrarDialogoComentario(item));
                    setGraphic(contenido);
                }
            }
        });

        // Inicializar contenedores
        sugerenciasContainer.setSpacing(10);
        solicitudesPendientesContainer.setSpacing(10);
    }

    public void setEstudianteActual(Estudiante estudiante) {
        this.estudianteActual = estudiante;
        academix = Persistencia.cargarRecursoBancoBinario();

        // Actualizar foto de perfil y nombre del usuario
        if (estudiante != null) {
            nombreUsuarioLabel.setText(estudiante.getNombre());
            actualizarFotoPerfil(estudiante.getFotoPerfil());
        }

        buscarContenido();
        cargarPublicaciones();
        cargarSugerenciasYSolicitudes();
    }

    private void actualizarFotoPerfil(String rutaFoto) {
        try {
            if (rutaFoto != null) {
                File archivoFoto = new File(rutaFoto);
                if (archivoFoto.exists()) {
                    Image imagen = new Image(archivoFoto.toURI().toString());
                    perfilImageView.setImage(imagen);
                } else {
                    perfilImageView.setImage(new Image(getClass().getResource("/images/img_1.png").toExternalForm()));
                }
            } else {
                perfilImageView.setImage(new Image(getClass().getResource("/images/img_1.png").toExternalForm()));
            }

            // Aplicar estilo circular
            javafx.scene.shape.Circle clip = new javafx.scene.shape.Circle(20);
            clip.setCenterX(20);
            clip.setCenterY(20);
            perfilImageView.setClip(clip);
            
            perfilImageView.setStyle(
                "-fx-background-radius: 20; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 2, 0, 0, 0); " +
                "-fx-background-color: white; " +
                "-fx-border-radius: 20; " +
                "-fx-border-color: #e0e0e0; " +
                "-fx-border-width: 1;"
            );
        } catch (Exception e) {
            System.err.println("Error al cargar la foto de perfil: " + e.getMessage());
            try {
                perfilImageView.setImage(new Image(getClass().getResource("/images/img_1.png").toExternalForm()));
            } catch (Exception ex) {
                System.err.println("Error al cargar la imagen por defecto: " + ex.getMessage());
            }
        }
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

        String contenidoPublicacion = titulo + "\n" + descripcion;
        PublicacionItem nuevaPublicacion = new PublicacionItem(
            contenidoPublicacion,
            estudianteActual.getUsuario(),
            estudianteActual.getNombre(),
            estudianteActual.getFotoPerfil()
        );

        // Copiar archivo a carpeta interna y guardar ruta relativa
        if (archivoSeleccionado != null && archivoSeleccionado.exists()) {
            try {
                File carpetaDestino = new File("data/archivos");
                if (!carpetaDestino.exists()) {
                    carpetaDestino.mkdirs();
                }
                String nombreArchivo = System.currentTimeMillis() + "_" + archivoSeleccionado.getName();
                File destino = new File(carpetaDestino, nombreArchivo);
                Files.copy(archivoSeleccionado.toPath(), destino.toPath(), StandardCopyOption.REPLACE_EXISTING);
                nuevaPublicacion.setRutaImagen("data/archivos/" + nombreArchivo);
            } catch (Exception e) {
                mostrarAlerta("Error", "No se pudo copiar el archivo adjunto.");
            }
        }

        estudianteActual.agregarPublicacion(nuevaPublicacion);
        Academix academix = Persistencia.cargarRecursoBancoBinario();
        Estudiante estudiantePersistente = academix.buscarEstudiante(estudianteActual.getUsuario());
        if (estudiantePersistente != null) {
            estudiantePersistente.agregarPublicacion(nuevaPublicacion);
            Persistencia.guardarRecursoBancoBinario(academix);
        }

        tituloField.clear();
        descripcionField.clear();
        tipoChoiceBox.getSelectionModel().selectFirst();
        urlField.clear();
        archivoSeleccionado = null;

        // Refrescar publicaciones en la vista de inicio inmediatamente
        refrescarPublicaciones();
    }

    public void buscarContenido() {
        String filtro = busquedaField != null ? busquedaField.getText().toLowerCase() : "";
        String criterioOrden = ordenChoiceBox != null ? ordenChoiceBox.getValue() : "Sin ordenar";
        String criterioBusqueda = criterioBusquedaChoiceBox != null ? criterioBusquedaChoiceBox.getValue() : "Tema";

        publicacionesListView.getItems().clear();
        if (academix != null) {
            ABB<PublicacionItem> abb;
            if ("Autor".equals(criterioBusqueda)) {
                abb = new ABB<>(new ABB.Comparador<PublicacionItem>() {
                    @Override
                    public int comparar(PublicacionItem a, PublicacionItem b) {
                        return a.getAutorNombre().compareToIgnoreCase(b.getAutorNombre());
                    }
                    @Override
                    public boolean cumpleFiltro(PublicacionItem dato, String filtro) {
                        return dato.getAutorNombre().toLowerCase().contains(filtro);
                    }
                });
            } else {
                abb = new ABB<>(new ABB.Comparador<PublicacionItem>() {
                    @Override
                    public int comparar(PublicacionItem a, PublicacionItem b) {
                        return a.getContenido().compareToIgnoreCase(b.getContenido());
                    }
                    @Override
                    public boolean cumpleFiltro(PublicacionItem dato, String filtro) {
                        return dato.getContenido().toLowerCase().contains(filtro);
                    }
                });
            }
            for (Estudiante est : academix.getListaEstudiantes()) {
                if (est.getPublicaciones() != null) {
                    for (PublicacionItem pub : est.getPublicaciones()) {
                        abb.insertar(pub);
                    }
                }
            }
            ListaSimple<PublicacionItem> resultado = abb.buscar(filtro);
            for (int i = 0; i < resultado.size(); i++) {
                publicacionesListView.getItems().add(resultado.get(i));
            }
            // Ordenar si corresponde
            if ("Fecha".equals(criterioOrden)) {
                publicacionesListView.getItems().sort((p1, p2) -> 
                    p2.getFechaPublicacion().compareTo(p1.getFechaPublicacion()));
            }
        }
    }

    private void agregarValoracionAEstudiante(PublicacionItem publicacion, int estrellas, String comentario) {
        // Buscar el estudiante autor de la publicación
        Academix academix = Persistencia.cargarRecursoBancoBinario();
        Estudiante autor = academix.buscarEstudiante(publicacion.getAutorId());
        if (autor != null) {
            ValoracionItem valoracion = new ValoracionItem(estudianteActual.getNombre(), estrellas, comentario, publicacion.getContenido());
            autor.agregarValoracion(valoracion);
            Persistencia.guardarRecursoBancoBinario(academix);
        }
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
            // Agregar la valoración al estudiante autor de la publicación
            agregarValoracionAEstudiante(publicacion, resultado.getKey(), resultado.getValue());
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
            if (est.getPublicaciones() != null && !est.getPublicaciones().estaVacia()) {
                for (PublicacionItem pub : est.getPublicaciones()) {
                    publicacionesListView.getItems().add(pub);
                }
            }
        }
        // Ordenar por fecha más reciente
        publicacionesListView.getItems().sort((p1, p2) -> 
            p2.getFechaPublicacion().compareTo(p1.getFechaPublicacion()));
    }

    // Método para cargar publicaciones (reagregado para evitar error de método no encontrado)
    private void cargarPublicaciones() {
        publicacionesListView.getItems().clear();
        for (Estudiante est : academix.getListaEstudiantes()) {
            if (est.getPublicaciones() != null && !est.getPublicaciones().estaVacia()) {
                for (PublicacionItem pub : est.getPublicaciones()) {
                    publicacionesListView.getItems().add(pub);
                }
            }
        }
        publicacionesListView.getItems().sort((p1, p2) -> 
            p2.getFechaPublicacion().compareTo(p1.getFechaPublicacion()));
    }

    private void cargarSugerenciasYSolicitudes() {
        cargarAmigos();
        cargarSugerencias();
        cargarSolicitudesPendientes();
    }

    private void cargarAmigos() {
        amigosContainer.getChildren().clear();
        Label tituloAmigos = new Label("Mis Amigos");
        tituloAmigos.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #666666; -fx-font-family: 'Segoe UI';");
        amigosContainer.getChildren().add(tituloAmigos);

        if (estudianteActual != null) {
            // Solo mostrar amigos cuando la solicitud ha sido aceptada
            ListaSimple<SolicitudAmistad> solicitudesAceptadas = new ListaSimple<>();
            ListaSimple<SolicitudAmistad> todas = academix.obtenerTodasLasSolicitudes();
            for (int i = 0; i < todas.size(); i++) {
                SolicitudAmistad s = todas.get(i);
                if ((s.getRemitente().equals(estudianteActual.getUsuario()) ||
                     s.getDestinatario().equals(estudianteActual.getUsuario())) &&
                    s.getEstado() == SolicitudAmistad.EstadoSolicitud.ACEPTADA) {
                    solicitudesAceptadas.agregar(s);
                }
            }

            if (solicitudesAceptadas.estaVacia()) {
                Label noAmigosLabel = new Label("No tienes amigos agregados");
                noAmigosLabel.setStyle("-fx-text-fill: #666666; -fx-font-size: 12px; -fx-font-family: 'Segoe UI';");
                amigosContainer.getChildren().add(noAmigosLabel);
            } else {
                for (int i = 0; i < solicitudesAceptadas.size(); i++) {
                    SolicitudAmistad solicitud = solicitudesAceptadas.get(i);
                    Estudiante amigo;
                    if (solicitud.getRemitente().equals(estudianteActual.getUsuario())) {
                        amigo = academix.buscarEstudiante(solicitud.getDestinatario());
                    } else {
                        amigo = academix.buscarEstudiante(solicitud.getRemitente());
                    }
                    if (amigo != null) {
                        VBox tarjeta = crearTarjetaAmigo(amigo);
                        amigosContainer.getChildren().add(tarjeta);
                    }
                }
            }
        }
    }

    private void cargarSugerencias() {
        sugerenciasContainer.getChildren().clear();
        Label tituloSugerencias = new Label("Sugerencias");
        tituloSugerencias.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #666666; -fx-font-family: 'Segoe UI';");
        sugerenciasContainer.getChildren().add(tituloSugerencias);
        
        if (estudianteActual != null) {
            ListaSimple<SugerenciasEstudio.SugerenciaCompañero> sugerencias =
                SugerenciasEstudio.obtenerSugerencias(estudianteActual, academix);
            
            if (sugerencias.estaVacia()) {
                Label noSugerenciasLabel = new Label("No hay sugerencias disponibles");
                noSugerenciasLabel.setStyle("-fx-text-fill: #666666; -fx-font-size: 12px; -fx-font-family: 'Segoe UI';");
                sugerenciasContainer.getChildren().add(noSugerenciasLabel);
            } else {
                for (int i = 0; i < sugerencias.size(); i++) {
                    SugerenciasEstudio.SugerenciaCompañero sugerencia = sugerencias.get(i);
                    boolean mostrarSugerencia = true;
                    
                    ListaSimple<SolicitudAmistad> solicitudes = academix.obtenerTodasLasSolicitudes();
                    for (int j = 0; j < solicitudes.size(); j++) {
                        SolicitudAmistad solicitud = solicitudes.get(j);
                        if ((solicitud.getRemitente().equals(estudianteActual.getUsuario()) && 
                             solicitud.getDestinatario().equals(sugerencia.getEstudiante().getUsuario())) ||
                            (solicitud.getRemitente().equals(sugerencia.getEstudiante().getUsuario()) && 
                             solicitud.getDestinatario().equals(estudianteActual.getUsuario()))) {
                            mostrarSugerencia = false;
                            break;
                        }
                    }
                    
                    if (mostrarSugerencia) {
                        VBox tarjeta = crearTarjetaSugerencia(sugerencia);
                        sugerenciasContainer.getChildren().add(tarjeta);
                    }
                }
            }
        }
    }

    private VBox crearTarjetaSugerencia(SugerenciasEstudio.SugerenciaCompañero sugerencia) {
        VBox tarjeta = new VBox(5);
        tarjeta.setStyle("-fx-background-color: white; -fx-padding: 10; -fx-border-color: #e0e0e0; -fx-border-radius: 5;");
        
        // Foto y nombre en la misma línea
        HBox infoBox = new HBox(10);
        infoBox.setAlignment(Pos.CENTER_LEFT);
        
        // Foto de perfil circular
        ImageView fotoPerfil = crearFotoPerfilCircular(sugerencia.getEstudiante().getFotoPerfil(), 40);
        
        VBox infoUsuario = new VBox(2);
        Label nombreLabel = new Label(sugerencia.getEstudiante().getNombre());
        nombreLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-font-family: 'Segoe UI';");
        Label universidadLabel = new Label(sugerencia.getEstudiante().getUniversidad());
        universidadLabel.setStyle("-fx-text-fill: #666666; -fx-font-size: 12px; -fx-font-family: 'Segoe UI';");
        
        // Agregar intereses en común si existen
        if (!sugerencia.getInteresesComunes().estaVacia()) {
            Label interesesLabel = new Label("Intereses en común: " + String.join(", ", sugerencia.getInteresesComunes()));
            interesesLabel.setStyle("-fx-text-fill: #1a73e8; -fx-font-size: 12px; -fx-font-family: 'Segoe UI';");
            interesesLabel.setWrapText(true);
            infoUsuario.getChildren().addAll(nombreLabel, universidadLabel, interesesLabel);
        } else {
            infoUsuario.getChildren().addAll(nombreLabel, universidadLabel);
        }
        
        infoBox.getChildren().addAll(fotoPerfil, infoUsuario);
        
        // Botón de enviar solicitud
        Button enviarSolicitudBtn = new Button("Enviar solicitud");
        enviarSolicitudBtn.setStyle("-fx-background-color: #1a73e8; -fx-text-fill: white; -fx-font-size: 12px; -fx-font-family: 'Segoe UI';");
        enviarSolicitudBtn.setOnAction(e -> enviarSolicitudAmistad(sugerencia.getEstudiante()));
        
        tarjeta.getChildren().addAll(infoBox, enviarSolicitudBtn);
        return tarjeta;
    }

    private void cargarSolicitudesPendientes() {
        solicitudesPendientesContainer.getChildren().clear();
        Label titleLabel = new Label("Solicitudes Pendientes");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #666666; -fx-font-family: 'Segoe UI';");
        solicitudesPendientesContainer.getChildren().add(titleLabel);
        
        if (estudianteActual != null) {
            // Obtener solo las solicitudes pendientes donde el usuario actual es el destinatario
            ListaSimple<SolicitudAmistad> solicitudes = new ListaSimple<>();
            ListaSimple<SolicitudAmistad> todas = academix.obtenerTodasLasSolicitudes();
            for (int i = 0; i < todas.size(); i++) {
                SolicitudAmistad s = todas.get(i);
                if (s.getDestinatario().equals(estudianteActual.getUsuario()) &&
                    s.getEstado() == SolicitudAmistad.EstadoSolicitud.PENDIENTE) {
                    solicitudes.agregar(s);
                }
            }
            
            if (solicitudes.estaVacia()) {
                Label noSolicitudesLabel = new Label("No tienes solicitudes pendientes");
                noSolicitudesLabel.setStyle("-fx-text-fill: #666666; -fx-font-size: 12px;");
                solicitudesPendientesContainer.getChildren().add(noSolicitudesLabel);
            } else {
                for (int i = 0; i < solicitudes.size(); i++) {
                    SolicitudAmistad solicitud = solicitudes.get(i);
                    VBox tarjeta = crearTarjetaSolicitud(solicitud);
                    solicitudesPendientesContainer.getChildren().add(tarjeta);
                }
            }
        }
    }

    private VBox crearTarjetaSolicitud(SolicitudAmistad solicitud) {
        VBox tarjeta = new VBox(5);
        tarjeta.setStyle("-fx-background-color: white; -fx-padding: 10; -fx-border-color: #e0e0e0; -fx-border-radius: 5;");
        
        Estudiante remitente = academix.buscarEstudiante(solicitud.getRemitente());
        
        // Foto y nombre en la misma línea
        HBox infoBox = new HBox(10);
        infoBox.setAlignment(Pos.CENTER_LEFT);
        
        // Foto de perfil
        ImageView fotoPerfil = new ImageView();
        fotoPerfil.setFitHeight(40);
        fotoPerfil.setFitWidth(40);
        
        try {
            if (remitente.getFotoPerfil() != null) {
                File archivoFoto = new File(remitente.getFotoPerfil());
                if (archivoFoto.exists()) {
                    fotoPerfil.setImage(new Image(archivoFoto.toURI().toString()));
                } else {
                    fotoPerfil.setImage(new Image(getClass().getResource("/images/img_1.png").toExternalForm()));
                }
            } else {
                fotoPerfil.setImage(new Image(getClass().getResource("/images/img_1.png").toExternalForm()));
            }
        } catch (Exception e) {
            System.err.println("Error al cargar la foto de perfil: " + e.getMessage());
        }
        
        VBox infoUsuario = new VBox(2);
        Label nombreLabel = new Label(remitente.getNombre());
        nombreLabel.setStyle("-fx-font-weight: bold;");
        Label universidadLabel = new Label(remitente.getUniversidad());
        universidadLabel.setStyle("-fx-text-fill: #666666; -fx-font-size: 12px;");
        infoUsuario.getChildren().addAll(nombreLabel, universidadLabel);
        
        infoBox.getChildren().addAll(fotoPerfil, infoUsuario);
        
        // Botones de acción
        HBox botonesBox = new HBox(10);
        botonesBox.setAlignment(Pos.CENTER_LEFT);
        botonesBox.setStyle("-fx-padding: 5 0 0 0;");
        
        Button aceptarBtn = new Button("Aceptar");
        aceptarBtn.setStyle("-fx-background-color: #1a73e8; -fx-text-fill: white; -fx-font-size: 12px; -fx-cursor: hand;");
        aceptarBtn.setOnAction(e -> {
            aceptarSolicitud(solicitud);
            mostrarAlerta("Solicitud aceptada", "Ahora eres amigo de " + remitente.getNombre());
        });
        
        Button rechazarBtn = new Button("Eliminar");
        rechazarBtn.setStyle("-fx-background-color: #1a73e8; -fx-text-fill: white; -fx-font-size: 12px; -fx-cursor: hand;");
        rechazarBtn.setOnAction(e -> {
            rechazarSolicitud(solicitud);
            mostrarAlerta("Solicitud eliminada", "Has eliminado la solicitud de " + remitente.getNombre());
        });
        
        botonesBox.getChildren().addAll(aceptarBtn, rechazarBtn);
        
        tarjeta.getChildren().addAll(infoBox, botonesBox);
        return tarjeta;
    }

    private void enviarSolicitudAmistad(Estudiante destinatario) {
        // 1. Enviar la solicitud
        academix.enviarSolicitudAmistad(estudianteActual.getUsuario(), destinatario.getUsuario());
        
        // 2. Guardar en persistencia
        Persistencia.guardarRecursoBancoBinario(academix);
        
        // 3. Recargar la vista de sugerencias y solicitudes
        cargarSugerenciasYSolicitudes();
        
        // 4. Mostrar mensaje de confirmación
        mostrarAlerta("Solicitud enviada", 
            "Se ha enviado una solicitud de amistad a " + destinatario.getNombre());
    }

    private void aceptarSolicitud(SolicitudAmistad solicitud) {
        // 1. Aceptar la solicitud
        academix.aceptarSolicitudAmistad(solicitud.getRemitente(), solicitud.getDestinatario());
        
        // 2. Guardar en persistencia
        Persistencia.guardarRecursoBancoBinario(academix);
        
        // 3. Recargar la vista de sugerencias y solicitudes
        cargarSugerenciasYSolicitudes();
        
        // 4. Mostrar mensaje de confirmación
        Estudiante remitente = academix.buscarEstudiante(solicitud.getRemitente());
        if (remitente != null) {
            mostrarAlerta("Solicitud aceptada", 
                "Ahora eres amigo de " + remitente.getNombre());
        }
    }

    private void rechazarSolicitud(SolicitudAmistad solicitud) {
        // 1. Rechazar la solicitud
        academix.rechazarSolicitudAmistad(solicitud.getRemitente(), solicitud.getDestinatario());
        
        // 2. Guardar en persistencia
        Persistencia.guardarRecursoBancoBinario(academix);
        
        // 3. Recargar la vista de sugerencias y solicitudes
        cargarSugerenciasYSolicitudes();
    }

    private VBox crearTarjetaAmigo(Estudiante amigo) {
        VBox tarjeta = new VBox(5);
        tarjeta.setStyle("-fx-background-color: white; -fx-padding: 10; -fx-border-color: #e0e0e0; -fx-border-radius: 5;");
        HBox infoBox = new HBox(10);
        infoBox.setAlignment(Pos.CENTER_LEFT);
        ImageView fotoPerfil = crearFotoPerfilCircular(amigo.getFotoPerfil(), 40);
        VBox infoUsuario = new VBox(2);
        Label nombreLabel = new Label(amigo.getNombre());
        nombreLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-font-family: 'Segoe UI';");
        Label universidadLabel = new Label(amigo.getUniversidad());
        universidadLabel.setStyle("-fx-text-fill: #666666; -fx-font-size: 12px; -fx-font-family: 'Segoe UI';");
        infoUsuario.getChildren().addAll(nombreLabel, universidadLabel);
        infoBox.getChildren().addAll(fotoPerfil, infoUsuario);
        Button eliminarAmigoBtn = new Button("Eliminar amigo");
        eliminarAmigoBtn.setStyle("-fx-background-color: #e53935; -fx-text-fill: white; -fx-font-size: 12px; -fx-cursor: hand; -fx-font-family: 'Segoe UI';");
        eliminarAmigoBtn.setOnAction(e -> {
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Confirmar eliminación");
            confirmacion.setHeaderText("¿Estás seguro de que quieres eliminar a " + amigo.getNombre() + " de tu lista de amigos?");
            confirmacion.setContentText("Esta acción no se puede deshacer.");
            confirmacion.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    eliminarAmigo(amigo);
                }
            });
        });
        tarjeta.getChildren().addAll(infoBox, eliminarAmigoBtn);
        return tarjeta;
    }

    private ImageView crearFotoPerfilCircular(String rutaFoto, double size) {
        ImageView fotoPerfil = new ImageView();
        fotoPerfil.setFitHeight(size);
        fotoPerfil.setFitWidth(size);
        try {
            if (rutaFoto != null) {
                File archivoFoto = new File(rutaFoto);
                if (archivoFoto.exists()) {
                    fotoPerfil.setImage(new Image(archivoFoto.toURI().toString()));
                } else {
                    fotoPerfil.setImage(new Image(getClass().getResource("/images/img_1.png").toExternalForm()));
                }
            } else {
                fotoPerfil.setImage(new Image(getClass().getResource("/images/img_1.png").toExternalForm()));
            }
        } catch (Exception e) {
            try {
                fotoPerfil.setImage(new Image(getClass().getResource("/images/img_1.png").toExternalForm()));
            } catch (Exception ex) {}
        }
        fotoPerfil.setStyle(
            "-fx-background-radius: " + (size/2) + "; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 2, 0, 0, 0); " +
            "-fx-background-color: white; " +
            "-fx-border-radius: " + (size/2) + "; " +
            "-fx-border-color: #e0e0e0; " +
            "-fx-border-width: 1;"
        );
        javafx.scene.shape.Circle clip = new javafx.scene.shape.Circle(size/2);
        clip.setCenterX(size/2);
        clip.setCenterY(size/2);
        fotoPerfil.setClip(clip);
        return fotoPerfil;
    }

    private void eliminarAmigo(Estudiante amigo) {
        ListaSimple<SolicitudAmistad> solicitudes = academix.obtenerTodasLasSolicitudes();
        for (int i = 0; i < solicitudes.size(); i++) {
            SolicitudAmistad solicitud = solicitudes.get(i);
            if ((solicitud.getRemitente().equals(estudianteActual.getUsuario()) && 
                 solicitud.getDestinatario().equals(amigo.getUsuario())) ||
                (solicitud.getRemitente().equals(amigo.getUsuario()) && 
                 solicitud.getDestinatario().equals(estudianteActual.getUsuario()))) {
                academix.eliminarSolicitudAmistad(solicitud);
                break;
            }
        }
        Persistencia.guardarRecursoBancoBinario(academix);
        cargarSugerenciasYSolicitudes();
        mostrarAlerta("Amigo eliminado", "Has eliminado a " + amigo.getNombre() + " de tu lista de amigos");
    }

    // Método para mostrar alertas (reagregado para evitar error de método no encontrado)
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    // Método para abrir archivos desde la celda de publicación
    private void abrirArchivo(String ruta) {
        try {
            File archivo = new File(ruta);
            if (!archivo.isAbsolute()) {
                archivo = new File(System.getProperty("user.dir"), ruta);
            }
            archivo = archivo.getCanonicalFile();
            if (archivo.exists()) {
                boolean abierto = false;
                try {
                    if (java.awt.Desktop.isDesktopSupported() && java.awt.Desktop.getDesktop().isSupported(java.awt.Desktop.Action.OPEN)) {
                        java.awt.Desktop.getDesktop().open(archivo);
                        abierto = true;
                    }
                } catch (Exception ex) {
                    // ignorar
                }
                if (!abierto && System.getProperty("os.name").toLowerCase().contains("win")) {
                    try {
                        new ProcessBuilder("cmd", "/c", "start", "", archivo.getAbsolutePath()).start();
                        abierto = true;
                    } catch (Exception ex2) {
                        // ignorar
                    }
                }
                if (!abierto) {
                    mostrarAlerta("No soportado", "No se pudo abrir el archivo automáticamente.\nRuta: " + archivo.getAbsolutePath());
                }
            } else {
                mostrarAlerta("Archivo no encontrado", "La ruta no es válida o el archivo no existe: " + archivo.getAbsolutePath());
            }
        } catch (Exception e) {
            mostrarAlerta("Error al abrir", "No se pudo abrir el archivo.\n" + e.getMessage());
        }
    }
}
