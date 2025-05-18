package uniquindio.com.academix.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import uniquindio.com.academix.Model.Estudiante;

public class MensajesController {

    @FXML
    private TextArea areaMensajes;

    @FXML
    private TextField campoMensaje;

    @FXML
    private Button botonEnviar;

    private Estudiante estudianteActual;

    // Setter para recibir el Estudiante actual
    public void setEstudianteActual(Estudiante estudiante) {
        this.estudianteActual = estudiante;
    }

    // Método para enviar un mensaje
    @FXML
    public void enviarMensaje() {
        String mensaje = campoMensaje.getText();
        if (mensaje.isEmpty()) {
            // Muestra un mensaje de error si no se ingresa nada en el campo
            Alert alert = new Alert(Alert.AlertType.WARNING, "Por favor ingresa un mensaje.");
            alert.showAndWait();
        } else {
            // Aquí puedes agregar la lógica para enviar el mensaje
            areaMensajes.appendText(estudianteActual.getUsuario() + ": " + mensaje + "\n");
            campoMensaje.clear();
        }
    }
}
