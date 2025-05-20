package uniquindio.com.academix.Model;

import uniquindio.com.academix.Estructuras.ListaSimple;

import java.io.Serializable;

public class Academix implements Serializable {

    private ListaSimple<ContenidoEducativo> contenidoEducativo = new ListaSimple<>();

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
}
