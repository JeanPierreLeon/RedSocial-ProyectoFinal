package uniquindio.com.academix.Estructuras;

import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class ListaSimple<T> implements Iterable<T>, Serializable {

    private Nodo<T> cabeza;
    private int tamaño;

    // Nodo interno
    private static class Nodo<T> implements Serializable {
        T dato;
        Nodo<T> siguiente;

        Nodo(T dato) {
            this.dato = dato;
        }
    }

    // Agrega un elemento al final de la lista
    public void agregar(T dato) {
        Nodo<T> nuevoNodo = new Nodo<>(dato);
        if (cabeza == null) {
            cabeza = nuevoNodo;
        } else {
            Nodo<T> actual = cabeza;
            while (actual.siguiente != null) {
                actual = actual.siguiente;
            }
            actual.siguiente = nuevoNodo;
        }
        tamaño++;
    }

    // Elimina un elemento por igualdad
    public void eliminar(T dato) {
        if (cabeza == null) return;

        if (cabeza.dato.equals(dato)) {
            cabeza = cabeza.siguiente;
            tamaño--;
            return;
        }

        Nodo<T> actual = cabeza;
        while (actual.siguiente != null) {
            if (actual.siguiente.dato.equals(dato)) {
                actual.siguiente = actual.siguiente.siguiente;
                tamaño--;
                return;
            }
            actual = actual.siguiente;
        }
    }

    // Obtiene un elemento por índice
    public T get(int index) {
        if (index < 0 || index >= tamaño) {
            throw new IndexOutOfBoundsException("Índice fuera de rango: " + index);
        }

        Nodo<T> actual = cabeza;
        for (int i = 0; i < index; i++) {
            actual = actual.siguiente;
        }
        return actual.dato;
    }

    // Tamaño de la lista
    public int size() {
        return tamaño;
    }

    // Implementación del iterador para usar for-each
    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private Nodo<T> actual = cabeza;

            @Override
            public boolean hasNext() {
                return actual != null;
            }

            @Override
            public T next() {
                if (!hasNext()) throw new NoSuchElementException();
                T dato = actual.dato;
                actual = actual.siguiente;
                return dato;
            }
        };
    }
}
