package src;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
/*import ec.satoolkit.tramoseats.TramoSeatsSpecification;
import ec.satoolkit.x13.X13Specification;
import ec.satoolkit.seats.SeatsSpecification;
import ec.tstoolkit.modelling.DefaultTransformationType;
import ec.tstoolkit.modelling.arima.tramo.ArimaSpec;
import ec.tstoolkit.modelling.arima.tramo.AutoModelSpec;
import ec.tstoolkit.modelling.arima.tramo.EasterSpec;
import ec.tstoolkit.modelling.arima.tramo.EstimateSpec;
import ec.tstoolkit.modelling.arima.tramo.OutlierSpec;
import ec.tstoolkit.modelling.arima.tramo.TradingDaysSpec;
import ec.tstoolkit.modelling.arima.tramo.TransformSpec;
import ec.tstoolkit.timeseries.Day;
import ec.tstoolkit.timeseries.TsPeriodSelector;
import ec.tstoolkit.timeseries.regression.OutlierType;*/
import java.util.List;


public class DestSpecificationsModel {
 
    //private TramoSeatsSpecification tsSpec=null;
    //private X13Specification x13Spec=null;
    
    @JsonProperty("series_name")
    private String seriesName;
    @JsonProperty("frequency")
    private int frequency;
    @JsonProperty("method")
    private String method;
    @JsonProperty("userdef.varFromFile")
    private boolean userdefVarFromFile;
    @JsonProperty("userdef.varFromFile.infoList")
    private List<DestSpecVarFromFileInfo> userDefVarFileInfo;
    @JsonProperty("spec")
    private String spec;
    @JsonProperty("preliminary.check")
    private boolean preliminaryCheck;
    @JsonProperty("estimate.from")
    private String estimateFrom;
    @JsonProperty("estimate.to")
    private String estimateTo;
    @JsonProperty("estimate.first")
    private String estimateFirst;
    private int estimateFirstN;
    @JsonProperty("estimate.last")
    private String estimateLast;
    private int estimateLastN;
    @JsonProperty("estimate.exclFirst")
    private int estimateExclFirst;
    @JsonProperty("estimate.exclLast")
    private int estimateExclLast;
    @JsonProperty("estimate.tol")
    private double estimateTol;
    @JsonProperty("estimate.eml")
    private boolean estimateEml;
    @JsonProperty("estimate.urfinal") //<<---- dove si setta?
    private double estimateUrfinal;
    @JsonProperty("transform.function")
    private String transformFunction;
    @JsonProperty("transform.fct")
    private double transformFct;
    @JsonProperty("usrdef.outliersEnabled")
    private boolean usrdefOutliersEnabled;
    @JsonProperty("usrdef.outliersType")
    private List<String> usrdefOutliersType;
    @JsonProperty("usrdef.outliersDate")
    private List<String> usrdefOutliersDate;
    @JsonProperty("usrdef.outliersCoef")
    private List<String> usrdefOutliersCoef; //Alessandro
    @JsonProperty("usrdef.varEnabled")
    private boolean usrdefVarEnabled;
    @JsonProperty("usrdef.var")
    private String usrdefVar;
    @JsonProperty("usrdef.varType")
    private List<String> usrdefVarType;
    @JsonProperty("usrdef.varCoef")
    private List<String> usrdefVarCoef; //Alessandro
    @JsonProperty("tradingdays.mauto")
    private String tradingdaysMauto;
    @JsonProperty("tradingdays.pftd")
    private double tradingdaysPftd;
    @JsonProperty("tradingdays.option") //<<---- dove si setta?
    private String tradingdaysOption;
    @JsonProperty("tradingdays.leapyear")
    private boolean tradingdaysLeapyear;
    @JsonProperty("tradingdays.stocktd")
    private int tradingdaysStocktd;
    @JsonProperty("tradingdays.test")
    private String tradingdaysTest;
    private boolean tradingdaysTestB;
    @JsonProperty("easter.type")
    private String easterType;
    @JsonProperty("easter.julian")
    private boolean easterJulian;
    @JsonProperty("easter.duration")
    private int easterDuration;
    @JsonProperty("easter.test")
    private boolean easterTest;
    @JsonProperty("outlier.enabled")
    private boolean outlierEnabled;
    @JsonProperty("outlier.from")
    private String outlierFrom;
    @JsonProperty("outlier.to")
    private String outlierTo;
    @JsonProperty("outlier.first")
    private String outlierFirst;
    private int outlierFirstN;
    @JsonProperty("outlier.last")
    private String outlierLast;
    private int outlierLastN;
    @JsonProperty("outlier.exclFirst")
    private int outlierExclFirst;
    @JsonProperty("outlier.exclLast")
    private int outlierExclLast;
    @JsonProperty("outlier.ao")
    private boolean outlierAo;
    @JsonProperty("outlier.tc")
    private boolean outlierTc;
    @JsonProperty("outlier.ls")
    private boolean outlierLs;
    @JsonProperty("outlier.so")
    private boolean outlierSo;
    @JsonProperty("outlier.usedefcv") //<<---- dove si setta?
    private boolean outlierUsedefcv;
    @JsonProperty("outlier.cv")
    private double outlierCv;
    @JsonProperty("outlier.eml")
    private boolean outlierEml;
    @JsonProperty("outlier.tcrate") //<<---- dove si setta?
    private double outlierTcrate;
    @JsonProperty("automdl.enabled")
    private boolean automdlEnabled;
    @JsonProperty("automdl.acceptdefault")
    private boolean automdlAcceptdefault;
    @JsonProperty("automdl.cancel")
    private double automdlCancel;
    @JsonProperty("automdl.ub1")
    private double automdlUb1;
    @JsonProperty("automdl.ub2")
    private double automdlUb2;
    @JsonProperty("automdl.armalimit") //<<---- dove si setta?
    private double automdlArmalimit;
    @JsonProperty("automdl.reducecv")
    private double automdlReducecv;
    @JsonProperty("automdl.ljungboxlimit")
    private double automdlLjungboxlimit;
    @JsonProperty("automdl.compare")
    private boolean automdlCompare;
    @JsonProperty("arima.mu")
    private boolean arimaMu;
    @JsonProperty("arima.p")
    private int arimaP;
    @JsonProperty("arima.d")
    private int arimaD;
    @JsonProperty("arima.q")
    private int arimaQ;
    @JsonProperty("arima.bp")
    private int arimaBP;
    @JsonProperty("arima.bd")
    private int arimaBD;
    @JsonProperty("arima.bq")
    private int arimaBQ;
    @JsonProperty("arima.coefEnabled") //<<---- dove si setta?
    private boolean arimaCoefEnabled;
    @JsonProperty("arima.coef") //<<---- dove si setta?
    private List<String> arimaCoef;
    @JsonProperty("arima.coefType") //<<---- dove si setta?
    private List<String> arimaCoefType;
    @JsonProperty("fcst.horizon") //<<---- dove si setta?
    private double fcstHorizon;
    @JsonProperty("seats.predictionLength")
    private int seatsPredictionLength;
    @JsonProperty("seats.approx")
    private String seatsApprox;
    @JsonProperty("seats.trendBoundary")
    private double seatsTrendBoundary;
    @JsonProperty("seats.seasdBoundary")
    private double seatsSeasdBoundary;
    @JsonProperty("seats.seasdBoundary1")
    private double seatsSeasdBoundary1;
    @JsonProperty("seats.seasTol")
    private double seatsSeasTol;
    @JsonProperty("seats.maBoundary") //<<---- dove si setta?
    private double seatsMaBoundary;
    @JsonProperty("seats.method")
    private String seatsMethod;
    
