package uniquindio.com.academix.Utils;

import java.io.*;

import uniquindio.com.academix.Model.Academix;

public class Persistencia {

    public static final String RUTA_ARCHIVO_MODELO_ACADEMIX_BINARIO = "model.dat";

    public static Academix cargarRecursoBancoBinario() {
        File archivo = new File(RUTA_ARCHIVO_MODELO_ACADEMIX_BINARIO);
        if (!archivo.exists()) {
            return new Academix();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivo))) {
            return (Academix) ois.readObject();
        } catch (InvalidClassException e) {
            // Si hay incompatibilidad de clases, elimina el archivo y crea uno nuevo
            System.err.println("¡Atención! El archivo de persistencia es incompatible con la versión actual del modelo. Se eliminará y se creará uno nuevo.");
            archivo.delete();
            return new Academix();
        } catch (Exception e) {
            e.printStackTrace();
            return new Academix();
        }
    }

    public static void guardarRecursoBancoBinario(Academix academix) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(RUTA_ARCHIVO_MODELO_ACADEMIX_BINARIO))) {
            oos.writeObject(academix);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
