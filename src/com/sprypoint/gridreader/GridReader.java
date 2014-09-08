package com.sprypoint.gridreader;

import java.sql.*;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

public class GridReader {
	private static String configFile = "conf/gridreader.properties";
	private static SerialPort serialPort; //= new SerialPort("COM5");
	private static java.util.regex.Pattern pattern = java.util.regex.Pattern
			.compile("^\\$(.*)\\*(.*)\r\n$");
	
	public static Connection connect() throws Exception {
		Configuration config = new PropertiesConfiguration(configFile);
		String url = config.getString("database.url");
		String user = config.getString("database.user");
		String password = config.getString("database.password");
		
//		Class.forName(driver);
		
	    return DriverManager.getConnection(url, user, password);
	}
	
	
    public static void main(String[] args) {
    	log("Establishing SQL connection");
    	Connection conn = null;
    	try {
    		conn = connect();
    		log("Sql connection OK");
    	} catch (Exception ex) {
    		log("SQL Connection failed: " + ex.getMessage());
    		ex.printStackTrace();
    		return;
    	} finally {
    		try { conn.close(); } catch (Exception e) {}
    	}
    	
    	log("Establishing serial port");
    	try { 
	    	log("Declaring port...");
	    	Configuration config = new PropertiesConfiguration(configFile);
	    	String portName = config.getString("serial.port");
	        serialPort = new SerialPort(portName);

	        log("Opening port...");
            serialPort.openPort();//Open port
        	
            log("Setting parameters...");
            serialPort.setParams(19200, 8, 1, 0);//Set params
            
            //Prepare mask
            int mask = SerialPort.MASK_RXCHAR + SerialPort.MASK_CTS + SerialPort.MASK_DSR;
        	log("Setting mask...");
            serialPort.setEventsMask(mask);//Set mask
        	
            log("Adding listener...");
            serialPort.addEventListener(new SerialPortReader());//Add SerialPortEventListener
    	} catch (ConfigurationException cfe) {
    		log("Problem reading configuration");
    	} catch (SerialPortException ex) {
        	log("Ooops...");
            System.out.println(ex);
        }
    }
    
    
    static class SerialPortReader implements SerialPortEventListener {

        public void serialEvent(SerialPortEvent event) {
        	//log("Event:");
            if(event.isRXCHAR()){//If data is available
            	Integer len = event.getEventValue();
            	//log("RX: " + len.toString());
                //if(event.getEventValue() == 10){//Check bytes count in the input buffer
                    //Read data, if 10 bytes available 
                    try {
                        byte buffer[] = serialPort.readBytes(len);
                        doWork(new String(buffer));
                    }
                    catch (SerialPortException ex) {
                        System.out.println(ex);
                    }
                //}
            }
            else if(event.isCTS()){//If CTS line has changed state
            	log("CTS");
                if(event.getEventValue() == 1){//If line is ON
                    System.out.println("CTS - ON");
                }
                else {
                    System.out.println("CTS - OFF");
                }
            }
            else if(event.isDSR()){///If DSR line has changed state
                if(event.getEventValue() == 1){//If line is ON
                    System.out.println("DSR - ON");
                }
                else {
                    System.out.println("DSR - OFF");
                }
            }
        }
    }
	
	private static void doWork(String line) {
//		System.out.println("Work with: " + line);
		
//		for (int i=0; i<line.length();i++)
//		System.out.println(line.charAt(i) + " as ASCII value of " + (int)line.charAt(i));
		
		java.util.regex.Matcher matcher = pattern.matcher(line);
		if (matcher.matches()) {
			String message = matcher.group(1);
			String checksum = matcher.group(2);
//			log("Match: " + message + " checksum: " + checksum);

			if (valid(message, checksum)) {
				String parts[] = message.split(",");
				String messageType = parts[0];
				
				if ((messageType.equals("UMSCM")) || (messageType.equals("UMSCP"))) {
					StandardConsumptionMessage scm = new StandardConsumptionMessage(parts);
					scm.log();
				} 
				else {
					if (messageType.equals("UMIDM")) {
						IntervalDataMessage idm = new IntervalDataMessage(parts);
						idm.log();
//						log(" IDM MESSAGE: " + line);
					}
					else {
						log("Unknown message type: " + line);
					}
				}
				// Otherwise, ignore
			}
			else { 
				log("Checksum failed on " + line);
			}
		}
		else {
			log("Match failed on line [" + line + "]");
		}
	}
	
	private static void log(String msg) {
		System.out.println(msg);
	}

	
	private static boolean valid(String value, String checksum) {
		int calculatedChecksum = 0;

		try {
			byte[] data = value.getBytes("UTF-8");
			calculatedChecksum = data[0] ^ data[1];
			for (int b = 2; b < data.length; ++b) {
				calculatedChecksum = calculatedChecksum ^ data[b];
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		String calcSumString = Integer.toHexString(calculatedChecksum);
//		log("Calculated checksum: " + calcSumString);

		if (calcSumString.equalsIgnoreCase(checksum)) {
			return true;
		}

		return false;
	}	
}





