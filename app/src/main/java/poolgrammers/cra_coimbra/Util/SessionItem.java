package poolgrammers.cra_coimbra.Util;

/**
 * Created by pedromql on 15/12/2016.
 */

public class SessionItem {
    private String data;
    private String tempo;
    private int idSessao;
    private int idProva;
    private Boolean checked;

    public SessionItem() {

    }

    public SessionItem(String data, String tempo, Boolean checked) {
        this.data = data;
        this.tempo = tempo;
        this.checked = checked;
    }

    public SessionItem(String data, String tempo, int idSessao, int idProva, Boolean checked) {
        this.data = data;
        this.tempo = tempo;
        this.idSessao = idSessao;
        this.idProva = idProva;
        this.checked = checked;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Boolean getChecked() {
        return checked;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }

    public int getIdSessao() {
        return idSessao;
    }

    public void setIdSessao(int idSessao) {
        this.idSessao = idSessao;
    }

    public int getIdProva() {
        return idProva;
    }

    public void setIdProva(int idProva) {
        this.idProva = idProva;
    }

    public String getTempo() {
        return tempo;
    }

    public void setTempo(String tempo) {
        this.tempo = tempo;
    }
}
