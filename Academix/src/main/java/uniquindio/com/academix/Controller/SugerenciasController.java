package uniquindio.com.academix.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import uniquindio.com.academix.Model.Estudiante;

public class SugerenciasController {

    @FXML
    private Label sugerenciasLabel;

    private Estudiante estudianteActual;

    // Setter para recibir el Estudiante actual
    public void setEstudianteActual(Estudiante estudiante) {
        this.estudianteActual = estudiante;
    }

    // Método para cargar sugerencias
    @FXML
    public void cargarSugerencias() {
        // Aquí puedes agregar la lógica para cargar sugerencias personalizadas
        sugerenciasLabel.setText("Sugerencias para " + estudianteActual.getUsuario() + ":");
        // Ejemplo de sugerencias
        sugerenciasLabel.setText(sugerenciasLabel.getText() + "\n- Contenido sobre Java\n- Grupos de estudio sobre matemáticas");
    }
}
