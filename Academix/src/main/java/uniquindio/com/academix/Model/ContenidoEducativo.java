package uniquindio.com.academix.Model;

import java.io.Serializable;

public class ContenidoEducativo implements Serializable {
    private static int contadorId = 0;

    private int id;
    private String titulo;
    private String tipo;
    private String descripcion;
    private String url;

    // Constructor vacío (requerido por XMLEncoder)
    public ContenidoEducativo() {
        this.id = ++contadorId;
    }

    // Constructor adicional para crear contenidos desde la aplicación
    public ContenidoEducativo(String titulo, String tipo, String descripcion, String url) {
        this.id = ++contadorId;
        this.titulo = titulo;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.url = url;
    }

    // Getters y setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
}
