package unused;


import unused.DestOutput;
import ec.tstoolkit.timeseries.simplets.TsData;
//import it.istat.smetabrowser.dto.jms.RigaDettaglioDTO;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import src.DestSpecificationsModel;

/**
 *
 * @author cazora
 */
public class SeasonalData {

    //dati estratti da DestagionalizzazioneMessageDTO / RigaDettaglioDTO
    private String key; //combinazione dei campi seguenti identificativi della serie

    private long[] idTipoOutput;
    private Map<Long, RigaDettaglioDTO> dati=new HashMap<>(); //key della map è idPeriodo
    
    //dati derivati da elaborazione di RigaDettaglioDTO
    private int anno;
    private int mese;
    private double[] valori; //in ordine di idPeriodo
    
    //dati estratti in base a key in elaborazione
    private String modelloInput; //Stringa in formato Json
    private DestSpecificationsModel modello; //Json deserializzato

    private int frequency;
    
    //dati di risultato
    private DestOutput output;


    public SeasonalData(RigaDettaglioDTO riga) {
        output=new DestOutput();
        this.setKey(calcKey(riga));
        this.output.setIdAggregazione(riga.getIdAggregazione());
        this.output.setIdBase(riga.getIdBase());
        this.output.setIdEdizioneIndicatore(riga.getIdEdizioneIndicatore());
        this.output.setIdTipoIndicatore(riga.getIdTipoIndicatore());
        this.output.setIdVariabile(riga.getIdVariabile());
    }

    public static String calcKey(RigaDettaglioDTO dettaglio) {
            return dettaglio.getIdAggregazione()+"-"
                    +dettaglio.getIdBase()+"-"
                    +dettaglio.getIdTipoIndicatore()+"-"
                    +dettaglio.getIdVariabile();
    }

    public String getKey() {
        return key;
    }

    public final void setKey(String key) {
        this.key = key;
        this.output.setOutputKey(key);
    }
    
    public String getOutputKey() {
        return output.getOutputKey();
    }

    public String getIdAggregazione() {
        return output.getIdAggregazione();
    }

    public void setIdAggregazione(String idAggregazione) {
        this.output.setIdAggregazione(idAggregazione);
    }

    public long getIdBase() {
        return output.getIdBase();
    }

    public void setIdBase(long idBase) {
        this.output.setIdBase(idBase);
    }

    public long getIdTipoIndicatore() {
        return output.getIdTipoIndicatore();
    }

    public void setIdTipoIndicatore(long idTipoIndicatore) {
        this.output.setIdTipoIndicatore(idTipoIndicatore);
    }

    public long getIdEdizioneIndicatore() {
        return output.getIdEdizioneIndicatore();
    }

    public void setIdEdizioneIndicatore(long idEdizioneIndicatore) {
        this.output.setIdEdizioneIndicatore(idEdizioneIndicatore);
    }

    public long getIdVariabile() {
        return output.getIdVariabile();
    }

    public void setIdVariabile(long idVariabile) {
        this.output.setIdVariabile(idVariabile);
    }

    public long getIdPeriodoStart() {
        return output.getIdPeriodoStart();
    }

    public void setIdPeriodoStart(long idPeriodoStart) {
        this.output.setIdPeriodoStart(idPeriodoStart);
        String sPeriodo = idPeriodoStart + "";
        String sAnno = sPeriodo.substring(0, 4);
        String sMese = sPeriodo.substring(4, 6);
        this.setAnno(Integer.parseInt(sAnno));
        this.setMese(Integer.parseInt(sMese) - 1);        
    }

    public long[] getIdTipoOutput() {
        return idTipoOutput;
    }

    public void setIdTipoOutput(long[] idTipoOutput) {
        this.idTipoOutput = idTipoOutput;
    }

    public Map<Long, RigaDettaglioDTO> getDati() {
        return dati;
    }

    public void setDati(Map<Long, RigaDettaglioDTO> dati) {
        this.dati = dati;
    }

    public int getAnno() {
        return anno;
    }

    public void setAnno(int anno) {
        this.anno = anno;
    }

    public int getMese() {
        return mese;
    }

    public void setMese(int mese) {
        this.mese = mese;
    }

    public double[] getValori() {
        return valori;
    }

    public void setValori(double[] valori) {
        this.valori = valori;
    }

    public String getModelloInput() {
        return modelloInput;
    }

    public void setModelloInput(String modelloInput) {
        this.modelloInput = modelloInput;
    }

    public DestSpecificationsModel getModello() {
        return modello;
    }

    public void setModello(DestSpecificationsModel modello) {
        this.modello = modello;
        if (this.modello!=null) {
            this.setFrequency(this.modello.getFrequency());
            /* TODO togliere questo dopo averlo letto da DB oppure mettere un controllo*/
            this.setSeriesName(this.modello.getSeriesName());
        }
    }

    public String getSeriesName() {
        return output.getSeriesName();
    }

    public void setSeriesName(String seriesName) {
        this.output.setSeriesName(seriesName);
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public DestOutput getOutput() {
        return output;
    }

    public void setOutput(DestOutput output) {
        this.output = output;
    }

    public Map<String, Map<String,Double>> getRisultati() {
        return output.getRisultati();
    }

    public void addRisultato(String tipoOutput, TsData risultato) {
        this.output.addRisultato(tipoOutput,risultato);
    }

    public Double getLikelihoodBIC() {
        return output.getLikelihoodBIC();
    }

    public void setLikelihoodBIC(Double likelihoodBIC) {
        this.output.setLikelihoodBIC(likelihoodBIC);
    }
    
    public String getEsito() {
        return output.getEsito();
    }
    
    public void setEsito(String esito) {
        this.output.setEsito(esito);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 11 * hash + Objects.hashCode(this.key);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SeasonalData other = (SeasonalData) obj;
        return Objects.equals(this.key, other.key);
    }
}
