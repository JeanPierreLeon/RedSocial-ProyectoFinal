package uniquindio.com.academix.Model;

public class ContenidoEducativo {
    private static int contadorId = 0;

    private final int id;
    private final String titulo;
    private final String tipo;
    private final String descripcion;
    private final String url;

    public ContenidoEducativo(String titulo, String tipo, String descripcion, String url) {
        this.id = ++contadorId;
        this.titulo = titulo;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.url = url;
    }

    public int getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getTipo() {
        return tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getUrl() {
        return url;
    }
}
