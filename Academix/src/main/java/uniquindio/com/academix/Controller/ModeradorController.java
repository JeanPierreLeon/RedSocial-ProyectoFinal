package uniquindio.com.academix.Controller;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import uniquindio.com.academix.Model.Estudiante;
import uniquindio.com.academix.Model.GrafoUsuarios;
import uniquindio.com.academix.Model.GrafoUsuarios.UsuarioConexiones;
import uniquindio.com.academix.Model.PublicacionItem;
import uniquindio.com.academix.View.GrafoAfinidadView;

public class ModeradorController {

    @FXML
    private TableView<UsuarioConexiones> tablaUsuarios;

    @FXML
    private TableColumn<UsuarioConexiones, String> columnaNombreUsuario;

    @FXML
    private TableColumn<UsuarioConexiones, String> columnaConexionesUsuario;

    @FXML
    private TableView<PublicacionItem> tablaContenidos;

    @FXML
    private TableColumn<PublicacionItem, String> columnaTituloPublicacion;

    @FXML
    private TableColumn<PublicacionItem, String> columnaAutorPublicacion;

    @FXML
    private TableColumn<PublicacionItem, String> columnaValoracionPublicacion;

    private GrafoUsuarios grafoUsuarios;
    private uniquindio.com.academix.Model.Academix academix;

