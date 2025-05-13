package uniquindio.com.academix.Model;


public class Estudiante {
    private String usuario;
    private String contrasena;

    public Estudiante(String usuario, String contrasena) {
        this.usuario = usuario;
        this.contrasena = contrasena;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getContrasena() {
        return contrasena;
    }
}

