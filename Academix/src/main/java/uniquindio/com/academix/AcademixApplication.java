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

    /**
     * Inicializa los usuarios por defecto y asegura que las imágenes necesarias estén en la carpeta data/perfiles.
     * Si el proyecto se comparte, asegúrate de incluir la carpeta resources con las imágenes necesarias.
     */
    private void inicializarUsuariosPorDefecto() {
        Academix academix = Persistencia.cargarRecursoBancoBinario();
        if (academix == null) {
            academix = new Academix();
        }

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

            // Asegurar carpeta y foto de perfil/portada
            File carpetaPerfiles = new File("data/perfiles");
            if (!carpetaPerfiles.exists()) carpetaPerfiles.mkdirs();

            File perfilAna = new File("data/perfiles/ana_perfil.jpg");
            if (!perfilAna.exists() && getClass().getResourceAsStream("/images/ana_perfil.jpg") != null) {
                try {
                    Files.copy(getClass().getResourceAsStream("/images/ana_perfil.jpg"), perfilAna.toPath());
                } catch (Exception ex) {
                    System.out.println("No se pudo copiar ana_perfil.jpg: " + ex.getMessage());
                }
            }
            ana.setFotoPerfil(perfilAna.exists() ? perfilAna.getPath() : "data/perfiles/img_1.png");

            File portadaAna = new File("data/perfiles/ana_portada.jpg");
            if (!portadaAna.exists() && getClass().getResourceAsStream("/images/ana_portada.jpg") != null) {
                try {
                    Files.copy(getClass().getResourceAsStream("/images/ana_portada.jpg"), portadaAna.toPath());
                } catch (Exception ex) {
                    System.out.println("No se pudo copiar ana_portada.jpg: " + ex.getMessage());
                }
            }
            ana.setFotoPortada(portadaAna.exists() ? portadaAna.getPath() : null);

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

            // Asegurar carpeta y foto de perfil/portada
            File carpetaPerfiles = new File("data/perfiles");
            if (!carpetaPerfiles.exists()) carpetaPerfiles.mkdirs();

            // Imagen de perfil por defecto
            File imagenPorDefecto = new File("data/perfiles/img_1.png");
            if (!imagenPorDefecto.exists() && getClass().getResourceAsStream("/images/img_1.png") != null) {
                try {
                    Files.copy(getClass().getResourceAsStream("/images/img_1.png"), imagenPorDefecto.toPath());
                } catch (Exception ex) {
                    System.out.println("No se pudo copiar img_1.png: " + ex.getMessage());
                }
            }
            jean.setFotoPerfil(imagenPorDefecto.getPath());

            // Copiar foto de portada si existe en resources
            File portadaJean = new File("data/perfiles/jean_portada.jpg");
            if (!portadaJean.exists() && getClass().getResourceAsStream("/images/jean_portada.jpg") != null) {
                try {
                    Files.copy(getClass().getResourceAsStream("/images/jean_portada.jpg"), portadaJean.toPath());
                } catch (Exception ex) {
                    System.out.println("No se pudo copiar jean_portada.jpg: " + ex.getMessage());
                }
            }
            jean.setFotoPortada(portadaJean.getPath());
            academix.agregarEstudiante(jean);
        }

        // Guardar cambios si se creó algún usuario nuevo
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
