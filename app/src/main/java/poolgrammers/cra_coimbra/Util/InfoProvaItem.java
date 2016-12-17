package poolgrammers.cra_coimbra.Util;

/**
 * Created by pedromql on 17/12/2016.
 */

public class InfoProvaItem {
    private int tipo;
    private String modalidade;
    private String regulamento;
    private String local;
    private String responsavelCra;
    private String juizArbitro;
    private String data;
    private String hora;

    public InfoProvaItem() {

    }

    public InfoProvaItem(int tipo, String modalidade, String regulamento, String local, String responsavelCra, String juizArbitro) {
        this.tipo = tipo;
        this.modalidade = modalidade;
        this.regulamento = regulamento;
        this.local = local;
        this.responsavelCra = responsavelCra;
        this.juizArbitro = juizArbitro;
    }

    public InfoProvaItem(int tipo, String hora, String data) {
        this.tipo = tipo;
        this.hora = hora;
        this.data = data;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    public String getRegulamento() {
        return regulamento;
    }

    public void setRegulamento(String regulamento) {
        this.regulamento = regulamento;
    }

    public String getModalidade() {
        return modalidade;
    }

    public void setModalidade(String modalidade) {
        this.modalidade = modalidade;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public String getResponsavelCra() {
        return responsavelCra;
    }

    public void setResponsavelCra(String responsavelCra) {
        this.responsavelCra = responsavelCra;
    }

    public String getJuizArbitro() {
        return juizArbitro;
    }

    public void setJuizArbitro(String juizArbitro) {
        this.juizArbitro = juizArbitro;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
