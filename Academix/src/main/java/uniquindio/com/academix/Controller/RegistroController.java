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

        // Validar que las contraseñas coincidan
        if (!contrasena.equals(confirmarContrasena)) {
            mensajeError.setText("Las contraseñas no coinciden");
            return;
        }

        // Validar que el campo de usuario no esté vacío
        if (usuario.isEmpty() || contrasena.isEmpty()) {
            mensajeError.setText("Por favor, complete todos los campos");
            return;
        }

        // Verificar si el usuario ya existe
        for (Estudiante est : estudiantes) {
            if (est.getUsuario().equals(usuario)) {
                mensajeError.setText("El usuario ya existe");
                return;
            }
        }

        // Si todo está bien, agregar el nuevo estudiante a la lista
        estudiantes.add(new Estudiante(usuario, contrasena));

        // Mostrar mensaje de éxito
        mensajeError.setText("Registro exitoso, ahora puedes iniciar sesión.");

        // Cerrar la ventana de registro después de 2 segundos (opcional)
        cerrarVentana();
    }

    // Método para cerrar la ventana de registro
    private void cerrarVentana() {
        Stage stage = (Stage) btnRegistrar.getScene().getWindow();
        stage.close();
    }
}
