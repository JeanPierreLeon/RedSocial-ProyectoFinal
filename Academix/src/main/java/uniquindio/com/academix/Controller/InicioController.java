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
import uniquindio.com.academix.Model.SugerenciasEstudio;
import uniquindio.com.academix.Model.SolicitudAmistad;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
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
                    
                    // Mostrar el contenido sin la parte de "Imagen seleccionada: "
                    String contenidoTexto = item.getContenido();
                    if (contenidoTexto.startsWith("Imagen seleccionada: ")) {
                        contenidoTexto = contenidoTexto.substring("Imagen seleccionada: ".length());
                    }
                    texto.setText(contenidoTexto);
                    
                    // Cargar imagen de perfil con manejo de errores mejorado
                    try {
                        if (item.getImagenPerfil() != null) {
                            File archivoFoto = new File(item.getImagenPerfil());
                            if (archivoFoto.exists()) {
                                Image imagen = new Image(archivoFoto.toURI().toString());
                                fotoPerfil.setImage(imagen);
                            } else {
                                System.out.println("Archivo de foto de perfil no encontrado: " + item.getImagenPerfil());
                                fotoPerfil.setImage(new Image(new File("src/main/resources/images/img_1.png").toURI().toString()));
                            }
                        } else {
                            System.out.println("Ruta de foto de perfil es null para: " + item.getAutorNombre());
                            fotoPerfil.setImage(new Image(new File("src/main/resources/images/img_1.png").toURI().toString()));
                        }
                    } catch (Exception e) {
                        System.err.println("Error cargando foto de perfil para " + item.getAutorNombre() + ": " + e.getMessage());
                        try {
                            fotoPerfil.setImage(new Image(new File("src/main/resources/images/img_1.png").toURI().toString()));
                        } catch (Exception ex) {
                            System.err.println("Error al cargar la imagen por defecto: " + ex.getMessage());
                        }
                    }

                    // Cargar imagen de la publicación si existe
                    if (item.getRutaImagen() != null) {
                        try {
                            File archivoImagen = new File(item.getRutaImagen());
                            if (archivoImagen.exists()) {
                                Image imagen = new Image(archivoImagen.toURI().toString());
                                imagenPublicacion.setImage(imagen);
                                if (!contenido.getChildren().contains(imagenPublicacion)) {
                                    int idx = contenido.getChildren().indexOf(interacciones);
                                    if (idx >= 0) {
                                        contenido.getChildren().add(idx, imagenPublicacion);
                                    } else {
                                        contenido.getChildren().add(imagenPublicacion); // lo agrega al final si no encuentra interacciones
                                    }
                                }
                            } else {
                                System.out.println("Archivo de imagen de publicación no encontrado: " + item.getRutaImagen());
                                contenido.getChildren().remove(imagenPublicacion);
                            }
                        } catch (Exception e) {
                            System.err.println("Error cargando imagen de publicación: " + e.getMessage());
                            contenido.getChildren().remove(imagenPublicacion);
                        }
                    } else {
                        contenido.getChildren().remove(imagenPublicacion);
                    }

                    // Asegurarse de que las interacciones estén al final
                    if (!contenido.getChildren().contains(interacciones)) {
                        contenido.getChildren().add(interacciones);
                    }

                    int promedio = item.getPromedioValoraciones();
                    estrellas.setText("★".repeat(promedio) + "☆".repeat(5 - promedio));
                    
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
                    perfilImageView.setImage(new Image(new File("src/main/resources/images/img_1.png").toURI().toString()));
                }
            } else {
                perfilImageView.setImage(new Image(new File("src/main/resources/images/img_1.png").toURI().toString()));
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
                perfilImageView.setImage(new Image(new File("src/main/resources/images/img_1.png").toURI().toString()));
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
            if (est.getPublicaciones() != null && !est.getPublicaciones().estaVacia()) {
                for (PublicacionItem pub : est.getPublicaciones()) {
                    publicacionesListView.getItems().add(pub);
                }
            }
        }
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
            java.util.List<SolicitudAmistad> solicitudesAceptadas = new java.util.ArrayList<>();
            ListaSimple<SolicitudAmistad> todas = academix.obtenerTodasLasSolicitudes();
            for (int i = 0; i < todas.size(); i++) {
                SolicitudAmistad s = todas.get(i);
                if ((s.getRemitente().equals(estudianteActual.getUsuario()) ||
                     s.getDestinatario().equals(estudianteActual.getUsuario())) &&
                    s.getEstado() == SolicitudAmistad.EstadoSolicitud.ACEPTADA) {
                    solicitudesAceptadas.add(s);
                }
            }

            if (solicitudesAceptadas.isEmpty()) {
                Label noAmigosLabel = new Label("No tienes amigos agregados");
                noAmigosLabel.setStyle("-fx-text-fill: #666666; -fx-font-size: 12px; -fx-font-family: 'Segoe UI';");
                amigosContainer.getChildren().add(noAmigosLabel);
            } else {
                for (SolicitudAmistad solicitud : solicitudesAceptadas) {
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
                    fotoPerfil.setImage(new Image(new File("src/main/resources/images/img_1.png").toURI().toString()));
                }
            } else {
                fotoPerfil.setImage(new Image(new File("src/main/resources/images/img_1.png").toURI().toString()));
            }
        } catch (Exception e) {
            System.err.println("Error al cargar la foto de perfil: " + e.getMessage());
            try {
                fotoPerfil.setImage(new Image(new File("src/main/resources/images/img_1.png").toURI().toString()));
            } catch (Exception ex) {
                System.err.println("Error al cargar la imagen por defecto: " + ex.getMessage());
            }
        }

        // Aplicar estilo circular
        fotoPerfil.setStyle(
            "-fx-background-radius: " + (size/2) + "; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 2, 0, 0, 0); " +
            "-fx-background-color: white; " +
            "-fx-border-radius: " + (size/2) + "; " +
            "-fx-border-color: #e0e0e0; " +
            "-fx-border-width: 1;"
        );

        // Crear y aplicar el recorte circular
        javafx.scene.shape.Circle clip = new javafx.scene.shape.Circle(size/2);
        clip.setCenterX(size/2);
        clip.setCenterY(size/2);
        fotoPerfil.setClip(clip);

        return fotoPerfil;
    }

    private VBox crearTarjetaAmigo(Estudiante amigo) {
        VBox tarjeta = new VBox(5);
        tarjeta.setStyle("-fx-background-color: white; -fx-padding: 10; -fx-border-color: #e0e0e0; -fx-border-radius: 5;");
        
        // Foto y nombre en la misma línea
        HBox infoBox = new HBox(10);
        infoBox.setAlignment(Pos.CENTER_LEFT);
        
        // Foto de perfil circular
        ImageView fotoPerfil = crearFotoPerfilCircular(amigo.getFotoPerfil(), 40);
        
        VBox infoUsuario = new VBox(2);
        Label nombreLabel = new Label(amigo.getNombre());
        nombreLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-font-family: 'Segoe UI';");
        Label universidadLabel = new Label(amigo.getUniversidad());
        universidadLabel.setStyle("-fx-text-fill: #666666; -fx-font-size: 12px; -fx-font-family: 'Segoe UI';");
        infoUsuario.getChildren().addAll(nombreLabel, universidadLabel);
        
        infoBox.getChildren().addAll(fotoPerfil, infoUsuario);

        // Botón de eliminar amigo
        Button eliminarAmigoBtn = new Button("Eliminar amigo");
        eliminarAmigoBtn.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-font-size: 12px; -fx-cursor: hand; -fx-font-family: 'Segoe UI';");
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

    private void eliminarAmigo(Estudiante amigo) {
        // Buscar y eliminar la solicitud de amistad correspondiente
        ListaSimple<SolicitudAmistad> solicitudes = academix.obtenerTodasLasSolicitudes();
        for (SolicitudAmistad solicitud : solicitudes) {
            if ((solicitud.getRemitente().equals(estudianteActual.getUsuario()) && 
                 solicitud.getDestinatario().equals(amigo.getUsuario())) ||
                (solicitud.getRemitente().equals(amigo.getUsuario()) && 
                 solicitud.getDestinatario().equals(estudianteActual.getUsuario()))) {
                
                // Eliminar la solicitud
                academix.eliminarSolicitudAmistad(solicitud);
                break;
            }
        }

        // Guardar en persistencia
        Persistencia.guardarRecursoBancoBinario(academix);
        
        // Recargar la vista
        cargarSugerenciasYSolicitudes();
        
        // Mostrar mensaje de confirmación
        mostrarAlerta("Amigo eliminado", 
            "Has eliminado a " + amigo.getNombre() + " de tu lista de amigos");
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
                for (SugerenciasEstudio.SugerenciaCompañero sugerencia : sugerencias) {
                    boolean mostrarSugerencia = true;
                    
                    for (SolicitudAmistad solicitud : academix.obtenerTodasLasSolicitudes()) {
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
            java.util.List<SolicitudAmistad> solicitudes = new java.util.ArrayList<>();
            ListaSimple<SolicitudAmistad> todas = academix.obtenerTodasLasSolicitudes();
            for (int i = 0; i < todas.size(); i++) {
                SolicitudAmistad s = todas.get(i);
                if (s.getDestinatario().equals(estudianteActual.getUsuario()) &&
                    s.getEstado() == SolicitudAmistad.EstadoSolicitud.PENDIENTE) {
                    solicitudes.add(s);
                }
            }
            
            if (solicitudes.isEmpty()) {
                Label noSolicitudesLabel = new Label("No tienes solicitudes pendientes");
                noSolicitudesLabel.setStyle("-fx-text-fill: #666666; -fx-font-size: 12px;");
                solicitudesPendientesContainer.getChildren().add(noSolicitudesLabel);
            } else {
                for (SolicitudAmistad solicitud : solicitudes) {
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
                    fotoPerfil.setImage(new Image(new File("src/main/resources/images/img_1.png").toURI().toString()));
                }
            } else {
                fotoPerfil.setImage(new Image(new File("src/main/resources/images/img_1.png").toURI().toString()));
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
        rechazarBtn.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-font-size: 12px; -fx-cursor: hand;");
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
}
