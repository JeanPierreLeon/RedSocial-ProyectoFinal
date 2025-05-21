package uniquindio.com.academix.Estructuras;

import java.io.Serial;
import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class ListaSimple<T> implements Iterable<T>, Serializable {

    private Nodo<T> cabeza;
    private int tamaño;
    @Serial
    private static final long serialVersionUID = 1L;

    public void limpiar() {
        cabeza = null;
        tamaño = 0;
    }


    public static class Nodo<T> implements Serializable {

        @Serial
        private static final long serialVersionUID = 9032758452518630826L;
        T dato;
        Nodo<T> siguiente;

        Nodo(T dato) {
            this.dato = dato;
        }
    }

    public boolean estaVacia() {
        return cabeza == null;
        // o también se podría usar: return tamaño == 0;
    }


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

    // Nuevo método para insertar en cualquier posición
    public void insertarEn(int index, T dato) {
        if (index < 0 || index > tamaño) {
            throw new IndexOutOfBoundsException("Índice fuera de rango: " + index);
        }
        Nodo<T> nuevoNodo = new Nodo<>(dato);
        if (index == 0) {
            nuevoNodo.siguiente = cabeza;
            cabeza = nuevoNodo;
        } else {
            Nodo<T> actual = cabeza;
            for (int i = 0; i < index - 1; i++) {
                actual = actual.siguiente;
            }
            nuevoNodo.siguiente = actual.siguiente;
            actual.siguiente = nuevoNodo;
        }
        tamaño++;
    }

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

    public int size() {
        return tamaño;
    }

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

    public void imprimir() {
        for (T item : this) {
            System.out.println(item);
        }
    }
}
