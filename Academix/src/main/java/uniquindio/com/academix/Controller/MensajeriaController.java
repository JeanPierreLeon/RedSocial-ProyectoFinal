package uniquindio.com.academix.Controller;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import uniquindio.com.academix.Model.Academix;
import uniquindio.com.academix.Model.Estudiante;
import uniquindio.com.academix.Model.ListaSimple;
import uniquindio.com.academix.Model.Mensaje;
import uniquindio.com.academix.Utils.Persistencia;

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
    // Elimina el Map noLeidos y usa arreglos paralelos
    private String[] usuariosNoLeidos = new String[0];
    private int[] noLeidos = new int[0];

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

    // Método auxiliar para intercambiar dos elementos en una ListaSimple
    private void intercambiar(ListaSimple<String> lista, int i, int j) {
        String temp = lista.get(i);
        // No hay set, así que eliminamos y reinsertamos
        lista.eliminar(lista.get(i));
        lista.insertarEn(i, lista.get(j - 1)); // j-1 porque al eliminar i, los índices bajan
        lista.eliminar(lista.get(j));
        lista.insertarEn(j, temp);
    }

    /** Crea la lista de conversaciones ordenadas por último mensaje. */
    private void cargarListaConversaciones() {
        if (academix == null || estudianteActual == null) return;

        // Obtiene usuarios que tengan al menos un mensaje
        ListaSimple<String> usuarios = new ListaSimple<>();
        ListaSimple<Estudiante> listaEstudiantes = academix.getListaEstudiantes();
        for (int i = 0; i < listaEstudiantes.size(); i++) {
            Estudiante est = listaEstudiantes.get(i);
            String usuario = est.getUsuario();
            if (!usuario.equals(estudianteActual.getUsuario())) {
                usuarios.agregar(usuario);
            }
        }

        // Ordena por fecha del último mensaje (burbuja sin set)
        for (int i = 0; i < usuarios.tamano() - 1; i++) {
            for (int j = 0; j < usuarios.tamano() - i - 1; j++) {
                long fecha1 = obtenerFechaUltimoMensaje(usuarios.get(j));
                long fecha2 = obtenerFechaUltimoMensaje(usuarios.get(j+1));
                if (fecha1 < fecha2) {
                    intercambiar(usuarios, j, j+1);
                }
            }
        }

        // Calcula no-leídos usando arreglos paralelos
        usuariosNoLeidos = new String[usuarios.tamano()];
        noLeidos = new int[usuarios.tamano()];
        for (int i = 0; i < usuarios.tamano(); i++) {
            String user = usuarios.get(i);
            usuariosNoLeidos[i] = user;
            noLeidos[i] = contarNoLeidos(user);
        }

        // Actualiza la ListView
        listaConversaciones.getItems().clear();
        for (int i = 0; i < usuarios.tamano(); i++) {
            listaConversaciones.getItems().add(usuarios.get(i));
        }
        listaConversaciones.setCellFactory(lv -> new ListCell<String>() {
            @Override
            protected void updateItem(String usuario, boolean empty) {
                super.updateItem(usuario, empty);
                if (empty || usuario == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Estudiante est = academix.buscarEstudiante(usuario);
                    int noLeido = 0;
                    for (int i = 0; i < usuariosNoLeidos.length; i++) {
                        if (usuariosNoLeidos[i].equals(usuario)) {
                            noLeido = noLeidos[i];
                            break;
                        }
                    }
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

        ListaSimple<String> filtrados = new ListaSimple<>();
        ListaSimple<Estudiante> listaEstudiantes = academix.getListaEstudiantes();
        for (int i = 0; i < listaEstudiantes.size(); i++) {
            Estudiante est = listaEstudiantes.get(i);
            String usuario = est.getUsuario();
            if (!usuario.equals(estudianteActual.getUsuario()) && usuario.toLowerCase().contains(f)) {
                filtrados.agregar(usuario);
            }
        }
        for (int i = 0; i < filtrados.tamano() - 1; i++) {
            for (int j = 0; j < filtrados.tamano() - i - 1; j++) {
                long fecha1 = obtenerFechaUltimoMensaje(filtrados.get(j));
                long fecha2 = obtenerFechaUltimoMensaje(filtrados.get(j+1));
                if (fecha1 < fecha2) {
                    intercambiar(filtrados, j, j+1);
                }
            }
        }
        listaConversaciones.getItems().clear();
        for (int i = 0; i < filtrados.tamano(); i++) {
            listaConversaciones.getItems().add(filtrados.get(i));
        }
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
        // cargarListaConversaciones(); // Eliminar esta línea para evitar recarga/cierre inesperado
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
