package uniquindio.com.academix.Model;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Mensaje implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final String remitente;
    private final String destinatario;
    private final String contenido;
    private final LocalDateTime fecha;

    public Mensaje(String remitente, String destinatario, String contenido) {
        this.remitente    = remitente;
        this.destinatario = destinatario;
        this.contenido    = contenido;
        this.fecha        = LocalDateTime.now();
    }

    public String getRemitente()    { return remitente; }
    public String getDestinatario() { return destinatario; }
    public String getContenido()    { return contenido; }
    public LocalDateTime getFecha() { return fecha; }

    @Override
    public String toString() {
        String f = fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        return "[" + f + "] " + remitente + " → " + destinatario + ": " + contenido;
    }
    private boolean leido = false;

    public boolean isLeido() { return leido; }
    public void setLeido(boolean l) { leido = l; }

}
