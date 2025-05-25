package uniquindio.com.academix.Model;

import java.io.Serializable;

public class MensajesPorUsuario implements Serializable {
    private String usuario;
    private uniquindio.com.academix.Model.ListaSimple<Mensaje> mensajes;

    public MensajesPorUsuario(String usuario) {
        this.usuario = usuario;
        this.mensajes = new uniquindio.com.academix.Model.ListaSimple<>();
    }

    public String getUsuario() { return usuario; }

    public uniquindio.com.academix.Model.ListaSimple<Mensaje> getMensajes() { return mensajes; }

    public void agregarMensaje(Mensaje mensaje) {
        mensajes.agregar(mensaje);
    }
}
