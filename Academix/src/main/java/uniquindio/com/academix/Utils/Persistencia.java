package uniquindio.com.academix.Utils;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import uniquindio.com.academix.Model.Academix;

public class Persistencia {

    // Ruta en la carpeta del usuario (home)
    public static final String RUTA_ARCHIVO_MODELO_ACADEMIX_XML = System.getProperty("user.home") + File.separator + "academix_model.xml";

    public static Academix cargarRecursoBancoXML() {
        Academix academix = null;

        File archivo = new File(RUTA_ARCHIVO_MODELO_ACADEMIX_XML);
        if (!archivo.exists()) {
            // Si no existe el archivo, devolvemos un objeto nuevo
            return new Academix();
        }

        try (XMLDecoder decodificadorXML = new XMLDecoder(new FileInputStream(archivo))) {
            academix = (Academix) decodificadorXML.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            // En caso de error, devolvemos un nuevo objeto para no bloquear la app
            return new Academix();
        }

        return academix;
    }

    public static void guardarRecursoBancoXML(Academix academix) {
        try (XMLEncoder codificadorXML = new XMLEncoder(new FileOutputStream(RUTA_ARCHIVO_MODELO_ACADEMIX_XML))) {
            codificadorXML.writeObject(academix);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


