package uniquindio.com.academix.Controller;

import java.util.List;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import uniquindio.com.academix.HelloApplication;
import uniquindio.com.academix.Model.Estudiante;

public class LoginController {

    @FXML private TextField campoUsuario;
    @FXML private PasswordField campoContrasena;
    @FXML private Label mensajeError;

    private List<Estudiante> estudiantes;

    public LoginController() {
        estudiantes = HelloApplication.getEstudiantes(); // Usa la lista centralizada
    }

    @FXML
    public void initialize() {
        if (estudiantes.isEmpty()) {
            estudiantes.add(new Estudiante("juan@academix.com.co", "1234"));
            estudiantes.add(new Estudiante("ana@academix.com.co", "abcd"));
        }
    }

    @FXML
    public void iniciarSesion() {
        String usuario     = campoUsuario.getText();
        String contrasena  = campoContrasena.getText();

        /* 1. ‑‑‑‑‑‑‑‑‑‑‑‑ Comprobación de moderador ‑‑‑‑‑‑‑‑‑‑‑‑ */
        if ("admin".equals(usuario) && "12345".equals(contrasena)) {
            HelloApplication.cambiarVista("moderador.fxml", "Panel del Moderador");
            return;
        }

        /* 2. ‑‑‑‑‑‑‑‑‑‑‑‑ Comprobación de estudiantes ‑‑‑‑‑‑‑‑‑‑‑‑ */
        for (Estudiante est : estudiantes) {
            if (est.getUsuario().equals(usuario) &&
                    est.getContrasena().equals(contrasena)) {

                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/uniquindio/com/academix/principal.fxml"));
                    Parent root = loader.load();

                    // Obtener el controlador e inyectar el nombre del usuario
                    DashboardController controller = loader.getController();
                    controller.setNombreUsuario(est.getUsuario());


                    Stage stage = new Stage();
                    stage.setTitle("Inicio");
                    stage.setScene(new Scene(root));
                    stage.show();

                    // Cerrar ventana actual (login)
                    Stage ventanaLogin = (Stage) campoUsuario.getScene().getWindow();
                    ventanaLogin.close();

                    HelloApplication.setEstudianteActual(est);
                } catch (Exception e) {
                    mensajeError.setText("Error al cargar la vista principal");
                    e.printStackTrace();
                }
                // ✅ Sesión iniciada como estudiante
            }
        }

        /* 3. ‑‑‑‑‑‑‑‑‑‑‑‑ Credenciales inválidas ‑‑‑‑‑‑‑‑‑‑‑‑ */
        mensajeError.setText("Usuario o contraseña incorrectos");
    }

    public void abrirVentanaRegistro() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/uniquindio/com/academix/registroEstudiantes.fxml"));
            Parent root = loader.load();

            RegistroController registroController = loader.getController();
            registroController.setEstudiantes(estudiantes); // Pasa la lista compartida

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