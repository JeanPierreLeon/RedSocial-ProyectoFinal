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
import uniquindio.com.academix.Model.Academix;
import uniquindio.com.academix.Utils.Persistencia;

public class LoginController {

    @FXML private TextField campoUsuario;
    @FXML private PasswordField campoContrasena;
    @FXML private Label mensajeError;

    private Academix academix;

    public LoginController() {
        // La carga real se hace en initialize()
    }

    @FXML
    public void initialize() {
        // Cargar el modelo completo desde la persistencia
        academix = Persistencia.cargarRecursoBancoBinario();
        // Si la lista está vacía, agregar los de ejemplo y guardar
        if (academix.getListaEstudiantes().estaVacia()) {
            academix.agregarEstudiante(new Estudiante("juan@academix.com.co", "1234"));
            academix.agregarEstudiante(new Estudiante("ana@academix.edu", "abcd"));
            Persistencia.guardarRecursoBancoBinario(academix);
        }
    }

    @FXML
    public void iniciarSesion() {
        String usuario    = campoUsuario.getText().trim();
        String contrasena = campoContrasena.getText();

        // Comprobación de moderador
        if ("admin".equalsIgnoreCase(usuario) && "12345".equals(contrasena)) {
            uniquindio.com.academix.HelloApplication.cambiarVista("moderador.fxml", "Panel del Moderador");
            return;
        }

        for (Estudiante est : academix.getListaEstudiantes()) {
            if (est.getUsuario().equalsIgnoreCase(usuario) && est.getContrasena().equals(contrasena)) {
                try {
                    uniquindio.com.academix.HelloApplication.setEstudianteActual(est);

                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/uniquindio/com/academix/principal.fxml"));
                    Parent root = loader.load();

                    DashboardController controller = loader.getController();
                    controller.setEstudianteActual(est);

                    Stage stage = new Stage();
                    stage.setTitle("Inicio");
                    stage.setScene(new Scene(root));
                    stage.show();

                    Stage ventanaLogin = (Stage) campoUsuario.getScene().getWindow();
                    ventanaLogin.close();

                } catch (Exception e) {
                    mensajeError.setText("Error al cargar la vista principal");
                    e.printStackTrace();
                }
                return;
            }
        }

        mensajeError.setText("Usuario o contraseña incorrectos");
    }

    // Método temporal para depuración
    public void imprimirUsuariosEnConsola() {
        for (Estudiante est : academix.getListaEstudiantes()) {
            System.out.println("Usuario: " + est.getUsuario() + " | Contraseña: " + est.getContrasena());
        }
    }

    public void abrirVentanaRegistro() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/uniquindio/com/academix/registroEstudiantes.fxml"));
            Parent root = loader.load();

            RegistroController registroController = loader.getController();
            registroController.setAcademix(academix); // Mejor pasar el objeto completo

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
