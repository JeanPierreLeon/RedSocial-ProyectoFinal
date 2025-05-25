package uniquindio.com.academix.Utils;

import java.io.*;

import uniquindio.com.academix.Model.Academix;

public class Persistencia {

    private static final String RUTA_ARCHIVO = "data/academix.dat";

    public static Academix cargarRecursoBancoBinario() {
        File archivo = new File(RUTA_ARCHIVO);
        if (!archivo.exists()) {
            // Si el archivo no existe, crear uno nuevo con datos por defecto
            Academix academix = new Academix();
            guardarRecursoBancoBinario(academix);
            return academix;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivo))) {
            return (Academix) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error al cargar los datos: " + e.getMessage());
            // Si hay error al leer, crear nuevo archivo con datos por defecto
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
