package uniquindio.com.academix.Model;

import uniquindio.com.academix.Model.ListaSimple;
import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

public class Estudiante implements Serializable {

    private static final long serialVersionUID = 2474567390893604368L; // Cambia el valor para que coincida con el archivo persistente

    private String usuario;
    private String contrasena;
    private String nombre;
    private String ubicacion;
    private String universidad;
    private String fotoPerfil;
    private String fotoPortada;

    // Lista para guardar mensajes recibidos
    private ListaSimple<Mensaje> mensajesRecibidos;

    // Lista de intereses del estudiante
    private List<String> intereses = new ArrayList<>();
    
    // Lista de amigos
    private List<Estudiante> amigos = new ArrayList<>();
    
    // Lista de publicaciones
    private List<PublicacionItem> publicaciones = new ArrayList<>();
    
    // Lista de valoraciones recibidas
    private List<ValoracionItem> valoraciones = new ArrayList<>();

    public Estudiante(String usuario, String contrasena) {
        this.usuario = usuario;
        this.contrasena = contrasena;
        this.mensajesRecibidos = new ListaSimple<>();
        this.nombre = usuario; // Por defecto, usar el usuario como nombre
        this.ubicacion = "Armenia";
        this.universidad = "Universidad del Quindío";
    }

    public String getUsuario() {
        return usuario;
    }

    public String getContrasena() {
        return contrasena;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getUniversidad() {
        return universidad;
    }

    public void setUniversidad(String universidad) {
        this.universidad = universidad;
    }

    public String getFotoPerfil() {
        return fotoPerfil;
    }

    public void setFotoPerfil(String fotoPerfil) {
        this.fotoPerfil = fotoPerfil;
    }

    public String getFotoPortada() {
        return fotoPortada;
    }

    public void setFotoPortada(String fotoPortada) {
        this.fotoPortada = fotoPortada;
    }

    public ListaSimple<Mensaje> getMensajesRecibidos() {
        return mensajesRecibidos;
    }

    public List<String> getIntereses() {
        if (intereses == null) {
            intereses = new ArrayList<>();
        }
        return intereses;
    }

    public void setIntereses(List<String> intereses) {
        this.intereses = intereses;
    }

    public List<Estudiante> getAmigos() {
        return amigos;
    }

    public void agregarAmigo(Estudiante amigo) {
        if (amigo != null && !amigos.contains(amigo)) {
            amigos.add(amigo);
        }
    }

    public List<PublicacionItem> getPublicaciones() {
        if (publicaciones == null) {
            publicaciones = new ArrayList<>();
        }
        return publicaciones;
    }

    public void agregarPublicacion(PublicacionItem publicacion) {
        if (publicacion != null) {
            publicaciones.add(0, publicacion); // Agregar al inicio de la lista
        }
    }

    public List<ValoracionItem> getValoraciones() {
        if (valoraciones == null) {
            valoraciones = new ArrayList<>();
        }
        return valoraciones;
    }

    public void agregarValoracion(ValoracionItem valoracion) {
        if (valoracion != null) {
            valoraciones.add(valoracion);
        }
    }

    public void agregarInteres(String interes) {
        if (interes != null && !interes.trim().isEmpty() && !intereses.contains(interes.trim())) {
            intereses.add(interes.trim());
        }
    }

    public void recibirMensaje(Mensaje mensaje) {
        mensajesRecibidos.agregar(mensaje);
    }

    public double getPromedioValoraciones() {
        if (valoraciones.isEmpty()) {
            return 0.0;
        }
        int total = 0;
        for (ValoracionItem valoracion : valoraciones) {
            total += valoracion.getEstrellas(); // Ahora estrellas es un int, no necesitamos length()
        }
        return (double) total / valoraciones.size();
    }
}