    @FXML
    public void initialize() {
        columnaNombreUsuario.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().usuario));
        columnaConexionesUsuario.setCellValueFactory(data -> {
            String conexiones = String.join(", ", data.getValue().conexiones);
            return new SimpleStringProperty(conexiones);
        });

        columnaTituloPublicacion.setCellValueFactory(data -> {
            PublicacionItem pub = data.getValue();
            return new SimpleStringProperty(pub != null && pub.getContenido() != null ? pub.getContenido().replaceAll("\n.*", "") : "");
        });
        columnaAutorPublicacion.setCellValueFactory(data -> {
            PublicacionItem pub = data.getValue();
            return new SimpleStringProperty(pub != null && pub.getAutorNombre() != null ? pub.getAutorNombre() : "");
        });
        columnaValoracionPublicacion.setCellValueFactory(data -> {
            PublicacionItem pub = data.getValue();
            double promedio = pub != null ? pub.getPromedioValoraciones() : 0.0;
            return new SimpleStringProperty(String.format("%.2f", promedio));
        });

        if (grafoUsuarios == null || academix == null) {
            this.academix = uniquindio.com.academix.Utils.Persistencia.cargarRecursoBancoBinario();
            this.grafoUsuarios = academix.getGrafoUsuarios();
        }
        cargarUsuarios();
        cargarContenidos();
    }

    public void refrescarDatos() {
        this.academix = uniquindio.com.academix.Utils.Persistencia.cargarRecursoBancoBinario();
        this.grafoUsuarios = academix.getGrafoUsuarios();
        cargarUsuarios();
        cargarContenidos();
    }

    private void cargarUsuarios() {
        if (grafoUsuarios != null) {
            tablaUsuarios.getItems().clear();
            tablaUsuarios.getItems().addAll(grafoUsuarios.getTodasLasConexiones());
        }
    }

    private void cargarContenidos() {
        if (academix == null) return;
        tablaContenidos.getItems().clear();
        for (Estudiante estudiante : academix.getListaEstudiantes()) {
            if (estudiante.getPublicaciones() != null && !estudiante.getPublicaciones().estaVacia()) {
                for (PublicacionItem pub : estudiante.getPublicaciones()) {
                    if (pub.getValoraciones() != null && pub.getValoraciones().tamano() > 0) {
                        tablaContenidos.getItems().add(pub);
                    }
                }
            }
        }
    }

    @FXML
    public void mostrarTopContenidos() {
        if (academix == null) {
            mostrarAlerta("Sin datos", "No hay publicaciones cargadas para mostrar el top.");
            return;
        }
        java.util.List<PublicacionItem> publicaciones = new java.util.ArrayList<>();
        for (Estudiante e : academix.getListaEstudiantes()) {
            if (e.getPublicaciones() != null && !e.getPublicaciones().estaVacia()) {
                for (PublicacionItem pub : e.getPublicaciones()) {
                    if (pub.getValoraciones() != null && pub.getValoraciones().tamano() > 0) {
                        publicaciones.add(pub);
                    }
                }
            }
        }
        publicaciones.sort((a, b) -> Double.compare(b.getPromedioValoraciones(), a.getPromedioValoraciones()));
        StringBuilder sb = new StringBuilder();
        sb.append("TOP 5 Publicaciones Mejor Valoradas:\n\n");
        for (int i = 0; i < Math.min(5, publicaciones.size()); i++) {
            PublicacionItem p = publicaciones.get(i);
            sb.append((i+1) + ". " + p.getContenido().replaceAll("\n.*","") + " | Autor: " + p.getAutorNombre() + " | Promedio: " + p.getPromedioValoraciones() + " (" + (p.getValoraciones()!=null?p.getValoraciones().tamano():0) + " valoraciones)\n");
        }
        if (publicaciones.isEmpty()) {
            sb.append("No hay publicaciones valoradas.");
        }
        mostrarAlerta("Top Publicaciones", sb.toString());
    }

    @FXML
    public void mostrarTopEstudiantes() {
        if (academix == null) {
            mostrarAlerta("Sin datos", "No hay información de estudiantes para mostrar el top.");
            return;
        }
        // Reemplazo de java.util.List por ListaSimple
        uniquindio.com.academix.Model.ListaSimple<Estudiante> estudiantes = new uniquindio.com.academix.Model.ListaSimple<>();
        for (Estudiante e : academix.getListaEstudiantes()) {
            if (e.getValoraciones() != null && !e.getValoraciones().estaVacia()) {
                estudiantes.agregar(e);
            }
        }
        // Ordenar manualmente por cantidad de valoraciones (descendente)
        for (int i = 0; i < estudiantes.tamano() - 1; i++) {
            for (int j = 0; j < estudiantes.tamano() - i - 1; j++) {
                if (estudiantes.get(j).getValoraciones().tamano() < estudiantes.get(j+1).getValoraciones().tamano()) {
                    Estudiante temp = estudiantes.get(j);
                    estudiantes.eliminar(estudiantes.get(j));
                    estudiantes.insertarEn(j, estudiantes.get(j));
                    estudiantes.eliminar(estudiantes.get(j+1));
                    estudiantes.insertarEn(j+1, temp);
                }
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append("TOP 5 Estudiantes Más Valorados:\n\n");
        int count = 0;
        for (Estudiante e : estudiantes) {
            if (count >= 5) break;
            sb.append((count+1) + ". " + e.getNombre() + " (" + e.getUsuario() + ") | Valoraciones recibidas: " + e.getValoraciones().tamano() + "\n");
            count++;
        }
        if (count == 0) {
            sb.append("No hay estudiantes valorados.");
        }
        mostrarAlerta("Top Estudiantes", sb.toString());
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    @FXML private void verGrafo() {
        if (academix == null) {
            System.out.println("No se pudo cargar la instancia de Academix para el grafo visual.");
            return;
        }
        GrafoAfinidadView.mostrarGrafo(academix.getListaEstudiantes(), academix);
    }

    public void inicializarDatos(GrafoUsuarios grafoUsuarios) {
        this.grafoUsuarios = grafoUsuarios;
        this.academix = uniquindio.com.academix.Utils.Persistencia.cargarRecursoBancoBinario();
        cargarUsuarios();
        cargarContenidos();
    }

    @FXML
    private void descargarReporte() {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("=== REPORTE DE MODERADOR ===\n\n");
            sb.append("USUARIOS Y CONEXIONES:\n");
            for (int i = 0; i < tablaUsuarios.getItems().size(); i++) {
                UsuarioConexiones uc = tablaUsuarios.getItems().get(i);
                sb.append("Usuario: ").append(uc.usuario != null ? uc.usuario.toString() : "").append(" | Conectado con: ");
                if (uc.conexiones != null && uc.conexiones.length > 0) {
                    for (int j = 0; j < uc.conexiones.length; j++) {
                        Object val = uc.conexiones[j];
                        sb.append(val != null ? val.toString() : "");
                        if (j < uc.conexiones.length - 1) sb.append(", ");
                    }
                }
                sb.append("\n");
            }
            sb.append("\nCONTENIDOS VALORADOS:\n");
            for (int i = 0; i < tablaContenidos.getItems().size(); i++) {
                PublicacionItem pub = tablaContenidos.getItems().get(i);
                String titulo = pub.getContenido() != null ? pub.getContenido().replaceAll("\n.*", "") : "";
                String autor = pub.getAutorNombre() != null ? pub.getAutorNombre() : "";
                String promedio;
                double promedioValoracion = pub.getPromedioValoraciones();
                if (Double.isFinite(promedioValoracion)) {
                    promedio = String.format("%.2f", promedioValoracion);
                } else {
                    promedio = "0.00";
                }
                String cantidad = (pub.getValoraciones() != null && pub.getValoraciones().tamano() >= 0) ? String.valueOf(pub.getValoraciones().tamano()) : "0";
                sb.append("Título: ").append(titulo);
                sb.append(" | Autor: ").append(autor);
                sb.append(" | Valoración promedio: ").append(promedio);
                sb.append(" | Valoraciones: ").append(cantidad);
                sb.append("\n");
            }
            // Guardar archivo en la carpeta actual del usuario
            String nombreArchivo = "reporte_moderador_" + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".txt";
            File archivo = new File(System.getProperty("user.dir"), nombreArchivo);
            try (PrintWriter pw = new PrintWriter(new FileWriter(archivo))) {
                pw.print(sb.toString());
            }
            mostrarAlerta("Reporte generado", "El reporte se ha guardado como: " + archivo.getAbsolutePath());
            // Abrir el archivo automáticamente después de guardarlo
            try {
                java.awt.Desktop.getDesktop().open(archivo);
            } catch (Exception ex) {
                mostrarAlerta("Aviso", "No se pudo abrir el archivo automáticamente: " + ex.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo generar el reporte: " + e.getMessage());
        }
    }
}
