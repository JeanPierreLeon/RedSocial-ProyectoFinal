package uniquindio.com.academix.Model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class SolicitudAmistad implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String remitente;
    private String destinatario;
    private LocalDateTime fecha;
    private EstadoSolicitud estado;
    
    public enum EstadoSolicitud {
        PENDIENTE,
        ACEPTADA,
        RECHAZADA
    }
    
    public SolicitudAmistad(String remitente, String destinatario) {
        this.remitente = remitente;
        this.destinatario = destinatario;
        this.fecha = LocalDateTime.now();
        this.estado = EstadoSolicitud.PENDIENTE;
    }
    
    public String getRemitente() {
        return remitente;
    }
    
    public String getDestinatario() {
        return destinatario;
    }
    
    public LocalDateTime getFecha() {
        return fecha;
    }
    
    public EstadoSolicitud getEstado() {
        return estado;
    }
    
    public void setEstado(EstadoSolicitud estado) {
        this.estado = estado;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SolicitudAmistad that = (SolicitudAmistad) o;
        return remitente.equals(that.remitente) && destinatario.equals(that.destinatario);
    }
    
    @Override
    public int hashCode() {
        return 31 * remitente.hashCode() + destinatario.hashCode();
    }
} 