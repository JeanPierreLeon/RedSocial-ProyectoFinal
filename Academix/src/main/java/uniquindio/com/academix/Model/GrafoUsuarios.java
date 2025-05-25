package uniquindio.com.academix.Model;

import java.io.Serializable;

public class GrafoUsuarios implements Serializable {

    private static final int MAX_USUARIOS = 100;

    private String[] usuarios = new String[MAX_USUARIOS];
    private String[][] conexiones = new String[MAX_USUARIOS][MAX_USUARIOS];
    private int totalUsuarios = 0;

    public void conectar(String usuarioA, String usuarioB) {
        if (usuarioA.equals(usuarioB)) return;

        int indexA = obtenerIndice(usuarioA);
        int indexB = obtenerIndice(usuarioB);

        if (indexA == -1) {
            indexA = agregarUsuario(usuarioA);
        }

        if (indexB == -1) {
            indexB = agregarUsuario(usuarioB);
        }

        agregarConexion(indexA, usuarioB);
        agregarConexion(indexB, usuarioA);
    }

    public String[] getConexiones(String usuario) {
        int index = obtenerIndice(usuario);
        if (index == -1) return new String[0];

        // Contar cuántas conexiones hay
        int count = 0;
        while (count < MAX_USUARIOS && conexiones[index][count] != null) {
            count++;
        }

        String[] resultado = new String[count];
        for (int i = 0; i < count; i++) {
            resultado[i] = conexiones[index][i];
        }
        return resultado;
    }

    public UsuarioConexiones[] getTodasLasConexiones() {
        UsuarioConexiones[] resultado = new UsuarioConexiones[totalUsuarios];
        for (int i = 0; i < totalUsuarios; i++) {
            resultado[i] = new UsuarioConexiones(usuarios[i], getConexiones(usuarios[i]));
        }
        return resultado;
    }

    public void desconectar(String usuarioA, String usuarioB) {
        if (usuarioA.equals(usuarioB)) return;

        int indexA = obtenerIndice(usuarioA);
        int indexB = obtenerIndice(usuarioB);

        if (indexA != -1 && indexB != -1) {
            eliminarConexion(indexA, usuarioB);
            eliminarConexion(indexB, usuarioA);
        }
    }

    // Utilidades internas

    private int obtenerIndice(String usuario) {
        for (int i = 0; i < totalUsuarios; i++) {
            if (usuarios[i].equals(usuario)) {
                return i;
            }
        }
        return -1;
    }

    private int agregarUsuario(String usuario) {
        if (totalUsuarios >= MAX_USUARIOS) return -1;
        usuarios[totalUsuarios] = usuario;
        return totalUsuarios++;
    }

    private void agregarConexion(int index, String otroUsuario) {
        for (int i = 0; i < MAX_USUARIOS; i++) {
            if (conexiones[index][i] == null) {
                conexiones[index][i] = otroUsuario;
                return;
            }
            if (conexiones[index][i].equals(otroUsuario)) {
                return; // Ya está conectado
            }
        }
    }

    private void eliminarConexion(int index, String otroUsuario) {
        for (int i = 0; i < MAX_USUARIOS && conexiones[index][i] != null; i++) {
            if (conexiones[index][i].equals(otroUsuario)) {
                // Mover todas las conexiones siguientes una posición hacia atrás
                while (i < MAX_USUARIOS - 1 && conexiones[index][i + 1] != null) {
                    conexiones[index][i] = conexiones[index][i + 1];
                    i++;
                }
                conexiones[index][i] = null;
                return;
            }
        }
    }

    // Clase auxiliar para devolver todas las conexiones
    public static class UsuarioConexiones {
        public String usuario;
        public String[] conexiones;

        public UsuarioConexiones(String usuario, String[] conexiones) {
            this.usuario = usuario;
            this.conexiones = conexiones;
        }
    }
}
