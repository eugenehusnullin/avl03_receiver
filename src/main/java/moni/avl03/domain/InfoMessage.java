package moni.avl03.domain;

import java.util.Date;

public class InfoMessage extends Message {
	private long imei;
	private String alarmType;
	private char state;
	private double lat;
	private double lon;
	private char latLetter;
	private char lonLetter;
	private double speed;
	private double course;
	private double pdop;
	private double hdop;
	private double vdop;
	private String status;
	private Date navDate;
	private Date rtcDate;
	private String voltage;
	private String adc;
	private String lacci;
	private String temperature;
	private double odometer;
	private int serialId;
	private String rfidno;
	private int fuelImpuls;
	private char chip;
	private int satellites;

	public InfoMessage(ProtocolType protocolType) {
		super(protocolType);
	}

	public long getImei() {
		return imei;
	}

	public void setImei(long imei) {
		this.imei = imei;
	}

	public String getAlarmType() {
		return alarmType;
	}

	public void setAlarmType(String alarmType) {
		this.alarmType = alarmType;
	}

	public char getState() {
		return state;
	}

	public void setState(char state) {
		this.state = state;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLon() {
		return lon;
	}

	public void setLon(double lon) {
		this.lon = lon;
	}

	public double getSpeed() {
		return speed;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}

	public double getCourse() {
		return course;
	}

	public void setCourse(double course) {
		this.course = course;
	}

	public char getLatLetter() {
		return latLetter;
	}

	public void setLatLetter(char latLetter) {
		this.latLetter = latLetter;
	}

	public char getLonLetter() {
		return lonLetter;
	}

	public void setLonLetter(char lonLetter) {
		this.lonLetter = lonLetter;
	}

	public double getPdop() {
		return pdop;
	}

	public void setPdop(double pdop) {
		this.pdop = pdop;
	}

	public double getHdop() {
		return hdop;
	}

	public void setHdop(double hdop) {
		this.hdop = hdop;
	}

	public double getVdop() {
		return vdop;
	}

	public void setVdop(double vdop) {
		this.vdop = vdop;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getRtcDate() {
		return rtcDate;
	}

	public void setRtcDate(Date rtcDate) {
		this.rtcDate = rtcDate;
	}

	public String getVoltage() {
		return voltage;
	}

	public void setVoltage(String voltage) {
		this.voltage = voltage;
	}

	public String getAdc() {
		return adc;
	}

	public void setAdc(String adc) {
		this.adc = adc;
	}

	public String getLacci() {
		return lacci;
	}

	public void setLacci(String lacci) {
		this.lacci = lacci;
	}

	public String getTemperature() {
		return temperature;
	}

	public void setTemperature(String temperature) {
		this.temperature = temperature;
	}

	public double getOdometer() {
		return odometer;
	}

	public void setOdometer(double odometer) {
		this.odometer = odometer;
	}

	public int getSerialId() {
		return serialId;
	}

	public void setSerialId(int serialId) {
		this.serialId = serialId;
	}

	public Date getNavDate() {
		return navDate;
	}

	public void setNavDate(Date navDate) {
		this.navDate = navDate;
	}

	public String getRfidno() {
		return rfidno;
	}

	public void setRfidno(String rfidno) {
		this.rfidno = rfidno;
	}

	public int getFuelImpuls() {
		return fuelImpuls;
	}

	public void setFuelImpuls(int fuelImpuls) {
		this.fuelImpuls = fuelImpuls;
	}

	public char getChip() {
		return chip;
	}

	public void setChip(char chip) {
		this.chip = chip;
	}

	public int getSatellites() {
		return satellites;
	}

	public void setSatellites(int satellites) {
		this.satellites = satellites;
	}
}
