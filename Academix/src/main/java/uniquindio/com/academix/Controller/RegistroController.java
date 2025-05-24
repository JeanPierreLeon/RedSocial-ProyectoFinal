package uniquindio.com.academix.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import uniquindio.com.academix.Model.Estudiante;
import uniquindio.com.academix.Model.Academix;
import uniquindio.com.academix.Utils.Persistencia;
import uniquindio.com.academix.Estructuras.ListaSimple;

public class RegistroController {

    @FXML private TextField campoUsuario;
    @FXML private PasswordField campoContrasena;
    @FXML private PasswordField campoConfirmarContrasena;
    @FXML private Label mensajeError;
    @FXML private Button btnRegistrar;

    private ListaSimple<Estudiante> estudiantes;
    private Academix academix;

    // Constructor sin parámetros, necesario para FXML
    public RegistroController() {
        // Aquí no se necesita inicializar nada en este caso
    }

    // Método para inyectar los parámetros después de la creación del controlador
    public void setEstudiantes(ListaSimple<Estudiante> estudiantes) {
        this.estudiantes = estudiantes;
    }

    // Agrega este setter para que LoginController pueda pasar el objeto Academix
    public void setAcademix(Academix academix) {
        this.academix = academix;
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

        // Usar la lista de estudiantes de academix para evitar referencias distintas
        for (Estudiante est : academix.getListaEstudiantes()) {
            if (est.getUsuario().equals(usuario)) {
                mensajeError.setText("El usuario ya existe");
                return;
            }
        }

        Estudiante nuevo = new Estudiante(usuario, contrasena);

        inicializarEstudiantePorDefecto(nuevo);

        academix.agregarEstudiante(nuevo);
        Persistencia.guardarRecursoBancoBinario(academix);

        uniquindio.com.academix.HelloApplication.cambiarVista("login.fxml", "Iniciar sesión");
        cerrarVentana();
    }

    /**
     * Inicializa la lógica/configuración que tienen los estudiantes por defecto.
     * Puedes agregar aquí lo que se le asigna a juan@academix.com.co y ana@academix.edu.
     */
    private void inicializarEstudiantePorDefecto(Estudiante estudiante) {
        // Ejemplo: estudiante.setRol("estudiante");
        // Ejemplo: estudiante.setGruposPorDefecto();
        // Ejemplo: estudiante.setMensajesBienvenida();
        // Si tienes lógica específica, agrégala aquí.
    }

    // Método para cerrar la ventana de registro
    private void cerrarVentana() {
        Stage stage = (Stage) btnRegistrar.getScene().getWindow();
        stage.close();
    }
}
