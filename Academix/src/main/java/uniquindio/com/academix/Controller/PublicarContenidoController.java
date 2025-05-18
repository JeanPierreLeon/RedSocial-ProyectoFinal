package uniquindio.com.academix.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import uniquindio.com.academix.Model.Estudiante;

public class PublicarContenidoController {

    @FXML
    private TextField campoTitulo;
    @FXML
    private TextArea campoDescripcion;
    @FXML
    private Button botonPublicar;

    private Estudiante estudianteActual;

    // Setter para recibir el Estudiante actual
    public void setEstudianteActual(Estudiante estudiante) {
        this.estudianteActual = estudiante;
    }

    // Acción para publicar el contenido
    @FXML
    public void publicarContenido() {
        String titulo = campoTitulo.getText();
        String descripcion = campoDescripcion.getText();

        if (titulo.isEmpty() || descripcion.isEmpty()) {
            // Muestra un mensaje de error si los campos están vacíos
            Alert alert = new Alert(Alert.AlertType.WARNING, "Por favor ingresa un título y una descripción.");
            alert.showAndWait();
        } else {
            // Aquí puedes agregar la lógica para guardar el contenido en la base de datos o realizar la acción de publicación
            System.out.println("Contenido publicado por " + estudianteActual.getUsuario());
            System.out.println("Título: " + titulo);
            System.out.println("Descripción: " + descripcion);
        }
    }
}
