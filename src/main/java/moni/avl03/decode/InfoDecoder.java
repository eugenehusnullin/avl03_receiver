package moni.avl03.decode;

import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import moni.avl03.domain.InfoMessage;
import moni.avl03.domain.Message;
import moni.avl03.domain.ProtocolType;
import moni.avl03.netty.MessageContainer;

public class InfoDecoder implements Decoder {
	private static final Logger logger = LoggerFactory.getLogger(InfoDecoder.class);
	private static final Logger packetsLogger = LoggerFactory.getLogger("packets");
	private Charset asciiCharset = Charset.forName("ASCII");

	private String regGlonass = "\\$\\$(?<Len>\\w{2})(?<Imei>\\d{15})\\|(?<AlarmType>\\w{2})(?<Chip>U|R)(?<State>A|V)(?<Satellites>\\d{2})"
			+ "(?<Lat>[0-9\\.]{8})(?<LatLetter>N|S)(?<Lon>[0-9\\.]{9})(?<LonLetter>E|W)(?<Speed>[0-9]{3})(?<Course>[0-9]{3})"
			+ "\\|(?<PDOP>[0-9\\.]{4})\\|(?<HDOP>[0-9\\.]{4})\\|(?<VDOP>[0-9\\.]{4})\\|(?<DateTime>[0-9]{14})\\|(?<RTC>[0-9]{14})\\|(?<Status>[0-9]{12})"
			+ "\\|(?<Voltage>[0-9]{8})\\|(?<ADC>[0-9]{8})\\|(?<LACCI>\\w{8})\\|(?<Temperature>\\w{4})\\|(?<Odometer>[0-9\\.]{6,})\\|(?<SerialID>\\d{4})\\|(?<RFIDNo>\\d*)\\|"
			+ "(?<Checksum>\\w{4})";
	private Pattern patternGlonass;

	private String regGlonassImpuls = "\\$\\$(?<Len>\\w{2})(?<Imei>\\d{15})\\|(?<AlarmType>\\w{2})(?<Chip>U|R)(?<State>A|V)(?<Satellites>\\d{2})"
			+ "(?<Lat>[0-9\\.]{8})(?<LatLetter>N|S)(?<Lon>[0-9\\.]{9})(?<LonLetter>E|W)(?<Speed>[0-9]{3})(?<Course>[0-9]{3})"
			+ "\\|(?<PDOP>[0-9\\.]{4})\\|(?<HDOP>[0-9\\.]{4})\\|(?<VDOP>[0-9\\.]{4})\\|(?<DateTime>[0-9]{14})\\|(?<RTC>[0-9]{14})\\|(?<Status>[0-9]{12})"
			+ "\\|(?<Voltage>[0-9]{8})\\|(?<ADC>[0-9]{8})\\|(?<LACCI>\\w{8})\\|(?<Temperature>\\w{4})\\|(?<Odometer>[0-9\\.]{6,})\\|(?<SerialID>\\d{4})\\|(?<RFIDNo>\\d*)"
			+ "\\|(?<FuelImpuls>\\d{5})\\|(?<Checksum>\\w{4})";
	private Pattern patternGlonassImpuls;

	private String regGlonassFuel = "\\$\\$(?<Len>\\w{2})(?<Imei>\\d{15})\\|(?<AlarmType>\\w{2})(?<Chip>U|R)(?<State>A|V)(?<Satellites>\\d{2})"
			+ "(?<Lat>[0-9\\.]{8})(?<LatLetter>N|S)(?<Lon>[0-9\\.]{9})(?<LonLetter>E|W)(?<Speed>[0-9]{3})(?<Course>[0-9]{3})"
			+ "\\|(?<PDOP>[0-9\\.]{4})\\|(?<HDOP>[0-9\\.]{4})\\|(?<VDOP>[0-9\\.]{4})\\|(?<DateTime>[0-9]{14})\\|(?<RTC>[0-9]{14})\\|(?<Status>[0-9]{12})"
			+ "\\|(?<Voltage>[0-9]{8})\\|(?<ADC>[0-9]{8})\\|(?<f1>\\w{4})\\|(?<f2>\\w{4})\\|(?<LACCI>\\w{8})\\|(?<Temperature>\\w{4})\\|(?<Odometer>[0-9\\.]{6,})\\|(?<SerialID>\\d{4})\\|(?<RFIDNo>\\d*)"
			+ "\\|(?<FuelImpuls>\\d{5})\\|(?<Checksum>\\w{4})";
	private Pattern patternGlonassFuel;

	private String regGprmc = "\\$\\$(?<Len>\\w{2})(?<Imei>\\d{15})\\|(?<AlarmType>\\w{2})((\\$GPRMC,(?<Time>[0-9\\.]{9,11}),(?<State>A|V),(?<Lat>[0-9\\.]{7,10}),(?<LatLetter>N|S),"
			+ "(?<Lon>[0-9\\.]{8,11}),(?<LonLetter>E|W),(?<Speed>[0-9\\.]*),(?<Course>[0-9\\.]*),(?<Date>[0-9]{6}),([0-9\\.]{1,}|),([0-9\\.]{1,}|\\w{1,}|)(,(A|D|E|N|)|)\\*\\w{2,})|(\\d{1,}))"
			+ "\\|(?<PDOP>[0-9\\.]{3,4})\\|(?<HDOP>[0-9\\.]{3,4})\\|(?<VDOP>[0-9\\.]{3,4})\\|(?<Status>[0-9]{12})\\|(?<RTC>[0-9]{14})\\|(?<Voltage>[0-9]{8})\\|(?<ADC>[0-9]{8})"
			+ "\\|(?<LACCI>\\w{8})\\|(?<Temperature>\\w{4})\\|(?<Odometer>[0-9\\.]{6})\\|(?<SerialID>\\d{4})\\|(\\|?)(?<Checksum>\\w{4})";
	private Pattern patternGprmc;

