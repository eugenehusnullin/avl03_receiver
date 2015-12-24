package decode;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.junit.Test;

public class TimeTest {
	
	@Test
	public void testUtc() {
		LocalDateTime ldt = LocalDateTime.now(Clock.systemUTC());
		System.out.println(ldt.toString());
		
		Date d = Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
		System.out.println(d.toString());
	}

}
