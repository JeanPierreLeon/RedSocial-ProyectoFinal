package uniquindio.com.academix.Model;

import java.io.Serializable;

public class MensajesPorUsuario implements Serializable {
    private String usuario;
    // Hacer la lista de mensajes serializable para persistir mensajes
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

    // Serialización personalizada para guardar y restaurar mensajes
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException {
        oos.defaultWriteObject();
    }

    private void readObject(java.io.ObjectInputStream ois) throws java.io.IOException, ClassNotFoundException {
        ois.defaultReadObject();
        if (mensajes == null) {
            mensajes = new uniquindio.com.academix.Model.ListaSimple<>();
        }
    }
}
