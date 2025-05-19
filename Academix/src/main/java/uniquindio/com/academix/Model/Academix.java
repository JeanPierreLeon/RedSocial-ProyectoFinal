package uniquindio.com.academix.Model;

import java.io.Serializable;
import java.util.ArrayList;

public class Academix implements Serializable {

    private ArrayList<ContenidoEducativo> contenidoEducativo = new ArrayList<>();

    public ArrayList<ContenidoEducativo> getContenidoEducativo() {
        return contenidoEducativo;
    }

    public void setContenidoEducativo(ArrayList<ContenidoEducativo> contenidoEducativo) {
        this.contenidoEducativo = contenidoEducativo;
    }

    public void agregarContenido(ContenidoEducativo contenido) {
        contenidoEducativo.add(contenido);
    }
}
