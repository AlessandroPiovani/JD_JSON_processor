package unused;


import unused.JDemetraServiceImpl;
import unused.DestOutput;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
//import it.istat.koala.utility.DestSpecificationsModel;
import java.util.Calendar;
import java.util.List;
import java.util.Arrays;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.config.ConfigurableBeanFactory;
//import org.springframework.context.annotation.Scope;
//import org.springframework.stereotype.Service;
//
//import it.istat.koala.data.repository.JobFilesRepository;
//import it.istat.koala.dto.JobDTO;
//import it.istat.koala.enumerator.DestTipoDestag;
//import it.istat.koala.utility.SeasonalData;

//import it.istat.koala.enumerator.StatoJob;

//import it.istat.koala.service.JobErroriService;
//import it.istat.koala.service.JobFilesService;
//import it.istat.koala.service.JobService;
//import it.istat.koala.service.StatoJobService;
//import it.istat.smetabrowser.dto.jms.DestagionalizzazioneMessageDTO;
//import it.istat.smetabrowser.dto.jms.RigaDettaglioDTO;
//
//import it.istat.smetabrowser.exception.BusinessServiceException;
//
//import it.istat.koala.service.JDemetraService;
//import it.istat.koala.service.SeasonalDataProcessingService;
//import it.istat.koala.utility.DestOutput;
//import it.istat.smetabrowser.util.ClobUtils;
import java.sql.Clob;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import src.DestSpecificationsModel;

//@Service("seasonalDataProcessingService")
//@Scope(scopeName = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class SeasonalDataProcessingServiceImpl {

//    @Autowired
//    private JobService jobService;

//    @Autowired
//    private JobFilesService jobFilesService;

//    @Autowired
//    private StatoJobService statoJobService;

//    @Autowired
//    private JobErroriService jobErroriService;

//    @Autowired
//    private JobFilesRepository jobFilesRepository;

//    @Autowired
    private JDemetraServiceImpl jDemetraService;

//    private final Logger logger = LoggerFactory.getLogger(getClass());
    /* La key della mappa datiDaDestagionalizzare è la combinazione che determina 
    la serie ed è calcolata in calcKey in base ai contenuti di RigaDettaglioDTO*/
    private final HashMap<String,SeasonalData> datiDaDestagionalizzare=new HashMap();
    /* La key della mappa datiElaborati è seriesName seguito dalla key di datiDaDestagionalizzare*/
    private final HashMap<String,DestOutput> datiElaborati=new HashMap();
    /* Lista di errori non bloccanti da inserire in Job errori alla fine del processo*/
    private final HashMap<String,String> errori = new HashMap();
    
    //OUTPUT_MAIN.NAME per fase Destagionalizzazione diretta
    /* TODO i nomi degli output si devono estrarre da OUTPUT_MAIN in base a id_indagine e id_destinatario (destagionalizzazione)*/
    private final String DEST_DIRETTA_RESULT = "DEST_DIRETTA_RSLT";
    private final String DEST_DIRETTA_ERRORI = "DEST_DIRETTA_ERR";
    //Dimensione minima della serie per poter essere elaborata
    private final int MIN_SERIES_LENGTH = 36;
    
    private String fase;

    /**
     * 1. legge tutti le RigaDettaglioDTO contenute in DestagionalizzazioneMessageDTO e le
     * raggruppa in SeasonalData per chiave della serie determinata in SeasonalData.calcKey
     * 2. Per ogni serie (SeasonalData in datiDaDestagionalizzare) 
     * - riordina i dati in base al periodo (idPeriodo in RigaDettaglioDTO)
     * - verifica la correttezza e completezza della serie
     * - estrae dal DB il modello json e i tipi di output richiesti
     * - deserializza il modello json effettuando gli opportuni aggiustamenti
     * - lancia il processo di calcolo della destagionalizzazione
     * - inserisce gli output in datiElaborati
     * - in caso di errore inserisce in JOB_ERRORI e se possibile elabora la prossima serie
     * 3. Salva gli output della destagionalizzazione diretta in JOB_FILES in formato json
     * 4. DESTAGIONALIZZAZIONE INDIRETTA
     * @param job Dati del job in elaborazione
     * @param dto Dati di input per l'elaborazione
     * @throws BusinessServiceException 
     */
