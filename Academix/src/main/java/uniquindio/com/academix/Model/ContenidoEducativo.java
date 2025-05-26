package uniquindio.com.academix.Model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.time.LocalDateTime;

public class ContenidoEducativo implements Serializable {

    private static final long serialVersionUID = -407735784476436657L; // Usa el valor que aparece en el error

    private static int contadorId = 0;

    private int id;
    private String titulo;
    private String tipo;
    private String descripcion;
    private String url;
    private String autor;
    private LocalDateTime fechaPublicacion;

    // Valoraciones: usuario -> puntuación (1-5)
    private ListaSimple<Valoracion> valoraciones = new ListaSimple<>();

    public ContenidoEducativo() {
        this.id = ++contadorId;
        this.fechaPublicacion = LocalDateTime.now();
        this.valoraciones = new ListaSimple<>();
    }

    public ContenidoEducativo(String titulo, String tipo, String descripcion, String url, String autor) {
        this.id = ++contadorId;
        this.titulo = titulo;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.url = url;
        this.autor = autor;
        this.fechaPublicacion = LocalDateTime.now();
        this.valoraciones = new ListaSimple<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
        contadorId = Math.max(contadorId, id);
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public LocalDateTime getFechaPublicacion() {
        return fechaPublicacion;
    }

    public void setFechaPublicacion(LocalDateTime fechaPublicacion) {
        this.fechaPublicacion = fechaPublicacion;
    }

    public void valorar(String usuario, int puntuacion) {
        if (usuario != null && puntuacion >= 1 && puntuacion <= 5) {
            for (int i = 0; i < valoraciones.size(); i++) {
                Valoracion v = valoraciones.get(i);
                if (v.getEstudiante().equals(usuario)) {
                    v.setPuntuacion(puntuacion);
                    return;
                }
            }
            valoraciones.agregar(new Valoracion(usuario, this.titulo, puntuacion));
        }
    }

    public double getPromedioValoracion() {
        if (valoraciones.size() == 0) return 0;
        int suma = 0;
        for (int i = 0; i < valoraciones.size(); i++) {
            suma += valoraciones.get(i).getPuntuacion();
        }
        return (double) suma / valoraciones.size();
    }

    public int getValoracionDe(String usuario) {
        for (int i = 0; i < valoraciones.size(); i++) {
            Valoracion v = valoraciones.get(i);
            if (v.getEstudiante().equals(usuario)) {
                return v.getPuntuacion();
            }
        }
        return 0;
    }

    public int getCantidadValoraciones() {
        return valoraciones.size();
    }

    // Añade este método para asegurar la inicialización tras deserializar
    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        if (valoraciones == null) {
            valoraciones = new ListaSimple<>();
        }
    }
}
