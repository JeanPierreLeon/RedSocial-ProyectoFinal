package uniquindio.com.academix;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import uniquindio.com.academix.Model.Academix;
import uniquindio.com.academix.Model.Estudiante;
import uniquindio.com.academix.Model.ListaSimple;
import uniquindio.com.academix.Utils.Persistencia;

public class AcademixApplication extends Application {

    private static Stage primaryStage;
    private static ListaSimple<Estudiante> estudiantes = new ListaSimple<>();
    private static Estudiante estudianteActual; // <-- Sesión activa

    @Override
    public void start(Stage stage) throws IOException {
        // Inicializar usuarios por defecto si no existen
        inicializarUsuariosPorDefecto();
        
        primaryStage = stage;
        cambiarVista("login.fxml", "Login");
    }

    private void inicializarUsuariosPorDefecto() {
        Academix academix = Persistencia.cargarRecursoBancoBinario();
        if (academix == null) {
            academix = new Academix();
        }

        // Verificar si los usuarios por defecto ya existen
        boolean existeAna = false;
        boolean existeJean = false;

        for (Estudiante e : academix.getListaEstudiantes()) {
            if (e.getUsuario().equals("ana")) existeAna = true;
            if (e.getUsuario().equals("jean")) existeJean = true;
        }

        // Crear usuario Ana si no existe
        if (!existeAna) {
            Estudiante ana = new Estudiante("ana", "123");
            ana.setNombre("Ana García");
            ana.setUniversidad("Universidad del Quindío");
            ana.setUbicacion("Armenia");
            ana.agregarInteres("Matemáticas");
            ana.agregarInteres("Física");
            ana.setFotoPerfil("data/perfiles/ana_perfil.jpg");
            ana.setFotoPortada("data/perfiles/ana_portada.jpg");
            academix.agregarEstudiante(ana);
        }

        // Crear usuario Jean si no existe
        if (!existeJean) {
            Estudiante jean = new Estudiante("jean", "123");
            jean.setNombre("Jean");
            jean.setUniversidad("Universidad del Quindío");
            jean.setUbicacion("Armenia");
            jean.agregarInteres("Programación");
            jean.agregarInteres("Matemáticas");
            
            // Copiar las imágenes a la carpeta de perfiles
            try {
                File carpetaPerfiles = new File("data/perfiles");
                if (!carpetaPerfiles.exists()) carpetaPerfiles.mkdirs();

                // Asignar imagen de perfil por defecto si no hay una personalizada
                File imagenPorDefecto = new File("data/perfiles/img_1.png");
                if (!imagenPorDefecto.exists()) {
                    Files.copy(getClass().getResourceAsStream("/images/img_1.png"), imagenPorDefecto.toPath());
                }
                jean.setFotoPerfil("data/perfiles/img_1.png");

                // Copiar foto de portada si existe en resources, si no, omitir
                File portadaJean = new File("data/perfiles/jean_portada.jpg");
                if (!portadaJean.exists()) {
                    // Si no existe en resources, no hacer nada
                    if (getClass().getResourceAsStream("/images/jean_portada.jpg") != null) {
                        Files.copy(getClass().getResourceAsStream("/images/jean_portada.jpg"), portadaJean.toPath());
                        jean.setFotoPortada(portadaJean.getAbsolutePath());
                    }
                }
            } catch (Exception e) {
                System.out.println("Error copiando imágenes de Jean: " + e.getMessage());
            }
            
            academix.agregarEstudiante(jean);
        }

        // Si se creó algún usuario nuevo, guardar los cambios
        if (!existeAna || !existeJean) {
            Persistencia.guardarRecursoBancoBinario(academix);
        }
    }

    public static void cambiarVista(String fxml, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(AcademixApplication.class.getResource("/uniquindio/com/academix/" + fxml));
            Scene scene = new Scene(loader.load());
            primaryStage.setTitle(titulo);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ListaSimple<Estudiante> getEstudiantes() {
        return estudiantes;
    }

    public static Estudiante getEstudianteActual() {
        return estudianteActual;
    }

    public static void setEstudianteActual(Estudiante estudiante) {
        estudianteActual = estudiante;
    }

    public static void main(String[] args) {
        launch(args);
    }
}

