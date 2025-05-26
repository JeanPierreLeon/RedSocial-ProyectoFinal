package uniquindio.com.academix.Controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Separator;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.util.Pair;
import uniquindio.com.academix.Model.Academix;
import uniquindio.com.academix.Model.Estudiante;
import uniquindio.com.academix.Model.PublicacionItem;
import uniquindio.com.academix.Model.ValoracionItem;
import uniquindio.com.academix.Utils.Persistencia;

public class PanelController {
    @FXML
    private ImageView portadaImageView;
    
   

    @FXML
private ListView<String> conexionesListView;

    @FXML
    private ImageView perfilImageView;
    
    @FXML
    private ImageView perfilMiniaturaImageView;
    
    @FXML
    private Label nombreUsuarioLabel;
    
    @FXML
    private Label correoLabel;
    
    @FXML
    private Label ubicacionLabel;
    
    @FXML
    private Label estudiosLabel;
    
    @FXML
    private GridPane amigosGrid;
    
    @FXML
    private ListView<PublicacionItem> contenidosListView;
    
    @FXML
    private Label valoracionPromedioLabel;
    
    @FXML
    private ListView<ValoracionItem> valoracionesListView;
    
    @FXML
    private ListView<String> sugerenciasListView;

    @FXML
    private ListView<String> solicitudesListView;

    @FXML
    private ComboBox<String> categoriaAyudaComboBox;

    @FXML
    private TextArea descripcionAyudaTextArea;

    @FXML
    private TextField publicacionTextField;

    @FXML
    private FlowPane materiasInteresFlowPane;

    @FXML
    private Label estudiosDetalleLabel;

    private Estudiante estudianteActual;
    private Academix academix;

   @FXML
private Spinner<Integer> spinnerUrgencia;

    // --- REFRESCO AUTOMÁTICO DEL PANEL DEL ESTUDIANTE DESDE OTRAS VISTAS ---
    private static PanelController instanciaActual;

    public PanelController() {
        instanciaActual = this;
    }

    public static void refrescarPanelEstudiante() {
        if (instanciaActual != null) {
            instanciaActual.cargarDatosEstudiante();
        }
    }

@FXML
public void initialize() {
    // Spinner urgencia rango 1 a 10, editable
    SpinnerValueFactory<Integer> valueFactory =
        new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 1);
    if (spinnerUrgencia != null) {
        spinnerUrgencia.setValueFactory(valueFactory);
        spinnerUrgencia.setEditable(true);
    }

    // Cargar materias en comboBox
    if (categoriaAyudaComboBox != null) {
        categoriaAyudaComboBox.getItems().addAll(
            "Matemáticas",
            "Programación",
            "Física",
            "Química",
            "Biología",
            "Historia",
            "Literatura"
        );
    }

    // Configurar la lista de publicaciones
    configurarListaPublicaciones();

