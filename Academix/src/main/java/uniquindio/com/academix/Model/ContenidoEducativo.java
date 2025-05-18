package uniquindio.com.academix.Model;



public class ContenidoEducativo {
    private static int contadorId = 0;

    private int id;
    private String titulo;
    private String tipo;
    private String descripcion;
    private String url;
    private double promedioValoracion;
    private int totalValoraciones;
    private int sumaValoraciones;

    public ContenidoEducativo(String titulo, String tipo, String descripcion, String url) {
        this.id = ++contadorId;
        this.titulo = titulo;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.url = url;
        this.promedioValoracion = 0.0;
        this.totalValoraciones = 0;
        this.sumaValoraciones = 0;
    }

    public void agregarValoracion(int valoracion) {
        totalValoraciones++;
        sumaValoraciones += valoracion;
        promedioValoracion = (double) sumaValoraciones / totalValoraciones;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getTipo() {
        return tipo;
    }

    public double getPromedioValoracion() {
        return promedioValoracion;
    }
}
