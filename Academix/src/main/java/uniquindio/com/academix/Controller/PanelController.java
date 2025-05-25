package uniquindio.com.academix.Controller;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.util.Pair;
import uniquindio.com.academix.Model.Academix;
import uniquindio.com.academix.Model.Estudiante;
import uniquindio.com.academix.Model.PublicacionItem;
import uniquindio.com.academix.Model.ValoracionItem;
import uniquindio.com.academix.Utils.Persistencia;

import java.io.File;

public class PanelController {
    @FXML
    private ImageView portadaImageView;
    
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
    private Label ubicacionDetalleLabel;

    @FXML
    private Label estudiosDetalleLabel;

    private Estudiante estudianteActual;
    private Academix academix;

    @FXML
    public void initialize() {
        // Inicializar categorías de ayuda
        categoriaAyudaComboBox.getItems().addAll(
            "Matemáticas",
            "Programación",
            "Física",
            "Química",
            "Biología",
            "Historia",
            "Literatura"
        );

        // Configurar la lista de publicaciones
        configurarListaPublicaciones();

        // Configurar el cell factory para las valoraciones
        valoracionesListView.setCellFactory(lv -> new ListCell<ValoracionItem>() {
            private final HBox contenido = new HBox(10);
            private final Label estrellas = new Label();
            private final VBox detalles = new VBox(5);
            private final Label comentario = new Label();
            private final Label evaluador = new Label();

            {
                detalles.getChildren().addAll(comentario, evaluador);
                contenido.getChildren().addAll(estrellas, detalles);
                estrellas.setStyle("-fx-font-size: 16px; -fx-text-fill: #FFD700;");
                evaluador.setStyle("-fx-font-size: 12px; -fx-text-fill: #666666;");
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
                    setGraphic(contenido);
                }
            }
        });

