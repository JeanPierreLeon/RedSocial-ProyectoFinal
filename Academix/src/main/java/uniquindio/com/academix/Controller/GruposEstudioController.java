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

    public void setEstudianteActual(Estudiante estudiante) {
        this.estudianteActual = estudiante;
        cargarInteresesEstudiante();
        formarGruposAutomaticos();
    }

    public void setAcademix(Academix academix) {
        this.academix = academix;
        // Al recibir la instancia de Academix, refresca los intereses y grupos
        cargarInteresesEstudiante();
        formarGruposAutomaticos();
    }

    @FXML
    public void initialize() {
        // Al inicializar, intenta cargar intereses y grupos si academix ya está seteado
        cargarInteresesEstudiante();
        formarGruposAutomaticos();
    }

    @FXML
    public void agregarInteres() {
        String interes = campoNuevoInteres.getText().trim();
        if (interes.isEmpty() || estudianteActual == null || academix == null) {
            return;
        }
        Estudiante estudiantePersistente = academix.buscarEstudiante(estudianteActual.getUsuario());
        if (estudiantePersistente != null && !contieneInteres(estudiantePersistente.getIntereses(), interes)) {
            estudiantePersistente.agregarInteres(interes);
            Persistencia.guardarRecursoBancoBinario(academix);
            cargarInteresesEstudiante();
            formarGruposAutomaticos();
            campoNuevoInteres.clear();
        }
    }

    /**
     * Verifica si la ListaSimple de intereses contiene el interés dado.
     */
    private boolean contieneInteres(uniquindio.com.academix.Model.ListaSimple<String> intereses, String interes) {
        for (int i = 0; i < intereses.tamano(); i++) {
            String actual = intereses.get(i);
            if (actual != null && actual.equalsIgnoreCase(interes)) {
                return true;
            }
        }
        return false;
    }

    private void cargarInteresesEstudiante() {
        if (listaIntereses == null) return;
        listaIntereses.getItems().clear();
        if (estudianteActual != null && academix != null) {
            Estudiante estudiantePersistente = academix.buscarEstudiante(estudianteActual.getUsuario());
            if (estudiantePersistente != null) {
                for (String interes : estudiantePersistente.getIntereses()) {
                    listaIntereses.getItems().add(interes);
                }
            }
        }
    }

    private void formarGruposAutomaticos() {
        if (listaGrupos == null) return;
        listaGrupos.getItems().clear();
        if (academix == null) return;

        Map<String, List<String>> gruposPorInteres = new HashMap<>();

        for (Estudiante est : academix.getListaEstudiantes()) {
            for (String interes : est.getIntereses()) {
                gruposPorInteres.putIfAbsent(interes, new ArrayList<>());
                gruposPorInteres.get(interes).add(est.getUsuario());
            }
        }

        // Conectar usuarios en el grafo por grupo de estudio
        for (List<String> miembros : gruposPorInteres.values()) {
            if (miembros.size() > 1) {
                for (int i = 0; i < miembros.size(); i++) {
                    for (int j = i + 1; j < miembros.size(); j++) {
                        academix.getGrafoUsuarios().conectar(miembros.get(i), miembros.get(j));
                    }
                }
            }
        }

        boolean hayGrupos = false;
        for (Map.Entry<String, List<String>> entry : gruposPorInteres.entrySet()) {
            String interes = entry.getKey();
            List<String> miembros = entry.getValue();
            if (miembros.size() > 1) {
                listaGrupos.getItems().add("Interés: " + interes + " | Miembros: " + String.join(", ", miembros));
                hayGrupos = true;
            }
        }
        if (!hayGrupos) {
            listaGrupos.getItems().add("No hay grupos de estudio automáticos aún.");
        }
    }
}
