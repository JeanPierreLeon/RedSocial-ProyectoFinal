package uniquindio.com.academix.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import uniquindio.com.academix.Model.Estudiante;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LoginController {

    @FXML private TextField campoUsuario;
    @FXML private PasswordField campoContrasena;
    @FXML private Label mensajeError;

    private List<Estudiante> estudiantes;

    // Constructor sin parámetros, necesario para FXML
    public LoginController() {
        estudiantes = new ArrayList<>();
    }

    // Método para inicializar los datos de ejemplo
    @FXML
    public void initialize() {
        if (estudiantes.isEmpty()) {
            estudiantes.add(new Estudiante("juan", "1234"));
            estudiantes.add(new Estudiante("ana", "abcd"));
        }
    }

    // Método para iniciar sesión
    @FXML
    public void iniciarSesion() {
        String usuario = campoUsuario.getText();
        String contrasena = campoContrasena.getText();

        for (Estudiante est : estudiantes) {
            if (est.getUsuario().equals(usuario) && est.getContrasena().equals(contrasena)) {
                abrirVentanaPrincipal();
                return;
            }
        }

        mensajeError.setText("Usuario o contraseña incorrectos");
    }

    // Método para abrir la ventana principal
    private void abrirVentanaPrincipal() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/uniquindio/com/academix/principal.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) campoUsuario.getScene().getWindow();
            stage.setScene(new Scene(root, 600, 400));
            stage.show();
        } catch (Exception e) {
            mensajeError.setText("Error al abrir la ventana principal");
            e.printStackTrace();
        }
    }

    // Abrir la ventana de registro de estudiantes
    public void abrirVentanaRegistro() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/uniquindio/com/academix/registroEstudiantes.fxml"));  // Ruta corregida
            Parent root = loader.load();

            // Obtener el controlador de la ventana de registro
            RegistroController registroController = loader.getController();
            registroController.setEstudiantes(estudiantes); // Aquí se pasa la lista de estudiantes

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
