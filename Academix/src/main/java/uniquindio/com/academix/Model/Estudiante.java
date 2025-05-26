package uniquindio.com.academix.Model;

import uniquindio.com.academix.Model.ListaSimple;
import java.io.Serializable;

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
    private transient ListaSimple<Mensaje> mensajesRecibidos;

    // Lista de intereses del estudiante
    private ListaSimple<String> intereses = new ListaSimple<>();

    // Lista de amigos
    private ListaSimple<String> amigos = new ListaSimple<>();

    // Lista de publicaciones
    private ListaSimple<PublicacionItem> publicaciones = new ListaSimple<>();

    // Lista de valoraciones recibidas
    private ListaSimple<ValoracionItem> valoraciones = new ListaSimple<>();

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

    public ListaSimple<String> getIntereses() {
        if (intereses == null) {
            intereses = new ListaSimple<>();
        }
        return intereses;
    }

    public void setIntereses(ListaSimple<String> intereses) {
        this.intereses = intereses;
    }

    public ListaSimple<String> getAmigos() {
        return amigos;
    }

    public void agregarAmigo(Estudiante amigo) {
        if (amigo != null && !amigos.contiene(amigo.getUsuario())) {
            // Validar que no haya objetos Estudiante en la lista de amigos
            ListaSimple<String> amigosLimpios = new ListaSimple<>();
            for (String usuario : amigos) {
                if (usuario instanceof String) {
                    amigosLimpios.agregar(usuario);
                }
            }
            amigos = amigosLimpios;
            amigos.agregar(amigo.getUsuario());
        }
    }

    public ListaSimple<PublicacionItem> getPublicaciones() {
        if (publicaciones == null) {
            publicaciones = new ListaSimple<>();
        }
        return publicaciones;
    }

    public void agregarPublicacion(PublicacionItem publicacion) {
        if (publicacion != null) {
            publicaciones.agregarAlInicio(publicacion); // Agregar al inicio de la lista
        }
    }

    public ListaSimple<ValoracionItem> getValoraciones() {
        if (valoraciones == null) {
            valoraciones = new ListaSimple<>();
        }
        return valoraciones;
    }

    public void agregarValoracion(ValoracionItem valoracion) {
        if (valoracion != null) {
            valoraciones.agregar(valoracion);
        }
    }

    public void agregarInteres(String interes) {
        if (interes != null && !interes.trim().isEmpty() && !intereses.contiene(interes.trim())) {
            intereses.agregar(interes.trim());
        }
    }

    public void recibirMensaje(Mensaje mensaje) {
        mensajesRecibidos.agregar(mensaje);
    }

    public double getPromedioValoraciones() {
        if (valoraciones.estaVacia()) {
            return 0.0;
        }
        int total = 0;
        for (ValoracionItem valoracion : valoraciones) {
            total += valoracion.getEstrellas(); // Ahora estrellas es un int, no necesitamos length()
        }
        return (double) total / valoraciones.tamano();
    }
}
