package uniquindio.com.academix.Utils;

import uniquindio.com.academix.Model.Academix;
import uniquindio.com.academix.Model.Estudiante;
import uniquindio.com.academix.Model.PublicacionItem;
import java.io.File;

public class MigrarRutasAbsolutas {
    public static void main(String[] args) {
        String rutaBase = new File("").getAbsolutePath();
        rutaBase = rutaBase.replace("\\", "/"); // Normalizar para Windows
        if (!rutaBase.endsWith("/")) rutaBase += "/";

        Academix academix = Persistencia.cargarRecursoBancoBinario();
        boolean cambios = false;

        for (Estudiante est : academix.getListaEstudiantes()) {
            // Migrar foto de perfil
            if (est.getFotoPerfil() != null && est.getFotoPerfil().startsWith(rutaBase)) {
                String nuevaRuta = est.getFotoPerfil().replace(rutaBase, "");
                est.setFotoPerfil(nuevaRuta);
                cambios = true;
            }
            // Migrar foto de portada
            if (est.getFotoPortada() != null && est.getFotoPortada().startsWith(rutaBase)) {
                String nuevaRuta = est.getFotoPortada().replace(rutaBase, "");
                est.setFotoPortada(nuevaRuta);
                cambios = true;
            }
            // Migrar publicaciones
            if (est.getPublicaciones() != null) {
                for (PublicacionItem pub : est.getPublicaciones()) {
                    // Migrar imagen de perfil de la publicación
                    if (pub.getImagenPerfil() != null) {
                        String imgPerfil = pub.getImagenPerfil().replace("\\", "/");
                        if (imgPerfil.startsWith(rutaBase)) {
                            String nuevaRuta = imgPerfil.replace(rutaBase, "");
                            pub.setImagenPerfil(nuevaRuta);
                            cambios = true;
                        }
                    }
                    // Migrar imagen adjunta de la publicación
                    if (pub.getRutaImagen() != null) {
                        String rutaImg = pub.getRutaImagen().replace("\\", "/");
                        if (rutaImg.startsWith(rutaBase)) {
                            String nuevaRuta = rutaImg.replace(rutaBase, "");
                            pub.setRutaImagen(nuevaRuta);
                            cambios = true;
                        }
                    }
                }
            }
        }
        if (cambios) {
            Persistencia.guardarRecursoBancoBinario(academix);
            System.out.println("Migración completada. Todas las rutas absolutas fueron convertidas a relativas.");
        } else {
            System.out.println("No se encontraron rutas absolutas para migrar.");
        }
    }
}

