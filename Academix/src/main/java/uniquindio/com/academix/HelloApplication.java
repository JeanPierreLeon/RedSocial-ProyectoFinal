package uniquindio.com.academix;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import uniquindio.com.academix.Estructuras.ListaSimple;
import uniquindio.com.academix.Model.Estudiante;

public class HelloApplication extends Application {

    private static Stage primaryStage;
    private static final uniquindio.com.academix.Estructuras.ListaSimple<Estudiante> estudiantes = new ListaSimple<Estudiante>();
    private static Estudiante estudianteActual; // <-- Sesión activa

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        cambiarVista("login.fxml", "Iniciar sesión");
    }

    public static void cambiarVista(String fxml, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("/uniquindio/com/academix/" + fxml));
            Scene scene = new Scene(loader.load());
            primaryStage.setTitle(titulo);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ListaSimple<Estudiante> getEstudiantes() {
        return estudiantes;
    }

    public static Estudiante getEstudianteActual() {
        return estudianteActual;
    }

    public static void setEstudianteActual(Estudiante estudiante) {
        estudianteActual = estudiante;
    }

    public static void main(String[] args) {
        launch(args);
    }
}