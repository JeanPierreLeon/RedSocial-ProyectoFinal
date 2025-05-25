package uniquindio.com.academix.Model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class ValoracionItem implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String evaluador;
    private int estrellas;
    private String comentario;
    private LocalDateTime fecha;

    public ValoracionItem(String evaluador, int estrellas, String comentario) {
        this.evaluador = evaluador;
        this.estrellas = estrellas;
        this.comentario = comentario;
        this.fecha = LocalDateTime.now();
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
        return sb.toString() + "\n" + comentario + "\nPor: " + evaluador;
    }

    public String getEvaluador() {
        return evaluador;
    }

    public int getEstrellas() {
        return estrellas;
    }

    public void setEstrellas(int estrellas) {
        if (estrellas >= 1 && estrellas <= 5) {
            this.estrellas = estrellas;
        }
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
} 