package org.demoncode.portal.util;

import org.apache.log4j.Appender;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Layout;
import org.apache.log4j.PatternLayout;

import de.mindpipe.android.logging.log4j.LogCatAppender;

public class LogUtils {
	public static void configLog4j() {
		Layout layout = new PatternLayout("%d{HH:mm:ss,SSS} [%t] %-5p %c{1} - %m%n");
		Appender appender = new LogCatAppender(layout);
		BasicConfigurator.configure(appender);
	}
}
