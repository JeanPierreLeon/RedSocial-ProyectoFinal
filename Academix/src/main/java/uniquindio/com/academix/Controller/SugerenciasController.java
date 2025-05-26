package uniquindio.com.academix.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import uniquindio.com.academix.Model.*;
import uniquindio.com.academix.Utils.Persistencia;

import java.io.File;
import java.util.List;

public class SugerenciasController {

    @FXML
    private VBox contenedorSugerencias;

    private Estudiante estudianteActual;
    private Academix academix;
    private Image imagenPorDefecto;
    
    @FXML
    public void initialize() {
        academix = Persistencia.cargarRecursoBancoBinario();
        // Intentar cargar la imagen por defecto
        try {
            imagenPorDefecto = new Image(getClass().getResourceAsStream("/images/img_1.png"));
        } catch (Exception e) {
            System.err.println("No se pudo cargar la imagen por defecto: " + e.getMessage());
            // Crear una imagen en blanco como último recurso
            imagenPorDefecto = new Image(new File("src/main/resources/images/img_1.png").toURI().toString());
        }
    }
    
    public void setEstudianteActual(Estudiante estudiante) {
        this.estudianteActual = estudiante;
        cargarSugerencias();
    }
    
    private void cargarSugerencias() {
        contenedorSugerencias.getChildren().clear();
        ListaSimple<SugerenciasEstudio.SugerenciaCompañero> sugerencias =
            SugerenciasEstudio.obtenerSugerencias(estudianteActual, academix);
        for (SugerenciasEstudio.SugerenciaCompañero sugerencia : sugerencias) {
            VBox tarjetaSugerencia = crearTarjetaSugerencia(sugerencia);
            contenedorSugerencias.getChildren().add(tarjetaSugerencia);
        }
    }
    
    private VBox crearTarjetaSugerencia(SugerenciasEstudio.SugerenciaCompañero sugerencia) {
        VBox tarjeta = new VBox(10);
        tarjeta.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-border-color: #cccccc; -fx-border-radius: 5;");
        tarjeta.setPadding(new Insets(10));
        
        // Foto de perfil
        ImageView fotoPerfil = new ImageView();
        fotoPerfil.setFitHeight(100);
        fotoPerfil.setFitWidth(100);
        
        try {
            if (sugerencia.getEstudiante().getFotoPerfil() != null) {
                File archivoFoto = new File(sugerencia.getEstudiante().getFotoPerfil());
                if (archivoFoto.exists()) {
                    fotoPerfil.setImage(new Image(archivoFoto.toURI().toString()));
                } else {
                    fotoPerfil.setImage(imagenPorDefecto);
                }
            } else {
                fotoPerfil.setImage(imagenPorDefecto);
            }
        } catch (Exception e) {
            System.err.println("Error al cargar la foto de perfil: " + e.getMessage());
            fotoPerfil.setImage(imagenPorDefecto);
        }
        
        // Información del estudiante
        Label nombreLabel = new Label(sugerencia.getEstudiante().getNombre());
        nombreLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        Label universidadLabel = new Label(sugerencia.getEstudiante().getUniversidad());
        universidadLabel.setStyle("-fx-font-size: 14px;");
        
        // Afinidad
        Label afinidadLabel = new Label("Afinidad: " + sugerencia.getPuntajeAfinidad() + " puntos");
        afinidadLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #2196F3;");
        
        // Intereses en común
        VBox interesesBox = new VBox(5);
        Label interesesLabel = new Label("Intereses en común:");
        interesesLabel.setStyle("-fx-font-weight: bold;");
        interesesBox.getChildren().add(interesesLabel);
        
        for (String interes : sugerencia.getInteresesComunes()) {
            Label interesLabel = new Label("• " + interes);
            interesesBox.getChildren().add(interesLabel);
        }
        
        // Indicadores adicionales
        HBox indicadoresBox = new HBox(10);
        indicadoresBox.setAlignment(Pos.CENTER_LEFT);
        
        if (sugerencia.isMismaUniversidad()) {
            Label mismaUniLabel = new Label("Misma Universidad");
            mismaUniLabel.setStyle("-fx-background-color: #E3F2FD; -fx-padding: 5; -fx-background-radius: 3;");
            indicadoresBox.getChildren().add(mismaUniLabel);
        }
        
        if (sugerencia.isAmigoDeAmigo()) {
            Label amigoAmigoLabel = new Label("Amigo de Amigo");
            amigoAmigoLabel.setStyle("-fx-background-color: #E8F5E9; -fx-padding: 5; -fx-background-radius: 3;");
            indicadoresBox.getChildren().add(amigoAmigoLabel);
        }
        
        // Botón para agregar como amigo
        Button agregarButton = new Button("Agregar como Amigo");
        agregarButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        agregarButton.setOnAction(e -> agregarAmigo(sugerencia.getEstudiante()));
        
        // Agregar todos los elementos a la tarjeta
        tarjeta.getChildren().addAll(
            fotoPerfil,
            nombreLabel,
            universidadLabel,
            afinidadLabel,
            interesesBox,
            indicadoresBox,
            agregarButton
        );
        
        return tarjeta;
    }
    
    private void agregarAmigo(Estudiante nuevoAmigo) {
        estudianteActual.agregarAmigo(nuevoAmigo);
        nuevoAmigo.agregarAmigo(estudianteActual);
        
        // Actualizar el grafo de usuarios
        academix.getGrafoUsuarios().conectar(
            estudianteActual.getUsuario(),
            nuevoAmigo.getUsuario()
        );
        
        // Guardar cambios
        Persistencia.guardarRecursoBancoBinario(academix);
        
        // Recargar sugerencias
        cargarSugerencias();
    }
}
