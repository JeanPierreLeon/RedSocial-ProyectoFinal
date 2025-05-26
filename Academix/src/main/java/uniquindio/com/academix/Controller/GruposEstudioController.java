package uniquindio.com.academix.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import uniquindio.com.academix.Model.Academix;
import uniquindio.com.academix.Model.Estudiante;
import uniquindio.com.academix.Utils.Persistencia;

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

    // Clase auxiliar para agrupar interés y miembros
    private static class GrupoInteres {
        String interes;
        uniquindio.com.academix.Model.ListaSimple<String> miembros;
        GrupoInteres(String interes) {
            this.interes = interes;
            this.miembros = new uniquindio.com.academix.Model.ListaSimple<>();
        }
    }

    private void formarGruposAutomaticos() {
        if (listaGrupos == null) return;
        listaGrupos.getItems().clear();
        if (academix == null) return;

        uniquindio.com.academix.Model.ListaSimple<GrupoInteres> gruposPorInteres = new uniquindio.com.academix.Model.ListaSimple<>();

        for (Estudiante est : academix.getListaEstudiantes()) {
            for (String interes : est.getIntereses()) {
                GrupoInteres grupo = null;
                for (int i = 0; i < gruposPorInteres.tamano(); i++) {
                    if (gruposPorInteres.get(i).interes.equals(interes)) {
                        grupo = gruposPorInteres.get(i);
                        break;
                    }
                }
                if (grupo == null) {
                    grupo = new GrupoInteres(interes);
                    gruposPorInteres.agregar(grupo);
                }
                if (!grupo.miembros.contiene(est.getUsuario())) {
                    grupo.miembros.agregar(est.getUsuario());
                }
            }
        }

        // Conectar usuarios en el grafo por grupo de estudio
        for (int g = 0; g < gruposPorInteres.tamano(); g++) {
            GrupoInteres grupo = gruposPorInteres.get(g);
            if (grupo.miembros.tamano() > 1) {
                for (int i = 0; i < grupo.miembros.tamano(); i++) {
                    for (int j = i + 1; j < grupo.miembros.tamano(); j++) {
                        academix.getGrafoUsuarios().conectar(grupo.miembros.get(i), grupo.miembros.get(j));
                    }
                }
            }
        }

        boolean hayGrupos = false;
        for (int g = 0; g < gruposPorInteres.tamano(); g++) {
            GrupoInteres grupo = gruposPorInteres.get(g);
            if (grupo.miembros.tamano() > 1) {
                // Unir miembros en string
                StringBuilder miembrosStr = new StringBuilder();
                for (int m = 0; m < grupo.miembros.tamano(); m++) {
                    miembrosStr.append(grupo.miembros.get(m));
                    if (m < grupo.miembros.tamano() - 1) miembrosStr.append(", ");
                }
                listaGrupos.getItems().add("Interés: " + grupo.interes + " | Miembros: " + miembrosStr);
                hayGrupos = true;
            }
        }
        if (!hayGrupos) {
            listaGrupos.getItems().add("No hay grupos de estudio automáticos aún.");
        }
    }
}
