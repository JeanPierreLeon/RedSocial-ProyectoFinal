package uniquindio.com.academix.Factory;

import uniquindio.com.academix.Model.Academix;
import uniquindio.com.academix.Utils.Persistencia;

public class ModelFactory {

    private Academix academix;

    private static class SingletonHolder {
        private final static ModelFactory eINSTANCE = new ModelFactory();
    }

    public static ModelFactory getInstance() {
        return SingletonHolder.eINSTANCE;
    }

    private ModelFactory() {
        cargarRecursosXML();
    }

    public Academix getAcademix() {
        return academix;
    }

    private void cargarRecursosXML() {
        this.academix = Persistencia.cargarRecursoBancoBinario();

        // Si al cargar es null, crea uno nuevo para evitar errores
        if (this.academix == null) {
            this.academix = new Academix();
        }
    }


    public void guardarRecursosXML() {
        Persistencia.guardarRecursoBancoBinario(academix);
    }
}
