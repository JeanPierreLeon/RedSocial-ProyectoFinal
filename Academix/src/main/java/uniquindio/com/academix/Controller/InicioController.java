package uniquindio.com.academix.Controller;
import javafx.scene.Node;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.awt.Desktop;
import java.net.URI;

import javafx.scene.text.Text;
import uniquindio.com.academix.Model.ContenidoEducativo;

public class InicioController {

    @FXML private TextField tituloField;
    @FXML private TextField descripcionField;
    @FXML private TextField urlField;
    @FXML private ChoiceBox<String> tipoChoiceBox;
    @FXML private VBox contenedorPublicaciones;

    @FXML
    public void initialize() {
        tipoChoiceBox.getItems().addAll("Archivo", "Enlace", "Video");
        tipoChoiceBox.setValue("Archivo");
    }

    @FXML
    private void publicarContenido() {
        String titulo = tituloField.getText().trim();
        String descripcion = descripcionField.getText().trim();
        String url = urlField.getText().trim();
        String tipo = tipoChoiceBox.getValue();

        if (titulo.isEmpty() || descripcion.isEmpty() || url.isEmpty()) {
            mostrarAlerta("Por favor completa todos los campos.");
            return;
        }

        ContenidoEducativo contenido = new ContenidoEducativo(titulo, tipo, descripcion, url);
        mostrarPublicacion(contenido);

        tituloField.clear();
        descripcionField.clear();
        urlField.clear();
    }

    private void mostrarPublicacion(ContenidoEducativo contenido) {
        VBox tarjeta = new VBox();
        tarjeta.setStyle(
            "-fx-background-color: white; " +
            "-fx-border-color: #ddd; " +
            "-fx-border-radius: 10; " +
            "-fx-background-radius: 10; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5,0,0,2);" +
            "-fx-padding: 15;" +
            "-fx-spacing: 10;"
        );

        Label tituloLabel = new Label(contenido.getTitulo());
        tituloLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

        Label tipoLabel = new Label("Tipo: " + contenido.getTipo());
        tipoLabel.setStyle("-fx-text-fill: #555; -fx-font-size: 12px;");

        Text descripcionText = new Text(contenido.getDescripcion());
        descripcionText.setWrappingWidth(500);

        Node urlNode = crearNodoContenido(contenido);

        Button eliminarBtn = new Button("Eliminar");
        eliminarBtn.setStyle("-fx-background-color: #e53935; -fx-text-fill: white; -fx-font-size: 12px;");
        eliminarBtn.setOnAction(e -> contenedorPublicaciones.getChildren().remove(tarjeta));

        tarjeta.getChildren().addAll(tituloLabel, tipoLabel, descripcionText, urlNode, eliminarBtn);

        contenedorPublicaciones.getChildren().add(0, tarjeta);
    }

    private Node crearNodoContenido(ContenidoEducativo contenido) {
        String tipo = contenido.getTipo();
        String url = contenido.getUrl();

        switch (tipo) {
            case "Enlace":
                Hyperlink link = new Hyperlink(url);
                link.setOnAction(e -> {
                    try {
                        java.awt.Desktop.getDesktop().browse(new java.net.URI(url));
                    } catch (Exception ex) {
                        mostrarAlerta("No se pudo abrir el enlace.");
                    }
                });
                return link;

            case "Video":
                Label videoLabel = new Label("🎬 Video: " + url);
                videoLabel.setStyle("-fx-text-fill: #1565c0; -fx-underline: true;");
                return videoLabel;

            case "Archivo":
            default:
                Label archivoLabel = new Label("📎 Archivo: " + url);
                archivoLabel.setStyle("-fx-text-fill: #333;");
                return archivoLabel;
        }
    }

    private void mostrarAlerta(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.WARNING);
        alerta.setTitle("Advertencia");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    @FXML
    private Label nombreUsuarioLabel;

    public void setNombreUsuario(String nombre) {
        nombreUsuarioLabel.setText(nombre);
    }

}
