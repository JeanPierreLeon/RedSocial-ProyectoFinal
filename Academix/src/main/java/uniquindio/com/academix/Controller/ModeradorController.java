package uniquindio.com.academix.Controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import uniquindio.com.academix.Model.ContenidoEducativo;
import uniquindio.com.academix.Model.GrafoUsuarios;
import uniquindio.com.academix.Model.GrafoUsuarios.UsuarioConexiones;
import uniquindio.com.academix.Model.ListaSimple;

public class ModeradorController {

    @FXML
    private TableView<UsuarioConexiones> tablaUsuarios;

    @FXML
    private TableColumn<UsuarioConexiones, String> columnaNombreUsuario;

    @FXML
    private TableColumn<UsuarioConexiones, String> columnaConexionesUsuario;

    @FXML
    private TableView<ContenidoEducativo> tablaContenidos;

    @FXML
    private TableColumn<ContenidoEducativo, String> columnaTituloContenido;

    @FXML
    private TableColumn<ContenidoEducativo, String> columnaAutorContenido;

    @FXML
    private TableColumn<ContenidoEducativo, String> columnaValoracionContenido;

    private GrafoUsuarios grafoUsuarios;
    private ListaSimple<ContenidoEducativo> listaContenidos;

    public void inicializarDatos(GrafoUsuarios grafoUsuarios, ListaSimple<ContenidoEducativo> listaContenidos) {
        this.grafoUsuarios = grafoUsuarios;
        this.listaContenidos = listaContenidos;

        cargarUsuarios();
        cargarContenidos();
    }

    @FXML
    public void initialize() {
        columnaNombreUsuario.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().usuario));
        columnaConexionesUsuario.setCellValueFactory(data -> {
            String conexiones = String.join(", ", data.getValue().conexiones);
            return new SimpleStringProperty(conexiones);
        });

        columnaTituloContenido.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        columnaAutorContenido.setCellValueFactory(new PropertyValueFactory<>("autor"));
        columnaValoracionContenido.setCellValueFactory(data ->
            new SimpleStringProperty(String.format("%.2f", data.getValue().getPromedioValoracion())));
    }

    private void cargarUsuarios() {
        if (grafoUsuarios != null) {
            tablaUsuarios.getItems().clear();
            tablaUsuarios.getItems().addAll(grafoUsuarios.getTodasLasConexiones());
        }
    }

    private void cargarContenidos() {
        if (listaContenidos != null) {
            tablaContenidos.getItems().clear();
            for (ContenidoEducativo contenido : listaContenidos) {
                tablaContenidos.getItems().add(contenido);
            }
        }
    }

   @FXML
private void mostrarTopContenidos() {
    if (listaContenidos == null || listaContenidos.estaVacia()) {
        System.out.println("No hay contenidos cargados.");
        return;
    }

    ContenidoEducativo[] top = new ContenidoEducativo[Math.min(5, listaContenidos.size())];
    boolean[] usados = new boolean[listaContenidos.size()];

    for (int i = 0; i < top.length; i++) {
        double mejorValoracion = -1;
        int mejorIndice = -1;

        for (int j = 0; j < listaContenidos.size(); j++) {
            if (!usados[j]) {
                double valoracion = listaContenidos.get(j).getPromedioValoracion();
                if (valoracion > mejorValoracion) {
                    mejorValoracion = valoracion;
                    mejorIndice = j;
                }
            }
        }

        if (mejorIndice != -1) {
            top[i] = listaContenidos.get(mejorIndice);
            usados[mejorIndice] = true;
        }
    }

    tablaContenidos.getItems().clear();
    for (ContenidoEducativo c : top) {
        if (c != null) tablaContenidos.getItems().add(c);
    }

    System.out.println("Top contenidos mostrado.");
}


    @FXML
    private void mostrarTopEstudiantes() {
        if (grafoUsuarios == null) return;

        UsuarioConexiones[] todos = grafoUsuarios.getTodasLasConexiones();
        UsuarioConexiones[] top = new UsuarioConexiones[Math.min(5, todos.length)];
        int[] usados = new int[todos.length];

        for (int i = 0; i < top.length; i++) {
            int maxConexiones = -1;
            int mejorIndice = -1;

            for (int j = 0; j < todos.length; j++) {
                if (usados[j] == 1) continue;
                if (todos[j].conexiones.length > maxConexiones) {
                    maxConexiones = todos[j].conexiones.length;
                    mejorIndice = j;
                }
            }

            if (mejorIndice != -1) {
                top[i] = todos[mejorIndice];
                usados[mejorIndice] = 1;
            }
        }

        tablaUsuarios.getItems().clear();
        for (UsuarioConexiones uc : top) {
            if (uc != null) tablaUsuarios.getItems().add(uc);
        }
    }

    @FXML private void verGrafo() {
        System.out.println("📊 Mostrar grafo de afinidad (pendiente implementación visual)");
    }
}
