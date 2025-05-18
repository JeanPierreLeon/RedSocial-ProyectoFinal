package uniquindio.com.academix.Controller;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class DashboardController {

    @FXML
    private BorderPane rootPane;

    public Hyperlink inicioLink;
    public Hyperlink panelLink;
    public Hyperlink gruposLink;
    public Hyperlink mensajeriaLink;

    @FXML
    public void initialize() {
        try {
            goToInicio(null); // Carga la vista de inicio al iniciar
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void goToInicio(ActionEvent event) throws IOException {
        cambiarVista("/uniquindio/com/academix/inicio.fxml");
    }

    public void goToPanel(ActionEvent event) throws IOException {
        cambiarVista("/uniquindio/com/academix/panel.fxml");
    }

    public void goToGrupos(ActionEvent event) throws IOException {
        cambiarVista("/uniquindio/com/academix/grupos.fxml");
    }

    public void goToMensajeria(ActionEvent event) throws IOException {
        cambiarVista("/uniquindio/com/academix/mensajeria.fxml");
    }


    private void cambiarVista(String rutaFXML) throws IOException {
        Parent nuevaVista = FXMLLoader.load(getClass().getResource(rutaFXML));
        rootPane.setCenter(nuevaVista);
    }
}
