package uniquindio.com.academix.Model;

import java.io.Serializable;

public class MensajesPorUsuario implements Serializable {
    private String usuario;
    private uniquindio.com.academix.Estructuras.ListaSimple<Mensaje> mensajes;

    public MensajesPorUsuario(String usuario) {
        this.usuario = usuario;
        this.mensajes = new uniquindio.com.academix.Estructuras.ListaSimple<>();
    }

    public String getUsuario() { return usuario; }

    public uniquindio.com.academix.Estructuras.ListaSimple<Mensaje> getMensajes() { return mensajes; }

    public void agregarMensaje(Mensaje mensaje) {
        mensajes.agregar(mensaje);
    }
}
