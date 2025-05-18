package uniquindio.com.academix.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import uniquindio.com.academix.Model.Estudiante;

import java.util.List;

public class RegistroController {

    @FXML private TextField campoUsuario;
    @FXML private PasswordField campoContrasena;
    @FXML private PasswordField campoConfirmarContrasena;
    @FXML private Label mensajeError;
    @FXML private Button btnRegistrar;

    private List<Estudiante> estudiantes;

    // Constructor sin parámetros, necesario para FXML
    public RegistroController() {
        // Aquí no se necesita inicializar nada en este caso
    }

    // Método para inyectar los parámetros después de la creación del controlador
    public void setEstudiantes(List<Estudiante> estudiantes) {
        this.estudiantes = estudiantes;
    }

    @FXML
    public void initialize() {
        // Inicialización si es necesario
    }
    @FXML
    public void registrarEstudiante() {
        String usuario = campoUsuario.getText();
        String contrasena = campoContrasena.getText();
        String confirmarContrasena = campoConfirmarContrasena.getText();

        if (!contrasena.equals(confirmarContrasena)) {
            mensajeError.setText("Las contraseñas no coinciden");
            return;
        }

        if (usuario.isEmpty() || contrasena.isEmpty()) {
            mensajeError.setText("Por favor, complete todos los campos");
            return;
        }

        for (Estudiante est : estudiantes) {
            if (est.getUsuario().equals(usuario)) {
                mensajeError.setText("El usuario ya existe");
                return;
            }
        }

        Estudiante nuevo = new Estudiante(usuario, contrasena);
        estudiantes.add(nuevo);

        // Establecer el estudiante en sesión
        uniquindio.com.academix.HelloApplication.setEstudianteActual(nuevo);

        // Cambiar a la vista principal
        uniquindio.com.academix.HelloApplication.cambiarVista("principal.fxml", "Inicio");

        // También podrías cerrar esta ventana si fuese un pop-up (no obligatorio si estás cambiando de escena)
        // cerrarVentana();
    }

    // Método para cerrar la ventana de registro
    private void cerrarVentana() {
        Stage stage = (Stage) btnRegistrar.getScene().getWindow();
        stage.close();
    }
}
