package com.sprypoint.gridreader;

public class ERTMessage implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7698020190604590414L;
	
	
	private java.util.Date date;
	private String serial;
	private java.math.BigDecimal reading;
	private short frequency;
	private short signalStrength;

	private java.math.BigDecimal deltaReading;
	private long deltaSeconds;

	public ERTMessage() {
		date = new java.util.Date();
	}

	public java.util.Date getDate() {
		return this.date;
	}

	public void setDate(java.util.Date d) {
		this.date = d;
	}

	public String getSerial() {
		return serial;
	}

	public void setSerial(String s) {
		this.serial = s;
	}

	public java.math.BigDecimal getReading() {
		return reading;
	}

	public void setReading(java.math.BigDecimal r) {
		this.reading = r;
	}

	public short getFrequency() {
		return frequency;
	}

	public void setFrequency(short f) {
		this.frequency = f;
	}

	public short getSignalStrength() {
		return signalStrength;
	}

	public void setSignalStrength(short ss) {
		this.signalStrength = ss;
	}

	public java.math.BigDecimal getDeltaReading() {
		return deltaReading;
	}

	public void setDeltaReading(java.math.BigDecimal dr) {
		this.deltaReading = dr;
	}

	public long getDeltaSeconds() {
		return deltaSeconds;
	}

	public void setDeltaSeconds(long ds) {
		this.deltaSeconds = ds;
	}
}