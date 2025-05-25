package uniquindio.com.academix.Model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PublicacionItem implements Serializable {
    private String contenido;
    private String autorId; // ID/correo del autor
    private String autorNombre; // Nombre del autor
    private LocalDateTime fechaPublicacion;
    private List<Valoracion> valoraciones;
    private String imagenPerfil; // Ruta de la imagen de perfil del autor
    private List<Comentario> comentarios; // Lista de comentarios
    private String rutaImagen; // Ruta de la imagen de la publicación

    public PublicacionItem(String contenido, String autorId, String autorNombre, String imagenPerfil) {
        this.contenido = contenido;
        this.autorId = autorId;
        this.autorNombre = autorNombre;
        this.imagenPerfil = imagenPerfil;
        this.fechaPublicacion = LocalDateTime.now();
        this.valoraciones = new ArrayList<>();
        this.comentarios = new ArrayList<>();
    }

    @Override
    public String toString() {
        return contenido + "\n" + autorNombre + " • " + fechaPublicacion.toString();
    }

    // Getters y setters
    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public String getAutorId() {
        return autorId;
    }

    public void setAutorId(String autorId) {
        this.autorId = autorId;
    }

    public String getAutorNombre() {
        return autorNombre;
    }

    public void setAutorNombre(String autorNombre) {
        this.autorNombre = autorNombre;
    }

    public LocalDateTime getFechaPublicacion() {
        return fechaPublicacion;
    }

    public void setFechaPublicacion(LocalDateTime fechaPublicacion) {
        this.fechaPublicacion = fechaPublicacion;
    }

    public String getImagenPerfil() {
        return imagenPerfil;
    }

    public void setImagenPerfil(String imagenPerfil) {
        this.imagenPerfil = imagenPerfil;
    }

    public String getRutaImagen() {
        return rutaImagen;
    }

    public void setRutaImagen(String rutaImagen) {
        this.rutaImagen = rutaImagen;
    }

    public void agregarValoracion(String autorId, String autorNombre, int puntuacion, String comentario) {
        Valoracion nuevaValoracion = new Valoracion(autorId, autorNombre, puntuacion, comentario);
        valoraciones.add(nuevaValoracion);
    }

    public int getPromedioValoraciones() {
        if (valoraciones.isEmpty()) {
            return 0;
        }
        return (int) valoraciones.stream()
                .mapToInt(Valoracion::getPuntuacion)
                .average()
                .orElse(0);
    }

    public List<Valoracion> getValoraciones() {
        return valoraciones;
    }

    public String getTiempoTranscurrido() {
        LocalDateTime ahora = LocalDateTime.now();
        long minutos = java.time.Duration.between(fechaPublicacion, ahora).toMinutes();
        
        if (minutos < 1) {
            return "Hace un momento";
        } else if (minutos < 60) {
            return "Hace " + minutos + " minutos";
        } else if (minutos < 1440) { // menos de 24 horas
            long horas = minutos / 60;
            return "Hace " + horas + " horas";
        } else {
            long dias = minutos / 1440;
            return "Hace " + dias + " días";
        }
    }

    public void agregarComentario(String autorId, String autorNombre, String contenido) {
        Comentario nuevoComentario = new Comentario(autorId, autorNombre, contenido);
        comentarios.add(nuevoComentario);
    }

    public List<Comentario> getComentarios() {
        return comentarios;
    }

    public static class Comentario implements Serializable {
        private String autorId;
        private String autorNombre;
        private String contenido;
        private LocalDateTime fecha;

        public Comentario(String autorId, String autorNombre, String contenido) {
            this.autorId = autorId;
            this.autorNombre = autorNombre;
            this.contenido = contenido;
            this.fecha = LocalDateTime.now();
        }

        public String getAutorId() {
            return autorId;
        }

        public String getAutorNombre() {
            return autorNombre;
        }

        public String getContenido() {
            return contenido;
        }

        public LocalDateTime getFecha() {
            return fecha;
        }

        public String getTiempoTranscurrido() {
            LocalDateTime ahora = LocalDateTime.now();
            long minutos = java.time.Duration.between(fecha, ahora).toMinutes();
            
            if (minutos < 1) {
                return "Hace un momento";
            } else if (minutos < 60) {
                return "Hace " + minutos + " minutos";
            } else if (minutos < 1440) { // menos de 24 horas
                long horas = minutos / 60;
                return "Hace " + horas + " horas";
            } else {
                long dias = minutos / 1440;
                return "Hace " + dias + " días";
            }
        }
    }

    public static class Valoracion implements Serializable {
        private String autorId;
        private String autorNombre;
        private int puntuacion;
        private String comentario;
        private LocalDateTime fecha;

        public Valoracion(String autorId, String autorNombre, int puntuacion, String comentario) {
            this.autorId = autorId;
            this.autorNombre = autorNombre;
            this.puntuacion = puntuacion;
            this.comentario = comentario;
            this.fecha = LocalDateTime.now();
        }

        public String getAutorId() {
            return autorId;
        }

        public String getAutorNombre() {
            return autorNombre;
        }

        public int getPuntuacion() {
            return puntuacion;
        }

        public String getComentario() {
            return comentario;
        }

        public LocalDateTime getFecha() {
            return fecha;
        }

        public String getTiempoTranscurrido() {
            LocalDateTime ahora = LocalDateTime.now();
            long minutos = java.time.Duration.between(fecha, ahora).toMinutes();
            
            if (minutos < 1) {
                return "Hace un momento";
            } else if (minutos < 60) {
                return "Hace " + minutos + " minutos";
            } else if (minutos < 1440) { // menos de 24 horas
                long horas = minutos / 60;
                return "Hace " + horas + " horas";
            } else {
                long dias = minutos / 1440;
                return "Hace " + dias + " días";
            }
        }
    }
}
