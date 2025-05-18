package uniquindio.com.academix.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import uniquindio.com.academix.Model.Estudiante;

public class GruposEstudioController {

    @FXML
    private ListView<String> listaGrupos;

    private Estudiante estudianteActual;

    // Setter para recibir el Estudiante actual
    public void setEstudianteActual(Estudiante estudiante) {
        this.estudianteActual = estudiante;
    }

    // Método para cargar los grupos de estudio
    @FXML
    public void cargarGrupos() {
        // Aquí puedes agregar la lógica para cargar los grupos de estudio
        listaGrupos.getItems().add("Grupo de Estudio sobre Programación");
        listaGrupos.getItems().add("Grupo de Estudio sobre Matemáticas");
        listaGrupos.getItems().add("Grupo de Estudio sobre Física");
    }
}
