package com.sprypoint.gridreader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Calendar;
import java.util.GregorianCalendar;


public class StandardConsumptionMessage extends ERTMessage implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3676906591718914802L;

	public StandardConsumptionMessage() {
	}

	public StandardConsumptionMessage(String parts[]) {
		setSerial(parts[1]);
		setReading(new java.math.BigDecimal(parts[3]));

		if (parts.length > 4) {
			setFrequency(Short.parseShort(parts[4]));
		}

		if (parts.length > 5) {
			setSignalStrength(Short.parseShort(parts[5]));
		}
	}
	
	public void log() {
		Connection conn = null;
		try {
			Calendar cal = new GregorianCalendar();
			
			conn = GridReader.connect();
			
			PreparedStatement stmt = conn.prepareStatement("INSERT INTO scm(meter, readtime, kwhreading) VALUES (?, ?, ?)");
			stmt.setString(1, this.getSerial());
			stmt.setTimestamp(2, new java.sql.Timestamp(cal.getTime().getTime()));
			stmt.setString(3, this.getReading().toPlainString());
			stmt.execute();	
			
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try { conn.close(); } catch (Exception e) {}
		}
	}
}
