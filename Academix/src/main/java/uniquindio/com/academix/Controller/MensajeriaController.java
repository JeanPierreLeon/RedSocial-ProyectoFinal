package uniquindio.com.academix.Controller;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import uniquindio.com.academix.Estructuras.ListaSimple;
import uniquindio.com.academix.Factory.ModelFactory;
import uniquindio.com.academix.Model.Academix;
import uniquindio.com.academix.Model.Estudiante;
import uniquindio.com.academix.Model.Mensaje;
import uniquindio.com.academix.Estructuras.ListaSimple.Nodo;
import java.net.URL;
import java.util.ResourceBundle;

public class MensajeriaController implements Initializable {

    /* Modelo y sesión */
    private Academix   academix;
    private Estudiante estudianteActual;

    /* UI */
    @FXML private ListView<String>  listaMensajes;
    @FXML private TextField         mensajeField;
    @FXML private ComboBox<String>  comboDestinatarios;

    /* ───────────────────── Inicialización ───────────────────── */
    @FXML private Button btnEnviar;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        btnEnviar.disableProperty().bind(
                comboDestinatarios.valueProperty().isNull()
                        .or(mensajeField.textProperty().isEmpty()) // opción alternativa
                        //.or(Bindings.createBooleanBinding(() -> mensajeField.getText().isBlank(), mensajeField.textProperty())) // opción con binding más seguro
                        .or(Bindings.createBooleanBinding(
                                () -> mensajeField.textProperty().get().isBlank(),
                                mensajeField.textProperty()
                        ))
        );
    }



    public void setEstudianteActual(Estudiante estudiante) {
        this.estudianteActual = estudiante;
        this.academix        = ModelFactory.getInstance().getAcademix();
        cargarDestinatarios();
        cargarMensajesPrevios();
    }

    /* ───────────────────── Enviar ───────────────────── */
    @FXML
    private void enviarMensaje() {
        String texto        = mensajeField.getText();
        String destinatario = comboDestinatarios.getValue();

        if (texto == null || texto.isBlank() || destinatario == null || destinatario.isBlank()) return;

        Mensaje mensaje = new Mensaje(estudianteActual.getUsuario(), destinatario, texto);
        academix.agregarMensaje(mensaje);          // ← guarda en ambos
        mensajeField.clear();

        cargarMensajesPrevios();                   // refresca solo la conversación
        ModelFactory.getInstance().guardarRecursosXML();
    }

    /* ───────────────────── Historial ───────────────────── */
    @FXML
    private void cargarMensajesPrevios() {
        listaMensajes.getItems().clear();
        String destinatario = comboDestinatarios.getValue();
        if (destinatario == null) return; // ningún destinatario seleccionado

        for (Mensaje mensaje : academix.getConversacion(estudianteActual.getUsuario(), destinatario)) {
            listaMensajes.getItems().add(mensaje.toString());
        }
    }

    private void cargarDestinatarios() {
        academix.sincronizarEstudiantesConGlobal(); // <-- sincronizar

        comboDestinatarios.getItems().clear();

        for (Estudiante est : academix.getListaEstudiantes()) {
            if (!est.getUsuario().equals(estudianteActual.getUsuario())) {
                comboDestinatarios.getItems().add(est.getUsuario());
            }
        }
    }




}
