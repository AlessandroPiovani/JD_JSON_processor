package src;

import ec.satoolkit.seats.SeatsSpecification;
import ec.satoolkit.tramoseats.TramoSeatsSpecification;
import ec.tstoolkit.Parameter;
import ec.tstoolkit.ParameterType;
import ec.tstoolkit.algorithm.ProcessingContext;
import ec.tstoolkit.modelling.DefaultTransformationType;
import ec.tstoolkit.modelling.TsVariableDescriptor;
import ec.tstoolkit.modelling.arima.tramo.ArimaSpec;
import ec.tstoolkit.modelling.arima.tramo.AutoModelSpec;
import ec.tstoolkit.modelling.arima.tramo.EasterSpec;
import ec.tstoolkit.modelling.arima.tramo.EstimateSpec;
import ec.tstoolkit.modelling.arima.tramo.OutlierSpec;
import ec.tstoolkit.modelling.arima.tramo.RegressionSpec;
import ec.tstoolkit.modelling.arima.tramo.TradingDaysSpec;
import ec.tstoolkit.modelling.arima.tramo.TransformSpec;
import ec.tstoolkit.sarima.SarimaModel;
import ec.tstoolkit.sarima.SarmaSpecification;
import ec.tstoolkit.timeseries.Day;
import ec.tstoolkit.timeseries.TsPeriodSelector;
import ec.tstoolkit.timeseries.calendars.TradingDaysType;
import ec.tstoolkit.timeseries.regression.InterventionVariable;
import ec.tstoolkit.timeseries.regression.OutlierDefinition;
import ec.tstoolkit.timeseries.regression.OutlierType;
import ec.tstoolkit.timeseries.regression.Ramp;
//import ec.tstoolkit.timeseries.regression.Sequence;
import ec.tstoolkit.timeseries.regression.TsVariable;
import ec.tstoolkit.timeseries.regression.TsVariables;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.timeseries.simplets.TsFrequency;
import ec.tstoolkit.timeseries.simplets.TsPeriod;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import static java.util.Objects.isNull;
import java.util.logging.Level;
import java.util.logging.Logger;

import static src.ArimaSplitter.splitArimaCoefficients;

/**
 *
 * @author cazora apiovani
 */
public class TSmodelSetup {

    public static final String EXTERNAL = "external";
    private final DestSpecificationsModel model;
    private final TramoSeatsSpecification tsSpec;
    private final ProcessingContext context; // Alessandro    
    private String[] tdVarNames     = {}; //added
    private String[] usrDefVarNames = {}; //added
    private TsData tsData; //added

    //added
    public TsData getTsData() {
        return tsData;
    }

    //added
    public void setTsData(TsData tsData) {
        this.tsData = tsData;
    }

    public ProcessingContext getContext() {
        return context;
    }

    public DestSpecificationsModel getModel() {
        return model;
    }

    public TramoSeatsSpecification getTsSpec() {
        return tsSpec;
    }

    public TSmodelSetup(DestSpecificationsModel model, ProcessingContext context, String directoryPathExtReg, TsData tsData) {
        this.model = model;
        this.context = context;
        this.tsData=tsData;
        TsVariables vars = context.getTsVariables(EXTERNAL);
        if (vars == null) {
            context.getTsVariableManagers().set(EXTERNAL, new TsVariables());
        }

        if (model != null && model.getSpec() != null) {
            //String jsonString = model.getSpec();
            // Don't forget to clone the default spec
            this.tsSpec = TramoSeatsSpecification.fromString(model.getSpec()).clone();
            setupTSmodel(directoryPathExtReg);
        } else {
            // Don't forget to clone the default spec
            this.tsSpec = TramoSeatsSpecification.RSAfull.clone();
        }

    }

    private void setupTSmodel(String directoryPathExtReg) {
        setTransform();
        setEstimate();
        setTradingDays(directoryPathExtReg);
        setUserDefinedVariables(directoryPathExtReg); //Added by Alessandro
        setEaster();
        setOutliers();
        setAutoModeling();
        setArima();
        setSeats();
        setRamps(); // Alessandro, very last update.
        setInterventionVariables(); // Alessandro, very last update. 
        //EXAMPLE OF RAMPS FROM R: "[{\"start\":\"2020-01-01\",\"end\":\"2020-12-31\", \"fixed_coef\": 2},{\"start\":\"2008-01-01\",\"end\":\"2009-01-01\", \"fixed_coef\":\"NA\"}]" // fixed_coef is a double or "NA"
        //EXAMPLE of INTERVENTION VARS FROM R "[{\"delta\":1,\"delta_s\":1,\"seq\":[{\"start\":\"2001-01-01\",\"end\":\"2001-12-31\"}, \"D1DS\":true]},{\"delta\":0.75,\"delta_s\":0,\"seq\":[{\"start\":\"2004-01-01\",\"end\":\"2005-12-31\"}]\"D1DS\":false}]"
        
        fixOutliersAndVariablesCoefficients(); // to be completed
        fixDefaultJDplusCalendarCoefficients(); //Easter, JD+ automatic TDs and Leap Year
    }

    private void setTransform() {
        TransformSpec tf = tsSpec.getTramoSpecification().getTransform();
        if (tf == null) {
            tf = new TransformSpec();
            tsSpec.getTramoSpecification().setTransform(tf);
        }
        tf.setFunction(DefaultTransformationType.valueOf(model.getTransformFunction()));
        tf.setFct(model.getTransformFct());
        tf.setPreliminaryCheck(model.isPreliminaryCheck());
    }

