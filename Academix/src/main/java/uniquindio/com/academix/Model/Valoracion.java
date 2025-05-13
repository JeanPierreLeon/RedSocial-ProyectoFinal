package uniquindio.com.academix.Model;

public class Valoracion {
    private String estudiante;
    private String contenido;
    private int puntuacion;

    public Valoracion(String estudiante, String contenido, int puntuacion) {
        this.estudiante = estudiante;
        this.contenido = contenido;
        this.puntuacion = puntuacion;
    }

    public String getEstudiante() { return estudiante; }
    public String getContenido() { return contenido; }
    public int getPuntuacion() { return puntuacion; }
}

