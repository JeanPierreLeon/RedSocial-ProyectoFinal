package uniquindio.com.academix.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import uniquindio.com.academix.Model.ContenidoEducativo;


public class InicioController {

    @FXML private TextField tituloField;
    @FXML private TextField descripcionField;
    @FXML private TextField urlField;
    @FXML private ChoiceBox<String> tipoChoiceBox;
    @FXML private TableView<ContenidoEducativo> tablaContenidos;
    @FXML private TableColumn<ContenidoEducativo, String> colTitulo;
    @FXML private TableColumn<ContenidoEducativo, String> colTipo;
    @FXML private TableColumn<ContenidoEducativo, String> colValoracion;
    @FXML private TextField valoracionField;

    private final ObservableList<ContenidoEducativo> listaContenidos = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        tipoChoiceBox.setValue("Archivo");

        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colValoracion.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        String.format("%.2f", cellData.getValue().getPromedioValoracion())
                )
        );

        tablaContenidos.setItems(listaContenidos);
    }

    @FXML
    private void publicarContenido() {
        String titulo = tituloField.getText();
        String tipo = tipoChoiceBox.getValue();
        String descripcion = descripcionField.getText();
        String url = urlField.getText();

        if (titulo.isEmpty() || descripcion.isEmpty() || url.isEmpty()) {
            mostrarAlerta("Por favor completa todos los campos.");
            return;
        }

        ContenidoEducativo contenido = new ContenidoEducativo(titulo, tipo, descripcion, url);
        listaContenidos.add(contenido);

        tituloField.clear();
        descripcionField.clear();
        urlField.clear();
    }

    @FXML
    private void valorarSeleccionado() {
        ContenidoEducativo seleccionado = tablaContenidos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Selecciona un contenido para valorar.");
            return;
        }

        try {
            int valor = Integer.parseInt(valoracionField.getText());
            if (valor < 1 || valor > 5) {
                mostrarAlerta("La valoración debe estar entre 1 y 5.");
                return;
            }

            seleccionado.agregarValoracion(valor);
            tablaContenidos.refresh();
            valoracionField.clear();
        } catch (NumberFormatException e) {
            mostrarAlerta("Introduce un número válido.");
        }
    }

    private void mostrarAlerta(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.WARNING);
        alerta.setTitle("Advertencia");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
