package uniquindio.com.academix.Utils;

import uniquindio.com.academix.Model.Academix;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;

public class Persistencia {

    public static final String RUTA_ARCHIVO_MODELO_ACADEMIX_XML = "src/main/resources/persistencia/model.xml";

    public static Academix cargarRecursoBancoXML() {
        Academix academix = null;

        File archivo = new File(RUTA_ARCHIVO_MODELO_ACADEMIX_XML);
        if (!archivo.exists()) {
            return new Academix(); // Crea uno nuevo si no existe
        }

        try (XMLDecoder decodificadorXML = new XMLDecoder(new FileInputStream(archivo))) {
            academix = (Academix) decodificadorXML.readObject();
        } catch (Exception e) {
            e.printStackTrace();
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
