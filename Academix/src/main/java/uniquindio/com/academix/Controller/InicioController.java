package uniquindio.com.academix.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import uniquindio.com.academix.Model.Academix;
import uniquindio.com.academix.Model.ContenidoEducativo;
import uniquindio.com.academix.Model.Estudiante;
import uniquindio.com.academix.Utils.Persistencia;
import uniquindio.com.academix.Estructuras.ListaSimple;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import javafx.geometry.Pos;

public class InicioController {

    @FXML private TextField tituloField;
    @FXML private TextField descripcionField;
    @FXML private ChoiceBox<String> tipoChoiceBox;
    @FXML private TextField urlField;
    @FXML private VBox contenedorPublicaciones;
    @FXML private TextField busquedaField;
    @FXML private ChoiceBox<String> ordenChoiceBox;

    private File archivoSeleccionado;
    private Academix academix;
    private Estudiante estudianteActual;

    public void setEstudianteActual(Estudiante estudiante) {
        this.estudianteActual = estudiante;
        academix = Persistencia.cargarRecursoBancoBinario();
        buscarContenido();
    }

    @FXML
    public void initialize() {
        tipoChoiceBox.getItems().addAll("Imagen", "PDF", "Video", "Otro");
        tipoChoiceBox.getSelectionModel().selectFirst();

        if (ordenChoiceBox != null) {
            ordenChoiceBox.getItems().addAll("Sin ordenar", "Tipo", "Fecha");
            ordenChoiceBox.getSelectionModel().selectFirst();

            ordenChoiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> buscarContenido());
        }