        // Configurar eventos de hover para los botones de foto
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
        solicitudesListView.getItems().add(0, solicitud);

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
            publicacionTextField.setUserData(rutaArchivo); // Guardamos la ruta completa
            publicacionTextField.setText("Imagen seleccionada: " + archivo.getName());
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
            publicacionTextField.setText("Material seleccionado: " + archivo.getName());
        }
    }

    @FXML
    public void onPublicar() {
        String contenido = publicacionTextField.getText();
        if (contenido.isEmpty()) {
            mostrarAlerta("Error", "Por favor escribe algo para publicar.");
            return;
        }

        // Obtener la ruta de la imagen si existe
        String rutaImagen = (String) publicacionTextField.getUserData();
        
        PublicacionItem nuevaPublicacion = new PublicacionItem(
            contenido,
            estudianteActual.getUsuario(),
            estudianteActual.getNombre(),
            estudianteActual.getFotoPerfil()
        );

        // Si hay una imagen, establecerla en la publicación
        if (rutaImagen != null) {
            nuevaPublicacion.setRutaImagen(rutaImagen);
        }
        
        // Agregar la publicación al estudiante actual y persistir
        if (estudianteActual != null) {
            estudianteActual.agregarPublicacion(nuevaPublicacion);
            
            // Guardar en persistencia
            Academix academix = Persistencia.cargarRecursoBancoBinario();
            Estudiante estudiantePersistente = academix.buscarEstudiante(estudianteActual.getUsuario());
            if (estudiantePersistente != null) {
                estudiantePersistente.agregarPublicacion(nuevaPublicacion);
                Persistencia.guardarRecursoBancoBinario(academix);
            }
        }
        
        // Limpiar el campo y actualizar la vista
        publicacionTextField.clear();
        publicacionTextField.setUserData(null); // Limpiar la referencia a la imagen
        contenidosListView.getItems().add(0, nuevaPublicacion);
    }

    private void configurarListaPublicaciones() {
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
                            Image imagen = new Image(new File(item.getImagenPerfil()).toURI().toString());
                            fotoPerfil.setImage(imagen);
                        } catch (Exception e) {
                            // Usar imagen por defecto si hay error
                        }
                    }

                    // Cargar imagen de la publicación si existe
                    if (item.getRutaImagen() != null) {
                        try {
                            Image imagen = new Image(new File(item.getRutaImagen()).toURI().toString());
                            imagenPublicacion.setImage(imagen);
                            
                            // Asegurarse de que la imagen esté en el contenido
                            if (!contenido.getChildren().contains(imagenPublicacion)) {
                                contenido.getChildren().add(contenido.getChildren().indexOf(interacciones), imagenPublicacion);
                            }
                        } catch (Exception e) {
                            contenido.getChildren().remove(imagenPublicacion);
                        }
                    } else {
                        contenido.getChildren().remove(imagenPublicacion);
                    }

                    // Mostrar/ocultar botón eliminar según el autor
                    btnEliminar.setVisible(item.getAutorId().equals(estudianteActual.getUsuario()));
                    btnEliminar.setOnAction(e -> {
                        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION, 
                            "¿Seguro que deseas eliminar esta publicación?", 
                            ButtonType.YES, ButtonType.NO);
                        confirmacion.setHeaderText("Eliminar publicación");
                        confirmacion.showAndWait().ifPresent(respuesta -> {
                            if (respuesta == ButtonType.YES) {
                                estudianteActual.getPublicaciones().remove(item);

                                Academix academix = Persistencia.cargarRecursoBancoBinario();
                                Estudiante estudiantePersistente = academix.buscarEstudiante(estudianteActual.getUsuario());
                                if (estudiantePersistente != null) {
                                    estudiantePersistente.getPublicaciones().removeIf(pub ->
                                        pub.getContenido().equals(item.getContenido()) &&
                                        pub.getAutorId().equals(item.getAutorId())
                                    );
                                    Persistencia.guardarRecursoBancoBinario(academix);
                                }

                                configurarListaPublicaciones();
                                contenidosListView.getItems().remove(item);
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
                    
                    btnValorar.setOnAction(e -> mostrarDialogoValoracion(item));
                    btnComentar.setOnAction(e -> mostrarDialogoComentario(item));
                    
                    setGraphic(contenido);
                }
            }
        });
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
            
            // Actualizar la vista
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
        materiasInteresFlowPane.getChildren().clear();
        if (estudianteActual != null) {
            for (String materia : estudianteActual.getIntereses()) {
                Button materiaBtn = new Button(materia);
                materiaBtn.setStyle("-fx-background-color: #e4e6eb; -fx-text-fill: #050505; -fx-background-radius: 15;");
                materiasInteresFlowPane.getChildren().add(materiaBtn);
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

    private void cargarDatosEstudiante() {
        if (estudianteActual != null) {
            // Cargar información básica
            nombreUsuarioLabel.setText(estudianteActual.getNombre());
            correoLabel.setText(estudianteActual.getUsuario());
            
            // Actualizar ubicación y estudios
            ubicacionLabel.setText(estudianteActual.getUbicacion());
            ubicacionDetalleLabel.setText(estudianteActual.getUbicacion());
            
            estudiosLabel.setText(estudianteActual.getUniversidad());
            estudiosDetalleLabel.setText(estudianteActual.getUniversidad());
            
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
            contenidosListView.getItems().clear();
            if (estudianteActual.getPublicaciones() != null && !estudianteActual.getPublicaciones().isEmpty()) {
                contenidosListView.getItems().addAll(estudianteActual.getPublicaciones());
            }
            
            // Cargar valoraciones de manera segura
            valoracionesListView.getItems().clear();
            if (estudianteActual.getValoraciones() != null && !estudianteActual.getValoraciones().isEmpty()) {
                valoracionesListView.getItems().addAll(estudianteActual.getValoraciones());
            }
            
            // Actualizar promedio de valoraciones
            double promedio = estudianteActual.getPromedioValoraciones();
            valoracionPromedioLabel.setText(String.format("%.1f", promedio));

            // Cargar materias de interés
            actualizarMateriasInteres();

            // Cargar sugerencias basadas en intereses
            cargarSugerencias();
        }
    }

    private void cargarSugerencias() {
        sugerenciasListView.getItems().clear();
        if (estudianteActual != null && !estudianteActual.getIntereses().isEmpty()) {
            // Cargar estudiantes del sistema
            Academix academix = Persistencia.cargarRecursoBancoBinario();
            for (Estudiante est : academix.getListaEstudiantes()) {
                // No sugerir al propio estudiante
                if (!est.getUsuario().equals(estudianteActual.getUsuario())) {
                    // Buscar intereses en común
                    for (String interes : est.getIntereses()) {
                        if (estudianteActual.getIntereses().contains(interes)) {
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

    @FXML
    public void onCambiarFotoPerfil() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Foto de Perfil");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg")
        );
        
        File archivo = fileChooser.showOpenDialog(null);
        if (archivo != null) {
            String rutaArchivo = archivo.getAbsolutePath();
            estudianteActual.setFotoPerfil(rutaArchivo);
            
            // Actualizar las imágenes
            actualizarFotoPerfil(rutaArchivo);
            
            // Guardar en persistencia
            Academix academix = Persistencia.cargarRecursoBancoBinario();
            Estudiante estudiantePersistente = academix.buscarEstudiante(estudianteActual.getUsuario());
            if (estudiantePersistente != null) {
                estudiantePersistente.setFotoPerfil(rutaArchivo);
                Persistencia.guardarRecursoBancoBinario(academix);
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
            String rutaArchivo = archivo.getAbsolutePath();
            estudianteActual.setFotoPortada(rutaArchivo);
            
            // Actualizar la imagen de portada
            try {
                Image imagen = new Image(new File(rutaArchivo).toURI().toString());
                portadaImageView.setImage(imagen);
            } catch (Exception e) {
                mostrarAlerta("Error", "No se pudo cargar la imagen de portada.");
            }
            
            // Guardar en persistencia
            Academix academix = Persistencia.cargarRecursoBancoBinario();
            Estudiante estudiantePersistente = academix.buscarEstudiante(estudianteActual.getUsuario());
            if (estudiantePersistente != null) {
                estudiantePersistente.setFotoPortada(rutaArchivo);
                Persistencia.guardarRecursoBancoBinario(academix);
            }
        }
    }

    private void actualizarFotoPerfil(String rutaArchivo) {
        try {
            Image imagen = new Image(new File(rutaArchivo).toURI().toString());
            perfilImageView.setImage(imagen);
            perfilMiniaturaImageView.setImage(imagen);
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo cargar la imagen de perfil.");
        }
    }
} 