    private void setEstimate() {
        EstimateSpec espec = tsSpec.getTramoSpecification().getEstimate();
        if (espec == null) {
            espec = new EstimateSpec();
            tsSpec.getTramoSpecification().setEstimate(espec);
        }
        /*
            "estimate.urfinal":0.96,
         */
        espec.setUbp(model.getEstimateUrfinal()); // Alessandro
        espec.setTol(model.getEstimateTol());
        espec.setEML(model.isEstimateEml());
        try {
            if (model.getEstimateFrom() != null && model.getEstimateTo() != null) {
                TsPeriodSelector period = new TsPeriodSelector();
                Day from = Day.fromString(model.getEstimateFrom());
                Day to = Day.fromString(model.getEstimateTo());
                period.between(from, to);
                period.excluding(model.getEstimateExclFirst(), model.getEstimateExclLast());
                period.first(model.getEstimateFirst());
                period.last(model.getEstimateLast());
                espec.setSpan(period);
            }
        } catch (Exception e) {
        }
    }

    private void setTradingDays(String directoryPathExtReg) {

        TradingDaysSpec tdspec = tsSpec.getTramoSpecification().getRegression().getCalendar().getTradingDays();
        if (tdspec == null) {
            tdspec = new TradingDaysSpec();
            tsSpec.getTramoSpecification().getRegression().getCalendar().setTradingDays(tdspec);
        }
        /*
    "tradingdays.option":"None",
         */
        //Alessandro
//        if(model.getSeriesName().equals("VATASA") || model.getSeriesName().equals("C_DEFL") || model.getSeriesName().equals("FATEXP_14"))
//        {
//            System.out.println("debug");
//        }    
        if (!isNull(model.getTradingdaysOption())) {
            if (model.getTradingdaysOption().equals("TradingDays")) {
                tdspec.setTradingDaysType(TradingDaysType.TradingDays);
            } else if (model.getTradingdaysOption().equals("WorkingDays")) {
                tdspec.setTradingDaysType(TradingDaysType.WorkingDays);
            } else if (model.getTradingdaysOption().equals("UserDefined")) {
                List<DestSpecVarFromFileInfo> userDefVarFileInfoList = model.getUserDefVarFileInfo();
                int idxUsrDefVarTypes = 0;
                List<String> usrdefVarTypes = model.getUsrdefVarType();
 
                TsVariables vars = context.getTsVariables(EXTERNAL);
                List<String> varNames = new ArrayList<>(); // AFTER
                for (DestSpecVarFromFileInfo usrDefVar : userDefVarFileInfoList) {
                    if (usrdefVarTypes.get(idxUsrDefVarTypes).equals("Calendar")) 
                    {
//                        List<String> varNames = new ArrayList<>(); // BEFORE
                        Map<String, TsData> usrDefVariables;
                        String varNameForDescriptor = usrDefVar.getContainer().substring(0, usrDefVar.getContainer().lastIndexOf('.')); 
                        if (!vars.contains(varNameForDescriptor) && !vars.contains(varNameForDescriptor + "_1"))
                        {
                            // not yet read
                            try {
                                String startDate = usrDefVar.getStart();
                                int startYear = Integer.parseInt(startDate.substring(0, 4));
                                int startMonth = Integer.parseInt(startDate.substring(5, 7));

                                //double[] extRegDataArray = ; // external regressor data
                                //TsData extRegData = new TsData(TsFrequency.Monthly, startYear, startMonth-1, extRegDataArray, true);
                                TsFrequency freq = TsFrequency.valueOf(model.getFrequency());
                                usrDefVariables = ExtRegDataReaderTSplus.readTsFile(directoryPathExtReg, usrDefVar.getContainer(), freq, startYear, startMonth - 1);
                                usrDefVar.setNumVar(usrDefVariables.size());
                                for (Map.Entry<String, TsData> variable : usrDefVariables.entrySet()) {
                                    String key = variable.getKey();
                                    TsData value = variable.getValue();
                                    TsVariable var = new TsVariable(key, value); // KEY AS A DESK FUNDAMENTAL TO PLOT THE NAME!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                                    var.setName(key); //asd
                                    //var.setName( EXTERNAL + '.' + key);
                                    vars.set(key, var);
                                    varNames.add(key);
                                    
                                }
                                System.out.print("External regressors loaded for Trading Days: ");
                                System.out.println(varNameForDescriptor);
                            } catch (IOException ex) {
                                Logger.getLogger(TSmodelSetup.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        } else if (usrDefVar.getNumVar() == 1) {
                            varNames.add(varNameForDescriptor);
                        } else {
                            for (int i = 1; i <= usrDefVar.getNumVar(); ++i) {
                                StringBuilder builder = new StringBuilder();
                                builder.append(varNameForDescriptor)
                                        .append('_').append(i);
                                varNames.add(builder.toString());
                            }
                        }
                        // BEFORE
                        //String[] vDescStr = varNames.toArray(String[]::new); // without EXTERNAL prefix the variable is not valid
                        String[] vDescStr = varNames.stream().map(name -> EXTERNAL + '.' +name).toArray(String[]::new);
                        tdspec.setUserVariables(vDescStr);
                        
                        this.tdVarNames = vDescStr; //ADDED
                    }
                    idxUsrDefVarTypes++;
                }
                //AFTER
//                String[] vDescStr = varNames.stream().map(name -> EXTERNAL + '.' + name).toArray(String[]::new);                
//                tdspec.setUserVariables(vDescStr);
            } else {
                if (!model.getTradingdaysOption().equals("None") && !model.getTradingdaysOption().equals("NA")) {
                    System.out.println("tradingdays.option field has an unknown value");
                    tdspec.setTradingDaysType(TradingDaysType.None);
                    // Stop the program and throw Exception?
                } else {
                    tdspec.setTradingDaysType(TradingDaysType.None);
                }
            }

        }
        // end Alessandro's part

        tdspec.setAutomaticMethod(TradingDaysSpec.AutoMethod.valueOf(model.getTradingdaysMauto()));
        tdspec.setProbabibilityForFTest(model.getTradingdaysPftd());
        tdspec.setLeapYear(model.isTradingdaysLeapyear());
        int w = model.getTradingdaysStocktd();
        if (w != 0) {
            tdspec.setStockTradingDays(w);
        }
        tdspec.setTest(model.isTradingdaysTest());
    }

    // "Added by Alessandro" block Begin
    private void setUserDefinedVariables(String directoryPathExtReg)
    {
        RegressionSpec regSpec = tsSpec.getTramoSpecification().getRegression();
        if (regSpec == null) 
        {
            regSpec = new RegressionSpec();
            tsSpec.getTramoSpecification().setRegression(regSpec);
        }

        List<DestSpecVarFromFileInfo> userDefVarFileInfoList = model.getUserDefVarFileInfo();
        int idxUsrDefVarTypes = 0;
        List<String> usrdefVarTypes = model.getUsrdefVarType();

        if (isNull(model.getUserDefVarFileInfo())) 
        {
            return;
        }

        List<String> varDescrNames = new ArrayList<String>();
        for (DestSpecVarFromFileInfo usrDefVar : userDefVarFileInfoList) 
        {

            if (!usrdefVarTypes.get(idxUsrDefVarTypes).equals("Calendar")) //Calendar are for trading days
            {
                LinkedHashMap<String, TsData> usrDefVariables = null;  // NOTA: LinkedHashMap invece di MAP anche nel ExtRegDataReader !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

                try 
                {
                    String startDate = usrDefVar.getStart();
                    if (!isValidDate(startDate)) {
                        System.out.println(startDate + " is not correctly formatted (yyyy-MM-dd)");
                        // exception (?)
                    }
                    int startYear  = Integer.parseInt(startDate.substring(0, 4)); //YYYY
                    int startMonth = Integer.parseInt(startDate.substring(5, 7)); //MM

                    TsFrequency freq = TsFrequency.valueOf(model.getFrequency());

                    usrDefVariables = ExtRegDataReaderTSplus.readTsFile(directoryPathExtReg, usrDefVar.getContainer(), freq, startYear, startMonth - 1);

                } catch (IOException ex) {
                    Logger.getLogger(TSmodelSetup.class
                            .getName()).log(Level.SEVERE, null, ex);
                }

                TsVariables vars = new TsVariables();
                //List<TsVariableDescriptor> varsDescriptor = new ArrayList<TsVariableDescriptor>();
                String varNameForDescriptor = usrDefVar.getContainer().substring(0, usrDefVar.getContainer().lastIndexOf('.'));
                List<String> varNames = new ArrayList<String>();

                for (Map.Entry<String, TsData> variable : usrDefVariables.entrySet())
                {
                    String key = variable.getKey();
                    TsData value = variable.getValue();

                    TsVariable var = new TsVariable(value);
                    var.setName(key);
                    vars.set(key, var);
                    varNames.add(key);

                }
                // get filename without extension, to be used as vars Name
                if(!this.context.getTsVariableManagers().contains(varNameForDescriptor))
                {
                    this.context.getTsVariableManagers().set(varNameForDescriptor, vars);
                    System.out.print("External regressors loaded as UsrDef variables: ");
                    System.out.println(varNameForDescriptor);
                }    
                
                TsVariableDescriptor vDesc = new TsVariableDescriptor(); //asd
                int idx = 0;
                for (String vName : varNames) 
                {
                    
                    vDesc.setName(varNameForDescriptor + "." + vName);
                    varDescrNames.add(varNameForDescriptor + "." + vName);
                    
                    String type = model.getUsrdefVarType().get(idx);
                    if (type != null) {
                        vDesc.setEffect(TsVariableDescriptor.UserComponentType.valueOf(type));
                    }
                    idx++;
                }
                this.tsSpec.getTramoSpecification().getRegression().add(vDesc);
                
            }
            idxUsrDefVarTypes++;
        }
        this.usrDefVarNames = varDescrNames.toArray(new String[0]);
    }

    // End "Added by Alessandro" block
    private void setEaster() {
        EasterSpec ieast = tsSpec.getTramoSpecification().getRegression().getCalendar().getEaster();
        if (ieast == null) {
            ieast = new EasterSpec();
            tsSpec.getTramoSpecification().getRegression().getCalendar().setEaster(ieast);
        }
        ieast.setOption(EasterSpec.Type.valueOf(model.getEasterType()));
        ieast.setDuration(model.getEasterDuration());
        ieast.setJulian(model.isEasterJulian());
        ieast.setTest(model.isEasterTest());

    }

    private void setOutliers() {
        // Alessandro
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date;
        String type;

        if (!(model.getUsrdefOutliersDate().size() == model.getUsrdefOutliersType().size())) {
            System.out.println("Number of userDefined outliers dates is different from the number of outlierTypes");
            // throw exceprtion (?)
        }
        if (!Objects.isNull(model.getUsrdefOutliersDate()) && !(model.getUsrdefOutliersDate().size() == 0) && !model.getUsrdefOutliersDate().get(0).equals("NA")) {
            for (int i = 0; i < model.getUsrdefOutliersDate().size(); i++) {
                try {
                    date = dateFormat.parse(model.getUsrdefOutliersDate().get(i));
                    TsPeriod outlierPeriod = new TsPeriod(TsFrequency.valueOf(model.getFrequency()), date);

                    type = model.getUsrdefOutliersType().get(i);
                    tsSpec.getTramoSpecification().getRegression().add(new OutlierDefinition(outlierPeriod, type));

                } catch (ParseException ex) {
                    Logger.getLogger(TSmodelSetup.class
                            .getName()).log(Level.SEVERE, "Error in user defined outliers", ex);
                }
            }
        }
        // end Alessandro's part

        if (model.isOutlierEnabled()) 
        {
            OutlierSpec o = tsSpec.getTramoSpecification().getOutliers();
            if (o == null) {
                o = new OutlierSpec();
                tsSpec.getTramoSpecification().setOutliers(o);
            }
            /*
    "outlier.tcrate":0.7
    "outlier.usedefcv":true/false
             */
            //Alessandro's block
            o.setDeltaTC(model.getOutlierTcrate()); //Alessandro
            if (!model.isOutlierUsedefcv())
            {
                if(model.getOutlierCv()!=0.0 && !("NA".equals(model.getOutlierCv())))
                {
                    o.setCriticalValue(model.getOutlierCv());
                }
                else
                {   
                } 
            } else 
            {
                // aggiungere logica per il calcolo del CV
                double CV;
                if(model.getOutlierFrom()!=null && !model.getOutlierFrom().equals("NA"))
                {
                   SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");        
                   Date date_from=new Date();
                    try {
                        date_from = simpleDateFormat.parse(model.getOutlierFrom());
                    } catch (ParseException ex) {
                        Logger.getLogger(TSmodelSetup.class.getName()).log(Level.SEVERE, null, ex);
                    }
                   
                   TsPeriod periodFrom = new TsPeriod(tsData.getFrequency(), date_from);
                   
                   int nPeriods=tsData.getEnd().minus(periodFrom);
                   CV = calculateCriticalValue(nPeriods);
                } else
                {
                    int nObsTs= tsData.getObsCount();
                    CV = calculateCriticalValue(nObsTs);
                }    
                

                o.setCriticalValue(CV);
//                o.setCriticalValue(3.5);
            }
            // end Alessandro's block
            // Alessandro: Added !o.contains(OutlierType.AO) && in every if
            if (!o.contains(OutlierType.AO) && model.isOutlierAo()) {
                o.add(OutlierType.AO);
            }
            if (!o.contains(OutlierType.TC) && model.isOutlierTc()) {
                o.add(OutlierType.TC);
            }
            if (!o.contains(OutlierType.LS) && model.isOutlierLs()) {
                o.add(OutlierType.LS);
            }
            if (!o.contains(OutlierType.SO) && model.isOutlierSo()) {
                o.add(OutlierType.SO);
            }
            // Alessandro's block begin
            if (o.contains(OutlierType.AO) && !model.isOutlierAo()) {
                o.remove(OutlierType.AO);
            }
            if (o.contains(OutlierType.TC) && !model.isOutlierTc()) {
                o.remove(OutlierType.TC);
            }
            if (o.contains(OutlierType.LS) && !model.isOutlierLs()) {
                o.remove(OutlierType.LS);
            }
            if (o.contains(OutlierType.SO) && !model.isOutlierSo()) {
                o.remove(OutlierType.SO);
            }

            // end Alessandro's block
            o.setEML(model.isOutlierEml());
            //o.setCriticalValue(model.getOutlierCv()); //placed before into if that controls if it should be read

            try {
                if (model.getOutlierFrom() != null && model.getOutlierTo() != null) {
                    TsPeriodSelector period = new TsPeriodSelector();
                    Day from = Day.fromString(model.getOutlierFrom());
                    Day to = Day.fromString(model.getOutlierTo());
                    period.between(from, to);
                    period.excluding(model.getOutlierExclFirst(), model.getOutlierExclLast());
                    period.first(model.getOutlierFirst());
                    period.last(model.getOutlierLast());
                    o.setSpan(period);
                }
            } catch (Exception e) {
                System.out.println("Problem in SetOutlier function");
            }
        }
    }

    //Method added by Alessandro
    private void fixOutliersAndVariablesCoefficients() {
//   E.G. names: "Java-Object{{AO (2020-03-01)=[D@4ff3af97, AO (2020-06-01)=[D@5680f009, AO (2007-12-01)=[D@3a4e6da6, _ts_external_3@LYM_02_0=[D@73e0c775, _ts_external_2@TDU02M_0=[D@213d5189}}"
//   return "_ts_external_3@LYM_02_0" "_ts_external_2@TDU02M_0"

        List<String> usrDefOutlierCoefs = model.getUsrdefOutliersCoef();
        List<String> usrDefVarCoefs = model.getUsrdefVarCoef();
        List<String> usrDefVarTypes = model.getUsrdefVarType();
        List<Double> tdCoef         = getUsrDefCoefTd(usrDefVarCoefs, usrDefVarTypes);
        List<Double> usrDefVarsCoef = getUsrDefCoefVars(usrDefVarCoefs, usrDefVarTypes);

        TsFrequency f = TsFrequency.valueOf(model.getFrequency());
        //String[] varNames = tsSpec.getTramoSpecification().getRegression().getRegressionVariableNames(f);
        //String[] varShortNames = tsSpec.getTramoSpecification().getRegression().getRegressionVariableShortNames(f);
        
//        for(int k=0; k<tsSpec.getTramoSpecification().getRegression().getUserDefinedVariablesCount();k++)
//        {
//            TsVariableDescriptor vdesc = tsSpec.getTramoSpecification().getRegression().getUserDefinedVariable(k);
//            System.out.println("");
//        }
                
        if (usrDefOutlierCoefs != null && !usrDefOutlierCoefs.isEmpty())
        {
            boolean naField = (usrDefOutlierCoefs.size()==1 && usrDefOutlierCoefs.get(0).equals("NA"));

            if(!naField)
            {
                OutlierDefinition[] outs = tsSpec.getTramoSpecification().getRegression().getOutliers();
                int i = 0;
                for (OutlierDefinition outDef : outs)
                {
                    String outName = outDef.toString();
                    outName=transformOutlierStringFromOutlierDef(outName);

                    //if(!usrDefOutlierCoefs.get(i).equals("NA"))
                    //{
                        double[] outVal = {Double.parseDouble(usrDefOutlierCoefs.get(i))};
                        if(outVal[0] != 0)
                        {
                            tsSpec.getTramoSpecification().getRegression().setFixedCoefficients(outName, outVal);
                        }      
                        i++;
                    //}    

                }
            }    

        }

//        int nOuts = tsSpec.getTramoSpecification().getRegression().getOutliersCount(); // the first nOuts variables are outliers
        
        //String[] tdVarNames=filterStringsStartingWithTd(varNames);
        
        
        int j=0;
        for(String vName : this.tdVarNames)
        {   
//            vName = vName.replaceFirst("^(td\\|external@)td\\|", "$1"); // removes "td|" after @
//            vName = vName.replaceFirst("^td\\|external@", "external@").replace("@", ".");
//            vName = vName.replaceFirst("^td\\|external@td\\|", "");
//            vName = vName.replaceAll("^td\\|", "")  // Rimuove "td|" all'inizio
//             .replaceAll("td\\|", "")    // Rimuove "td|" dopo la "@"
//             .replaceAll("@", ".");      // Sostituisce "@" con "."
//            vName = vName.replaceAll("^td\\|", "")  // Rimuove "td|" all'inizio
//            .replaceAll("td\\|", "");    // Rimuove "td|" dopo la "@"
//            vName = "td|" + vName.replaceAll(".", "@");
            vName = "td|"+vName.replaceAll("\\.", "@");
            
            //double[] varVal = {Double.parseDouble(usrDefVarCoefs.get(j))};
            if(j<tdCoef.size())
            {    
                double varVal = tdCoef.get(j);
                
                if(varVal!=0)
                {
                    tsSpec.getTramoSpecification().getRegression().setFixedCoefficients(vName, new double[]{varVal});
                }    
                
                //tsSpec.getTramoSpecification().getRegression().setCoefficients(vName, new double[]{varVal});
                

                j++;
            }

        }
        
        j=0;
        for(String vName : this.usrDefVarNames)
        {   
            vName = vName.replaceAll("\\.", "@");
            
            //double[] varVal = {Double.parseDouble(usrDefVarCoefs.get(j))};
            if(j<usrDefVarsCoef.size())
            {    
                double varVal = usrDefVarsCoef.get(j);
                
                if(varVal!=0)
                {
                    tsSpec.getTramoSpecification().getRegression().setFixedCoefficients(vName, new double[]{varVal});
                }    
                
                //tsSpec.getTramoSpecification().getRegression().setCoefficients(vName, new double[]{varVal});
                

                j++;
            }

        }

        
        
    }

    private void setAutoModeling() {
        AutoModelSpec aspec = tsSpec.getTramoSpecification().getAutoModel();
        if (aspec == null) {
            aspec = new AutoModelSpec();
            tsSpec.getTramoSpecification().setAutoModel(aspec);
        }
        /*
    "automdl.armalimit":1, 
    "automdl.reducecv":0.12, 
    "automdl.ljungboxlimit":0.95, 

         */
        aspec.setTsig(model.getAutomdlArmalimit()); //Alessandro
        aspec.setPc(model.getAutomdlReducecv()); //Alessandro
        aspec.setPcr(model.getAutomdlLjungboxlimit()); //Alessandro
        aspec.setEnabled(model.isAutomdlEnabled());
        aspec.setAcceptDefault(model.isAutomdlAcceptdefault());
        aspec.setCancel(model.getAutomdlCancel());
        aspec.setUb1(model.getAutomdlUb1());
        aspec.setUb2(model.getAutomdlUb2());
        aspec.setAmiCompare(model.isAutomdlCompare());

    }

    private void setArima() 
    {
        String seriesName = model.getSeriesName();

        
        
        ArimaSpec aspec = tsSpec.getTramoSpecification().getArima();
               
        if (aspec == null) {
            aspec = new ArimaSpec();
            tsSpec.getTramoSpecification().setArima(aspec);
        }
        /*
    "arima.coefEnabled":true/false, 
    "arima.coef":"NA" o vettore di coefficienti
    "arima.coefType":"NA", o vettore di procedure di stima
         */
        //begin Alessandro
        List<String> arimaCoefs = model.getArimaCoef();
        List<String> arimaCoefTypes = model.getArimaCoefType();
        int p = model.getArimaP();
        int q = model.getArimaQ();
        int bp = model.getArimaBP();
        int bq = model.getArimaBQ();

        
        aspec.setMean(model.isArimaMu());
        aspec.setP(p);
        aspec.setD(model.getArimaD());
        aspec.setQ(q);
        aspec.setBP(bp);
        aspec.setBD(model.getArimaBD());
        aspec.setBQ(bq);
            
        
        if(arimaCoefs!=null && !(  "NA".equals(arimaCoefs.get(0)) && arimaCoefs.size()==1  ))
        {
            
            
            List<List<ArimaCoefficient>> coefsLists = splitArimaCoefficients(arimaCoefs, arimaCoefTypes, p, bp, q, bq);

            List<ArimaCoefficient> pCoefs  = coefsLists.get(0);
            List<ArimaCoefficient> qCoefs = coefsLists.get(1);
            List<ArimaCoefficient> bpCoefs  = coefsLists.get(2);
            List<ArimaCoefficient> bqCoefs = coefsLists.get(3);

            
            if(p>0)
            {   
                int phiIdx=0;
                Parameter[] phiCoefficients = new Parameter[p];
                for(int i = 0; i < pCoefs.size(); i++) 
                {
                    phiIdx = pCoefs.get(i).getIndex()-1; // set directly to i if you want the inverse order
                    
                    phiCoefficients[phiIdx] = new Parameter();
                    
                    String phiType = pCoefs.get(i).getType();
                    if(phiType.equals("Fixed"))
                    {
                        if(!pCoefs.get(i).getCoef().equals("NA"))
                        {
                            double coefficient = Double.parseDouble(pCoefs.get(i).getCoef());
                                                        
                            phiCoefficients[phiIdx].setType(ParameterType.Fixed);
                            phiCoefficients[phiIdx].setValue(coefficient);
                            //phiCoefficients[phiIdx].setStde(0.0);
                        }
                        else
                        {
                            phiCoefficients[phiIdx].setType(ParameterType.Undefined);
                        }
                        
                    }
                    else if(phiType.equals("Initial"))
                    {
                        phiCoefficients[phiIdx].setType(ParameterType.Initial);
                        // what is it?
                    }else if(phiType.equals("Undefined")) 
                    {
                        phiCoefficients[phiIdx].setType(ParameterType.Undefined);
                    }
                }
                aspec.setPhi(phiCoefficients); 

            }

            if(bp>0)
            {     
                int bPhiIdx=0;
                Parameter[] bPhiCoefficients = new Parameter[bp];
                for(int i = 0; i < bpCoefs.size(); i++) 
                {
                    bPhiIdx = bpCoefs.get(i).getIndex()-1; // set directly to i if you want the inverse order
                    
                    bPhiCoefficients[bPhiIdx] = new Parameter();
                    
                    String bPhiType = bpCoefs.get(i).getType();
                    if(bPhiType.equals("Fixed"))
                    {
                        if(!pCoefs.get(i).getCoef().equals("NA"))
                        {
                            double coefficient = Double.parseDouble(bpCoefs.get(i).getCoef());

                            
                            bPhiCoefficients[bPhiIdx].setType(ParameterType.Fixed);
                            bPhiCoefficients[bPhiIdx].setValue(coefficient);
                            //bPhiCoefficients[bPhiIdx].setStde(0.0);
                        }
                        else
                        {
                            bPhiCoefficients[bPhiIdx].setType(ParameterType.Undefined);
                        }
                    }
                    else if(bPhiType.equals("Initial"))
                    {
                        bPhiCoefficients[bPhiIdx].setType(ParameterType.Initial);
                        // what is it?
                    }else if(bPhiType.equals("Undefined")) 
                    {
                        bPhiCoefficients[bPhiIdx].setType(ParameterType.Undefined);
                    }              
                }
                aspec.setBPhi(bPhiCoefficients); 
            }
            
            if(q>0)
            {   
                int thetaIdx=0;
                Parameter[] thetaCoefficients = new Parameter[q];
                for(int i = 0; i < qCoefs.size(); i++) 
                {
                    thetaIdx = qCoefs.get(i).getIndex()-1; // set directly to i if you want the inverse order
                    
                    thetaCoefficients[thetaIdx] = new Parameter();
                    
                    String thetaType = qCoefs.get(i).getType();
                    if(thetaType.equals("Fixed"))
                    {
                        if(!qCoefs.get(i).getCoef().equals("NA"))
                        {
                            double coefficient = Double.parseDouble(qCoefs.get(i).getCoef());
                            
                            thetaCoefficients[thetaIdx].setType(ParameterType.Fixed);
                            thetaCoefficients[thetaIdx].setValue(coefficient);
                            //thetaCoefficients[thetaIdx].setStde(0.0);
                        }
                        else
                        {
                            thetaCoefficients[thetaIdx].setType(ParameterType.Undefined);
                        }
                        
                    }
                    else if(thetaType.equals("Initial"))
                    {
                        thetaCoefficients[thetaIdx].setType(ParameterType.Initial);
                        // what is it?
                    }else if(thetaType.equals("Undefined")) 
                    {
                        thetaCoefficients[thetaIdx].setType(ParameterType.Undefined);
                    }              
                }
                aspec.setTheta(thetaCoefficients); 
            }
            
            if(bq>0)
            {   
                int bthetaIdx=0;
                Parameter[] bthetaCoefficients = new Parameter[bq];
                for(int i = 0; i < bqCoefs.size(); i++) 
                {
                    bthetaIdx = bqCoefs.get(i).getIndex()-1; // set directly to i if you want the inverse order
                      
                    bthetaCoefficients[bthetaIdx] = new Parameter();
                    
                    String bthetaType = bqCoefs.get(i).getType();
                    if(bthetaType.equals("Fixed"))
                    {
                        if(!bqCoefs.get(i).getCoef().equals("NA"))
                        {
                            double coefficient = Double.parseDouble(bqCoefs.get(i).getCoef());

                            
                            bthetaCoefficients[bthetaIdx].setType(ParameterType.Fixed);
                            bthetaCoefficients[bthetaIdx].setValue(coefficient);
                            //bthetaCoefficients[bthetaIdx].setStde(0.0);
                        }
                        
                        else
                        {
                            bthetaCoefficients[bthetaIdx].setType(ParameterType.Undefined);
                        }
                        
                    }
                    else if(bthetaType.equals("Initial"))
                    {
                        bthetaCoefficients[bthetaIdx].setType(ParameterType.Initial);
                        // what is it?
                    }else if(bthetaType.equals("Undefined")) 
                    {
                        bthetaCoefficients[bthetaIdx].setType(ParameterType.Undefined);
                    }              
                }
                aspec.setBTheta(bthetaCoefficients); 
            }
            
            
            
        }    
             

    }

    private void setSeats() {
        SeatsSpecification sspec = tsSpec.getSeatsSpecification();
        if (sspec == null) {
            sspec = new SeatsSpecification();
            tsSpec.setSeatsSpecification(sspec);
        }
        /*"seats.maBoundary":0.95,*/
        sspec.setXlBoundary(model.getSeatsMaBoundary()); //Alessandro
        sspec.setPredictionLength(model.getSeatsPredictionLength());
        sspec.setApproximationMode(SeatsSpecification.ApproximationMode.valueOf(model.getSeatsApprox()));
        sspec.setTrendBoundary(model.getSeatsTrendBoundary());
        sspec.setSeasBoundary(model.getSeatsSeasdBoundary());
        sspec.setSeasBoundary1(model.getSeatsSeasdBoundary1());
        sspec.setSeasTolerance(model.getSeatsSeasTol());
        sspec.setMethod(SeatsSpecification.EstimationMethod.valueOf(model.getSeatsMethod()));
    }
    // Method added by Alessandro
    private boolean isValidDate(String dateStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try {
            // Try to parse the string
            LocalDate date = LocalDate.parse(dateStr, formatter);
            // If the Date is correctly parsed, then the string is correctly formatted
            return true;
        } catch (DateTimeParseException e) {
            // If an exception is catched, the Date is not well formatted
            return false;
        }
    }

    // Aggiunta - Added - Nuovo
    private void setRamps()
    {
        List<RampsInfo> ramps = model.getRamps(); 
        Day dayStart, dayEnd;
        Date dateStart, dateEnd;
        Ramp rp;
        ArrayList<Ramp> rampsList = new ArrayList<Ramp>();
        String rampName;
        
        int year_s, month_s, day_s, year_e, month_e, day_e;
        
        for(RampsInfo r : ramps)
        {   
            year_s  = Integer.parseInt(r.getStart().substring(0, 4));//substr(ramp$start, 1, 4))
            month_s = Integer.parseInt(r.getStart().substring(5, 7));
            day_s   = Integer.parseInt(r.getStart().substring(8, 10));
            // day_start_int = Day.calc(year_s, month_s-1, day_s-1));
            
            dateStart = new Date(year_s-1900, month_s-1, day_s);
            dayStart  = new Day(dateStart);
            
            year_e  = Integer.parseInt(r.getEnd().substring(0, 4));//substr(ramp$start, 1, 4))
            month_e = Integer.parseInt(r.getEnd().substring(5, 7));
            day_e   = Integer.parseInt(r.getEnd().substring(8, 10));
            
            dateEnd = new Date(year_e-1900, month_e-1, day_e);
            dayEnd  = new Day(dateEnd);
            
            rp = new Ramp(dayStart, dayEnd);
            
            rampsList.add(rp);
            
            if(r.getFixed_coef()!=0.0)
            {
                rampName = "rp$" + r.getStart() + "$" + r.getEnd();
                tsSpec.getTramoSpecification().getRegression().setFixedCoefficients(rampName, new double[]{r.getFixed_coef()});
            }    
        }
        
        Ramp[] rampsArray = rampsList.toArray(new Ramp[0]);
        tsSpec.getTramoSpecification().getRegression().setRamps(rampsArray);
        
    }        
    
    private void setInterventionVariables()
    {
        List<InterventionVariablesInfo> ivs = model.getInterventionVariables(); 
        Day dayStart, dayEnd;
        Date dateStart, dateEnd;
        InterventionVariable interventionVar = null;

        int year_s, month_s, day_s, year_e, month_e, day_e;
        //List<Sequence> seqList=new ArrayList<Sequence>();
        
        for(InterventionVariablesInfo iv : ivs)
        {   
            interventionVar = new InterventionVariable();
            interventionVar.setDelta(iv.getDelta());
            interventionVar.setDeltaS(iv.getDelta_s());
            if(iv.getD1DS()) //beacause setting of D1DS resets all the deltas
            {
                interventionVar.setD1DS(iv.getD1DS());
            }    
            
            
            List<SequenceInfo> seqs = iv.getSeq();
            for(SequenceInfo s : seqs)
            {
                year_s  = Integer.parseInt(s.getStart().substring(0, 4));//substr(ramp$start, 1, 4))
                month_s = Integer.parseInt(s.getStart().substring(5, 7));
                day_s   = Integer.parseInt(s.getStart().substring(8, 10));
                // day_start_int = Day.calc(year_s, month_s-1, day_s-1));
            
                dateStart = new Date(year_s-1900, month_s-1, day_s);
                dayStart  = new Day(dateStart);
            
                year_e  = Integer.parseInt(s.getEnd().substring(0, 4));//substr(ramp$start, 1, 4))
                month_e = Integer.parseInt(s.getEnd().substring(5, 7));
                day_e   = Integer.parseInt(s.getEnd().substring(8, 10));
            
                dateEnd = new Date(year_e-1900, month_e-1, day_e);
                dayEnd  = new Day(dateEnd);
                
                interventionVar.add(dayStart, dayEnd);
            }    
            
            tsSpec.getTramoSpecification().getRegression().add(interventionVar);
        }
        
    }
    
    
    private void fixDefaultJDplusCalendarCoefficients() 
    {
        double easterCoef = model.getEasterCoef();
        if(easterCoef!=0.0)
        {    
            tsSpec.getTramoSpecification().getRegression().setFixedCoefficients("easter", new double[]{easterCoef});
        }
        
    }
    
    
    // Jean
//    public static ProcessingContext readContext(String directoryPathExtReg) {
//                
//        for(DestSpecVarFromFileInfo usrDefVar:userDefVarFileInfoList)
//        {
//            
//            if(!usrdefVarTypes.get(idxUsrDefVarTypes).equals("Calendar")) //Calendar are for trading days
//            {        
//                Map<String, TsData> usrDefVariables = null;
//                try {
//                     String startDate = usrDefVar.getStart();
//                     if(!isValidDate(startDate))
//                     {
//                         System.out.println(startDate+" is not correctly formatted (yyyy-MM-dd)");
//                         // exception (?)
//                     }
//                    int startYear  = Integer.parseInt(startDate.substring(0, 4)); //YYYY
//                    int startMonth = Integer.parseInt(startDate.substring(5, 7)); //MM
//
//
//                    TsFrequency freq=TsFrequency.valueOf(model.getFrequency());
//
//                    usrDefVariables = ExtRegDataReaderTSplus.readTsFile(directoryPathExtReg, usrDefVar.getContainer(), freq , startYear, startMonth-1);
//                    } catch (IOException ex) {
//                        Logger.getLogger(TSmodelSetup.class.getName()).log(Level.SEVERE, null, ex);
//                    }
//
//                    TsVariables vars = new TsVariables();
//                    List<TsVariableDescriptor> varsDescriptor = new ArrayList<TsVariableDescriptor>();
//                    String varNameForDescriptor=usrDefVar.getContainer().substring(0, usrDefVar.getContainer().lastIndexOf('.'));
//                    List<String> varNames = new ArrayList<String>();
//                    
//                    for (Map.Entry<String, TsData> variable : usrDefVariables.entrySet()) {
//                        String key = variable.getKey();
//                        TsData value = variable.getValue();
//
//                        TsVariable var = new TsVariable(value);
//                        var.setName(key); 
//                        vars.set(key, var);
//                        varNames.add(key);
//
//                        
//                        
//                    }
//                    // get filename without extension, to be used as vars Name
//                    this.context.getTsVariableManagers().set(varNameForDescriptor, vars);
//                        
//                    TsVariableDescriptor vDesc = new TsVariableDescriptor();
//                    int idx=0;
//                    for(String vName:varNames)
//                    {    
//                        vDesc.setName(varNameForDescriptor+"."+vName);
//                        String type = model.getUsrdefVarType().get(idx);
//                        if(type!=null)
//                        {
//                            vDesc.setEffect(TsVariableDescriptor.UserComponentType.valueOf(type));
//                        }
//                        idx++;
//                    }    
//                    this.tsSpec.getTramoSpecification().getRegression().add(vDesc);
//                        
//            }
//            idxUsrDefVarTypes++;
//        }    
//    }
//
    //

    public static String transformOutlierStringFromOutlierDef(String input) {
        // Verifica che la stringa sia del formato corretto
        if (input == null || !input.matches("^[A-Za-z]+\\.\\d{4}-\\d{2}-\\d{2}$")) {
            throw new IllegalArgumentException("Formato della stringa non valido.");
        }

        // Estrai il prefisso (es. TC, AO) e la data (es. 2001-10-01)
        String[] parts = input.split("\\.");
        String outPrefix = parts[0];
        String dateString = parts[1];

        // Parsing della data
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat outputDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        try {
            Date date = dateFormat.parse(dateString);
            String formattedDate = outputDateFormat.format(date);

            // Cambia il mese in base al formato richiesto
            String[] dateParts = formattedDate.split("-");
            String year  = dateParts[0];
            String month = dateParts[1];
            String day   = dateParts[2];


            // Se il mese è tra 10 e 12, usiamo il mese con due cifre (10, 11, 12)
//            if (Integer.parseInt(month) < 10) {
//                month = String.valueOf(Integer.parseInt(month));  // Rimuove lo zero iniziale se presente
//            }

            // Formatta l'output come richiesto
            return outPrefix + " (" + year + "-" + month + "-" + day + ")";
        } catch (ParseException e) {
            throw new IllegalArgumentException("Errore nel parsing della data.", e);
        }
    }    
    
    public static String[] filterStringsStartingWithTd(String[] input) {
        if (input == null) {
            return new String[0];  // Restituisce un array vuoto se l'input è nullo
        }
        
        List<String> filteredList = new ArrayList<>();
        
        for (String str : input) {
            if (str != null && str.startsWith("td|")) {
                filteredList.add(str);
            }
        }
        
        // Converte la lista filtrata in un array di stringhe
        return filteredList.toArray(new String[0]);
    }
    
    
    public static List<Double> getUsrDefCoefTd(List<String> usrDefVarCoefs, List<String> usrDefVarTypes) {
        
        List<Double> usrDefVarCoefsTd = new ArrayList<Double>();

        if (usrDefVarCoefs == null || usrDefVarTypes == null || "NA".equals(usrDefVarTypes.get(0)) || "NA".equals(usrDefVarCoefs.get(0))) {
            return usrDefVarCoefsTd;  // Restituisce una lista vuota se uno degli input è nullo
        }

        // Itera sulle liste fino alla lunghezza minima per evitare IndexOutOfBoundsException
        int minSize = Math.min(usrDefVarCoefs.size(), usrDefVarTypes.size());
        for (int i = 0; i < minSize; i++) {
            if ("calendar".equalsIgnoreCase(usrDefVarTypes.get(i))) {
                usrDefVarCoefsTd.add(Double.parseDouble(usrDefVarCoefs.get(i)));
            }
        }
        return usrDefVarCoefsTd;
    }
 
    
    
    public static List<Double> getUsrDefCoefVars(List<String> usrDefVarCoefs, List<String> usrDefVarTypes) {
        
        List<Double> usrDefVarCoefsVars = new ArrayList<Double>();

        if (usrDefVarCoefs == null || usrDefVarTypes == null || "NA".equals(usrDefVarTypes.get(0)) || "NA".equals(usrDefVarCoefs.get(0))) {
            return usrDefVarCoefsVars;  // Restituisce una lista vuota se uno degli input è nullo
        }

        // Itera sulle liste fino alla lunghezza minima per evitare IndexOutOfBoundsException
        int minSize = Math.min(usrDefVarCoefs.size(), usrDefVarTypes.size());
        for (int i = 0; i < minSize; i++) {
            if (!"calendar".equalsIgnoreCase(usrDefVarTypes.get(i))) {
                usrDefVarCoefsVars.add(Double.parseDouble(usrDefVarCoefs.get(i)));
            }
        }
        return usrDefVarCoefsVars;
    }

    public static double calculateCriticalValue(int numberOfObservations)
    {
        if (numberOfObservations <= 50) {
            return 3.0;
        } else if (numberOfObservations >= 450) {
            return 4.0;
        } else {
            return 3.0 + 0.0025 * (numberOfObservations - 50);
        }
    }
    
}