	public InfoDecoder() {
		patternGlonass = Pattern.compile(regGlonass);
		patternGlonassImpuls = Pattern.compile(regGlonassImpuls);
		patternGlonassFuel = Pattern.compile(regGlonassFuel);
		patternGprmc = Pattern.compile(regGprmc);
	}

	@Override
	public Message decode(MessageContainer mc) {
		byte[] bytes = mc.getBytes();
		String str = new String(bytes, asciiCharset);
		packetsLogger.debug(str);
		return decode(str);
	}

	@Override
	public Message decode(String str) {
		String[] strArray = str.split("\\|");

		switch (strArray.length) {
		case 14:
		case 15:
			return decodeGprmc(str);

		case 16:
		case 17:
		case 19:
			return decodeGlonass(str, strArray.length);

		default:
			logger.error("Undefined protocol for message: " + str);
			return null;
		}
	}

	private InfoMessage decodeGlonass(String str, int items) {
		InfoMessage mes;
		Matcher m;
		switch (items) {
		case 16:
			mes = new InfoMessage(ProtocolType.glonass);
			m = patternGlonass.matcher(str);
			break;

		case 17:
			mes = new InfoMessage(ProtocolType.glonassImpuls);
			m = patternGlonassImpuls.matcher(str);
			break;

		case 19:
		default:
			mes = new InfoMessage(ProtocolType.glonassFuel);
			m = patternGlonassFuel.matcher(str);
			break;
		}
		m.find();

		decodeCommonPart(mes, m);

		mes.setLat(Double.parseDouble(m.group("Lat")));
		mes.setLon(Double.parseDouble(m.group("Lon")));
		mes.setNavDate(parseUtcDate(m.group("DateTime")));
		mes.setRfidno(m.group("RFIDNo"));
		if (mes.getProtocolType() == ProtocolType.glonassImpuls || mes.getProtocolType() == ProtocolType.glonassFuel) {
			mes.setFuelImpuls(Integer.parseInt(m.group("FuelImpuls")));
		}
		mes.setChip(m.group("Chip").charAt(0));
		mes.setSatellites(Integer.parseInt(m.group("Satellites")));

		return mes;
	}

	private InfoMessage decodeGprmc(String str) {
		InfoMessage mes = new InfoMessage(ProtocolType.gprmc);
		Matcher m = patternGprmc.matcher(str);
		m.find();

		decodeCommonPart(mes, m);

		mes.setLat(convertGprmcCoord(m.group("Lat")));
		mes.setLon(convertGprmcCoord(m.group("Lon")));
		mes.setNavDate(parceGprmcUtcDate(m.group("Date"), m.group("Time")));

		return mes;
	}

	private void decodeCommonPart(InfoMessage mes, Matcher m) {
		mes.setImei(Long.parseLong(m.group("Imei")));
		mes.setAlarmType(m.group("AlarmType"));
		mes.setState(m.group("State").charAt(0));
		mes.setLatLetter(m.group("LatLetter").charAt(0));
		mes.setLonLetter(m.group("LonLetter").charAt(0));
		if (!m.group("Speed").isEmpty()) {
			mes.setSpeed(Double.parseDouble(m.group("Speed")));
		}
		if (!m.group("Course").isEmpty()) {
			mes.setCourse(Double.parseDouble(m.group("Course")));
		}
		mes.setPdop(Double.parseDouble(m.group("PDOP")));
		mes.setHdop(Double.parseDouble(m.group("HDOP")));
		mes.setVdop(Double.parseDouble(m.group("VDOP")));
		mes.setStatus(m.group("Status"));
		mes.setRtcDate(parseUtcDate(m.group("RTC")));
		mes.setVoltage(m.group("Voltage"));
		mes.setAdc(m.group("ADC"));
		mes.setLacci(m.group("LACCI"));
		mes.setTemperature(m.group("Temperature"));
		mes.setOdometer(Double.parseDouble(m.group("Odometer")));
		mes.setSerialId(Integer.parseInt(m.group("SerialID")));
	}

	private Date parseUtcDate(String date) {
		try {
			Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
			cal.set(
					Integer.parseInt(date.substring(0, 4)),
					Integer.parseInt(date.substring(4, 6)) - 1,
					Integer.parseInt(date.substring(6, 8)),
					Integer.parseInt(date.substring(8, 10)),
					Integer.parseInt(date.substring(10, 12)),
					Integer.parseInt(date.substring(12)));

			return cal.getTime();
		} catch (Exception e) {
			logger.warn(date, e);
		}
		return null;
	}

	private Date parceGprmcUtcDate(String date, String time) {
		if (date != null && time != null) {
			int yearHundred = ((int) (LocalDate.now().getYear() / 100)) * 100;
			Calendar gprmcCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
			gprmcCalendar.set(
					Integer.parseInt(date.substring(4)) + yearHundred,
					Integer.parseInt(date.substring(2, 4)) - 1,
					Integer.parseInt(date.substring(0, 2)),
					Integer.parseInt(time.substring(0, 2)),
					Integer.parseInt(time.substring(2, 4)),
					Integer.parseInt(time.substring(4, 6)));

			return gprmcCalendar.getTime();
		}
		return null;
	}

	private double convertGprmcCoord(String coord) {
		double p = Double.parseDouble(coord);
		int p1 = (int) (p / 100);
		double p2 = (p - (p1 * 100)) / 60;
		return p1 + p2;
	}
}
