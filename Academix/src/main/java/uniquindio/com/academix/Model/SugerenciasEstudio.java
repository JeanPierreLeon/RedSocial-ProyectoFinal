package uniquindio.com.academix.Model;

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
        ListaSimple<SugerenciaCompañero> sugerencias = new ListaSimple<>();
        ListaSimple<String> sugeridosUsuarios = new ListaSimple<>();

        // Obtener todos los estudiantes excepto el actual
        for (int i = 0; i < academix.getListaEstudiantes().tamano(); i++) {
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
                Estudiante amigo = null;
                for (int j = 0; j < academix.getListaEstudiantes().tamano(); j++) {
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
            // Solo agregar si hay algún tipo de afinidad y no está repetido
            if (sugerencia.puntajeAfinidad > 0 && !sugeridosUsuarios.contiene(otroEstudiante.getUsuario())) {
                sugerencias.agregar(sugerencia);
                sugeridosUsuarios.agregar(otroEstudiante.getUsuario());
            }
        }
        // Ordenar sugerencias por puntaje de afinidad (burbuja simple)
        for (int i = 0; i < sugerencias.tamano() - 1; i++) {
            for (int j = 0; j < sugerencias.tamano() - i - 1; j++) {
                if (sugerencias.get(j).puntajeAfinidad < sugerencias.get(j + 1).puntajeAfinidad) {
                    SugerenciaCompañero temp = sugerencias.get(j);
                    sugerencias.insertarEn(j, sugerencias.get(j + 1));
                    sugerencias.eliminar(sugerencias.get(j + 2));
                    sugerencias.insertarEn(j + 1, temp);
                    sugerencias.eliminar(sugerencias.get(j + 2));
                }
            }
        }
        return sugerencias;
    }
}

