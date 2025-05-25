package uniquindio.com.academix.Model;

public class SolicitudAyuda implements Comparable<SolicitudAyuda> {
    private String estudiante;
    private String tema;
    private int urgencia;

    public SolicitudAyuda(String estudiante, String tema, int urgencia) {
        this.estudiante = estudiante;
        this.tema = tema;
        this.urgencia = urgencia;
    }

    public String getEstudiante() { return estudiante; }
    public String getTema() { return tema; }
    public int getUrgencia() { return urgencia; }

    @Override
    public int compareTo(SolicitudAyuda otra) {
        return Integer.compare(otra.urgencia, this.urgencia); // Prioridad mayor va primero
    }

    @Override
    public String toString() {
        return "[" + urgencia + "] " + estudiante + ": " + tema;
    }
}
