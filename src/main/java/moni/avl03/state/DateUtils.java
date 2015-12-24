package moni.avl03.state;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class DateUtils {

	static public Date getDateInUtc() {
		LocalDateTime ldt = LocalDateTime.now(Clock.systemUTC());
		return Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
	}

}
