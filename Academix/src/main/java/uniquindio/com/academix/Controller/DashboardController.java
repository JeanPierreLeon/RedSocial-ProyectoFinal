package uniquindio.com.academix.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import uniquindio.com.academix.Model.Estudiante;
import uniquindio.com.academix.Factory.ModelFactory;

import java.io.IOException;

public class DashboardController {

    @FXML
    private BorderPane rootPane;

    @FXML
    private Label IDcorreoEst;

    private Estudiante estudianteActual;

    @FXML
    public void initialize() {
        // No hacer nada hasta que se setee el estudiante
    }

    public void setEstudianteActual(Estudiante estudiante) {
        this.estudianteActual = estudiante;
        IDcorreoEst.setText(estudianteActual.getUsuario());
        try {
            goToInicio();  // Mostrar vista inicio al cargar
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void goToInicio() throws IOException {
        cargarVista("/uniquindio/com/academix/inicio.fxml");
    }

    @FXML
    public void goToPanel() throws IOException {
        cargarVista("/uniquindio/com/academix/panelEstudiante.fxml");
    }

    @FXML
    public void goToGrupos() throws IOException {
        cargarVista("/uniquindio/com/academix/grupos.fxml");
    }

    @FXML
    public void goToMensajeria() throws IOException {
        cargarVista("/uniquindio/com/academix/mensajeria.fxml");
    }

    private void cargarVista(String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent vista = loader.load();

        // Aquí si cada controlador tiene método setEstudianteActual, lo llamamos para pasarle el estudiante
        Object controller = loader.getController();
        try {
            controller.getClass()
                    .getMethod("setEstudianteActual", Estudiante.class)
                    .invoke(controller, estudianteActual);
        } catch (NoSuchMethodException e) {
            // El controlador no tiene método setEstudianteActual, lo ignoramos
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Si el controlador es GruposEstudioController, pásale también la instancia de Academix
        if (fxmlPath.contains("grupos.fxml")) {
            try {
                controller.getClass()
                        .getMethod("setAcademix", uniquindio.com.academix.Model.Academix.class)
                        .invoke(controller, ModelFactory.getInstance().getAcademix());
            } catch (NoSuchMethodException e) {
                // No tiene setAcademix, lo ignoramos
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        rootPane.setCenter(vista);
    }
}
