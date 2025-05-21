package uniquindio.com.academix.Model;

import uniquindio.com.academix.Estructuras.ListaSimple;
import java.io.Serializable;

public class Estudiante implements Serializable {
    private String usuario;
    private String contrasena;

    // Lista para guardar mensajes recibidos
    private ListaSimple<Mensaje> mensajesRecibidos;

    public Estudiante(String usuario, String contrasena) {
        this.usuario = usuario;
        this.contrasena = contrasena;
        this.mensajesRecibidos = new ListaSimple<>();
    }

    public String getUsuario() {
        return usuario;
    }

    public String getContrasena() {
        return contrasena;
    }

    public ListaSimple<Mensaje> getMensajesRecibidos() {
        return mensajesRecibidos;
    }

    public void recibirMensaje(Mensaje mensaje) {
        mensajesRecibidos.agregar(mensaje);
    }
}
