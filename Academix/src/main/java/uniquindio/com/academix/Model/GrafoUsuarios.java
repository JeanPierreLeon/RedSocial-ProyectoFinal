package uniquindio.com.academix.Model;

import java.io.Serializable;
import java.util.*;

public class GrafoUsuarios implements Serializable {
    private Map<String, Set<String>> conexiones = new HashMap<>();

    public void conectar(String usuarioA, String usuarioB) {
        if (usuarioA.equals(usuarioB)) return;
        conexiones.computeIfAbsent(usuarioA, k -> new HashSet<>()).add(usuarioB);
        conexiones.computeIfAbsent(usuarioB, k -> new HashSet<>()).add(usuarioA);
    }

    public Set<String> getConexiones(String usuario) {
        return conexiones.getOrDefault(usuario, Collections.emptySet());
    }

    public Map<String, Set<String>> getTodasLasConexiones() {
        return conexiones;
    }
}
