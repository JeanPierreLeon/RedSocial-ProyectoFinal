package uniquindio.com.academix.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextArea;
import uniquindio.com.academix.Model.ColaPrioridadSolicitudes;
import uniquindio.com.academix.Model.Estudiante;
import uniquindio.com.academix.Model.SolicitudAyuda;

public class SolicitarAyudaController {

    @FXML private TextArea campoSolicitud;
    @FXML private Spinner<Integer> spinnerUrgencia;
    @FXML private Button botonEnviar;

    private Estudiante estudianteActual;

    // ⚠️ Asegúrate de que esta cola sea compartida donde se atienden las solicitudes
    private static final ColaPrioridadSolicitudes cola = new ColaPrioridadSolicitudes();

    public void setEstudianteActual(Estudiante estudiante) {
        this.estudianteActual = estudiante;
    }

    @FXML
    public void initialize() {
        spinnerUrgencia.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 5));
    }

    @FXML
    public void enviarSolicitud() {
        String tema = campoSolicitud.getText().trim();
        int urgencia = spinnerUrgencia.getValue();

        if (tema.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Por favor describe tu solicitud.").showAndWait();
            return;
        }

        SolicitudAyuda solicitud = new SolicitudAyuda(estudianteActual.getUsuario(), tema, urgencia);
        cola.agregarSolicitud(solicitud);

        new Alert(Alert.AlertType.INFORMATION, "¡Solicitud enviada con prioridad " + urgencia + "!").showAndWait();

        campoSolicitud.clear();
        spinnerUrgencia.getValueFactory().setValue(5);
    }
}

