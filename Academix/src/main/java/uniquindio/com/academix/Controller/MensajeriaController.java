package uniquindio.com.academix.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.control.ScrollPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import uniquindio.com.academix.Model.Academix;
import uniquindio.com.academix.Model.Estudiante;
import uniquindio.com.academix.Model.Mensaje;
import uniquindio.com.academix.Estructuras.ListaSimple;
import uniquindio.com.academix.Utils.Persistencia;

public class MensajeriaController {

    @FXML private ComboBox<String> comboDestinatarios;
    @FXML private TextField mensajeField;
    @FXML private Button btnEnviar;
    @FXML private VBox contenedorMensajes;
    @FXML private ScrollPane scrollMensajes;

    private Academix academix;
    private Estudiante estudianteActual;

    public void setEstudianteActual(Estudiante estudiante) {
        this.estudianteActual = estudiante;
        academix = Persistencia.cargarRecursoBancoBinario();
        cargarDestinatarios();
        comboDestinatarios.getSelectionModel().clearSelection();
        contenedorMensajes.getChildren().clear();
        // No cargar mensajes previos aquí, solo cuando el usuario seleccione un destinatario
    }

    @FXML
    public void initialize() {
        // El estudianteActual se setea desde el DashboardController

        // Enviar mensaje con Enter
        mensajeField.setOnAction(e -> enviarMensaje());

        // Deshabilitar botón enviar si el campo está vacío
        btnEnviar.disableProperty().bind(mensajeField.textProperty().isEmpty());
    }

    private void cargarDestinatarios() {
        comboDestinatarios.getItems().clear();
        if (academix == null || estudianteActual == null) return;
        java.util.HashSet<String> usuariosAgregados = new java.util.HashSet<>();
        for (Estudiante est : academix.getListaEstudiantes()) {
            String usuario = est.getUsuario();
            if (!usuario.equals(estudianteActual.getUsuario()) && usuariosAgregados.add(usuario)) {
                comboDestinatarios.getItems().add(usuario);
            }
        }
    }

    @FXML
    public void cargarMensajesPrevios() {
        contenedorMensajes.getChildren().clear();
        String destinatario = comboDestinatarios.getValue();
        if (destinatario == null) return;
        ListaSimple<Mensaje> conversacion = academix.getConversacion(estudianteActual.getUsuario(), destinatario);
        if (conversacion.size() == 0) {
            Text placeholder = new Text("No hay mensajes con este usuario.");
            placeholder.setStyle("-fx-fill: #888; -fx-font-style: italic;");
            contenedorMensajes.getChildren().add(placeholder);
        } else {
            for (Mensaje mensaje : conversacion) {
                agregarBurbujaMensaje(mensaje);
            }
        }
        hacerScrollAbajo();
    }

    @FXML
    public void enviarMensaje() {
        String destinatario = comboDestinatarios.getValue();
        String texto = mensajeField.getText().trim();
        if (destinatario == null || texto.isEmpty()) return;

        Mensaje mensaje = new Mensaje(estudianteActual.getUsuario(), destinatario, texto);
        academix.agregarMensaje(mensaje);
        Persistencia.guardarRecursoBancoBinario(academix);

        agregarBurbujaMensaje(mensaje);
        mensajeField.clear();
        hacerScrollAbajo();
    }

    private void agregarBurbujaMensaje(Mensaje mensaje) {
        boolean esMio = mensaje.getRemitente().equals(estudianteActual.getUsuario());

        TextFlow burbuja = new TextFlow();
        Text texto = new Text(mensaje.getContenido());
        texto.setFill(esMio ? Color.WHITE : Color.BLACK);
        burbuja.getChildren().add(texto);

        burbuja.setStyle(
            "-fx-background-color: " + (esMio ? "#4f8cff" : "#e5e5ea") + ";" +
            "-fx-background-radius: 18;" +
            "-fx-padding: 8 14 8 14;" +
            "-fx-font-size: 14px;" +
            "-fx-max-width: 340px;"
        );

        Text fecha = new Text("  " + mensaje.getFecha().toLocalTime().withSecond(0).toString());
        fecha.setStyle("-fx-font-size: 10px; -fx-fill: #888;");
        burbuja.getChildren().add(fecha);

        HBox fila = new HBox();
        Region espacio = new Region();
        HBox.setHgrow(espacio, Priority.ALWAYS);

        if (esMio) {
            fila.setAlignment(Pos.CENTER_RIGHT);
            fila.getChildren().addAll(espacio, burbuja);
        } else {
            fila.setAlignment(Pos.CENTER_LEFT);
            fila.getChildren().addAll(burbuja, espacio);
        }
        fila.setSpacing(5);
        contenedorMensajes.getChildren().add(fila);
    }

    private void hacerScrollAbajo() {
        javafx.application.Platform.runLater(() -> scrollMensajes.setVvalue(1.0));
    }
}
