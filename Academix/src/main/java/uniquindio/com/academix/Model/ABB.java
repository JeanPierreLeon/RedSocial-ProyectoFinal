package uniquindio.com.academix.Model;

public class ABB<T> {
    private NodoABB<T> raiz;
    private Comparador<T> comparador;

    public ABB(Comparador<T> comparador) {
        this.comparador = comparador;
    }

    public void insertar(T dato) {
        raiz = insertarRec(raiz, dato);
    }

    private NodoABB<T> insertarRec(NodoABB<T> nodo, T dato) {
        if (nodo == null) {
            return new NodoABB<>(dato);
        }
        int cmp = comparador.comparar(dato, nodo.dato);
        if (cmp < 0) {
            nodo.izq = insertarRec(nodo.izq, dato);
        } else {
            nodo.der = insertarRec(nodo.der, dato);
        }
        return nodo;
    }

    public ListaSimple<T> buscar(String filtro) {
        ListaSimple<T> resultado = new ListaSimple<>();
        buscarRec(raiz, filtro.toLowerCase(), resultado);
        return resultado;
    }

    private void buscarRec(NodoABB<T> nodo, String filtro, ListaSimple<T> resultado) {
        if (nodo == null) return;
        if (comparador.cumpleFiltro(nodo.dato, filtro)) {
            resultado.agregar(nodo.dato);
        }
        buscarRec(nodo.izq, filtro, resultado);
        buscarRec(nodo.der, filtro, resultado);
    }

    public ListaSimple<T> inOrden() {
        ListaSimple<T> lista = new ListaSimple<>();
        inOrdenRec(raiz, lista);
        return lista;
    }

    private void inOrdenRec(NodoABB<T> nodo, ListaSimple<T> lista) {
        if (nodo == null) return;
        inOrdenRec(nodo.izq, lista);
        lista.agregar(nodo.dato);
        inOrdenRec(nodo.der, lista);
    }

    public interface Comparador<T> {
        int comparar(T a, T b);
        boolean cumpleFiltro(T dato, String filtro);
    }

    private static class NodoABB<T> {
        T dato;
        NodoABB<T> izq, der;
        NodoABB(T dato) { this.dato = dato; }
    }
}

