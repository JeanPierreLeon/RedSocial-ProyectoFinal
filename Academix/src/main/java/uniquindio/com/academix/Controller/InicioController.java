package uniquindio.com.academix.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
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

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class InicioController {

    @FXML private TextField tituloField;
    @FXML private TextField descripcionField;
    @FXML private ChoiceBox<String> tipoChoiceBox;
    @FXML private TextField urlField;
    @FXML private VBox contenedorPublicaciones;

    private File archivoSeleccionado;
    private Academix academix;

    @FXML
    public void initialize() {
        tipoChoiceBox.getItems().addAll("Imagen", "PDF", "Video", "Otro");
        academix = Persistencia.cargarRecursoBancoXML();
        cargarPublicaciones();
    }

    private void cargarPublicaciones() {
        contenedorPublicaciones.getChildren().clear();
        for (ContenidoEducativo contenido : academix.getContenidoEducativo()) {
            agregarPublicacionVista(contenido);
        }
    }

    @FXML
    public void seleccionarArchivo() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar archivo");
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
        tarjeta.setStyle("-fx-background-color: #ffffff; -fx-padding: 10; -fx-background-radius: 10; -fx-border-color: #cccccc;");

        Label titulo = new Label(contenido.getTitulo());
        titulo.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        Label descripcion = new Label(contenido.getDescripcion());
        Label autor = new Label("Publicado por: " + contenido.getAutor());

        tarjeta.getChildren().addAll(titulo, descripcion);

        if (contenido.getTipo().equalsIgnoreCase("Imagen") && contenido.getUrl().toLowerCase().matches(".*\\.(png|jpg|jpeg|gif|bmp)")) {
            try {
                Image imagen = new Image(new File(contenido.getUrl()).toURI().toString(), 300, 200, true, true);
                ImageView imageView = new ImageView(imagen);
                tarjeta.getChildren().add(imageView);
            } catch (Exception e) {
                tarjeta.getChildren().add(new Label("No se pudo cargar la imagen."));
            }
        }

        HBox botones = new HBox(10);
        Button abrirBtn = new Button("Abrir");
        abrirBtn.setStyle("-fx-background-color: #007bff; -fx-text-fill: white;");
        abrirBtn.setOnAction(e -> abrirArchivo(contenido.getUrl()));
        botones.getChildren().add(abrirBtn);

        Estudiante estudianteActual = HelloApplication.getEstudianteActual();
        if (contenido.getAutor().equals(estudianteActual.getUsuario())) {
            Button eliminarBtn = new Button("Eliminar");
            eliminarBtn.setStyle("-fx-background-color: #007bff; -fx-text-fill: white;");
            eliminarBtn.setOnAction(e -> {
                contenedorPublicaciones.getChildren().remove(tarjeta);
                academix.eliminarContenido(contenido);
                Persistencia.guardarRecursoBancoXML(academix);
            });
            botones.getChildren().add(eliminarBtn);
        }

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
