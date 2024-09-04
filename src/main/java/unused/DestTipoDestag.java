package unused;

/**
 *
 * @author cazora
 */
public enum DestTipoDestag {
        DATIGREZZI(0L,"datiGrezzi")
	,SA(1L, "sa") //*****
	,T(2L, "t")  //*****
	,SAF(3L, "sa_f")
	,TF(4L, "t_f")
	,BMARK(5L, "benchmarking.result")
        ,DIAGNOSTICS(6L, "diagnostics") //*****
        ,FCAST5(7L,"fcasts(-5)")
        ,BCAST1(8L,"bcasts(-1)")
        ,CALEFF(9L,"cal") //calendar effects *****
        ,CALFOR(10L,"cal_f") //Forecasts of the calendar effects
        ,EASTER(11L,"ee") //Easter effects
        ,EASTERFOR(12L,"ee_f") //Forecasts of the Easter effect
        ,STAG(13L,"s") //componente stagionale *****
        ,IRR(14L,"i") //componente irregolare *****
        ;
	
	private final Long idTipoIndicatoreOutput;
	private final String tipoDestag;

	private DestTipoDestag(Long idTipoIndicatoreOutput, String tipoDestag) {
		this.idTipoIndicatoreOutput = idTipoIndicatoreOutput;
		this.tipoDestag = tipoDestag;
	}

	public Long getIdTipoIndicatoreOutput() {
		return idTipoIndicatoreOutput;
	}

	public String getTipoDestag() {
		return tipoDestag;
	}

	public static String getTipoDestag(Long idTipoIndicatoreOutput) {
		if (idTipoIndicatoreOutput == null)
			return "";

		for (DestTipoDestag value : DestTipoDestag.values()) {
			if (value.getIdTipoIndicatoreOutput().equals(idTipoIndicatoreOutput))
				return value.getTipoDestag();
		}

		return "";
	}    
}
