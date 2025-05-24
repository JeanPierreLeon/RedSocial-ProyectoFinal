package uniquindio.com.academix.Model;

import uniquindio.com.academix.Estructuras.ListaSimple;
import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

public class Estudiante implements Serializable {

    private static final long serialVersionUID = 2474567390893604368L; // Cambia el valor para que coincida con el archivo persistente

    private String usuario;
    private String contrasena;

    // Lista para guardar mensajes recibidos
    private ListaSimple<Mensaje> mensajesRecibidos;

    // Lista de intereses del estudiante
    private List<String> intereses = new ArrayList<>();

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

    public List<String> getIntereses() {
        return intereses;
    }

    public void setIntereses(List<String> intereses) {
        this.intereses = intereses;
    }

    public void agregarInteres(String interes) {
        if (interes != null && !interes.trim().isEmpty() && !intereses.contains(interes.trim())) {
            intereses.add(interes.trim());
        }
    }

    public void recibirMensaje(Mensaje mensaje) {
        mensajesRecibidos.agregar(mensaje);
    }
}