    @JsonCreator
    public DestSpecificationsModel(
            @JsonProperty("method") String method,
            @JsonProperty("spec") String spec
    ){
        super();
        this.method=method;
        this.spec=spec;
        switch (this.method) {
         case "TS":
             //this.tsSpec=TramoSeatsSpecification.fromString(spec);
             break;
         case "X":
             //this.x13Spec=X13Specification.fromString(spec);
             break;
         default:
             //this.tsSpec=TramoSeatsSpecification.fromString(spec);
        }
    }
    
    private boolean isNull(String valore) {
        return (valore==null || valore.trim().isEmpty() || "NA".equals(valore.trim().toUpperCase()));
    }

    /*public TramoSeatsSpecification getTsSpec() {
        return tsSpec;
    }

    public X13Specification getX13Spec() {
        return x13Spec;
    }*/
    
    public void setupModel() {
        switch (this.method) {
         case "TS":
             setupTSmodel();
             break;
         case "X":
             setupX13model();
             break;
         default:
             setupTSmodel();
        }        
    }
    
    private void setupTSmodel() {
        /*setTransform();
        setEstimate();
        setTradingDays();
        setEaster();
        setOutliers();
        setAutoModeling();
        setArima();
        setSeats();*/
    }
    
    private void setupX13model() {
        
    }
    
