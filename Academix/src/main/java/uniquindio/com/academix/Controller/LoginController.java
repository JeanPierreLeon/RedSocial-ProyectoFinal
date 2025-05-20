package uniquindio.com.academix.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import uniquindio.com.academix.Model.Estudiante;

import java.util.List;

public class LoginController {

    @FXML private TextField campoUsuario;
    @FXML private PasswordField campoContrasena;
    @FXML private Label mensajeError;

    private List<Estudiante> estudiantes;

    public LoginController() {
        estudiantes = uniquindio.com.academix.HelloApplication.getEstudiantes(); // Lista centralizada
    }

    @FXML
    public void initialize() {
        if (estudiantes.isEmpty()) {
            estudiantes.add(new Estudiante("juan@academix.com.co", "1234"));
            estudiantes.add(new Estudiante("ana@academix.edu", "abcd"));
        }
    }

    @FXML
    public void iniciarSesion() {
        String usuario    = campoUsuario.getText();
        String contrasena = campoContrasena.getText();

        // Comprobación de moderador
        if ("admin".equals(usuario) && "12345".equals(contrasena)) {
            uniquindio.com.academix.HelloApplication.cambiarVista("moderador.fxml", "Panel del Moderador");
            return;
        }

        // Comprobación de estudiantes
        for (Estudiante est : estudiantes) {
            if (est.getUsuario().equals(usuario) && est.getContrasena().equals(contrasena)) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/uniquindio/com/academix/principal.fxml"));
                    Parent root = loader.load();

                    // Pasar el estudiante al dashboard
                    DashboardController controller = loader.getController();
                    controller.setEstudianteActual(est);

                    Stage stage = new Stage();
                    stage.setTitle("Inicio");
                    stage.setScene(new Scene(root));
                    stage.show();

                    // Cerrar ventana login
                    Stage ventanaLogin = (Stage) campoUsuario.getScene().getWindow();
                    ventanaLogin.close();

                    uniquindio.com.academix.HelloApplication.setEstudianteActual(est);

                } catch (Exception e) {
                    mensajeError.setText("Error al cargar la vista principal");
                    e.printStackTrace();
                }
                return;  // Salir al encontrar usuario
            }
        }

        mensajeError.setText("Usuario o contraseña incorrectos");
    }

    public void abrirVentanaRegistro() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/uniquindio/com/academix/registroEstudiantes.fxml"));
            Parent root = loader.load();

            RegistroController registroController = loader.getController();
            registroController.setEstudiantes(estudiantes);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Registro de Estudiantes");
            stage.show();
        } catch (Exception e) {
            mensajeError.setText("Error al abrir el registro");
            e.printStackTrace();
        }
    }
}
