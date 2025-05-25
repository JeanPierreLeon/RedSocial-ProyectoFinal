package uniquindio.com.academix.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class ColaPrioridadSolicitudes {
    private PriorityQueue<SolicitudAyuda> cola;

    public ColaPrioridadSolicitudes() {
        cola = new PriorityQueue<>();
    }

    public void agregarSolicitud(SolicitudAyuda solicitud) {
        cola.offer(solicitud);
    }

    public SolicitudAyuda atenderSolicitud() {
        return cola.poll();
    }

    public List<SolicitudAyuda> listarSolicitudes() {
        return new ArrayList<>(cola); // Para mostrar sin alterar la cola
    }

    public boolean estaVacia() {
        return cola.isEmpty();
    }
}
