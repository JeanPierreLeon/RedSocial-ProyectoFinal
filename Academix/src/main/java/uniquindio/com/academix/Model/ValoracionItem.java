package uniquindio.com.academix.Model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class ValoracionItem implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String evaluador;
    private int estrellas;
    private String comentario;
    private LocalDateTime fecha;
    private String publicacion; // NUEVO: referencia a la publicación valorada

    public ValoracionItem(String evaluador, int estrellas, String comentario, String publicacion) {
        this.evaluador = evaluador;
        this.estrellas = estrellas;
        this.comentario = comentario;
        this.publicacion = publicacion;
        this.fecha = LocalDateTime.now();
    }

    // Constructor antiguo para compatibilidad
    public ValoracionItem(String evaluador, int estrellas, String comentario) {
        this(evaluador, estrellas, comentario, "");
    }

    public String getEvaluador() {
        return evaluador;
    }

    public void setEvaluador(String evaluador) {
        this.evaluador = evaluador;
    }

    public int getEstrellas() {
        return estrellas;
    }

    public void setEstrellas(int estrellas) {
        this.estrellas = estrellas;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getPublicacion() {
        return publicacion;
    }

    public void setPublicacion(String publicacion) {
        this.publicacion = publicacion;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < estrellas; i++) {
            sb.append("★");
        }
        for (int i = estrellas; i < 5; i++) {
            sb.append("☆");
        }
        sb.append("\nComentario: ").append(comentario);
        sb.append("\nPor: ").append(evaluador);
        if (publicacion != null && !publicacion.isEmpty()) {
            sb.append("\nSobre la publicación: \"").append(publicacion).append("\"");
        }
        return sb.toString();
    }
}
