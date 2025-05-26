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

    /** Carga SIEMPRE la instancia desde persistencia. */
    public static Academix getAcademixInstance() {
        return Persistencia.cargarRecursoBancoBinario();
    }

    /** Guarda la instancia actual de Academix. */
    public static void guardarAcademixInstance(Academix academix) {
        if (academix != null) {
            Persistencia.guardarRecursoBancoBinario(academix);
        }
    }

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
        // Carga SIEMPRE la instancia más reciente
        this.academix = getAcademixInstance();
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

        // Evita recursión infinita: NO guardar academix aquí ni en métodos llamados por listeners
        // Si necesitas persistir, hazlo solo en acciones explícitas del usuario (ej: enviar mensaje)

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
        ListaSimple<Mensaje> mensajes = academix.getConversacion(usuarioRemitente, estudianteActual.getUsuario());
        int contador = 0;
        if (mensajes != null) {
            for (int i = 0; i < mensajes.size(); i++) {
                Mensaje m = mensajes.get(i);
                if (m != null && !m.isLeido() && m.getDestinatario().equals(estudianteActual.getUsuario())) {
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
        ListaSimple<Mensaje> mensajes = academix.getConversacion(estudianteActual.getUsuario(), usuario);
        if (mensajes == null || mensajes.size() == 0) {
            return Long.MIN_VALUE;
        }
        long max = Long.MIN_VALUE;
        for (int i = 0; i < mensajes.size(); i++) {
            Mensaje m = mensajes.get(i);
            if (m != null && m.getFecha() != null) {
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
        // Carga la instancia más reciente antes de enviar
        this.academix = getAcademixInstance();
        academix.enviarMensaje(estudianteActual.getUsuario(), usuarioDestinatarioActual, texto);
        guardarAcademixInstance(academix);
        mensajeField.clear();
        // Marcar como leídos los mensajes recibidos del destinatario actual
        marcarMensajesComoLeidos(usuarioDestinatarioActual);
        cargarMensajesPrevios();
        cargarListaConversaciones(); // Refresca la lista y el contador de no leídos
    }

    /**
     * Marca como leídos todos los mensajes recibidos del usuario dado.
     */
    private void marcarMensajesComoLeidos(String usuarioRemitente) {
        ListaSimple<Mensaje> mensajes = academix.getConversacion(usuarioRemitente, estudianteActual.getUsuario());
        if (mensajes != null) {
            for (int i = 0; i < mensajes.size(); i++) {
                Mensaje m = mensajes.get(i);
                if (m != null && !m.isLeido() && m.getDestinatario().equals(estudianteActual.getUsuario())) {
                    m.setLeido(true);
                }
            }
        }
        // Eliminar persistencia aquí para evitar escrituras excesivas
        //guardarAcademixInstance();
    }

    /**
     * Carga los mensajes previos con el usuario seleccionado.
     */
    private void cargarMensajesPrevios() {
        if (usuarioDestinatarioActual == null) {
            limpiarChat();
            return;
        }
        // Carga la instancia más reciente antes de mostrar mensajes
        this.academix = getAcademixInstance();
        marcarMensajesComoLeidos(usuarioDestinatarioActual);
        contenedorMensajes.getChildren().clear();
        // Obtener todos los mensajes entre ambos usuarios (solo una vez, ya que getConversacion los incluye todos)
        ListaSimple<Mensaje> mensajes = academix.getConversacion(estudianteActual.getUsuario(), usuarioDestinatarioActual);
        java.util.List<Mensaje> todosMensajes = new java.util.ArrayList<>();
        if (mensajes != null) {
            for (int i = 0; i < mensajes.size(); i++) {
                Mensaje m = mensajes.get(i);
                if (m != null) {
                    todosMensajes.add(m);
                }
            }
        }
        // Ordenar por fecha
        todosMensajes.sort(Comparator.comparing(Mensaje::getFecha));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");
        for (Mensaje msg : todosMensajes) {
            HBox mensajeBox = new HBox();
            mensajeBox.setAlignment(msg.getRemitente().equals(estudianteActual.getUsuario()) ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
            TextFlow textFlow = new TextFlow(new Text(msg.getContenido() + "\n" + msg.getFecha().format(formatter)));
            textFlow.setStyle("-fx-background-color: " + (msg.getRemitente().equals(estudianteActual.getUsuario()) ? "#DCF8C6" : "#FFFFFF") + "; -fx-background-radius: 10; -fx-padding: 8;");
            mensajeBox.getChildren().add(textFlow);
            contenedorMensajes.getChildren().add(mensajeBox);
        }
        Platform.runLater(() -> scrollMensajes.setVvalue(1.0));
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