    /*private void setTransform(){
        TransformSpec tf=tsSpec.getTramoSpecification().getTransform();
        if (tf==null){
            tf=new TransformSpec();
            tsSpec.getTramoSpecification().setTransform(tf);
        }
        tf.setFunction(DefaultTransformationType.valueOf(transformFunction));
        tf.setFct(transformFct);
        tf.setPreliminaryCheck(preliminaryCheck);
    }
    private void setEstimate() {
        EstimateSpec espec = tsSpec.getTramoSpecification().getEstimate();
        if (espec==null) {
            espec=new EstimateSpec();
            tsSpec.getTramoSpecification().setEstimate(espec);
        }
        /*
"estimate.urfinal":0.96,
        * /
        espec.setTol(estimateTol);
        espec.setEML(estimateEml);
        try {
            if (!isNull(estimateFrom)&&!isNull(estimateTo)) {
                TsPeriodSelector period=new TsPeriodSelector();
                Day from = Day.fromString(estimateFrom);
                Day to = Day.fromString(estimateTo);
                period.between(from, to);
                period.excluding(estimateExclFirst, estimateExclLast);
                period.first(estimateFirstN);
                period.last(estimateLastN);
                espec.setSpan(period);
            }
        } catch (Exception e) {
        }
    }
    private void setTradingDays(){
        TradingDaysSpec tdspec = tsSpec.getTramoSpecification().getRegression().getCalendar().getTradingDays();
        if (tdspec==null) {
            tdspec=new TradingDaysSpec();
            tsSpec.getTramoSpecification().getRegression().getCalendar().setTradingDays(tdspec);
        }
        /*
"tradingdays.option":"None",
        * /
        tdspec.setAutomaticMethod(TradingDaysSpec.AutoMethod.valueOf(tradingdaysMauto));
        tdspec.setProbabibilityForFTest(tradingdaysPftd);
        tdspec.setLeapYear(tradingdaysLeapyear);
        tdspec.setStockTradingDays(tradingdaysStocktd);
        tdspec.setTest(tradingdaysTestB);
    }
    private void setEaster() {
        EasterSpec ieast = tsSpec.getTramoSpecification().getRegression().getCalendar().getEaster();
        if (ieast==null) {
            ieast=new EasterSpec();
            tsSpec.getTramoSpecification().getRegression().getCalendar().setEaster(ieast);
        }
        ieast.setOption(EasterSpec.Type.valueOf(easterType));
        ieast.setDuration(easterDuration);
        ieast.setJulian(easterJulian);
        ieast.setTest(easterTest);
        
    }
    private void setOutliers() {

        if (outlierEnabled) {
            OutlierSpec o = tsSpec.getTramoSpecification().getOutliers();
            if (o==null) {
                o=new OutlierSpec();
                tsSpec.getTramoSpecification().setOutliers(o);
            }
    /*
"outlier.tcrate":0.7
"outlier.usedefcv":true/false
            * /
            if(outlierAo) o.add(OutlierType.AO);
            if(outlierTc) o.add(OutlierType.TC);
            if(outlierLs) o.add(OutlierType.LS);
            if(outlierSo) o.add(OutlierType.SO);
            o.setEML(outlierEml);
            o.setCriticalValue(outlierCv);
            //o.setDeltaTC(outlierTcrate);
            try {
                if (!isNull(outlierFrom)&&!isNull(outlierTo)) {
                    TsPeriodSelector period=new TsPeriodSelector();
                    Day from = Day.fromString(outlierFrom);
                    Day to = Day.fromString(outlierTo);
                    period.between(from, to);
                    period.excluding(outlierExclFirst, outlierExclLast);
                    period.first(outlierFirstN);
                    period.last(outlierLastN);
                    o.setSpan(period);
                }
            } catch (Exception e) {
            }
        }
    }
    private void setAutoModeling(){
        AutoModelSpec aspec = tsSpec.getTramoSpecification().getAutoModel();
        if (aspec==null) {
            aspec=new AutoModelSpec();
            tsSpec.getTramoSpecification().setAutoModel(aspec);
        }
        /*
"automdl.armalimit":1, 
"automdl.reducecv":0.12, 
"automdl.ljungboxlimit":0.95, 

        * /
        aspec.setEnabled(automdlEnabled);
        aspec.setAcceptDefault(automdlAcceptdefault);
        aspec.setCancel(automdlCancel);
        aspec.setUb1(automdlUb1);
        aspec.setUb2(automdlUb2);
        aspec.setAmiCompare(automdlCompare);
        //aspec.setPc(double value);
        //aspec.setPcr(double value);
        //aspec.setTsig(double value);
    }
    private void setArima() {
        ArimaSpec aspec = tsSpec.getTramoSpecification().getArima();
        if (aspec==null) {
            aspec=new ArimaSpec();
            tsSpec.getTramoSpecification().setArima(aspec);
        }
/*
"arima.coefEnabled":true/false, 
"arima.coef":"NA" o vettore di coefficienti
"arima.coefType":"NA", o vettore di procedure di stima
* /
        aspec.setMean(arimaMu);
        aspec.setP(arimaP);
        aspec.setD(arimaD);
        aspec.setQ(arimaQ);
        aspec.setBD(arimaP);
        aspec.setBP(arimaBP);
        aspec.setBD(arimaBD);
        aspec.setBQ(arimaBQ);
    }
    private void setSeats(){
        SeatsSpecification sspec = tsSpec.getSeatsSpecification();
        if (sspec==null) {
            sspec=new SeatsSpecification();
            tsSpec.setSeatsSpecification(sspec);
        }
        /*"seats.maBoundary":0.95,* /
        sspec.setPredictionLength(seatsPredictionLength);
        sspec.setApproximationMode(SeatsSpecification.ApproximationMode.valueOf(seatsApprox));
        sspec.setTrendBoundary(seatsTrendBoundary);
        sspec.setSeasBoundary(seatsSeasdBoundary);
        sspec.setSeasBoundary1(seatsSeasdBoundary1);
        sspec.setSeasTolerance(seatsSeasTol);
        sspec.setMethod(SeatsSpecification.EstimationMethod.valueOf(seatsMethod));
    }*/
    
