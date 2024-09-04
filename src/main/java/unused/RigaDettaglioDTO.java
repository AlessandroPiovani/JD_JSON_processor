package unused;

//package it.istat.smetabrowser.dto.jms;

//import it.istat.smetabrowser.dto.AbstractDTO;

public class RigaDettaglioDTO{
	private static final long serialVersionUID = -9080159791461733835L;

	private Long idPeriodoIndicatore;
	private Long idEdizioneIndicatore;
	private String idAggregazione;
	private Long idVariabile;
	private Long idTipoIndicatore;
	private Long idBase;
	private Double valore;

	public Long getIdPeriodoIndicatore() {
		return idPeriodoIndicatore;
	}

	public void setIdPeriodoIndicatore(Long idPeriodoIndicatore) {
		this.idPeriodoIndicatore = idPeriodoIndicatore;
	}

	public Long getIdEdizioneIndicatore() {
		return idEdizioneIndicatore;
	}

	public void setIdEdizioneIndicatore(Long idEdizioneIndicatore) {
		this.idEdizioneIndicatore = idEdizioneIndicatore;
	}

	public String getIdAggregazione() {
		return idAggregazione;
	}

	public void setIdAggregazione(String idAggregazione) {
		this.idAggregazione = idAggregazione;
	}

	public Long getIdVariabile() {
		return idVariabile;
	}

	public void setIdVariabile(Long idVariabile) {
		this.idVariabile = idVariabile;
	}

	public Long getIdTipoIndicatore() {
		return idTipoIndicatore;
	}

	public void setIdTipoIndicatore(Long idTipoIndicatore) {
		this.idTipoIndicatore = idTipoIndicatore;
	}

	public Long getIdBase() {
		return idBase;
	}

	public void setIdBase(Long idBase) {
		this.idBase = idBase;
	}

	public Double getValore() {
		return valore;
	}

	public void setValore(Double valore) {
		this.valore = valore;
	}
}