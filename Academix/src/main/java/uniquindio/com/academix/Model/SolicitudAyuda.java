package uniquindio.com.academix.Model;

public class SolicitudAyuda {
    private String estudiante; // usuario del estudiante
    private String tema;
    private int urgencia; // 1: baja, 2: media, 3: alta

    public SolicitudAyuda(String estudiante, String tema, int urgencia) {
        this.estudiante = estudiante;
        this.tema = tema;
        this.urgencia = urgencia;
    }

    public String getEstudiante() { return estudiante; }
    public String getTema() { return tema; }
    public int getUrgencia() { return urgencia; }
}

