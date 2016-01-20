package decode;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import moni.avl03.decode.InfoDecoder;
import moni.avl03.domain.InfoMessage;

public class InfoDecoderTest {

	private InfoDecoder createInfoDecoder() {
		return new InfoDecoder();
	}

	@Test
	public void testGprmc() {
		// src: $$B2359772031093009|AA$GPRMC,210006.000,A,5542.2011,N,03741.1583,E,0.00,,111215,,,A*78|01.7|01.0|01.3|000000000000|20151211210006|14291311|00000000|1E5177FE|0000|0.0000|0375|5920
		// dst: $$B6359772031093009|AA$GPRMC,210006.000,A,5542.2012,N,3741.1582,E,0.00,0.00,111215,0,N,A*2A|1.7|1.0|1.3|000000000000|20151211210006|14291311|00000000|1E5177FE|0000|0.0000|0375|||7B3A
		String s = "$$B2359772031093009|AA$GPRMC,210006.000,A,5542.2011,N,03741.1583,E,0.00,,111215,,,A*78|01.7|01.0|01.3|000000000000|20151211210006|14291311|00000000|1E5177FE|0000|0.0000|0375|5920";

		InfoDecoder decoder = createInfoDecoder();
		InfoMessage mes = (InfoMessage) decoder.decode(s);

		Assert.assertEquals(359772031093009L, mes.getImei());
		Assert.assertEquals(55.703352, mes.getLat(), 0.000001);
		Assert.assertEquals(37.685972, mes.getLon(), 0.000001);
		Assert.assertEquals('N', mes.getLatLetter());
		Assert.assertEquals('E', mes.getLonLetter());
		Assert.assertEquals("AA", mes.getAlarmType());
		
		ZonedDateTime zdt = ZonedDateTime.of(2015, 12, 11, 21, 0, 6, 0, ZoneId.of("UTC"));
		Assert.assertEquals(Date.from(zdt.toInstant()), mes.getNavDate());
	}

	@Test
	public void testGlonass() {
		String s = "$$A5359772039720983|88UA1255.71022N037.76457E000022|01.2|00.7|01.0|20151001205956|20151001205956|101001000000|14151276|00000000|1E698B19|0000|0.0000|0522||00000|CBCB";
		InfoDecoder decoder = createInfoDecoder();
		InfoMessage mes = (InfoMessage) decoder.decode(s);

		Assert.assertEquals(359772039720983L, mes.getImei());
	}
}
