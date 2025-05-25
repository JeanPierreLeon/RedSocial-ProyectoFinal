package uniquindio.com.academix.Model;

import uniquindio.com.academix.Model.ListaSimple;
import uniquindio.com.academix.HelloApplication;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serial;
import java.io.Serializable;

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
    }

    public void sincronizarEstudiantesConGlobal() {
        listaEstudiantes.limpiar();  // método que vacía la lista

        for (Estudiante est : HelloApplication.getEstudiantes()) {
            listaEstudiantes.agregar(est);
        }
    }

}
