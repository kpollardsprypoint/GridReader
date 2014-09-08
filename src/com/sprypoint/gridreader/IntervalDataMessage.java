package com.sprypoint.gridreader;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class IntervalDataMessage extends ERTMessage implements
		java.io.Serializable {

	private static final long serialVersionUID = 3676906591718914802L;

	public IntervalDataMessage() {
	}

	public IntervalDataMessage(String parts[]) {
		setSerial(parts[1]);
		setReading(new java.math.BigDecimal(parts[4]));

		// Interval data is in fields 7-53

	}

	public void log() {
		Connection conn = null;
		try {
			Calendar cal = new GregorianCalendar();
			conn = GridReader.connect();

			PreparedStatement stmt = conn
					.prepareStatement("INSERT INTO scm(meter, readtime, kwhreading) VALUES (?, ?, ?)");
			stmt.setString(1, this.getSerial());
			stmt.setTimestamp(2,
					new java.sql.Timestamp(cal.getTime().getTime()));
			stmt.setString(3, this.getReading().toPlainString());
			stmt.execute();

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
			}
		}

	}
}
