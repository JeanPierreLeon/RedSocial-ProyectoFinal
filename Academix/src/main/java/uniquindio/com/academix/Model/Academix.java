package uniquindio.com.academix.Model;

import uniquindio.com.academix.Model.ListaSimple;
import uniquindio.com.academix.HelloApplication;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Academix implements Serializable {

    @Serial
    private static final long serialVersionUID = 2802726078832968396L;

    /* ─────────── Chat ─────────── */
    private ListaSimple<MensajesPorUsuario> mensajesPorUsuario = new ListaSimple<>();

    /** Guarda el mensaje tanto en el emisor como en el receptor. */
    public synchronized void agregarMensaje(Mensaje mensaje) {
        String emisor   = mensaje.getRemitente();
        String receptor = mensaje.getDestinatario();

        MensajesPorUsuario emisorMensajes = buscarMensajesPorUsuario(emisor);
        if (emisorMensajes == null) {
            emisorMensajes = new MensajesPorUsuario(emisor);
            mensajesPorUsuario.agregar(emisorMensajes);
        }
        emisorMensajes.agregarMensaje(mensaje);

        MensajesPorUsuario receptorMensajes = buscarMensajesPorUsuario(receptor);
        if (receptorMensajes == null) {
            receptorMensajes = new MensajesPorUsuario(receptor);
            mensajesPorUsuario.agregar(receptorMensajes);
        }
        receptorMensajes.agregarMensaje(mensaje);
    }

    /** Devuelve todos los mensajes recibidos y enviados por un usuario. */
    public ListaSimple<Mensaje> getMensajes(String usuario) {
        MensajesPorUsuario mpu = buscarMensajesPorUsuario(usuario);
        if (mpu != null) {
            return mpu.getMensajes();
        } else {
            return new ListaSimple<>(); // lista vacía si no existe usuario
        }
    }

    /** Devuelve solo los mensajes intercambiados entre ambos usuarios. */
    public ListaSimple<Mensaje> getConversacion(String userA, String userB) {
        ListaSimple<Mensaje> resultado = new ListaSimple<>();
        ListaSimple<Mensaje> mensajesUserA = getMensajes(userA);

        for (Mensaje m : mensajesUserA) {
            boolean entreAmbos =
                    (m.getRemitente().equals(userA) && m.getDestinatario().equals(userB)) ||
                            (m.getRemitente().equals(userB) && m.getDestinatario().equals(userA));
            if (entreAmbos) {
                resultado.agregar(m);
            }
        }
        return resultado;
    }

    private MensajesPorUsuario buscarMensajesPorUsuario(String usuario) {
        for (MensajesPorUsuario mpu : mensajesPorUsuario) {
            if (mpu.getUsuario().equalsIgnoreCase(usuario)) {
                return mpu;
            }
        }
        return null;
    }

    /* ─────────── Academia ─────────── */
    private ListaSimple<ContenidoEducativo> contenidoEducativo = new ListaSimple<>();
    private ListaSimple<Estudiante> listaEstudiantes           = new ListaSimple<>();

    public ListaSimple<ContenidoEducativo> getContenidoEducativo() {
        return contenidoEducativo;
    }
    public void setContenidoEducativo(ListaSimple<ContenidoEducativo> contenidoEducativo) {
        this.contenidoEducativo = contenidoEducativo;
    }

    public void agregarContenido(ContenidoEducativo contenido) {
        contenidoEducativo.agregar(contenido);
    }
    public void eliminarContenido(ContenidoEducativo contenido) {
        contenidoEducativo.eliminar(contenido);
    }

    public ListaSimple<Estudiante> getListaEstudiantes() {
        return listaEstudiantes;
    }
    public void agregarEstudiante(Estudiante estudiante) {
        listaEstudiantes.agregar(estudiante);
    }

    public Estudiante buscarEstudiante(String usuario) {
        for (Estudiante e : listaEstudiantes) {
            if (e.getUsuario().equalsIgnoreCase(usuario)) return e;
        }
        return null;
    }

    /* ─────────── Serialización ─────────── */
    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
        oos.writeObject(mensajesPorUsuario);
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();

        Object obj = ois.readObject();

        if (obj instanceof ListaSimple) {
            mensajesPorUsuario = (ListaSimple<MensajesPorUsuario>) obj;
        } else {
            throw new IOException("Tipo inesperado en mensajesPorUsuario: " + obj.getClass());
        }

        // Inicializar grafoUsuarios si es null (por compatibilidad con versiones anteriores)
        if (grafoUsuarios == null) {
            grafoUsuarios = new GrafoUsuarios();
        }

        // Inicializar solicitudesAmistad si es null
        if (solicitudesAmistad == null) {
            solicitudesAmistad = new ListaSimple<>();
        }
    }

    public void sincronizarEstudiantesConGlobal() {
        listaEstudiantes.limpiar();  // método que vacía la lista

        for (Estudiante est : HelloApplication.getEstudiantes()) {
            listaEstudiantes.agregar(est);
        }
    }

    // Grafo de conexiones entre usuarios
    private GrafoUsuarios grafoUsuarios = new GrafoUsuarios();

    public GrafoUsuarios getGrafoUsuarios() {
        return grafoUsuarios;
    }

    public void setGrafoUsuarios(GrafoUsuarios grafoUsuarios) {
        this.grafoUsuarios = grafoUsuarios;
    }

    private ListaSimple<SolicitudAmistad> solicitudesAmistad = new ListaSimple<>();

    public void enviarSolicitudAmistad(String remitente, String destinatario) {
        // Verificar que no exista una solicitud pendiente
        for (int i = 0; i < solicitudesAmistad.size(); i++) {
            SolicitudAmistad solicitud = solicitudesAmistad.get(i);
            if (solicitud.getRemitente().equals(remitente) && 
                solicitud.getDestinatario().equals(destinatario) &&
                solicitud.getEstado() == SolicitudAmistad.EstadoSolicitud.PENDIENTE) {
                return; // Ya existe una solicitud pendiente
            }
        }
        
        solicitudesAmistad.agregar(new SolicitudAmistad(remitente, destinatario));
    }

    public List<SolicitudAmistad> obtenerSolicitudesPendientes(String usuario) {
        List<SolicitudAmistad> pendientes = new ArrayList<>();
        
        // Verificar que la lista no sea null
        if (solicitudesAmistad == null) {
            solicitudesAmistad = new ListaSimple<>();
            return pendientes;
        }
        
        for (int i = 0; i < solicitudesAmistad.size(); i++) {
            SolicitudAmistad solicitud = solicitudesAmistad.get(i);
            if (solicitud.getDestinatario().equals(usuario) && 
                solicitud.getEstado() == SolicitudAmistad.EstadoSolicitud.PENDIENTE) {
                pendientes.add(solicitud);
            }
        }
        return pendientes;
    }

    public void aceptarSolicitudAmistad(String remitente, String destinatario) {
        for (int i = 0; i < solicitudesAmistad.size(); i++) {
            SolicitudAmistad solicitud = solicitudesAmistad.get(i);
            if (solicitud.getRemitente().equals(remitente) && 
                solicitud.getDestinatario().equals(destinatario) &&
                solicitud.getEstado() == SolicitudAmistad.EstadoSolicitud.PENDIENTE) {
                
                solicitud.setEstado(SolicitudAmistad.EstadoSolicitud.ACEPTADA);
                
                // Conectar usuarios en el grafo
                Estudiante estudianteRemitente = buscarEstudiante(remitente);
                Estudiante estudianteDestinatario = buscarEstudiante(destinatario);
                
                if (estudianteRemitente != null && estudianteDestinatario != null) {
                    estudianteRemitente.agregarAmigo(estudianteDestinatario);
                    estudianteDestinatario.agregarAmigo(estudianteRemitente);
                    grafoUsuarios.conectar(remitente, destinatario);
                }
                break;
            }
        }
    }

    public void rechazarSolicitudAmistad(String remitente, String destinatario) {
        for (int i = 0; i < solicitudesAmistad.size(); i++) {
            SolicitudAmistad solicitud = solicitudesAmistad.get(i);
            if (solicitud.getRemitente().equals(remitente) && 
                solicitud.getDestinatario().equals(destinatario) &&
                solicitud.getEstado() == SolicitudAmistad.EstadoSolicitud.PENDIENTE) {
                
                solicitud.setEstado(SolicitudAmistad.EstadoSolicitud.RECHAZADA);
                break;
            }
        }
    }

}
