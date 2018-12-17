/*
 * version date author 
 * ────────────────────────────────── 
 * 1.0  Nov 27, 2012 Neal Miao 
 * 
 * Copyright(c) 2012, by HTWater. All Rights Reserved.
 */
package cn.miao.framework.log;

import org.apache.log4j.Appender;
import org.apache.log4j.Layout;
import org.apache.log4j.spi.ErrorHandler;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;

/**
 * 
 * 
 * @author Neal Miao
 * @version
 * @since v 1.0
 * @Date Nov 27, 2012 11:11:19 AM
 * 
 * @see
 */
public class MyLogAppender implements Appender {

	/**
	 * 
	 */
	public MyLogAppender() {
	}

	/* (non-Javadoc)
	 * @see org.apache.log4j.Appender#addFilter(org.apache.log4j.spi.Filter)
	 */
	public void addFilter(Filter arg0) {

	}

	/* (non-Javadoc)
	 * @see org.apache.log4j.Appender#clearFilters()
	 */
	public void clearFilters() {

	}

	/* (non-Javadoc)
	 * @see org.apache.log4j.Appender#close()
	 */
	public void close() {

	}

	/* (non-Javadoc)
	 * @see org.apache.log4j.Appender#doAppend(org.apache.log4j.spi.LoggingEvent)
	 */
	public void doAppend(LoggingEvent arg0) {

	}

	/* (non-Javadoc)
	 * @see org.apache.log4j.Appender#getErrorHandler()
	 */
	public ErrorHandler getErrorHandler() {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.apache.log4j.Appender#getFilter()
	 */
	public Filter getFilter() {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.apache.log4j.Appender#getLayout()
	 */
	public Layout getLayout() {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.apache.log4j.Appender#getName()
	 */
	public String getName() {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.apache.log4j.Appender#requiresLayout()
	 */
	public boolean requiresLayout() {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.apache.log4j.Appender#setErrorHandler(org.apache.log4j.spi.ErrorHandler)
	 */
	public void setErrorHandler(ErrorHandler arg0) {

	}

	/* (non-Javadoc)
	 * @see org.apache.log4j.Appender#setLayout(org.apache.log4j.Layout)
	 */
	public void setLayout(Layout arg0) {

	}

	/* (non-Javadoc)
	 * @see org.apache.log4j.Appender#setName(java.lang.String)
	 */
	public void setName(String arg0) {

	}

}
