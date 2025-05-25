package uniquindio.com.academix.Model;

import java.io.Serializable;

public class ColaPrioridadSolicitudes implements Serializable {

    private Nodo cabeza;
    private int tamaño;

    private static class Nodo implements Serializable {
        SolicitudAyuda dato;
        Nodo siguiente;

        Nodo(SolicitudAyuda dato) {
            this.dato = dato;
        }
    }

    public ColaPrioridadSolicitudes() {
        cabeza = null;
        tamaño = 0;
    }

    // Inserta la solicitud en la posición correcta según urgencia (mayor urgencia, más adelante)
    public void agregarSolicitud(SolicitudAyuda solicitud) {
        Nodo nuevo = new Nodo(solicitud);
        if (cabeza == null || solicitud.getUrgencia() > cabeza.dato.getUrgencia()) {
            nuevo.siguiente = cabeza;
            cabeza = nuevo;
        } else {
            Nodo actual = cabeza;
            while (actual.siguiente != null && actual.siguiente.dato.getUrgencia() >= solicitud.getUrgencia()) {
                actual = actual.siguiente;
            }
            nuevo.siguiente = actual.siguiente;
            actual.siguiente = nuevo;
        }
        tamaño++;
    }

    // Atiende la solicitud de mayor prioridad (al frente)
    public SolicitudAyuda atenderSolicitud() {
        if (cabeza == null) return null;
        SolicitudAyuda dato = cabeza.dato;
        cabeza = cabeza.siguiente;
        tamaño--;
        return dato;
    }

    // Devuelve una ListaSimple con todas las solicitudes (sin alterar la cola)
    public ListaSimple<SolicitudAyuda> listarSolicitudes() {
        ListaSimple<SolicitudAyuda> lista = new ListaSimple<>();
        Nodo actual = cabeza;
        while (actual != null) {
            lista.agregar(actual.dato);
            actual = actual.siguiente;
        }
        return lista;
    }

    public boolean estaVacia() {
        return cabeza == null;
    }

    public int size() {
        return tamaño;
    }
}
