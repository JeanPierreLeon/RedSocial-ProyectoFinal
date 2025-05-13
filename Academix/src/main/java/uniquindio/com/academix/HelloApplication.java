package uniquindio.com.academix;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import uniquindio.com.academix.Model.Estudiante;

import java.util.ArrayList;
import java.util.List;

public class HelloApplication extends Application {

    private final List<Estudiante> estudiantes = new ArrayList<>();

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/uniquindio/com/academix/login.fxml"));

        // Elimina esta línea porque el controlador ya está especificado en el archivo FXML
        // LoginController loginController = new LoginController(estudiantes);
        // loader.setController(loginController);  <- Esta línea debe ser eliminada

        Scene scene = new Scene(loader.load());
        stage.setTitle("Iniciar sesión");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