    public String getSeriesName() {
        return seriesName;
    }

    public void setSeriesName(String seriesName) {
        this.seriesName = seriesName;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public boolean isUserdefVarFromFile() {
        return userdefVarFromFile;
    }

    public void setUserdefVarFromFile(boolean userdefVarFromFile) {
        this.userdefVarFromFile = userdefVarFromFile;
    }

    public List<DestSpecVarFromFileInfo> getUserDefVarFileInfo() {
        return userDefVarFileInfo;
    }

    public void setUserDefVarFileInfo(List<DestSpecVarFromFileInfo> userDefVarFileInfo) {
        this.userDefVarFileInfo = userDefVarFileInfo;
    }

    public String getSpec() {
        return spec;
    }

    public void setSpec(String spec) {
        this.spec = isNull(spec)?null:spec;
    }

    public boolean isPreliminaryCheck() {
        return preliminaryCheck;
    }

    public void setPreliminaryCheck(boolean preliminaryCheck) {
        this.preliminaryCheck = preliminaryCheck;
    }

    public String getEstimateFrom() {
        return estimateFrom;
    }

    public void setEstimateFrom(String estimateFrom) {
        this.estimateFrom = isNull(estimateFrom)?null:estimateFrom;
    }

    public String getEstimateTo() {
        return estimateTo;
    }

    public void setEstimateTo(String estimateTo) {
        this.estimateTo = isNull(estimateTo)?null:estimateTo;
    }

    public int getEstimateFirst() {
        return estimateFirstN;
    }

    public void setEstimateFirst(String estimateFirst) {
        this.estimateFirst = estimateFirst;
        if (!isNull(estimateFirst)) this.estimateFirstN=Integer.parseInt(estimateFirst);
    }

    public int getEstimateLast() {
        return estimateLastN;
    }

    public void setEstimateLast(String estimateLast) {
        this.estimateLast = estimateLast;
        if (!isNull(estimateLast)) this.estimateLastN=Integer.parseInt(estimateLast);
    }

    public int getEstimateExclFirst() {
        return estimateExclFirst;
    }

    public void setEstimateExclFirst(int estimateExclFirst) {
        this.estimateExclFirst = estimateExclFirst;
    }

    public int getEstimateExclLast() {
        return estimateExclLast;
    }

    public void setEstimateExclLast(int estimateExclLast) {
        this.estimateExclLast = estimateExclLast;
    }

    public double getEstimateTol() {
        return estimateTol;
    }

    public void setEstimateTol(double estimateTol) {
        this.estimateTol = estimateTol;
    }

    public boolean isEstimateEml() {
        return estimateEml;
    }

    public void setEstimateEml(boolean estimateEml) {
        this.estimateEml = estimateEml;
    }

    public double getEstimateUrfinal() {
        return estimateUrfinal;
    }

    public void setEstimateUrfinal(double estimateUrfinal) {
        this.estimateUrfinal = estimateUrfinal;
    }

    public String getTransformFunction() {
        return transformFunction;
    }

    public void setTransformFunction(String transformFunction) {
        this.transformFunction = transformFunction;
    }

    public double getTransformFct() {
        return transformFct;
    }

    public void setTransformFct(double transformFct) {
        this.transformFct = transformFct;
    }

    public boolean isUsrdefOutliersEnabled() {
        return usrdefOutliersEnabled;
    }

    public void setUsrdefOutliersEnabled(boolean usrdefOutliersEnabled) {
        this.usrdefOutliersEnabled = usrdefOutliersEnabled;
    }

    public List<String> getUsrdefOutliersType() {
        return usrdefOutliersType;
    }

    public void setUsrdefOutliersType(List<String> usrdefOutliersType) {
        this.usrdefOutliersType = usrdefOutliersType;
    }

    public List<String> getUsrdefOutliersDate() {
        return usrdefOutliersDate;
    }

    public void setUsrdefOutliersDate(List<String> usrdefOutliersDate) {
        this.usrdefOutliersDate = usrdefOutliersDate;
    }

    public List<String> getUsrdefOutliersCoef() {
        return usrdefOutliersCoef;
    }

    public void setUsrdefOutliersCoef(List<String> usrdefOutliersCoef) {
        this.usrdefOutliersCoef = usrdefOutliersCoef;
    }

    public boolean isUsrdefVarEnabled() {
        return usrdefVarEnabled;
    }

    public void setUsrdefVarEnabled(boolean usrdefVarEnabled) {
        this.usrdefVarEnabled = usrdefVarEnabled;
    }

    public String getUsrdefVar() {
        return usrdefVar;
    }

    public void setUsrdefVar(String usrdefVar) {
        this.usrdefVar = usrdefVar;
    }

    public List<String> getUsrdefVarType() {
        return usrdefVarType;
    }

    public void setUsrdefVarType(List<String> usrdefVarType) {
        this.usrdefVarType = usrdefVarType;
    }

    public List<String> getUsrdefVarCoef() { //Alessandro
        return usrdefVarCoef;
    }

    public void setUsrdefVarCoef(List<String> usrdefVarCoef) { //Alessandro
        this.usrdefVarCoef = usrdefVarCoef;
    }

    public String getTradingdaysMauto() {
        return tradingdaysMauto;
    }

    public void setTradingdaysMauto(String tradingdaysMauto) {
        this.tradingdaysMauto = tradingdaysMauto;
    }

    public double getTradingdaysPftd() {
        return tradingdaysPftd;
    }

    public void setTradingdaysPftd(double tradingdaysPftd) {
        this.tradingdaysPftd = tradingdaysPftd;
    }

    public String getTradingdaysOption() {
        return tradingdaysOption;
    }

    public void setTradingdaysOption(String tradingdaysOption) {
        this.tradingdaysOption = tradingdaysOption;
    }

    public boolean isTradingdaysLeapyear() {
        return tradingdaysLeapyear;
    }

    public void setTradingdaysLeapyear(boolean tradingdaysLeapyear) {
        this.tradingdaysLeapyear = tradingdaysLeapyear;
    }

    public int getTradingdaysStocktd() {
        return tradingdaysStocktd;
    }

    public void setTradingdaysStocktd(int tradingdaysStocktd) {
        this.tradingdaysStocktd = tradingdaysStocktd;
    }

    public boolean isTradingdaysTest() {
        return tradingdaysTestB;
    }

    public void setTradingdaysTest(String tradingdaysTest) {
        this.tradingdaysTest = tradingdaysTest;
        tradingdaysTestB=(!isNull(tradingdaysTest) && "true".equals(tradingdaysTest.toLowerCase()));
    }

    public String getEasterType() {
        return easterType;
    }

    public void setEasterType(String easterType) {
        this.easterType = easterType;
    }

    public boolean isEasterJulian() {
        return easterJulian;
    }

    public void setEasterJulian(boolean easterJulian) {
        this.easterJulian = easterJulian;
    }

    public int getEasterDuration() {
        return easterDuration;
    }

    public void setEasterDuration(int easterDuration) {
        this.easterDuration = easterDuration;
    }

    public boolean isEasterTest() {
        return easterTest;
    }

    public void setEasterTest(boolean easterTest) {
        this.easterTest = easterTest;
    }

    public boolean isOutlierEnabled() {
        return outlierEnabled;
    }

    public void setOutlierEnabled(boolean outlierEnabled) {
        this.outlierEnabled = outlierEnabled;
    }

    public String getOutlierFrom() {
        return outlierFrom;
    }

    public void setOutlierFrom(String outlierFrom) {
        this.outlierFrom = isNull(outlierFrom)?null:outlierFrom;
    }

    public String getOutlierTo() {
        return outlierTo;
    }

    public void setOutlierTo(String outlierTo) {
        this.outlierTo = isNull(outlierTo)?null:outlierTo;
    }

    public int getOutlierFirst() {
        return outlierFirstN;
    }

    public void setOutlierFirst(String outlierFirst) {
        this.outlierFirst = outlierFirst;
        if (!isNull(outlierFirst)) this.outlierFirstN=Integer.parseInt(outlierFirst);
    }

    public int getOutlierLast() {
        return outlierLastN;
    }

    public void setOutlierLast(String outlierLast) {
        this.outlierLast = outlierLast;
        if (!isNull(outlierLast)) this.outlierLastN=Integer.parseInt(outlierLast);
    }

    public int getOutlierExclFirst() {
        return outlierExclFirst;
    }

    public void setOutlierExclFirst(int outlierExclFirst) {
        this.outlierExclFirst = outlierExclFirst;
    }

    public int getOutlierExclLast() {
        return outlierExclLast;
    }

    public void setOutlierExclLast(int outlierExclLast) {
        this.outlierExclLast = outlierExclLast;
    }

    public boolean isOutlierAo() {
        return outlierAo;
    }

    public void setOutlierAo(boolean outlierAo) {
        this.outlierAo = outlierAo;
    }

    public boolean isOutlierTc() {
        return outlierTc;
    }

    public void setOutlierTc(boolean outlierTc) {
        this.outlierTc = outlierTc;
    }

    public boolean isOutlierLs() {
        return outlierLs;
    }

    public void setOutlierLs(boolean outlierLs) {
        this.outlierLs = outlierLs;
    }

    public boolean isOutlierSo() {
        return outlierSo;
    }

    public void setOutlierSo(boolean outlierSo) {
        this.outlierSo = outlierSo;
    }

    public boolean isOutlierUsedefcv() {
        return outlierUsedefcv;
    }

    public void setOutlierUsedefcv(boolean outlierUsedefcv) {
        this.outlierUsedefcv = outlierUsedefcv;
    }

    public double getOutlierCv() {
        return outlierCv;
    }

    public void setOutlierCv(double outlierCv) {
        this.outlierCv = outlierCv;
    }

    public boolean isOutlierEml() {
        return outlierEml;
    }

    public void setOutlierEml(boolean outlierEml) {
        this.outlierEml = outlierEml;
    }

    public double getOutlierTcrate() {
        return outlierTcrate;
    }

    public void setOutlierTcrate(double outlierTcrate) {
        this.outlierTcrate = outlierTcrate;
    }

    public boolean isAutomdlEnabled() {
        return automdlEnabled;
    }

    public void setAutomdlEnabled(boolean automdlEnabled) {
        this.automdlEnabled = automdlEnabled;
    }

    public boolean isAutomdlAcceptdefault() {
        return automdlAcceptdefault;
    }

    public void setAutomdlAcceptdefault(boolean automdlAcceptdefault) {
        this.automdlAcceptdefault = automdlAcceptdefault;
    }

    public double getAutomdlCancel() {
        return automdlCancel;
    }

    public void setAutomdlCancel(double automdlCancel) {
        this.automdlCancel = automdlCancel;
    }

    public double getAutomdlUb1() {
        return automdlUb1;
    }

    public void setAutomdlUb1(double automdlUb1) {
        this.automdlUb1 = automdlUb1;
    }

    public double getAutomdlUb2() {
        return automdlUb2;
    }

    public void setAutomdlUb2(double automdlUb2) {
        this.automdlUb2 = automdlUb2;
    }

    public double getAutomdlArmalimit() {
        return automdlArmalimit;
    }

    public void setAutomdlArmalimit(double automdlArmalimit) {
        this.automdlArmalimit = automdlArmalimit;
    }

    public double getAutomdlReducecv() {
        return automdlReducecv;
    }

    public void setAutomdlReducecv(double automdlReducecv) {
        this.automdlReducecv = automdlReducecv;
    }

    public double getAutomdlLjungboxlimit() {
        return automdlLjungboxlimit;
    }

    public void setAutomdlLjungboxlimit(double automdlLjungboxlimit) {
        this.automdlLjungboxlimit = automdlLjungboxlimit;
    }

    public boolean isAutomdlCompare() {
        return automdlCompare;
    }

    public void setAutomdlCompare(boolean automdlCompare) {
        this.automdlCompare = automdlCompare;
    }

    public boolean isArimaMu() {
        return arimaMu;
    }

    public void setArimaMu(boolean arimaMu) {
        this.arimaMu = arimaMu;
    }

    public int getArimaP() {
        return arimaP;
    }

    public void setArimaP(int arimaP) {
        this.arimaP = arimaP;
    }

    public int getArimaD() {
        return arimaD;
    }

    public void setArimaD(int arimaD) {
        this.arimaD = arimaD;
    }

    public int getArimaQ() {
        return arimaQ;
    }

    public void setArimaQ(int arimaQ) {
        this.arimaQ = arimaQ;
    }

    public int getArimaBP() {
        return arimaBP;
    }

    public void setArimaBP(int arimaBP) {
        this.arimaBP = arimaBP;
    }

    public int getArimaBD() {
        return arimaBD;
    }

    public void setArimaBD(int arimaBD) {
        this.arimaBD = arimaBD;
    }

    public int getArimaBQ() {
        return arimaBQ;
    }

    public void setArimaBQ(int arimaBQ) {
        this.arimaBQ = arimaBQ;
    }

    public boolean isArimaCoefEnabled() {
        return arimaCoefEnabled;
    }

    public void setArimaCoefEnabled(boolean arimaCoefEnabled) {
        this.arimaCoefEnabled = arimaCoefEnabled;
    }

    public List<String> getArimaCoef() {
        return arimaCoef;
    }

    public void setArimaCoef(List<String> arimaCoef) {
        this.arimaCoef = arimaCoef;
    }

    public List<String> getArimaCoefType() {
        return arimaCoefType;
    }

    public void setArimaCoefType(List<String> arimaCoefType) {
        this.arimaCoefType = arimaCoefType;
    }

    public double getFcstHorizon() {
        return fcstHorizon;
    }

    public void setFcstHorizon(double fcstHorizon) {
        this.fcstHorizon = fcstHorizon;
    }

    public int getSeatsPredictionLength() {
        return seatsPredictionLength;
    }

    public void setSeatsPredictionLength(int seatsPredictionLength) {
        this.seatsPredictionLength = seatsPredictionLength;
    }

    public String getSeatsApprox() {
        return seatsApprox;
    }

    public void setSeatsApprox(String seatsApprox) {
        this.seatsApprox = seatsApprox;
    }

    public double getSeatsTrendBoundary() {
        return seatsTrendBoundary;
    }

    public void setSeatsTrendBoundary(double seatsTrendBoundary) {
        this.seatsTrendBoundary = seatsTrendBoundary;
    }

    public double getSeatsSeasdBoundary() {
        return seatsSeasdBoundary;
    }

    public void setSeatsSeasdBoundary(double seatsSeasdBoundary) {
        this.seatsSeasdBoundary = seatsSeasdBoundary;
    }

    public double getSeatsSeasdBoundary1() {
        return seatsSeasdBoundary1;
    }

    public void setSeatsSeasdBoundary1(double seatsSeasdBoundary1) {
        this.seatsSeasdBoundary1 = seatsSeasdBoundary1;
    }

    public double getSeatsSeasTol() {
        return seatsSeasTol;
    }

    public void setSeatsSeasTol(double seatsSeasTol) {
        this.seatsSeasTol = seatsSeasTol;
    }

    public double getSeatsMaBoundary() {
        return seatsMaBoundary;
    }

    public void setSeatsMaBoundary(double seatsMaBoundary) {
        this.seatsMaBoundary = seatsMaBoundary;
    }

    public String getSeatsMethod() {
        return seatsMethod;
    }

    public void setSeatsMethod(String seatsMethod) {
        this.seatsMethod = seatsMethod;
    }

}
