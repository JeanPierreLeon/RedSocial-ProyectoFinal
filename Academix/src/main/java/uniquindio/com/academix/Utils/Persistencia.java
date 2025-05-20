package uniquindio.com.academix.Utils;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import uniquindio.com.academix.Model.Academix;

public class Persistencia {

    // Cambia esta ruta si ejecutas fuera de un IDE
    public static final String RUTA_ARCHIVO_MODELO_ACADEMIX_XML = "model.xml";

    public static Academix cargarRecursoBancoXML() {
        File archivo = new File(RUTA_ARCHIVO_MODELO_ACADEMIX_XML);
        if (!archivo.exists()) {
            return new Academix();
        }

        try (XMLDecoder decodificadorXML = new XMLDecoder(new BufferedInputStream(new FileInputStream(archivo)))) {
            return (Academix) decodificadorXML.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return new Academix();
        }
    }

    public static void guardarRecursoBancoXML(Academix academix) {
        try (XMLEncoder codificadorXML = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(RUTA_ARCHIVO_MODELO_ACADEMIX_XML)))) {
            codificadorXML.writeObject(academix);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
