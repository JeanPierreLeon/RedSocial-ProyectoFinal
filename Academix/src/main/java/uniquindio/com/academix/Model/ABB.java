package uniquindio.com.academix.Model;

import java.util.ArrayList;
import java.util.List;

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

    public List<T> buscar(String filtro) {
        List<T> resultado = new ArrayList<>();
        buscarRec(raiz, filtro.toLowerCase(), resultado);
        return resultado;
    }

    private void buscarRec(NodoABB<T> nodo, String filtro, List<T> resultado) {
        if (nodo == null) return;
        if (comparador.cumpleFiltro(nodo.dato, filtro)) {
            resultado.add(nodo.dato);
        }
        buscarRec(nodo.izq, filtro, resultado);
        buscarRec(nodo.der, filtro, resultado);
    }

    public List<T> inOrden() {
        List<T> lista = new ArrayList<>();
        inOrdenRec(raiz, lista);
        return lista;
    }

    private void inOrdenRec(NodoABB<T> nodo, List<T> lista) {
        if (nodo == null) return;
        inOrdenRec(nodo.izq, lista);
        lista.add(nodo.dato);
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

