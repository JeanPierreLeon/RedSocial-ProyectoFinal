package uniquindio.com.academix.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import uniquindio.com.academix.HelloApplication;

import java.io.IOException;

public class PrincipalController {

    @FXML private BorderPane borderPane; // Asegúrate de que esto esté presente en tu FXML

    // Método para cambiar el contenido de la vista
    public void cargarVista(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/uniquindio/com/academix/" + fxml));
            Parent root = loader.load();

            // Modificar el contenido del centro del BorderPane
            borderPane.setCenter(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Acción del botón "Publicar Contenido"
    @FXML
    public void irAPublicarContenido() {
        cargarVista("publicarContenido.fxml");
    }

    // Acción del botón "Buscar Contenido"
    @FXML
    public void irABuscarContenido() {
        cargarVista("buscarContenido.fxml");
    }

    // Acción del botón "Solicitar Ayuda"
    @FXML
    public void irASolicitarAyuda() {
        cargarVista("solicitarAyuda.fxml");
    }

    // Acción del botón "Ver Sugerencias"
    @FXML
    public void irASugerencias() {
        cargarVista("sugerencias.fxml");
    }

    // Acción del botón "Grupos de Estudio"
    @FXML
    public void irAGrupos() {
        cargarVista("gruposEstudio.fxml");
    }

    // Acción del botón "Mensajes"
    @FXML
    public void irAMensajes() {
        cargarVista("mensajes.fxml");
    }

    // Este método se usa para cerrar la aplicación
    @FXML
    public void cerrarSesion() {
        // Regresar a la vista de login
        HelloApplication.cambiarVista("login.fxml", "Iniciar sesión");
    }


}