    // Configurar el cell factory para las valoraciones
    if (valoracionesListView != null) {
        valoracionesListView.setCellFactory(lv -> new ListCell<ValoracionItem>() {
            private final HBox contenido = new HBox(10);
            private final Label estrellas = new Label();
            private final VBox detalles = new VBox(5);
            private final Label comentario = new Label();
            private final Label evaluador = new Label();
            private final Label publicacion = new Label(); // NUEVO

            {
                detalles.getChildren().addAll(comentario, evaluador, publicacion); // Agregar publicacion
                contenido.getChildren().addAll(estrellas, detalles);
                estrellas.setStyle("-fx-font-size: 16px; -fx-text-fill: #FFD700;");
                evaluador.setStyle("-fx-font-size: 12px; -fx-text-fill: #666666;");
                publicacion.setStyle("-fx-font-size: 12px; -fx-text-fill: #1a73e8;");
            }

            @Override
            protected void updateItem(ValoracionItem item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    estrellas.setText("★".repeat(item.getEstrellas()) + "☆".repeat(5 - item.getEstrellas()));
                    comentario.setText(item.getComentario());
                    evaluador.setText("Por: " + item.getEvaluador());
                    if (item.getPublicacion() != null && !item.getPublicacion().isEmpty()) {
                        publicacion.setText("Publicación: " + item.getPublicacion());
                    } else {
                        publicacion.setText("");
                    }
                    setGraphic(contenido);
                }
            }
        });
    }

    // Configurar eventos de hover para los botones de foto
    if (perfilImageView != null && perfilImageView.getParent() instanceof StackPane) {
        StackPane fotoPerfilContainer = (StackPane) perfilImageView.getParent();
        Button btnCambiarFoto = (Button) fotoPerfilContainer.getChildren().stream()
            .filter(node -> node instanceof Button)
            .findFirst()
            .orElse(null);

        if (btnCambiarFoto != null) {
            fotoPerfilContainer.setOnMouseEntered(e -> btnCambiarFoto.setVisible(true));
            fotoPerfilContainer.setOnMouseExited(e -> btnCambiarFoto.setVisible(false));
        }
    }
}

    @FXML
    public void onSolicitarAyuda() {
        String categoria = categoriaAyudaComboBox.getValue();
        String descripcion = descripcionAyudaTextArea.getText();

        if (categoria == null || descripcion.isEmpty()) {
            mostrarAlerta("Error", "Por favor selecciona una materia y describe tu solicitud.");
            return;
        }

        // Agregar la solicitud a la lista
        String solicitud = String.format("%s: %s", categoria, descripcion);
        if (solicitudesListView != null) {
            solicitudesListView.getItems().add(0, solicitud);
        }

        // Limpiar campos
        categoriaAyudaComboBox.setValue(null);
        descripcionAyudaTextArea.clear();

        mostrarAlerta("Éxito", "Tu solicitud de ayuda ha sido publicada.");
    }

    @FXML
    public void onAgregarFotoVideo() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Foto o Video");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg", "*.gif"),
            new FileChooser.ExtensionFilter("Videos", "*.mp4", "*.avi", "*.mov")
        );
        
        File archivo = fileChooser.showOpenDialog(null);
        if (archivo != null) {
            String rutaArchivo = archivo.getAbsolutePath();
            // Guardar la ruta del archivo en el campo de texto
            if (publicacionTextField != null) {
                publicacionTextField.setUserData(rutaArchivo); // Guardamos la ruta completa
                publicacionTextField.setText("Imagen seleccionada: " + archivo.getName());
            }
        }
    }

    @FXML
    public void onAgregarMaterial() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Material de Estudio");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Documentos", "*.pdf", "*.doc", "*.docx", "*.ppt", "*.pptx")
        );
        
        File archivo = fileChooser.showOpenDialog(null);
        if (archivo != null) {
            // TODO: Implementar lógica para manejar el archivo seleccionado
            if (publicacionTextField != null) {
                publicacionTextField.setText("Material seleccionado: " + archivo.getName());
            }
        }
    }

    @FXML
    public void onPublicar() {
        if (publicacionTextField == null) return;
        String contenido = publicacionTextField.getText();
        if (contenido == null || contenido.isEmpty()) {
            mostrarAlerta("Error", "Por favor escribe algo para publicar.");
            return;
        }

        // Obtener la ruta de la imagen si existe
        String rutaImagenOriginal = (String) publicacionTextField.getUserData();
        String rutaImagenRelativa = null;

        // Copiar archivo a carpeta interna y guardar ruta relativa
        if (rutaImagenOriginal != null) {
            try {
                File archivoOriginal = new File(rutaImagenOriginal);
                if (archivoOriginal.exists()) {
                    File carpetaDestino = new File("data/archivos");
                    if (!carpetaDestino.exists()) {
                        carpetaDestino.mkdirs();
                    }
                    String nombreArchivo = System.currentTimeMillis() + "_" + archivoOriginal.getName();
                    File destino = new File(carpetaDestino, nombreArchivo);
                    Files.copy(archivoOriginal.toPath(), destino.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    rutaImagenRelativa = "data/archivos/" + nombreArchivo;
                }
            } catch (Exception e) {
                mostrarAlerta("Error", "No se pudo copiar el archivo adjunto.");
            }
        }

        PublicacionItem nuevaPublicacion = new PublicacionItem(
            contenido,
            estudianteActual.getUsuario(),
            estudianteActual.getNombre(),
            estudianteActual.getFotoPerfil()
        );

        if (rutaImagenRelativa != null) {
            nuevaPublicacion.setRutaImagen(rutaImagenRelativa);
        }

        if (estudianteActual != null) {
            estudianteActual.agregarPublicacion(nuevaPublicacion);

            Academix academix = Persistencia.cargarRecursoBancoBinario();
            Estudiante estudiantePersistente = academix.buscarEstudiante(estudianteActual.getUsuario());
            if (estudiantePersistente != null) {
                estudiantePersistente.agregarPublicacion(nuevaPublicacion);
                Persistencia.guardarRecursoBancoBinario(academix);
            }
        }

        publicacionTextField.clear();
        publicacionTextField.setUserData(null);
        if (contenidosListView != null) {
            contenidosListView.getItems().add(0, nuevaPublicacion);
        }

        try {
            InicioController.refrescarPublicacionesDesdePanel();
        } catch (Exception ex) {
            // Silenciar si no está disponible
        }
    }

    private void configurarListaPublicaciones() {
        if (contenidosListView == null) return;
        contenidosListView.setCellFactory(lv -> new ListCell<PublicacionItem>() {
            private final VBox contenido = new VBox(10);
            private final HBox encabezado = new HBox(10);
            private final ImageView fotoPerfil = new ImageView();
            private final VBox infoUsuario = new VBox(5);
            private final Label nombreUsuario = new Label();
            private final Label tiempoPublicacion = new Label();
            private final Label texto = new Label();
            private final ImageView imagenPublicacion = new ImageView();
            private final VBox valoracionesBox = new VBox(5);
            private final HBox interacciones = new HBox(10);
            private final Label estrellas = new Label();
            private final Button btnValorar = new Button("Valorar");
            private final Button btnComentar = new Button("Comentar");
            private final Button btnEliminar = new Button("Eliminar");
            // NUEVO: para abrir archivos adjuntos
            private final HBox archivoBox = new HBox();
            private final Button btnAbrirArchivo = new Button();

            {
                // Configurar foto de perfil
                fotoPerfil.setFitHeight(40);
                fotoPerfil.setFitWidth(40);
                fotoPerfil.setStyle("-fx-background-radius: 20;");

                // Configurar imagen de la publicación
                imagenPublicacion.setFitWidth(400);
                imagenPublicacion.setPreserveRatio(true);
                imagenPublicacion.setSmooth(true);

                // Estilos
                nombreUsuario.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
                tiempoPublicacion.setStyle("-fx-text-fill: #65676b; -fx-font-size: 12;");
                texto.setWrapText(true);
                texto.setStyle("-fx-font-size: 14;");
                estrellas.setStyle("-fx-text-fill: #FFD700; -fx-font-size: 14;");
                btnValorar.setStyle("-fx-background-color: #1e88e5; -fx-text-fill: white;");
                btnComentar.setStyle("-fx-background-color: #1e88e5; -fx-text-fill: white;");
                btnEliminar.setStyle("-fx-background-color: #e53935; -fx-text-fill: white;");

                // Estructura
                infoUsuario.getChildren().addAll(nombreUsuario, tiempoPublicacion);
                encabezado.getChildren().addAll(fotoPerfil, infoUsuario);
                interacciones.getChildren().addAll(estrellas, btnValorar, btnComentar, btnEliminar);
                interacciones.setAlignment(Pos.CENTER_LEFT);
                
                contenido.getChildren().addAll(encabezado, texto, interacciones, valoracionesBox);
                contenido.setStyle("-fx-padding: 10; -fx-background-color: white; -fx-background-radius: 10; -fx-border-color: #e4e6eb; -fx-border-radius: 10;");
                archivoBox.setId("archivoBox");
                archivoBox.setAlignment(Pos.CENTER_LEFT);
                archivoBox.setSpacing(10);
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
                    
                    // Cargar imagen de perfil
                    if (item.getImagenPerfil() != null) {
                        try {
                            File file = new File(item.getImagenPerfil());
                            if (file.exists()) {
                                Image imagen = new Image(file.toURI().toString());
                                fotoPerfil.setImage(imagen);
                            } else {
                                // Imagen de perfil por defecto
                                fotoPerfil.setImage(new Image(getClass().getResourceAsStream("/images/img_1.png")));
                            }
                        } catch (Exception e) {
                            // Imagen de perfil por defecto en caso de error
                            fotoPerfil.setImage(new Image(getClass().getResourceAsStream("/images/img_1.png")));
                        }
                    } else {
                        // Imagen de perfil por defecto si es null
                        fotoPerfil.setImage(new Image(getClass().getResourceAsStream("/images/img_1.png")));
                    }

                    // Cargar imagen de la publicación si existe
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
                                        contenido.getChildren().add(3, imagenPublicacion);
                                    }
                                    archivoMostrado = true;
                                }
                            } catch (Exception e) {
                                contenido.getChildren().remove(imagenPublicacion);
                                imagenPublicacion.setImage(null);
                            }
                        } else {
                            // Mostrar botón con ícono para abrir PDF, video u otro archivo
                            String tipoBoton = "Abrir Archivo";
                            String iconPath = null;
                            if (rutaLower.endsWith(".pdf")) {
                                tipoBoton = "Abrir PDF";
                                iconPath = getClass().getResource("/images/icon_pdf.png") != null ? getClass().getResource("/images/icon_pdf.png").toExternalForm() : null;
                            } else if (rutaLower.endsWith(".mp4") || rutaLower.endsWith(".avi") || rutaLower.endsWith(".mov") || rutaLower.endsWith(".mkv")) {
                                tipoBoton = "Abrir Video";
                                iconPath = getClass().getResource("/images/icon_video.png") != null ? getClass().getResource("/images/icon_video.png").toExternalForm() : null;
                            } else {
                                iconPath = getClass().getResource("/images/icon_file.png") != null ? getClass().getResource("/images/icon_file.png").toExternalForm() : null;
                            }
                            btnAbrirArchivo.setText(tipoBoton);
                            if (iconPath != null) {
                                ImageView icono = new ImageView(new Image(iconPath));
                                icono.setFitWidth(20);
                                icono.setFitHeight(20);
                                btnAbrirArchivo.setGraphic(icono);
                            } else {
                                btnAbrirArchivo.setGraphic(null);
                            }
                            btnAbrirArchivo.setStyle("-fx-background-color: #1a73e8; -fx-text-fill: white; -fx-font-size: 12px; -fx-font-family: 'Segoe UI'; -fx-cursor: hand;");
                            btnAbrirArchivo.setOnAction(e -> abrirArchivo(item.getRutaImagen()));
                            archivoBox.getChildren().setAll(btnAbrirArchivo);
                            if (!contenido.getChildren().contains(archivoBox)) {
                                contenido.getChildren().add(3, archivoBox);
                            }
                            archivoMostrado = true;
                        }
                    }
                    if (!archivoMostrado) {
                        contenido.getChildren().remove(imagenPublicacion);
                        contenido.getChildren().remove(archivoBox);
                    }

                    // Mostrar/ocultar botón eliminar según el autor
                    btnEliminar.setVisible(estudianteActual != null && item.getAutorId().equals(estudianteActual.getUsuario()));
                    btnEliminar.setOnAction(e -> {
                        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION, 
                            "¿Seguro que deseas eliminar esta publicación?", 
                            ButtonType.YES, ButtonType.NO);
                        confirmacion.setHeaderText("Eliminar publicación");
                        confirmacion.showAndWait().ifPresent(respuesta -> {
                            if (respuesta == ButtonType.YES) {
                                // Eliminar la publicación
                                estudianteActual.getPublicaciones().eliminar(item);

                                // Eliminar valoraciones asociadas a esta publicación
                                if (estudianteActual.getValoraciones() != null && !estudianteActual.getValoraciones().estaVacia()) {
                                    String contenidoPublicacion = item.getContenido();
                                    for (int i = estudianteActual.getValoraciones().tamano() - 1; i >= 0; i--) {
                                        ValoracionItem val = estudianteActual.getValoraciones().get(i);
                                        if (val.getPublicacion() != null && val.getPublicacion().equals(contenidoPublicacion)) {
                                            estudianteActual.getValoraciones().eliminar(val);
                                        }
                                    }
                                }

                                Academix academix = Persistencia.cargarRecursoBancoBinario();
                                Estudiante estudiantePersistente = academix.buscarEstudiante(estudianteActual.getUsuario());
                                if (estudiantePersistente != null) {
                                    // Eliminar manualmente la publicación correspondiente
                                    uniquindio.com.academix.Model.ListaSimple<PublicacionItem> publicaciones = estudiantePersistente.getPublicaciones();
                                    for (int i = 0; i < publicaciones.tamano(); i++) {
                                        PublicacionItem pub = publicaciones.get(i);
                                        if (pub.getContenido().equals(item.getContenido()) && pub.getAutorId().equals(item.getAutorId())) {
                                            publicaciones.eliminar(pub);
                                            break;
                                        }
                                    }
                                    // Eliminar valoraciones asociadas en persistencia
                                    if (estudiantePersistente.getValoraciones() != null && !estudiantePersistente.getValoraciones().estaVacia()) {
                                        String contenidoPublicacion = item.getContenido();
                                        for (int i = estudiantePersistente.getValoraciones().tamano() - 1; i >= 0; i--) {
                                            ValoracionItem val = estudiantePersistente.getValoraciones().get(i);
                                            if (val.getPublicacion() != null && val.getPublicacion().equals(contenidoPublicacion)) {
                                                estudiantePersistente.getValoraciones().eliminar(val);
                                            }
                                        }
                                    }
                                    Persistencia.guardarRecursoBancoBinario(academix);
                                }

                                configurarListaPublicaciones();
                                if (contenidosListView != null) {
                                    contenidosListView.getItems().remove(item);
                                }
                                // Refrescar valoraciones en la vista
                                if (valoracionesListView != null) {
                                    valoracionesListView.getItems().clear();
                                    if (estudianteActual.getValoraciones() != null && !estudianteActual.getValoraciones().estaVacia()) {
                                        for (ValoracionItem val : estudianteActual.getValoraciones()) {
                                            valoracionesListView.getItems().add(val);
                                        }
                                    }
                                }
                            }
                        });
                    });

                    // Actualizar valoraciones
                    valoracionesBox.getChildren().clear();
                    for (PublicacionItem.Valoracion valoracion : item.getValoraciones()) {
                        if (valoracion.getComentario() != null && !valoracion.getComentario().isEmpty()) {
                            HBox valoracionBox = new HBox(10);
                            valoracionBox.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 10; -fx-background-radius: 10;");
                            
                            VBox infoValoracion = new VBox(3);
                            Label autorValoracion = new Label(valoracion.getAutorNombre());
                            autorValoracion.setStyle("-fx-font-weight: bold;");
                            
                            Label estrellasValoracion = new Label("★".repeat(valoracion.getPuntuacion()) + 
                                                      "☆".repeat(5 - valoracion.getPuntuacion()));
                            estrellasValoracion.setStyle("-fx-text-fill: #FFD700;");
                            
                            Label comentarioValoracion = new Label(valoracion.getComentario());
                            comentarioValoracion.setWrapText(true);
                            
                            Label tiempoValoracion = new Label(valoracion.getTiempoTranscurrido());
                            tiempoValoracion.setStyle("-fx-text-fill: #65676b; -fx-font-size: 11;");
                            
                            infoValoracion.getChildren().addAll(
                                autorValoracion,
                                estrellasValoracion,
                                comentarioValoracion,
                                tiempoValoracion
                            );
                            valoracionBox.getChildren().add(infoValoracion);
                            valoracionesBox.getChildren().add(valoracionBox);
                        }
                    }

                    int promedio = item.getPromedioValoraciones();
                    estrellas.setText("★".repeat(promedio) + "☆".repeat(5 - promedio));
                    
                    // Deshabilitar el botón de valorar si el usuario es el autor
                    btnValorar.setDisable(estudianteActual != null && item.getAutorId().equals(estudianteActual.getUsuario()));
                    btnValorar.setOnAction(e -> mostrarDialogoValoracion(item));
                    btnComentar.setOnAction(e -> mostrarDialogoComentario(item));
                    
                    setGraphic(contenido);
                }
            }
        });
    }

    // Método para abrir archivos igual que en InicioController
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
                    System.err.println("Desktop.open falló: " + ex.getMessage());
                }
                if (!abierto && System.getProperty("os.name").toLowerCase().contains("win")) {
                    try {
                        new ProcessBuilder("cmd", "/c", "start", "", archivo.getAbsolutePath()).start();
                        abierto = true;
                    } catch (Exception ex2) {
                        System.err.println("cmd /c start falló: " + ex2.getMessage());
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

    private void agregarValoracionAEstudianteActual(int estrellas, String comentario, String publicacion) {
        if (estudianteActual != null) {
            ValoracionItem valoracion = new ValoracionItem(estudianteActual.getNombre(), estrellas, comentario, publicacion);
            estudianteActual.agregarValoracion(valoracion);
            // Actualizar promedio en la vista
            if (valoracionPromedioLabel != null) {
                double promedio = estudianteActual.getPromedioValoraciones();
                valoracionPromedioLabel.setText(String.format("%.1f", promedio));
            }
            // Refrescar lista de valoraciones
            if (valoracionesListView != null) {
                valoracionesListView.getItems().clear();
                if (estudianteActual.getValoraciones() != null && !estudianteActual.getValoraciones().estaVacia()) {
                    for (ValoracionItem val : estudianteActual.getValoraciones()) {
                        valoracionesListView.getItems().add(val);
                    }
                }
            }
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
            // Agregar la valoración al estudiante actual con referencia a la publicación
            String nombrePublicacion = publicacion.getContenido();
            agregarValoracionAEstudianteActual(resultado.getKey(), resultado.getValue(), nombrePublicacion);
            // Guardar en persistencia y conectar usuarios por valoraciones similares
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
                        // Conectar usuarios que hayan valorado este contenido
                        for (PublicacionItem.Valoracion val : pub.getValoraciones()) {
                            if (!val.getAutorId().equals(estudianteActual.getUsuario())) {
                                academix.getGrafoUsuarios().conectar(estudianteActual.getUsuario(), val.getAutorId());
                            }
                        }
                        break;
                    }
                }
            }
            Persistencia.guardarRecursoBancoBinario(academix);
            configurarListaPublicaciones();
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
                configurarListaPublicaciones();
            }
        });
    }

    @FXML
    public void onAgregarMateria() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Agregar Materia");
        dialog.setHeaderText("Nueva Materia de Interés");
        dialog.setContentText("Nombre de la materia:");

        dialog.showAndWait().ifPresent(materia -> {
            if (!materia.isEmpty()) {
                estudianteActual.agregarInteres(materia);
                actualizarMateriasInteres();
                // Guardar cambios
                Academix academix = Persistencia.cargarRecursoBancoBinario();
                Estudiante estudiantePersistente = academix.buscarEstudiante(estudianteActual.getUsuario());
                if (estudiantePersistente != null) {
                    estudiantePersistente.agregarInteres(materia);
                    Persistencia.guardarRecursoBancoBinario(academix);
                }
            }
        });
    }

    private void actualizarMateriasInteres() {
        if (materiasInteresFlowPane == null) return;
        materiasInteresFlowPane.getChildren().clear();
        if (estudianteActual != null && estudianteActual.getIntereses() != null) {
            for (String materia : estudianteActual.getIntereses()) {
                Button materiaBtn = new Button(materia);
                materiaBtn.setStyle("-fx-background-color: #e4e6eb; -fx-text-fill: #050505; -fx-background-radius: 15;");
                materiasInteresFlowPane.getChildren().add(materiaBtn);
            }
        }
    }
     private void actualizarConexionesGrafo() {
        if (conexionesListView == null || estudianteActual == null) return;
        conexionesListView.getItems().clear();
        Academix academix = Persistencia.cargarRecursoBancoBinario();
        if (academix != null && academix.getGrafoUsuarios() != null) {
            String[] conexiones = academix.getGrafoUsuarios().getConexiones(estudianteActual.getUsuario());
            if (conexiones == null || conexiones.length == 0) {
                conexionesListView.getItems().add("Sin conexiones aún.");
            } else {
                for (String usuario : conexiones) {
                    Estudiante est = academix.buscarEstudiante(usuario);
                    if (est != null) {
                        conexionesListView.getItems().add(est.getNombre() + " (" + est.getUsuario() + ")");
                    } else {
                        conexionesListView.getItems().add(usuario);
                    }
                }
            }
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    public void setEstudianteActual(Estudiante estudiante) {
        this.estudianteActual = estudiante;
        cargarDatosEstudiante();
    }
private void cargarSugerenciasPorGrafo() {
    if (estudianteActual == null || sugerenciasListView == null) return;
    sugerenciasListView.getItems().removeIf(item -> item.startsWith("[Grafo]") || item.equals("Sin sugerencias por grafo."));

    Academix academix = Persistencia.cargarRecursoBancoBinario();
    if (academix == null || academix.getGrafoUsuarios() == null) return;

    String usuarioActual = estudianteActual.getUsuario();
    String[] conexionesDirectas = academix.getGrafoUsuarios().getConexiones(usuarioActual);
    if (conexionesDirectas == null) return;

    String[] sugerencias = new String[100];
    int total = 0;

    for (int i = 0; i < conexionesDirectas.length; i++) {
        String amigo = conexionesDirectas[i];
        String[] conexionesDelAmigo = academix.getGrafoUsuarios().getConexiones(amigo);
        if (conexionesDelAmigo == null) continue;

        for (int j = 0; j < conexionesDelAmigo.length; j++) {
            String candidato = conexionesDelAmigo[j];

            if (!candidato.equals(usuarioActual)
                && !estaEnArreglo(conexionesDirectas, candidato)
                && !estaEnArreglo(sugerencias, candidato)) {

                Estudiante e = academix.buscarEstudiante(candidato);
                if (e != null) {
                    sugerencias[total++] = "[Grafo] " + e.getNombre() + " (" + e.getUsuario() + ")";
                    if (total >= sugerencias.length) break;
                }
            }
        }
        if (total >= sugerencias.length) break;
    }

    if (total == 0) {
        sugerenciasListView.getItems().add("Sin sugerencias por grafo.");
    } else {
        for (int i = 0; i < total; i++) {
            sugerenciasListView.getItems().add(sugerencias[i]);
        }
    }
}
private boolean estaEnArreglo(String[] arreglo, String valor) {
    for (int i = 0; i < arreglo.length; i++) {
        if (arreglo[i] != null && arreglo[i].equals(valor)) {
            return true;
        }
    }
    return false;
}

   private void cargarDatosEstudiante() {
    if (estudianteActual != null) {
        // Cargar información básica
        nombreUsuarioLabel.setText(estudianteActual.getNombre());
        correoLabel.setText(estudianteActual.getUsuario());

        // Actualizar ubicación y estudios
        ubicacionLabel.setText(estudianteActual.getUbicacion());

        if (estudiosLabel != null) {
            estudiosLabel.setText(estudianteActual.getUniversidad());
        }
        if (estudiosDetalleLabel != null) {
            estudiosDetalleLabel.setText(estudianteActual.getUniversidad());
        }

        // Cargar foto de perfil si existe
        if (estudianteActual.getFotoPerfil() != null) {
            actualizarFotoPerfil(estudianteActual.getFotoPerfil());
        }

        // Cargar foto de portada si existe
        if (estudianteActual.getFotoPortada() != null) {
            try {
                Image imagen = new Image(new File(estudianteActual.getFotoPortada()).toURI().toString());
                portadaImageView.setImage(imagen);
            } catch (Exception e) {
                // Si hay error al cargar la imagen, mantener el fondo por defecto
            }
        }

        // Cargar publicaciones del estudiante
        if (contenidosListView != null) {
            contenidosListView.getItems().clear();
            if (estudianteActual.getPublicaciones() != null && !estudianteActual.getPublicaciones().estaVacia()) {
                for (PublicacionItem pub : estudianteActual.getPublicaciones()) {
                    contenidosListView.getItems().add(pub);
                }
            }
        }

        // LIMPIAR valoraciones recibidas que hagan referencia a publicaciones eliminadas
        if (estudianteActual.getValoraciones() != null && !estudianteActual.getValoraciones().estaVacia()) {
            for (int i = estudianteActual.getValoraciones().tamano() - 1; i >= 0; i--) {
                ValoracionItem val = estudianteActual.getValoraciones().get(i);
                boolean existe = false;
                if (val.getPublicacion() != null && !val.getPublicacion().isEmpty()) {
                    for (PublicacionItem pub : estudianteActual.getPublicaciones()) {
                        if (val.getPublicacion().equals(pub.getContenido())) {
                            existe = true;
                            break;
                        }
                    }
                    if (!existe) {
                        estudianteActual.getValoraciones().eliminar(val);
                    }
                }
            }
        }

        // Cargar valoraciones de manera segura
        if (valoracionesListView != null) {
            valoracionesListView.getItems().clear();
            if (estudianteActual.getValoraciones() != null && !estudianteActual.getValoraciones().estaVacia()) {
                for (ValoracionItem val : estudianteActual.getValoraciones()) {
                    valoracionesListView.getItems().add(val);
                }
            }
        }

        // Actualizar promedio de valoraciones
        if (valoracionPromedioLabel != null) {
            double promedio = estudianteActual.getPromedioValoraciones();
            valoracionPromedioLabel.setText(String.format("%.1f", promedio));
        }

        // Cargar materias de interés
        actualizarMateriasInteres();

        // Cargar sugerencias basadas en intereses
        cargarSugerencias();

        // Cargar conexiones directas del grafo
        actualizarConexionesGrafo();

        // Cargar sugerencias por grafo (amigos de amigos)
        cargarSugerenciasPorGrafo();
    }
}

    private void cargarSugerencias() {
        if (sugerenciasListView == null) return;
        sugerenciasListView.getItems().removeIf(item -> !item.startsWith("[Grafo]") && !item.equals("Sin sugerencias por grafo."));
        if (estudianteActual != null && estudianteActual.getIntereses() != null && !estudianteActual.getIntereses().estaVacia()) {
            // Cargar estudiantes del sistema
            Academix academix = Persistencia.cargarRecursoBancoBinario();
            for (Estudiante est : academix.getListaEstudiantes()) {
                // No sugerir al propio estudiante
                if (!est.getUsuario().equals(estudianteActual.getUsuario())) {
                    // Buscar intereses en común
                    for (String interes : est.getIntereses()) {
                        if (listaSimpleContiene(estudianteActual.getIntereses(), interes)) {
                            sugerenciasListView.getItems().add(
                                est.getNombre() + " - " + interes
                            );
                            break; // Solo mostrar una vez cada estudiante
                        }
                    }
                }
            }
        }
    }

    // Método auxiliar para verificar si una ListaSimple<String> contiene un valor
    private boolean listaSimpleContiene(uniquindio.com.academix.Model.ListaSimple<String> lista, String valor) {
        if (lista == null) return false;
        for (int i = 0; i < lista.tamano(); i++) {
            String elemento = lista.get(i);
            if (elemento != null && elemento.equals(valor)) {
                return true;
            }
        }
        return false;
    }

    @FXML
    public void onCambiarFotoPerfil() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Foto de Perfil");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg")
        );
        
        File archivo = fileChooser.showOpenDialog(null);
        if (archivo != null) {
            try {
                File carpetaDestino = new File("data/perfiles");
                if (!carpetaDestino.exists()) {
                    carpetaDestino.mkdirs();
                }
                String nombreArchivo = System.currentTimeMillis() + "_" + archivo.getName();
                File destino = new File(carpetaDestino, nombreArchivo);
                Files.copy(archivo.toPath(), destino.toPath(), StandardCopyOption.REPLACE_EXISTING);
                String rutaRelativa = "data/perfiles/" + nombreArchivo;
                estudianteActual.setFotoPerfil(rutaRelativa);

                // Actualizar las imágenes
                actualizarFotoPerfil(rutaRelativa);

                // Guardar en persistencia
                Academix academix = Persistencia.cargarRecursoBancoBinario();
                Estudiante estudiantePersistente = academix.buscarEstudiante(estudianteActual.getUsuario());
                if (estudiantePersistente != null) {
                    estudiantePersistente.setFotoPerfil(rutaRelativa);
                    Persistencia.guardarRecursoBancoBinario(academix);
                }
            } catch (Exception e) {
                mostrarAlerta("Error", "No se pudo copiar la imagen de perfil.");
            }
        }
    }

    @FXML
    public void onCambiarPortada() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Foto de Portada");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg")
        );
        
        File archivo = fileChooser.showOpenDialog(null);
        if (archivo != null) {
            try {
                File carpetaDestino = new File("data/perfiles");
                if (!carpetaDestino.exists()) {
                    carpetaDestino.mkdirs();
                }
                String nombreArchivo = "portada_" + System.currentTimeMillis() + "_" + archivo.getName();
                File destino = new File(carpetaDestino, nombreArchivo);
                Files.copy(archivo.toPath(), destino.toPath(), StandardCopyOption.REPLACE_EXISTING);
                String rutaRelativa = "data/perfiles/" + nombreArchivo;
                estudianteActual.setFotoPortada(rutaRelativa);

                // Actualizar la imagen de portada
                try {
                    Image imagen = new Image(new File(rutaRelativa).toURI().toString());
                    portadaImageView.setImage(imagen);
                } catch (Exception e) {
                    mostrarAlerta("Error", "No se pudo cargar la imagen de portada.");
                }

                // Guardar en persistencia
                Academix academix = Persistencia.cargarRecursoBancoBinario();
                Estudiante estudiantePersistente = academix.buscarEstudiante(estudianteActual.getUsuario());
                if (estudiantePersistente != null) {
                    estudiantePersistente.setFotoPortada(rutaRelativa);
                    Persistencia.guardarRecursoBancoBinario(academix);
                }
            } catch (Exception e) {
                mostrarAlerta("Error", "No se pudo copiar la imagen de portada.");
            }
        }
    }

    private void actualizarFotoPerfil(String rutaArchivo) {
        try {
            Image imagen = new Image(new File(rutaArchivo).toURI().toString());
            
            // Aplicar estilo circular a las imágenes de perfil
            perfilImageView.setImage(imagen);
            perfilImageView.setFitHeight(150);
            perfilImageView.setFitWidth(150);
            perfilImageView.setStyle(
                "-fx-background-radius: 75; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 4, 0, 0, 0); " +
                "-fx-background-color: white; " +
                "-fx-border-radius: 75; " +
                "-fx-border-color: #e0e0e0; " +
                "-fx-border-width: 1;"
            );
            
            // Aplicar el mismo estilo a la miniatura
            perfilMiniaturaImageView.setImage(imagen);
            perfilMiniaturaImageView.setFitHeight(40);
            perfilMiniaturaImageView.setFitWidth(40);
            perfilMiniaturaImageView.setStyle(
                "-fx-background-radius: 20; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 2, 0, 0, 0); " +
                "-fx-background-color: white; " +
                "-fx-border-radius: 20; " +
                "-fx-border-color: #e0e0e0; " +
                "-fx-border-width: 1;"
            );
            
            // Recortar las imágenes en forma circular
            perfilImageView.setClip(createCircularClip(150));
            perfilMiniaturaImageView.setClip(createCircularClip(40));
            
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo cargar la imagen de perfil.");
        }
    }

    private javafx.scene.shape.Circle createCircularClip(double size) {
        javafx.scene.shape.Circle clip = new javafx.scene.shape.Circle(size/2);
        clip.setCenterX(size/2);
        clip.setCenterY(size/2);
        return clip;
    }
}
