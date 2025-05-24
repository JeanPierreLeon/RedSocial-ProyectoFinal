package uniquindio.com.academix.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import uniquindio.com.academix.Model.Estudiante;
import uniquindio.com.academix.Model.Academix;
import uniquindio.com.academix.Utils.Persistencia;

import java.util.*;

public class GruposEstudioController {

    @FXML
    private ListView<String> listaGrupos;

    @FXML
    private ListView<String> listaIntereses;

    @FXML
    private TextField campoNuevoInteres;

    private Estudiante estudianteActual;
    private Academix academix;

    // Setter para recibir el Estudiante actual y la instancia de Academix
    public void setEstudianteActual(Estudiante estudiante) {
        this.estudianteActual = estudiante;
        // No recargar academix aquí, se debe pasar desde el DashboardController
        cargarInteresesEstudiante();
        formarGruposAutomaticos();
    }

    // Nuevo setter para recibir Academix desde el DashboardController
    public void setAcademix(Academix academix) {
        this.academix = academix;
    }

    @FXML
    public void initialize() {
        // Inicialización de controles si es necesario
    }

    @FXML
    public void agregarInteres() {
        String interes = campoNuevoInteres.getText().trim();
        if (!interes.isEmpty() && estudianteActual != null && academix != null) {
            estudianteActual.agregarInteres(interes);
            // Guardar SIEMPRE usando la instancia de Academix que se está usando en toda la app
            uniquindio.com.academix.Factory.ModelFactory.getInstance().guardarRecursosXML();
            cargarInteresesEstudiante();
            formarGruposAutomaticos();
            campoNuevoInteres.clear();
        }
    }

    private void cargarInteresesEstudiante() {
        listaIntereses.getItems().clear();
        if (estudianteActual != null) {
            listaIntereses.getItems().addAll(estudianteActual.getIntereses());
        }
    }

    // Forma grupos automáticos según intereses compartidos
    private void formarGruposAutomaticos() {
        listaGrupos.getItems().clear();
        if (academix == null) return;

        Map<String, List<String>> gruposPorInteres = new HashMap<>();

        for (Estudiante est : academix.getListaEstudiantes()) {
            for (String interes : est.getIntereses()) {
                gruposPorInteres.putIfAbsent(interes, new ArrayList<>());
                gruposPorInteres.get(interes).add(est.getUsuario());
            }
        }

        boolean hayGrupos = false;
        for (Map.Entry<String, List<String>> entry : gruposPorInteres.entrySet()) {
            String interes = entry.getKey();
            List<String> miembros = entry.getValue();
            if (miembros.size() > 1) { // Solo mostrar grupos con más de un miembro
                listaGrupos.getItems().add("Interés: " + interes + " | Miembros: " + String.join(", ", miembros));
                hayGrupos = true;
            }
        }
        if (!hayGrupos) {
            listaGrupos.getItems().add("No hay grupos de estudio automáticos aún.");
        }
    }
}
