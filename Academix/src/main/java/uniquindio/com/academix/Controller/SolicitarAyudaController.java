package uniquindio.com.academix.Controller;



import javafx.fxml.FXML;
import javafx.scene.control.*;
import uniquindio.com.academix.Model.Estudiante;

public class SolicitarAyudaController {

    @FXML
    private TextArea campoSolicitud;

    @FXML
    private Button botonEnviar;

    private Estudiante estudianteActual;

    // Setter para recibir el Estudiante actual
    public void setEstudianteActual(Estudiante estudiante) {
        this.estudianteActual = estudiante;
    }

    // Acción para enviar la solicitud de ayuda
    @FXML
    public void enviarSolicitud() {
        String solicitud = campoSolicitud.getText();
        if (solicitud.isEmpty()) {
            // Muestra un mensaje de error si no se ingresa ninguna solicitud
            Alert alert = new Alert(Alert.AlertType.WARNING, "Por favor ingresa tu solicitud.");
            alert.showAndWait();
        } else {
            // Aquí puedes agregar la lógica para enviar la solicitud
            System.out.println("Solicitud de ayuda enviada por " + estudianteActual.getUsuario());
            System.out.println("Solicitud: " + solicitud);
        }
    }
}

