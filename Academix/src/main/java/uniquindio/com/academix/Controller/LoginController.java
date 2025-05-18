package uniquindio.com.academix.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import uniquindio.com.academix.HelloApplication;
import uniquindio.com.academix.Model.Estudiante;

import java.util.ArrayList;
import java.util.List;

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
            estudiantes.add(new Estudiante("juan", "1234"));
            estudiantes.add(new Estudiante("ana", "abcd"));
        }
    }

    @FXML
    public void iniciarSesion() {
        String usuario     = campoUsuario.getText();
        String contrasena  = campoContrasena.getText();

        /* 1. ‑‑‑‑‑‑‑‑‑‑‑‑ Comprobación de moderador ‑‑‑‑‑‑‑‑‑‑‑‑ */
        if ("admin".equals(usuario) && "12345".equals(contrasena)) {
            HelloApplication.cambiarVista("moderador.fxml", "Panel del Moderador");
            return;                     // ✅  ¡Listo!  No seguimos revisando estudiantes
        }

        /* 2. ‑‑‑‑‑‑‑‑‑‑‑‑ Comprobación de estudiantes ‑‑‑‑‑‑‑‑‑‑‑‑ */
        for (Estudiante est : estudiantes) {
            if (est.getUsuario().equals(usuario) &&
                    est.getContrasena().equals(contrasena)) {

                HelloApplication.setEstudianteActual(est);
                HelloApplication.cambiarVista("principal.fxml", "Inicio");
                return;                 // ✅ Sesión iniciada como estudiante
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