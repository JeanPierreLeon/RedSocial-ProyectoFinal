package uniquindio.com.academix.Controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import uniquindio.com.academix.Model.*;

import uniquindio.com.academix.Utils.Persistencia;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class MensajeriaController {

    /* -------- FXML -------- */
    @FXML private ListView<String> listaConversaciones;
    @FXML private TextField buscadorContactos;
    @FXML private VBox contenedorMensajes;
    @FXML private ScrollPane scrollMensajes;
    @FXML private TextField mensajeField;
    @FXML private Button btnEnviar;
    @FXML private Label nombreContacto;
    @FXML private Label estadoContacto;

    /* -------- Datos -------- */
    private Academix academix;
    private Estudiante estudianteActual;
    /** Conversación mostrada actualmente */
    private String usuarioDestinatarioActual = null;
    /** Mapa rápido para saber cuántos no leídos hay por usuario */
    private final Map<String, Integer> noLeidos = new HashMap<>();

    /* =============================== */
    /* === MÉTODOS PÚBLICOS / INIT === */
    /* =============================== */

    /**
     * Establece el estudiante logueado y carga todo.
     */
    @FXML
    public void setEstudianteActual(Estudiante estudiante) {
        this.estudianteActual = estudiante;
        academix = Persistencia.cargarRecursoBancoBinario();
        cargarListaConversaciones();
        limpiarChat();
    }

    @FXML
    public void initialize() {
        // Enter envía mensaje
        mensajeField.setOnAction(e -> enviarMensaje());
        // Botón deshabilitado si el campo está vacío
        btnEnviar.disableProperty().bind(mensajeField.textProperty().isEmpty());

        // Al seleccionar un contacto en el ListView
        listaConversaciones.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            usuarioDestinatarioActual = newVal;
            cargarMensajesPrevios();
        });

        // Buscador reactivo
        buscadorContactos.textProperty().addListener((obs, oldTxt, newTxt) -> filtrarConversaciones(newTxt));
    }

    /* ============================= */
    /* === MANEJO DE CONVERSACIONES === */
    /* ============================= */

    /** Crea la lista de conversaciones ordenadas por último mensaje. */
    private void cargarListaConversaciones() {
        if (academix == null || estudianteActual == null) return;

        // Obtiene usuarios que tengan al menos un mensaje
        ObservableList<String> usuarios = FXCollections.observableArrayList();
        ListaSimple<Estudiante> listaEstudiantes = academix.getListaEstudiantes();
        for (int i = 0; i < listaEstudiantes.size(); i++) {
            Estudiante est = listaEstudiantes.get(i);
            String usuario = est.getUsuario();
            if (!usuario.equals(estudianteActual.getUsuario())) {
                usuarios.add(usuario);
            }
        }

        // Ordena por fecha del último mensaje (más reciente primero)
        usuarios.sort(Comparator.comparing(usuario -> obtenerFechaUltimoMensaje((String) usuario)).reversed());

        // Calcula no-leídos
        noLeidos.clear();
        for (String user : usuarios) {
            noLeidos.put(user, contarNoLeidos(user));
        }

        listaConversaciones.setItems(usuarios);
        listaConversaciones.setCellFactory(lv -> new ListCell<String>() {
            @Override
            protected void updateItem(String usuario, boolean empty) {
                super.updateItem(usuario, empty);
                if (empty || usuario == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Estudiante est = academix.buscarEstudiante(usuario);
                    int noLeido = noLeidos.getOrDefault(usuario, 0);
                    String texto = (est != null ? est.getNombre() : usuario);
                    if (noLeido > 0) {
                        texto += " (" + noLeido + ")";
                        setStyle("-fx-font-weight: bold;");
                    } else {
                        setStyle("");
                    }
                    setText(texto);
                }
            }
        });
    }

    /**
     * Cuenta la cantidad de mensajes no leídos del usuario dado hacia el usuario actual.
     */
    private int contarNoLeidos(String usuarioRemitente) {
        // Cambia obtenerConversacion por getConversacion
        ListaSimple<Mensaje> mensajes = academix.getConversacion(usuarioRemitente, estudianteActual.getUsuario());
        int contador = 0;
        if (mensajes != null) {
            for (int i = 0; i < mensajes.size(); i++) {
                Mensaje m = mensajes.get(i);
                if (!m.isLeido() && m.getDestinatario().equals(estudianteActual.getUsuario())) {
                    contador++;
                }
            }
        }
        return contador;
    }

    /** Filtra la ListView según el buscador. */
    private void filtrarConversaciones(String filtro) {
        if (filtro == null) filtro = "";
        final String f = filtro.toLowerCase();

        ObservableList<String> filtrados = FXCollections.observableArrayList();
        ListaSimple<Estudiante> listaEstudiantes = academix.getListaEstudiantes();
        for (int i = 0; i < listaEstudiantes.size(); i++) {
            Estudiante est = listaEstudiantes.get(i);
            String usuario = est.getUsuario();
            if (!usuario.equals(estudianteActual.getUsuario()) && usuario.toLowerCase().contains(f)) {
                filtrados.add(usuario);
            }
        }
        filtrados.sort(Comparator.comparing((String usuario) -> obtenerFechaUltimoMensaje(usuario)).reversed());
        listaConversaciones.getItems().setAll(filtrados);
        listaConversaciones.refresh();
    }

    /**
     * Devuelve la fecha del último mensaje con un usuario.
     * Si no hay mensajes, retorna Long.MIN_VALUE para que quede al final.
     */
    private long obtenerFechaUltimoMensaje(String usuario) {
        // Cambia obtenerConversacion por getConversacion
        ListaSimple<Mensaje> mensajes = academix.getConversacion(estudianteActual.getUsuario(), usuario);
        if (mensajes == null || mensajes.size() == 0) {
            return Long.MIN_VALUE;
        }
        // Buscar el mensaje más reciente
        long max = Long.MIN_VALUE;
        for (int i = 0; i < mensajes.size(); i++) {
            Mensaje m = mensajes.get(i);
            if (m.getFecha() != null) {
                long epoch = m.getFecha().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();
                if (epoch > max) max = epoch;
            }
        }
        return max;
    }

    /**
     * Limpia el área de mensajes y los labels de contacto.
     */
    private void limpiarChat() {
        if (contenedorMensajes != null) {
            contenedorMensajes.getChildren().clear();
        }
        if (nombreContacto != null) {
            nombreContacto.setText("");
        }
        if (estadoContacto != null) {
            estadoContacto.setText("");
        }
    }

    /**
     * Envía el mensaje escrito al usuario seleccionado.
     * Este método es compatible con onAction en FXML.
     */
    @FXML
    public void enviarMensaje() {
        String texto = mensajeField.getText();
        if (texto == null || texto.trim().isEmpty() || usuarioDestinatarioActual == null) {
            return;
        }
        academix.enviarMensaje(estudianteActual.getUsuario(), usuarioDestinatarioActual, texto);
        Persistencia.guardarRecursoBancoBinario(academix);
        mensajeField.clear();
        cargarMensajesPrevios();
    }

    /**
     * Carga los mensajes previos con el usuario seleccionado.
     */
    private void cargarMensajesPrevios() {
        if (usuarioDestinatarioActual == null) {
            limpiarChat();
            return;
        }
        contenedorMensajes.getChildren().clear();
        // Cambia getConversacion para asegurar que usuarioDestinatarioActual es String
        ListaSimple<Mensaje> mensajes = academix.getConversacion(estudianteActual.getUsuario(), usuarioDestinatarioActual);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");
        for (int i = 0; i < mensajes.size(); i++) {
            Mensaje msg = mensajes.get(i);
            HBox mensajeBox = new HBox();
            mensajeBox.setAlignment(msg.getRemitente().equals(estudianteActual.getUsuario()) ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
            TextFlow textFlow = new TextFlow(new Text(msg.getContenido() + "\n" + msg.getFecha().format(formatter)));
            textFlow.setStyle("-fx-background-color: " + (msg.getRemitente().equals(estudianteActual.getUsuario()) ? "#DCF8C6" : "#FFFFFF") + "; -fx-background-radius: 10; -fx-padding: 8;");
            mensajeBox.getChildren().add(textFlow);
            contenedorMensajes.getChildren().add(mensajeBox);
        }
        Platform.runLater(() -> scrollMensajes.setVvalue(1.0));
        // Actualizar nombre y estado del contacto
        String usuarioContacto = usuarioDestinatarioActual != null ? usuarioDestinatarioActual : "";
        if (usuarioContacto.isEmpty()) {
            nombreContacto.setText("");
            estadoContacto.setText("");
            return;
        }
        Estudiante contacto = academix.buscarEstudiante(usuarioContacto);
        if (contacto != null) {
            nombreContacto.setText(contacto.getNombre());
            estadoContacto.setText("En línea");
        }
    }
}