        if (busquedaField != null) {
            busquedaField.textProperty().addListener((obs, oldVal, newVal) -> buscarContenido());
        }
    }

    @FXML
    public void seleccionarArchivo() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar archivo");
        archivoSeleccionado = fileChooser.showOpenDialog(null);
        if (archivoSeleccionado != null) {
            urlField.setText(archivoSeleccionado.getAbsolutePath());
        }
    }

    @FXML
    public void publicarContenido() {
        String titulo = tituloField.getText();
        String descripcion = descripcionField.getText();
        String tipo = tipoChoiceBox.getValue();
        String url = urlField.getText();

        if (estudianteActual == null) {
            mostrarAlerta("Error", "No hay sesión activa.");
            return;
        }

        if (titulo.isEmpty() || descripcion.isEmpty() || tipo == null || url.isEmpty()) {
            mostrarAlerta("Campos incompletos", "Por favor, completa todos los campos.");
            return;
        }

        ContenidoEducativo contenido = new ContenidoEducativo(titulo, tipo, descripcion, url, estudianteActual.getUsuario());
        academix.agregarContenido(contenido);
        Persistencia.guardarRecursoBancoBinario(academix);
        buscarContenido();

        // Limpiar formulario y seleccionar primer tipo por defecto
        tituloField.clear();
        descripcionField.clear();
        tipoChoiceBox.getSelectionModel().selectFirst();
        urlField.clear();
        archivoSeleccionado = null;
    }

    public void buscarContenido() {
        String filtro = busquedaField != null ? busquedaField.getText().toLowerCase() : "";
        String criterioOrden = ordenChoiceBox != null ? ordenChoiceBox.getValue() : "Sin ordenar";

        ListaSimple<ContenidoEducativo> contenidos = academix.getContenidoEducativo();
        ListaSimple<ContenidoEducativo> filtrados = new ListaSimple<>();

        // Filtrado manual
        for (ContenidoEducativo c : contenidos) {
            if (c.getTitulo().toLowerCase().contains(filtro) ||
                    c.getAutor().toLowerCase().contains(filtro) ||
                    c.getTipo().toLowerCase().contains(filtro)) {
                filtrados.agregar(c);
            }
        }

        // Orden manual
        if ("Tipo".equals(criterioOrden)) {
            filtrados = ordenarPorTipo(filtrados);
        } else if ("Fecha".equals(criterioOrden)) {
            filtrados = ordenarPorFecha(filtrados);
        }

        contenedorPublicaciones.getChildren().clear();
        for (ContenidoEducativo contenido : filtrados) {
            agregarPublicacionVista(contenido);
        }
    }

    private ListaSimple<ContenidoEducativo> ordenarPorTipo(ListaSimple<ContenidoEducativo> lista) {
        ListaSimple<ContenidoEducativo> ordenada = new ListaSimple<>();

        for (int i = 0; i < lista.size(); i++) {
            ContenidoEducativo actual = lista.get(i);
            int j = 0;
            while (j < ordenada.size() && actual.getTipo().compareToIgnoreCase(ordenada.get(j).getTipo()) > 0) {
                j++;
            }
            insertarEnLista(ordenada, j, actual);
        }
        return ordenada;
    }

    private ListaSimple<ContenidoEducativo> ordenarPorFecha(ListaSimple<ContenidoEducativo> lista) {
        ListaSimple<ContenidoEducativo> ordenada = new ListaSimple<>();

        // Orden descendente: fecha más reciente primero
        for (int i = 0; i < lista.size(); i++) {
            ContenidoEducativo actual = lista.get(i);
            int j = 0;
            while (j < ordenada.size() && actual.getFechaPublicacion().isBefore(ordenada.get(j).getFechaPublicacion())) {
                j++;
            }
            insertarEnLista(ordenada, j, actual);
        }
        return ordenada;
    }

    private void insertarEnLista(ListaSimple<ContenidoEducativo> lista, int index, ContenidoEducativo dato) {
        lista.insertarEn(index, dato);
    }

    private void agregarPublicacionVista(ContenidoEducativo contenido) {
        VBox tarjeta = new VBox(5);
        tarjeta.setStyle("-fx-background-color: #ffffff; -fx-padding: 10; -fx-background-radius: 10; -fx-border-color: #cccccc;");

        Label titulo = new Label(contenido.getTitulo());
        titulo.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        Label descripcion = new Label(contenido.getDescripcion());
        Label autor = new Label("Publicado por: " + contenido.getAutor());

        // Mostrar promedio de valoraciones
        double promedio = contenido.getPromedioValoracion();
        Label promedioLabel = new Label("Valoración: " + (promedio > 0 ? String.format("%.1f", promedio) + " / 5 (" + contenido.getCantidadValoraciones() + ")" : "Sin valorar"));
        promedioLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #888;");

        tarjeta.getChildren().addAll(titulo, descripcion, promedioLabel);

        if ("Imagen".equalsIgnoreCase(contenido.getTipo()) &&
                contenido.getUrl().toLowerCase().matches(".*\\.(png|jpg|jpeg|gif|bmp)")) {
            try {
                Image imagen = new Image(new File(contenido.getUrl()).toURI().toString(), 300, 200, true, true);
                ImageView imageView = new ImageView(imagen);
                tarjeta.getChildren().add(imageView);
            } catch (Exception e) {
                tarjeta.getChildren().add(new Label("No se pudo cargar la imagen."));
            }
        }

        HBox botones = new HBox(10);
        botones.setAlignment(Pos.CENTER_LEFT);
        Button abrirBtn = new Button("Abrir");
        abrirBtn.setStyle("-fx-background-color: #007bff; -fx-text-fill: white;");
        abrirBtn.setOnAction(e -> abrirArchivo(contenido.getUrl()));
        botones.getChildren().add(abrirBtn);

        if (contenido.getAutor().equals(estudianteActual.getUsuario())) {
            Button eliminarBtn = new Button("Eliminar");
            eliminarBtn.setStyle("-fx-background-color: #007bff; -fx-text-fill: white;");
            eliminarBtn.setOnAction(e -> {
                contenedorPublicaciones.getChildren().remove(tarjeta);
                academix.eliminarContenido(contenido);
                Persistencia.guardarRecursoBancoBinario(academix);
                buscarContenido(); // actualizar vista tras eliminar
            });
            botones.getChildren().add(eliminarBtn);
        } else {
            // Control de valoración solo si NO es el autor
            int valoracionActual = contenido.getValoracionDe(estudianteActual.getUsuario());
            Label tuValoracion = new Label(valoracionActual > 0 ? "Tu valoración: " + valoracionActual : "Valorar:");
            ComboBox<Integer> valoracionCombo = new ComboBox<>();
            valoracionCombo.getItems().addAll(1, 2, 3, 4, 5);
            valoracionCombo.setValue(valoracionActual > 0 ? valoracionActual : 5);
            valoracionCombo.setDisable(valoracionActual > 0); // No permitir valorar dos veces

            Button valorarBtn = new Button("Enviar");
            valorarBtn.setDisable(valoracionActual > 0);
            valorarBtn.setOnAction(e -> {
                int val = valoracionCombo.getValue();
                contenido.valorar(estudianteActual.getUsuario(), val);
                Persistencia.guardarRecursoBancoBinario(academix);
                buscarContenido(); // refrescar vista
            });

            HBox valorarBox = new HBox(5, tuValoracion, valoracionCombo, valorarBtn);
            valorarBox.setAlignment(Pos.CENTER_LEFT);
            botones.getChildren().add(valorarBox);
        }

        tarjeta.getChildren().addAll(botones, autor);
        contenedorPublicaciones.getChildren().add(0, tarjeta);
    }

    private void abrirArchivo(String ruta) {
        try {
            File archivo = new File(ruta);
            if (archivo.exists()) {
                Desktop.getDesktop().open(archivo);
            } else {
                mostrarAlerta("Archivo no encontrado", "La ruta no es válida.");
            }
        } catch (IOException e) {
            mostrarAlerta("Error al abrir", "No se pudo abrir el archivo.");
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
