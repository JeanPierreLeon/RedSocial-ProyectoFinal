package uniquindio.com.academix.Controller;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import uniquindio.com.academix.HelloApplication;
import uniquindio.com.academix.Model.Academix;
import uniquindio.com.academix.Model.ContenidoEducativo;
import uniquindio.com.academix.Model.Estudiante;
import uniquindio.com.academix.Utils.Persistencia;

public class InicioController {

    @FXML private TextField tituloField;
    @FXML private TextField descripcionField;
    @FXML private ChoiceBox<String> tipoChoiceBox;
    @FXML private TextField urlField;
    @FXML private VBox contenedorPublicaciones;

    private Academix academix;
    private File archivoSeleccionado;

    @FXML
    public void initialize() {
        // Cargar datos guardados
        academix = Persistencia.cargarRecursoBancoXML();
        if (academix == null) {
            academix = new Academix();
        }

        // Mostrar las publicaciones existentes
        for (ContenidoEducativo contenido : academix.getContenidoEducativo()) {
            agregarPublicacionVista(contenido);
        }

        tipoChoiceBox.getItems().addAll("Imagen", "PDF", "Video", "Otro");
    }

    @FXML
    public void seleccionarArchivo() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar archivo");
        File archivo = fileChooser.showOpenDialog(null);
        if (archivo != null) {
            archivoSeleccionado = archivo;
            urlField.setText(archivo.getAbsolutePath());
        }
    }

    @FXML
    public void publicarContenido() {
        String titulo = tituloField.getText().trim();
        String descripcion = descripcionField.getText().trim();
        String tipo = tipoChoiceBox.getValue();
        String url = urlField.getText().trim();

        Estudiante estudianteActual = HelloApplication.getEstudianteActual();
        if (estudianteActual == null) {
            mostrarAlerta("Error", "No hay sesión activa.");
            return;
        }

        if (titulo.isEmpty() || descripcion.isEmpty() || tipo == null || url.isEmpty()) {
            mostrarAlerta("Campos incompletos", "Por favor, completa todos los campos.");
            return;
        }

        ContenidoEducativo contenido = new ContenidoEducativo(titulo, tipo, descripcion, url, estudianteActual.getUsuario());

        academix.agregarContenido(contenido);
        Persistencia.guardarRecursoBancoXML(academix);

        agregarPublicacionVista(contenido);

        // Limpiar campos
        tituloField.clear();
        descripcionField.clear();
        tipoChoiceBox.setValue(null);
        urlField.clear();
        archivoSeleccionado = null;
    }

    private void agregarPublicacionVista(ContenidoEducativo contenido) {
        VBox tarjeta = new VBox(5);
        tarjeta.setStyle("-fx-background-color: #f9f9f9; -fx-padding: 10; -fx-background-radius: 10; -fx-border-color: #ccc;");

        Label titulo = new Label(contenido.getTitulo());
        titulo.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        Label descripcion = new Label(contenido.getDescripcion());
        descripcion.setWrapText(true);

        Label autor = new Label("Publicado por: " + contenido.getAutor());
        autor.setStyle("-fx-font-style: italic; -fx-font-size: 10px; -fx-text-fill: #555;");

        tarjeta.getChildren().addAll(titulo, descripcion);

        if ("Imagen".equalsIgnoreCase(contenido.getTipo()) && contenido.getUrl().toLowerCase().matches(".*\\.(png|jpg|jpeg|gif|bmp)")) {
            try {
                Image imagen = new Image(new File(contenido.getUrl()).toURI().toString(), 300, 200, true, true);
                ImageView imageView = new ImageView(imagen);
                tarjeta.getChildren().add(imageView);
            } catch (Exception e) {
                tarjeta.getChildren().add(new Label("No se pudo cargar la imagen."));
            }
        }

        Button abrirBtn = new Button("Abrir");
        abrirBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        abrirBtn.setOnAction(e -> abrirArchivo(contenido.getUrl()));

        Button eliminarBtn = new Button("Eliminar");
        eliminarBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        eliminarBtn.setOnAction(e -> {
            contenedorPublicaciones.getChildren().remove(tarjeta);
            academix.getContenidoEducativo().remove(contenido);
            Persistencia.guardarRecursoBancoXML(academix);
        });

        HBox botones = new HBox(10, abrirBtn, eliminarBtn);
        tarjeta.getChildren().addAll(botones, autor);

        contenedorPublicaciones.getChildren().add(0, tarjeta);
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
}


