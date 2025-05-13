package uniquindio.com.academix.Model;

public class Contenido {
    private String titulo;
    private String autor;
    private String tipo;
    private String tema;

    public Contenido(String titulo, String autor, String tipo, String tema) {
        this.titulo = titulo;
        this.autor = autor;
        this.tipo = tipo;
        this.tema = tema;
    }

    public String getTitulo() { return titulo; }
    public String getAutor() { return autor; }
    public String getTipo() { return tipo; }
    public String getTema() { return tema; }
}
