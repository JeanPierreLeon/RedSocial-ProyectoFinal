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

import java.io.File;
import java.nio.file.Files;

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
        academix = Persistencia.cargarRecursoBancoBinario();
        // Si la lista está vacía, agregar los de ejemplo y guardar
        if (academix.getListaEstudiantes().estaVacia()) {
            try {
                // Crear carpeta interna para imágenes de perfil si no existe
                File carpetaPerfiles = new File("data/perfiles");
                if (!carpetaPerfiles.exists()) {
                    carpetaPerfiles.mkdirs();
                }
                // Copiar imágenes de ejemplo solo si no existen
                File perfilJuan = new File("data/perfiles/juan.png");
                File perfilAna = new File("data/perfiles/ana.png");
                if (!perfilJuan.exists()) {
                    Files.copy(getClass().getResourceAsStream("/images/perfil_juan.png"), perfilJuan.toPath());
                }
                if (!perfilAna.exists()) {
                    Files.copy(getClass().getResourceAsStream("/images/perfil_ana.png"), perfilAna.toPath());
                }

                Estudiante juan = new Estudiante("juan@academix.com.co", "1234");
                juan.setNombre("Juan Pérez");
                juan.setFotoPerfil("data/perfiles/juan.png");
                juan.setFotoPortada(null); // Puedes agregar portada si quieres
                juan.agregarInteres("Matemáticas");
                juan.agregarInteres("Física");
                juan.agregarInteres("Programación");

                Estudiante ana = new Estudiante("ana@academix.edu", "abcd");
                ana.setNombre("Ana Gómez");
                ana.setFotoPerfil("data/perfiles/ana.png");
                ana.setFotoPortada(null);
                ana.agregarInteres("Literatura");
                ana.agregarInteres("Historia");
                ana.agregarInteres("Biología");

                academix.agregarEstudiante(juan);
                academix.agregarEstudiante(ana);
                Persistencia.guardarRecursoBancoBinario(academix);
            } catch (Exception e) {
                System.out.println("Error copiando imágenes de ejemplo: " + e.getMessage());
            }
        }
        imprimirUsuariosEnConsola();
    }

    @FXML
    public void iniciarSesion() {
        String usuario    = campoUsuario.getText().trim();
        String contrasena = campoContrasena.getText();

        // Comprobación de moderador
        if ("admin".equalsIgnoreCase(usuario) && "12345".equals(contrasena)) {
            uniquindio.com.academix.HelloApplication.cambiarVista("moderador.fxml", "Panel del Moderador");
            imprimirUsuariosEnConsola();
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
                imprimirUsuariosEnConsola();
                return;
            }
        }

        mensajeError.setText("Usuario o contraseña incorrectos");
        imprimirUsuariosEnConsola();
    }

    public void imprimirUsuariosEnConsola() {
        System.out.println("=== Lista de usuarios registrados ===");
        for (Estudiante est : academix.getListaEstudiantes()) {
            System.out.println("Usuario: " + est.getUsuario() + " | Contraseña: " + est.getContrasena());
        }
        System.out.println("====================================");
    }

    public void abrirVentanaRegistro() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/uniquindio/com/academix/registroEstudiantes.fxml"));
            Parent root = loader.load();

            RegistroController registroController = loader.getController();
            registroController.setAcademix(academix); // Mejor pasar el objeto completo

            // Asegura que al registrar un usuario se guarde en model.dat
            registroController.setOnRegistroExitoso(() -> {
                Persistencia.guardarRecursoBancoBinario(academix);
                imprimirUsuariosEnConsola();
            });

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
