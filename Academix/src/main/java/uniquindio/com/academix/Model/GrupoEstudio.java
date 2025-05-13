package uniquindio.com.academix.Model;

public class GrupoEstudio {
    private String idGrupo;
    private String tema;

    public GrupoEstudio(String idGrupo, String tema) {
        this.idGrupo = idGrupo;
        this.tema = tema;
    }

    public String getIdGrupo() { return idGrupo; }
    public String getTema() { return tema; }
}

