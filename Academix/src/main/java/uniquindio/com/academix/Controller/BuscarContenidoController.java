

package uniquindio.com.academix.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import uniquindio.com.academix.Model.Estudiante;

public class BuscarContenidoController {

    @FXML
    private TextField campoBusqueda;

    @FXML
    private Button botonBuscar;

    private Estudiante estudianteActual;

    // Setter para recibir el Estudiante actual
    public void setEstudianteActual(Estudiante estudiante) {
        this.estudianteActual = estudiante;
    }

    // Acción para realizar la búsqueda
    @FXML
    public void realizarBusqueda() {
        String busqueda = campoBusqueda.getText();
        if (busqueda.isEmpty()) {
            // Muestra un mensaje de error si no se ingresó nada en el campo de búsqueda
            Alert alert = new Alert(Alert.AlertType.WARNING, "Por favor ingresa un término de búsqueda.");
            alert.showAndWait();
        } else {
            // Aquí puedes agregar la lógica de búsqueda de contenido
            System.out.println("Buscando: " + busqueda);
        }
    }
}

