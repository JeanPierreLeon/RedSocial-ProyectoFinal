package uniquindio.com.academix.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import uniquindio.com.academix.AcademixApplication;
import uniquindio.com.academix.Model.Estudiante;
import uniquindio.com.academix.Model.Academix;
import uniquindio.com.academix.Utils.Persistencia;

public class RegistroController {

    @FXML private TextField campoUsuario;
    @FXML private PasswordField campoContrasena;
    @FXML private PasswordField campoConfirmarContrasena;
    @FXML private Label mensajeError;
    @FXML private Button btnRegistrar;

    private Academix academix;
    private Runnable onRegistroExitoso;

    // Constructor sin parámetros, necesario para FXML
    public RegistroController() {
        // Aquí no se necesita inicializar nada en este caso
    }

    // Método para inyectar los parámetros después de la creación del controlador
    public void setAcademix(Academix academix) {
        this.academix = academix;
    }

    // Permite inyectar un callback para ejecutar tras el registro exitoso
    public void setOnRegistroExitoso(Runnable onRegistroExitoso) {
        this.onRegistroExitoso = onRegistroExitoso;
    }

    @FXML
    public void initialize() {
        // Nada necesario aquí
    }

    @FXML
    public void registrarEstudiante() {
        String usuario = campoUsuario.getText().trim();
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

        // Validación case-insensitive
        for (Estudiante est : academix.getListaEstudiantes()) {
            if (est.getUsuario().equalsIgnoreCase(usuario)) {
                mensajeError.setText("El usuario ya existe");
                return;
            }
        }

        Estudiante nuevo = new Estudiante(usuario, contrasena);

        inicializarEstudiantePorDefecto(nuevo);

        academix.agregarEstudiante(nuevo);
        Persistencia.guardarRecursoBancoBinario(academix);

        // Ejecutar callback si está definido
        if (onRegistroExitoso != null) {
            onRegistroExitoso.run();
        }

        AcademixApplication.cambiarVista("login.fxml", "Iniciar sesión");
        cerrarVentana();
    }

    /**
     * Inicializa la lógica/configuración que tienen los estudiantes por defecto.
     * Puedes agregar aquí lo que se le asigna a juan@academix.com.co y ana@academix.edu.
     */
    private void inicializarEstudiantePorDefecto(Estudiante estudiante) {
        // Asignar intereses por defecto según el usuario
        if (estudiante.getUsuario().equals("juan@academix.com.co")) {
            estudiante.agregarInteres("Matemáticas");
            estudiante.agregarInteres("Física");
            estudiante.agregarInteres("Programación");
        } else if (estudiante.getUsuario().equals("ana@academix.edu")) {
            estudiante.agregarInteres("Literatura");
            estudiante.agregarInteres("Historia");
            estudiante.agregarInteres("Biología");
        }
    }

    // Método para cerrar la ventana de registro
    private void cerrarVentana() {
        Stage stage = (Stage) btnRegistrar.getScene().getWindow();
        stage.close();
    }
}
