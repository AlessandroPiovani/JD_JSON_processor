package unused;


import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.timeseries.simplets.TsObservation;
import java.util.LinkedHashMap;
import java.util.Map;

public class DestOutput {
    private String outputKey;
    private String seriesName;
    private String idAggregazione;
    private long idBase;
    private long idTipoIndicatore;
    private long idEdizioneIndicatore;
    private long idVariabile;
    private long idPeriodoStart;

    //Risultati elaborazione
    private Double likelihoodBIC;
    private String esito=null;

    private final Map<String, Map<String,Double>> risultati=new LinkedHashMap<>(); //key=jDemetra output type registrato in DestTipoDestag

    public String getOutputKey() {
        return outputKey;
    }

    public void setOutputKey(String key) {
        outputKey="";
        if (seriesName!=null) outputKey=seriesName+"|";
        outputKey+=key;
    }

    public String getSeriesName() {
        return seriesName;
    }

    public void setSeriesName(String seriesName) {
        this.seriesName = seriesName;
        if (outputKey!=null&&!outputKey.startsWith(seriesName)) {
            outputKey=seriesName+"|"+outputKey;
        }
    }

    public String getIdAggregazione() {
        return idAggregazione;
    }

    public void setIdAggregazione(String idAggregazione) {
        this.idAggregazione = idAggregazione;
    }

    public long getIdBase() {
        return idBase;
    }

    public void setIdBase(long idBase) {
        this.idBase = idBase;
    }

    public long getIdTipoIndicatore() {
        return idTipoIndicatore;
    }

    public void setIdTipoIndicatore(long idTipoIndicatore) {
        this.idTipoIndicatore = idTipoIndicatore;
    }

    public long getIdEdizioneIndicatore() {
        return idEdizioneIndicatore;
    }

    public void setIdEdizioneIndicatore(long idEdizioneIndicatore) {
        this.idEdizioneIndicatore = idEdizioneIndicatore;
    }

    public long getIdVariabile() {
        return idVariabile;
    }

    public void setIdVariabile(long idVariabile) {
        this.idVariabile = idVariabile;
    }

    public long getIdPeriodoStart() {
        return idPeriodoStart;
    }

    public void setIdPeriodoStart(long idPeriodoStart) {
        this.idPeriodoStart = idPeriodoStart;
    }

    public Double getLikelihoodBIC() {
        return likelihoodBIC;
    }

    public void setLikelihoodBIC(Double likelihoodBIC) {
        this.likelihoodBIC = likelihoodBIC;
    }

    public String getEsito() {
        return esito;
    }

    public void setEsito(String esito) {
        this.esito = esito;
    }

    public Map<String, Map<String,Double>> getRisultati() {
        return risultati;
    }
    
    public void addRisultato(String tipoOutput, TsData risultato) {
        Map resultData=null;
        if(risultato!=null) {
            resultData=new LinkedHashMap<>();
            for (TsObservation obs : risultato) {
                resultData.put(obs.getPeriod()+"", obs.getValue());
            }
        }
        this.risultati.put(tipoOutput,resultData);
    }

        
}
