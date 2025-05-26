package uniquindio.com.academix.Model;

import java.util.*;
import uniquindio.com.academix.Model.ListaSimple;

public class SugerenciasEstudio {
    
    public static class SugerenciaCompañero {
        private Estudiante estudiante;
        private int puntajeAfinidad;
        private ListaSimple<String> interesesComunes;
        private boolean mismaUniversidad;
        private boolean amigoDeAmigo;
        
        public SugerenciaCompañero(Estudiante estudiante) {
            this.estudiante = estudiante;
            this.puntajeAfinidad = 0;
            this.interesesComunes = new ListaSimple<>();
        }
        
        public Estudiante getEstudiante() {
            return estudiante;
        }
        
        public int getPuntajeAfinidad() {
            return puntajeAfinidad;
        }
        
        public ListaSimple<String> getInteresesComunes() {
            return interesesComunes;
        }
        
        public boolean isMismaUniversidad() {
            return mismaUniversidad;
        }
        
        public boolean isAmigoDeAmigo() {
            return amigoDeAmigo;
        }
    }
    
    public static ListaSimple<SugerenciaCompañero> obtenerSugerencias(Estudiante estudiante, Academix academix) {
        Map<String, SugerenciaCompañero> sugerencias = new HashMap<>();
        
        // Obtener todos los estudiantes excepto el actual
        for (int i = 0; i < academix.getListaEstudiantes().size(); i++) {
            Estudiante otroEstudiante = academix.getListaEstudiantes().get(i);
            
            // No sugerir al mismo estudiante ni a sus amigos actuales
            if (otroEstudiante.getUsuario().equals(estudiante.getUsuario()) || 
                estudiante.getAmigos().contiene(otroEstudiante.getUsuario())) {
                continue;
            }
            
            SugerenciaCompañero sugerencia = new SugerenciaCompañero(otroEstudiante);
            
            // Verificar universidad en común (3 puntos)
            if (estudiante.getUniversidad().equals(otroEstudiante.getUniversidad())) {
                sugerencia.puntajeAfinidad += 3;
                sugerencia.mismaUniversidad = true;
            }
            
            // Verificar intereses en común (2 puntos por cada interés)
            for (String interes : otroEstudiante.getIntereses()) {
                if (estudiante.getIntereses().contiene(interes)) {
                    sugerencia.puntajeAfinidad += 2;
                    sugerencia.interesesComunes.agregar(interes);
                }
            }
            
            // Verificar amigos en común (5 puntos por ser amigo de amigo)
            for (String amigoUsuario : estudiante.getAmigos()) {
                // Buscar el objeto Estudiante correspondiente al usuario
                Estudiante amigo = null;
                for (int j = 0; j < academix.getListaEstudiantes().size(); j++) {
                    Estudiante posible = academix.getListaEstudiantes().get(j);
                    if (posible.getUsuario().equals(amigoUsuario)) {
                        amigo = posible;
                        break;
                    }
                }
                if (amigo != null && amigo.getAmigos().contiene(otroEstudiante.getUsuario())) {
                    sugerencia.puntajeAfinidad += 5;
                    sugerencia.amigoDeAmigo = true;
                    break;
                }
            }
            
            // Solo agregar si hay algún tipo de afinidad
            if (sugerencia.puntajeAfinidad > 0) {
                sugerencias.put(otroEstudiante.getUsuario(), sugerencia);
            }
        }
        
        // Convertir el mapa a lista y ordenar por puntaje de afinidad
        ListaSimple<SugerenciaCompañero> sugerenciasOrdenadas = new ListaSimple<>();
        List<SugerenciaCompañero> temp = new ArrayList<>(sugerencias.values());
        temp.sort((s1, s2) -> Integer.compare(s2.puntajeAfinidad, s1.puntajeAfinidad));
        for (SugerenciaCompañero s : temp) {
            sugerenciasOrdenadas.agregar(s);
        }
        return sugerenciasOrdenadas;
    }
}