//    @Override
    public void process(List<RigaDettaglioDTO> dto, String directoryPathExtReg) {



        try {

            try {
                fase="Elab.RigaDett";
                //legge tutti i RigaDettaglioDTO e li aggrega in base alla chiave
                //determinata in calcKey
                elabora(dto,1L);
                //elabora(dto);

            } catch (Exception e) {
                return;
            }

            // 2.DESTAGIONALIZZAZIONE DIRETTA

            try {
                fase="Elab.Dest.Diretta";
                datiDaDestagionalizzare.forEach((key,datiSerie) -> {
                    try {
                        datiSerie.setEsito(null);
                        //per ogni raggruppamento calcola i metadati, estrai il modello e riordina i valori
                        elaboraDettaglio(datiSerie);
                        if (datiSerie.getEsito()==null) {//la serie può essere elaborata
//                            System.out.println("\nInizio elaborazione: indagine:"+dto.getIdIndagine()+" periodoStart:"+dto.getIdPeriodoStart()+" serie:"+datiSerie.getOutputKey()+" numVal:"+datiSerie.getValori().length);
                            //Calcola valori destagionalizzati per questa serie
                            datiSerie.setEsito(jDemetraService.process(datiSerie,1L, directoryPathExtReg));
                            if (!"ok".equals(datiSerie.getEsito())) {
                                errori.put(datiSerie.getOutputKey(),datiSerie.getEsito());
                            }
                        } else {
//                            String testoErrore=("\nImpossibile elaborare serie: indagine:"+dto.getIdIndagine()
//                                    +" periodoStart:"+dto.getIdPeriodoStart()
//                                    +" variab:"+datiSerie.getOutputKey()+" numVal:"+datiSerie.getDati().size()
//                                    +" per "+datiSerie.getEsito()
//                            );
                            String testoErrore = "\nImpossibile elaborare la serie";
                            System.out.println(testoErrore);
                            errori.put(datiSerie.getOutputKey(),testoErrore);
                        }
                    } catch (Exception e) {
                        String testoErrore="Errore nel generare dati per "+key+e.getMessage();
                        errori.put(datiSerie.getOutputKey(),testoErrore);
                    } finally {
                        if (datiSerie.getEsito()==null||"ok".equals(datiSerie.getEsito())){
                            datiElaborati.put(datiSerie.getOutputKey(),datiSerie.getOutput());
                        }
                    }
                    
                });
                
            } catch (Exception e) {
                String testoErrore="Errore nell'elaborazione dettagli "+e.getMessage();
                errori.put(fase,testoErrore);
                //return;
            } finally {
                try {
                    fase="Dest.Diretta.Output";
                    ObjectMapper mapper = new ObjectMapper();
                    String jsonTxt=mapper.writeValueAsString(datiElaborati);
//                    Clob content = ClobUtils.toClob(jsonTxt);
//                    jobFilesService.save(job.getId(), DEST_DIRETTA_RESULT, content);
//                    jsonTxt=mapper.writeValueAsString(errori);
//                    content = ClobUtils.toClob(jsonTxt);
//                    jobFilesService.save(job.getId(), DEST_DIRETTA_ERRORI, content);
                    errori.clear();
                } catch (Exception e) {
                    String testoErrore="Dest. diretta - Errore nella scrittura dei risultati"+e.getMessage();
                    errori.put(fase,testoErrore);
                }
            }
            //Thread.sleep(1);

            // 3.DESTAGIONALIZZAZIONE INDIRETTA
//            jobService.updateStato(job, StatoJob.INDIRETTA);
            fase="Dest.Indiretta";
            errori.put(fase,"Destagionalizzazione indiretta non elaborata");

            // 4.. ESTRAZIONE DATI E CREAZIONE FILES
//            jobService.updateStato(job, StatoJob.GENERA_OUTPUT);
            if (!errori.isEmpty()) {
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    String jsonTxt=mapper.writeValueAsString(errori);
                    //jobErroriService.save(job.getId, jsonTxt, Calendar.getInstance());
                } catch (Exception e) {
                }
                
            }

            // PROCESSO COMPLETATO
//            jobService.updateStato(job, StatoJob.COMPLETATO);
//            jobService.save(job);

        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(SeasonalDataProcessingServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Legge tutte le righe di dettaglio del DTO e crea una mappa non ordinata 
     * di aggregazioni corrispondenti alla serie (SeasonalData) in base a 
     * SeasonalData.calcKey in cui viene inserita la lista, non ordinata, di 
     * RigaDettaglioDTO appartenenti alla serie

     * @param dettagli: tutte le righe di dettaglio del DTO
     * @param idTipoOutput: array di id di tipo output appartenenti a DestTipoDestag
     * @param idPeriodoStart : numerico che rappresenta aaaamm di inizio serie
     */
    private void elabora(List<RigaDettaglioDTO> dettagli, Long idPeriodoStart) {
        if (dettagli != null && !dettagli.isEmpty()) {
            dettagli.forEach((riga) -> {
                String key = SeasonalData.calcKey(riga);
                SeasonalData datiDest=datiDaDestagionalizzare.get(key);
                if (datiDest==null) {
                    datiDest=new SeasonalData(riga);
                    datiDest.setIdPeriodoStart(idPeriodoStart);
                    datiDaDestagionalizzare.put(key, datiDest);
                }
                datiDest.getDati().put(riga.getIdPeriodoIndicatore(),riga);
            });
        }
    }
    
    /** 
     * Per la serie in elaborazione (SeasonalData):
     * - riordina i valori in base al periodo di riferimento
     * - prepara il modello e le specifications per jDemetra+
     * @param datiSerie: serie (SeasonalData) da elaborare
     */
    private void elaboraDettaglio(SeasonalData datiSerie){
        if (datiSerie.getDati()!=null && !datiSerie.getDati().isEmpty()) {
            
            //imposto il modello di destagionalizzazione e i tipi output
            setSeasonalModel(datiSerie);
            
            if (datiSerie.getModello()!=null) {
                // **************************************
                riempiSerie(datiSerie); //solo per poter eseguire i test
                // **************************************

                int numValori=datiSerie.getDati().size();

                if (numValori<MIN_SERIES_LENGTH) {
                    datiSerie.setEsito("Il numero di valori ("+numValori+") della serie non è sufficiente per l'elaborazione"); 
                } else {
                    Long[] periodi=datiSerie.getDati().keySet().toArray(new Long[datiSerie.getDati().size()]);
                    Arrays.sort(periodi);//riordino gli idPeriodo
                    //verifico inizio e completezza della serie
                    if (!periodi[0].equals(datiSerie.getIdPeriodoStart())) {
                        datiSerie.setEsito("La serie non inizia nel periodo scelto per l'elaborazione("+datiSerie.getIdPeriodoStart()+")");
                    } else if (checkSerie(periodi)) {//correggere per frequenze diverse da mensile
                        double[] valori=new double[numValori];
                        datiSerie.setValori(valori);
                        for(int idx=0;idx<numValori;idx++){
                            //inserisco i valori in ordine di idperiodo
                            valori[idx]=datiSerie.getDati().get(periodi[idx]).getValore();
                        }

                    } else {//la serie è discontinua
                        datiSerie.setEsito("La serie è discontinua "+formatArray(periodi));
                    }
                }
            } else {
                datiSerie.setEsito("Serie da non destagionalizzare");
            }
        } else {
            datiSerie.setEsito("La serie è vuota");
        }
    }
    
    /**
     * Per la serie in elaborazione (SeasonalData):
     * - verifica la corrispondenza del primo elemento al periodo in input
     * - verifica la completezza e continuità della serie
     * @param datiSerie
     * @return 
     */
    private boolean checkSerie(Object[] periodiInput) {
        boolean result=true;
        Long[] periodi=((Long[]) periodiInput);
        
        long periodoPrec=periodi[0];
        for (int idx=1;idx<periodi.length;idx++) {
            String sPeriodo = periodoPrec+"";
            long periodoOk=periodoPrec+1;
            if (sPeriodo.endsWith("12")) {
                periodoOk=periodoPrec+100-11;
            }
            if (periodoOk!=periodi[idx]) {
                result=false;
        break;
            }
            periodoPrec=periodoOk;
        }

        return result;
    }
    
    /**
     * Per la serie in elaborazione (SeasonalData):
     * - estrae il modello Json e i tipi output corrispondenti alla serie
     * - converte il modello Json in DestSpecificationsModel che sarà poi
     * utilizzato da jDemetra+ per impostare le specifications e generare
     * gli output
     * 
     * @param datiSerie SeasonalData che raccoglie tutte le informazioni della serie in elaborazione
     */
    private void setSeasonalModel(SeasonalData datiSerie){
        //Leggo il modello e i tipi output dal DB
        setConfigDestDiretta(datiSerie);
        //
        if (datiSerie.getModelloInput()!=null && !datiSerie.getModelloInput().trim().isEmpty()
                && datiSerie.getIdTipoOutput()!=null &&datiSerie.getIdTipoOutput().length>0) {
            DestSpecificationsModel modello=null;
            try {   
                //deserializziamo per prendere i campi custom o che non sono direttamente
                //trattati da jDemetra in base al json
                ObjectMapper mapper = new ObjectMapper();
                //ignora le chiavi non definite (sono quelle trattate direttamente)
                mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
                //accetta singolo valore dove prevista anche array
                mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
                //imposta a null le stringhe vuote
                mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
                //crea l'oggetto corrispondente 
                modello=mapper.readValue(datiSerie.getModelloInput(), DestSpecificationsModel.class);
                datiSerie.setModello(modello);

            } catch (Exception e) {
                String testoErrore="Errore nella conversione del modello: "+e.getMessage();
//                logger.error(testoErrore, e);
                datiSerie.setEsito(testoErrore);
            } finally {
                if (modello!=null) {
                    System.out.println("Serie storica:"+modello.getSeriesName());
                }
            }
        }
    }
    
    private void setConfigDestDiretta(SeasonalData datiSerie) { 
        //prendiamo dal DB il modello json e tipi di output richiesti
        //in caso di errore impostiamo a null sia il modelloInput sia idTipoOutput e inseriamo l'errore in esito

        datiSerie.setModelloInput(modelloTest2);//leggere il modello da DEST_DIRETTA
        
        long[] idTipoOutput=new long[6];
        idTipoOutput[0]=DestTipoDestag.SA.getIdTipoIndicatoreOutput();
        idTipoOutput[1]=DestTipoDestag.CALEFF.getIdTipoIndicatoreOutput();
        idTipoOutput[2]=DestTipoDestag.STAG.getIdTipoIndicatoreOutput();
        idTipoOutput[3]=DestTipoDestag.IRR.getIdTipoIndicatoreOutput();
        idTipoOutput[4]=DestTipoDestag.T.getIdTipoIndicatoreOutput();
        idTipoOutput[5]=DestTipoDestag.DIAGNOSTICS.getIdTipoIndicatoreOutput();
        //idTipoOutput[4]=DestTipoDestag.SAF.getIdTipoIndicatoreOutput();
        //idTipoOutput[5]=DestTipoDestag.TF.getIdTipoIndicatoreOutput();
        //idTipoOutput[6]=DestTipoDestag.FCAST5.getIdTipoIndicatoreOutput();
        //idTipoOutput[7]=DestTipoDestag.BCAST1.getIdTipoIndicatoreOutput();
        datiSerie.setIdTipoOutput(idTipoOutput);//leggere i tipi output da DB
    }
    
    private void riempiSerie(SeasonalData datiSerie) {
        if ("40600".equals(datiSerie.getIdAggregazione())) {
            HashMap<Long, RigaDettaglioDTO> adding = new HashMap<>();
            Long[] periodi=datiSerie.getDati().keySet().toArray(new Long[datiSerie.getDati().size()]);
            Arrays.sort(periodi);

            long periodoCorrente=datiSerie.getIdPeriodoStart();
            while(periodi[0]>periodoCorrente){
                Iterator<RigaDettaglioDTO> dati = datiSerie.getDati().values().iterator();
                while(dati.hasNext()&&periodi[0]>periodoCorrente) {
                    System.out.println("Aggiungo in testa "+periodoCorrente);
                    adding.put(periodoCorrente,dati.next());
                    String sPeriodo=periodoCorrente+"";
                    if (sPeriodo.endsWith("12")) {
                        periodoCorrente=periodoCorrente+100-11;
                    } else {
                        periodoCorrente++;
                    }
                }
            }
            int numValori=datiSerie.getDati().size()+adding.size();
            if (numValori<MIN_SERIES_LENGTH) {
                int it = (MIN_SERIES_LENGTH-numValori);//numero minimo da aggiungere
                periodoCorrente=periodi[periodi.length-1];
                while(it>0) {
                    Iterator<RigaDettaglioDTO> dati = datiSerie.getDati().values().iterator();
                    while(it>0 && dati.hasNext()) {
                        String sPeriodo=periodoCorrente+"";
                        if (sPeriodo.endsWith("12")) {
                            periodoCorrente=periodoCorrente+100-11;
                        } else {
                            periodoCorrente++;
                        }
                        adding.put(periodoCorrente,dati.next());
                        System.out.println("Aggiungo in coda "+periodoCorrente);
                        it--;
                    }
                }
            }
            datiSerie.getDati().putAll(adding);
        }
    }
    
    private String formatArray(Object[] righe) {
        StringBuilder res = new StringBuilder();
        String sep="";
        for (Object riga : righe) {
            res.append(sep).append(riga);
            sep=", ";
        }
        String result=res.toString();
        if (result.length()>500) {
            result=result.substring(0, 499);
        }
        return result;
    }


private static final String modelloTest = "{\n" +
            "\"series_name\":\"FATEXP_10\", \n" +
            "\"method\":\"TS\", \n" +
            "\"spec\":\"RSA0\", \n" +
            "\"preliminary.check\":true, \n" +
            "\"estimate.from\":\"NA\", \n" +
            "\"estimate.to\":\"NA\", \n" +
            "\"estimate.first\":\"NA\", \n" +
            "\"estimate.last\":\"NA\", \n" +
            "\"estimate.exclFirst\":0, \n" +
            "\"estimate.exclLast\":0, \n" +
            "\"estimate.tol\":1e-07, \n" +
            "\"estimate.eml\":true, \n" +
            "\"estimate.urfinal\":0.96, \n" +
            "\"transform.function\":\"Log\", \n" +
            "\"transform.fct\":0.95, \n" +
            "\"usrdef.outliersEnabled\":true, \n" +
            "\"usrdef.outliersType\":[\"AO\", \"LS\"], \n" +
            "\"usrdef.outliersDate\":[\"2010-06-01\", \"2009-01-01\"], \n" +
            "\"usrdef.outliersCoef\":\"NA\", \n" +
            "\"userdef.varFromFile\":false, \n" +
            "\"usrdef.varEnabled\":false, \n" +
            "\"usrdef.var\":\"NA\", \n" +
            "\"usrdef.varType\":\"NA\", \n" +
            "\"usrdef.varCoef\":\"NA\", \n" +
            "\"tradingdays.mauto\":\"Unused\", \n" +
            "\"tradingdays.pftd\":0.01, \n" +
            "\"tradingdays.option\":\"None\", \n" +
            "\"tradingdays.leapyear\":false, \n" +
            "\"tradingdays.stocktd\":0, \n" +
            "\"tradingdays.test\":\"None\", \n" +
            "\"easter.type\":\"Unused\", \n" +
            "\"easter.julian\":false, \n" +
            "\"easter.duration\":6, \n" +
            "\"easter.test\":false, \n" +
            "\"outlier.enabled\":false, \n" +
            "\"outlier.from\":\"NA\", \n" +
            "\"outlier.to\":\"NA\", \n" +
            "\"outlier.first\":\"NA\", \n" +
            "\"outlier.last\":\"NA\", \n" +
            "\"outlier.exclFirst\":0, \n" +
            "\"outlier.exclLast\":0, \n" +
            "\"outlier.ao\":false, \n" +
            "\"outlier.tc\":false, \n" +
            "\"outlier.ls\":false, \n" +
            "\"outlier.so\":false, \n" +
            "\"outlier.usedefcv\":true, \n" +
            "\"outlier.cv\":3.5, \n" +
            "\"outlier.eml\":false, \n" +
            "\"outlier.tcrate\":0.7, \n" +
            "\"automdl.enabled\":false, \n" +
            "\"automdl.acceptdefault\":false, \n" +
            "\"automdl.cancel\":0.05, \n" +
            "\"automdl.ub1\":0.97, \n" +
            "\"automdl.ub2\":0.91, \n" +
            "\"automdl.armalimit\":1, \n" +
            "\"automdl.reducecv\":0.12, \n" +
            "\"automdl.ljungboxlimit\":0.95, \n" +
            "\"automdl.compare\":false, \n" +
            "\"arima.mu\":false, \n" +
            "\"arima.p\":0, \n" +
            "\"arima.d\":1, \n" +
            "\"arima.q\":1, \n" +
            "\"arima.bp\":0, \n" +
            "\"arima.bd\":1, \n" +
            "\"arima.bq\":1, \n" +
            "\"arima.coefEnabled\":false, \n" +
            "\"arima.coef\":\"NA\", \n" +
            "\"arima.coefType\":\"NA\", \n" +
            "\"fcst.horizon\":-2, \n" +
            "\"seats.predictionLength\":-1, \n" +
            "\"seats.approx\":\"Legacy\", \n" +
            "\"seats.trendBoundary\":0.5, \n" +
            "\"seats.seasdBoundary\":0.8, \n" +
            "\"seats.seasdBoundary1\":0.8, \n" +
            "\"seats.seasTol\":2, \n" +
            "\"seats.maBoundary\":0.95, \n" +
            "\"seats.method\":\"Burman\"\n" +
            "}"
;
private static final String modelloTest1 = "{\n" +
    "\"series_name\":\"FATEXP_14\", \n" +
    "\"frequency\":12, \n" +
    "\"method\":\"TS\", \n" +
    "\"spec\":\"RSA0\", \n" +
    "\"preliminary.check\":true, \n" +
    "\"estimate.from\":\"NA\", \n" +
    "\"estimate.to\":\"NA\", \n" +
    "\"estimate.first\":\"NA\", \n" +
    "\"estimate.last\":\"NA\", \n" +
    "\"estimate.exclFirst\":0, \n" +
    "\"estimate.exclLast\":0, \n" +
    "\"estimate.tol\":1e-07, \n" +
    "\"estimate.eml\":true, \n" +
    "\"estimate.urfinal\":0.96, \n" +
    "\"transform.function\":\"Log\", \n" +
    "\"transform.fct\":0.95, \n" +
    "\"usrdef.outliersEnabled\":true, \n" +
    "\"usrdef.outliersType\":[\"AO\", \"AO\", \"AO\"], \n" +
    "\"usrdef.outliersDate\":[\"2020-03-01\", \"2020-05-01\", \"2017-03-01\"], \n" +
    "\"usrdef.outliersCoef\":\"NA\", \n" +
    "\"userdef.varFromFile\":true, \n" +
    "\"userdef.varFromFile.infoList\":[{\n" +
    "\"container\":\"tdmnw05.txt\", \n" +
    "\"start\":\"2005-01-01\", \n" +
    "\"n_var\":1}], \n" +
    "\"usrdef.varEnabled\":true, \n" +
    "\"usrdef.var\":\"NA\", \n" +
    "\"usrdef.varType\":\"Calendar\", \n" +
    "\"usrdef.varCoef\":\"NA\", \n" +
    "\"tradingdays.mauto\":\"Unused\", \n" +
    "\"tradingdays.pftd\":0.01, \n" +
    "\"tradingdays.option\":\"UserDefined\", \n" +
    "\"tradingdays.leapyear\":false, \n" +
    "\"tradingdays.stocktd\":0, \n" +
    "\"tradingdays.test\":\"None\", \n" +
    "\"easter.type\":\"Unused\", \n" +
    "\"easter.julian\":false, \n" +
    "\"easter.duration\":6, \n" +
    "\"easter.test\":false, \n" +
    "\"outlier.enabled\":false, \n" +
    "\"outlier.from\":\"NA\", \n" +
    "\"outlier.to\":\"NA\", \n" +
    "\"outlier.first\":\"NA\", \n" +
    "\"outlier.last\":\"NA\", \n" +
    "\"outlier.exclFirst\":0, \n" +
    "\"outlier.exclLast\":0, \n" +
    "\"outlier.ao\":false, \n" +
    "\"outlier.tc\":false, \n" +
    "\"outlier.ls\":false, \n" +
    "\"outlier.so\":false, \n" +
    "\"outlier.usedefcv\":true, \n" +
    "\"outlier.cv\":3.5, \n" +
    "\"outlier.eml\":false, \n" +
    "\"outlier.tcrate\":0.7, \n" +
    "\"automdl.enabled\":false, \n" +
    "\"automdl.acceptdefault\":false, \n" +
    "\"automdl.cancel\":0.05, \n" +
    "\"automdl.ub1\":0.97, \n" +
    "\"automdl.ub2\":0.91, \n" +
    "\"automdl.armalimit\":1, \n" +
    "\"automdl.reducecv\":0.12, \n" +
    "\"automdl.ljungboxlimit\":0.95, \n" +
    "\"automdl.compare\":false, \n" +
    "\"arima.mu\":false, \n" +
    "\"arima.p\":0, \n" +
    "\"arima.d\":1, \n" +
    "\"arima.q\":1, \n" +
    "\"arima.bp\":0, \n" +
    "\"arima.bd\":1, \n" +
    "\"arima.bq\":0, \n" +
    "\"arima.coefEnabled\":false, \n" +
    "\"arima.coef\":\"NA\", \n" +
    "\"arima.coefType\":\"NA\", \n" +
    "\"fcst.horizon\":-2, \n" +
    "\"seats.predictionLength\":-1, \n" +
    "\"seats.approx\":\"Legacy\", \n" +
    "\"seats.trendBoundary\":0.5, \n" +
    "\"seats.seasdBoundary\":0.8, \n" +
    "\"seats.seasdBoundary1\":0.8, \n" +
    "\"seats.seasTol\":2, \n" +
    "\"seats.maBoundary\":0.95, \n" +
    "\"seats.method\":\"Burman\"" +
    "}"
    ;
private final static String modelloTest2 =
    "{\"series_name\":\"DIVID10\"\n" +
",\"frequency\":12,\"method\":\"TS\"\n" +
",\"spec\":\"RSA0\"\n" +
",\"preliminary.check\":true,\"estimate.from\":\"NA\"\n" +
",\"estimate.to\":\"NA\"\n" +
",\"estimate.first\":\"NA\"\n" +
",\"estimate.last\":\"NA\"\n" +
",\"estimate.exclFirst\":0,\"estimate.exclLast\":0,\"estimate.tol\":1e-07,\"estimate.eml\":true,\"estimate.urfinal\":0.96,\"transform.function\":\"Log\"\n" +
",\"transform.fct\":0.95,\"usrdef.outliersEnabled\":true,\"usrdef.outliersType\":\"LS\"\n" +
",\"usrdef.outliersDate\":\"2020-04-01\"\n" +
",\"usrdef.outliersCoef\":\"NA\"\n" +
",\"userdef.varFromFile\":true,\"userdef.varFromFile.infoList\":[{\"container\":\"tdmnw05.txt\"\n" +
",\"start\":\"2005-01-01\"\n" +
",\"n_var\":1}],\"usrdef.varEnabled\":true,\"usrdef.var\":\"NA\"\n" +
",\"usrdef.varType\":\"Calendar\"\n" +
",\"usrdef.varCoef\":\"NA\"\n" +
",\"tradingdays.mauto\":\"Unused\"\n" +
",\"tradingdays.pftd\":0.01,\"tradingdays.option\":\"UserDefined\"\n" +
",\"tradingdays.leapyear\":false,\"tradingdays.stocktd\":0,\"tradingdays.test\":\"None\"\n" +
",\"easter.type\":\"IncludeEasterMonday\"\n" +
",\"easter.julian\":false,\"easter.duration\":6,\"easter.test\":false,\"outlier.enabled\":false,\"outlier.from\":\"NA\"\n" +
",\"outlier.to\":\"NA\"\n" +
",\"outlier.first\":\"NA\"\n" +
",\"outlier.last\":\"NA\"\n" +
",\"outlier.exclFirst\":0,\"outlier.exclLast\":0,\"outlier.ao\":false,\"outlier.tc\":false,\"outlier.ls\":false,\"outlier.so\":false,\"outlier.usedefcv\":true,\"outlier.cv\":3.5,\"outlier.eml\":false,\"outlier.tcrate\":0.7,\"automdl.enabled\":false,\"automdl.acceptdefault\":false,\"automdl.cancel\":0.05,\"automdl.ub1\":0.97,\"automdl.ub2\":0.91,\"automdl.armalimit\":1,\"automdl.reducecv\":0.12,\"automdl.ljungboxlimit\":0.95,\"automdl.compare\":false,\"arima.mu\":false,\"arima.p\":0,\"arima.d\":1,\"arima.q\":1,\"arima.bp\":0,\"arima.bd\":1,\"arima.bq\":1,\"arima.coefEnabled\":false,\"arima.coef\":\"NA\"\n" +
",\"arima.coefType\":\"NA\"\n" +
",\"fcst.horizon\":-2,\"seats.predictionLength\":-1,\"seats.approx\":\"Legacy\"\n" +
",\"seats.trendBoundary\":0.5\n" +
",\"seats.seasdBoundary\":0.8\n" +
",\"seats.seasdBoundary1\":0.8\n" +
",\"seats.seasTol\":2\n" +
",\"seats.maBoundary\":0.95\n" +
",\"seats.method\":\"Burman\"\n" +
"}";
}

