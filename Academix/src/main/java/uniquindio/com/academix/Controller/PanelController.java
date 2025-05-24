package uniquindio.com.academix.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import uniquindio.com.academix.Model.Estudiante;

public class PanelController {
    @FXML
    private ListView<String> contenidosListView;
    
    @FXML
    private Button nuevoContenidoBtn;
    
    @FXML
    private Label valoracionPromedioLabel;
    
    @FXML
    private ListView<String> valoracionesListView;
    
    @FXML
    private TextArea sugerenciaTextArea;
    
    @FXML
    private Button enviarSugerenciaBtn;
    
    @FXML
    private ComboBox<String> categoriaComboBox;
    
    @FXML
    private TextArea solicitudTextArea;
    
    @FXML
    private Button enviarSolicitudBtn;
    
    @FXML
    private ListView<String> solicitudesListView;

    private Estudiante estudianteActual;

    @FXML
    public void initialize() {
        // Inicializar componentes
        categoriaComboBox.getItems().addAll("Matemáticas", "Programación", "Física", "Química", "Otros");
    }

    public void setEstudianteActual(Estudiante estudiante) {
        this.estudianteActual = estudiante;
        // Aquí puedes cargar los datos específicos del estudiante
        cargarDatosEstudiante();
    }

    private void cargarDatosEstudiante() {
        if (estudianteActual != null) {
            // Aquí cargarás los contenidos, valoraciones y solicitudes del estudiante
            // Por ahora solo mostraremos datos de ejemplo
            contenidosListView.getItems().addAll(
                "Contenido de ejemplo 1",
                "Contenido de ejemplo 2"
            );
            
            valoracionesListView.getItems().addAll(
                "Valoración 5★ - Gran explicación",
                "Valoración 4★ - Muy útil"
            );
            
            solicitudesListView.getItems().addAll(
                "Solicitud pendiente - Matemáticas",
                "Solicitud resuelta - Programación"
            );
        }
    }

    @FXML
    public void onNuevoContenido() {
        // Implementar lógica para nuevo contenido
        System.out.println("Nuevo contenido");
    }

    @FXML
    public void onEnviarSugerencia() {
        String sugerencia = sugerenciaTextArea.getText();
        if (!sugerencia.isEmpty()) {
            // Implementar lógica para enviar sugerencia
            System.out.println("Sugerencia enviada: " + sugerencia);
            sugerenciaTextArea.clear();
        }
    }

    @FXML
    public void onEnviarSolicitud() {
        String categoria = categoriaComboBox.getValue();
        String solicitud = solicitudTextArea.getText();
        
        if (categoria != null && !solicitud.isEmpty()) {
            // Implementar lógica para enviar solicitud
            System.out.println("Solicitud enviada - Categoría: " + categoria + ", Solicitud: " + solicitud);
            solicitudTextArea.clear();
            categoriaComboBox.setValue(null);
        }
    }
} 