package uniquindio.com.academix.Utils;

import java.io.*;

import uniquindio.com.academix.Model.Academix;

public class Persistencia {

    private static final String RUTA_ARCHIVO = "model.dat";

    public static Academix cargarRecursoBancoBinario() {
        File archivo = new File(RUTA_ARCHIVO);
        // Si el archivo no existe o está vacío, crear uno nuevo con datos por defecto
        if (!archivo.exists() || archivo.length() == 0) {
            if (archivo.exists()) {
                archivo.delete();
            }
            Academix academix = new Academix();
            guardarRecursoBancoBinario(academix);
            return academix;
        }
        // Verificar de nuevo el tamaño antes de leer
        if (archivo.length() == 0) {
            archivo.delete();
            Academix academix = new Academix();
            guardarRecursoBancoBinario(academix);
            return academix;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivo))) {
            return (Academix) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error al cargar los datos: " + e);
            if (archivo.exists()) {
                archivo.delete();
            }
            Academix academix = new Academix();
            guardarRecursoBancoBinario(academix);
            return academix;
        }
    }

    public static void guardarRecursoBancoBinario(Academix academix) {
        try {
            // Asegurar que el directorio existe
            File directorio = new File("data");
            if (!directorio.exists()) {
                directorio.mkdirs();
            }

            // Guardar el archivo
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(RUTA_ARCHIVO))) {
                oos.writeObject(academix);
            }
        } catch (IOException e) {
            System.err.println("Error al guardar los datos: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
