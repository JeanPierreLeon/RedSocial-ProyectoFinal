package uniquindio.com.academix.Model;

import java.io.Serializable;

public class ContenidoEducativo implements Serializable {

    private static int contadorId = 0;

    private int id;
    private String titulo;
    private String tipo;
    private String descripcion;
    private String url;
    private String autor;

    public ContenidoEducativo() {
        this.id = ++contadorId;
    }

    public ContenidoEducativo(String titulo, String tipo, String descripcion, String url, String autor) {
        this.id = ++contadorId;
        this.titulo = titulo;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.url = url;
        this.autor = autor;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
        contadorId = Math.max(contadorId, id); // importante para mantener IDs únicos
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
}
