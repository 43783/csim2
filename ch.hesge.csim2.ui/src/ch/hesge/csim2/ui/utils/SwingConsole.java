package ch.hesge.csim2.ui.utils;

import java.io.Serializable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.swing.JTextArea;

import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.appender.AppenderLoggingException;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;

@SuppressWarnings("serial")
@Plugin(name = "SwingConsole", category = "Core", elementType = "appender", printObject = true)
public class SwingConsole extends AbstractAppender {

	// Private attributes
	private static JTextArea swingConsole;
	private final ReadWriteLock rwLock = new ReentrantReadWriteLock();
	private final Lock readLock = rwLock.readLock();

	/**
	 * Default constructor.
	 * 
	 * @param name
	 * @param filter
	 * @param layout
	 * @param manager
	 * @param ignoreExceptions
	 */
	protected SwingConsole(String name, Filter filter, Layout<? extends Serializable> layout, final boolean ignoreExceptions) {
		super(name, filter, layout, ignoreExceptions);
	}

	/**
	 * Set the target JTextArea for the logging information to appear.
	 */
	public static void setTextArea(JTextArea textArea) {
		swingConsole = textArea;
	}

	/*
	 * Format and then append the loggingEvent to the stored
	 * JTextArea.
	 */
	// The append method is where the appender does the work.
	// Given a log event, you are free to do with it what you want.
	// This example demonstrates:
	// 1. Concurrency: this method may be called by multiple threads concurrently
	// 2. How to use layouts
	// 3. Error handling
	@Override
	public void append(LogEvent event) {

		readLock.lock();
		try {
			final byte[] bytes = getLayout().toByteArray(event);
			System.out.write(bytes);
		}
		catch (Exception ex) {
			if (!ignoreExceptions()) {
				throw new AppenderLoggingException(ex);
			}
		}
		finally {
			readLock.unlock();
		}

		//		if (swingConsole != null) {
		//			
		//			final String message = "this.getLayout().format(loggingEvent)";
		//
		//			// Append formatted message to TextArea using the Swing Thread.
		//			SwingUtilities.invokeLater(new Runnable() {
		//				public void run() {
		//					swingConsole.append(message);
		//				}
		//			});
		//		}

	}

	@PluginFactory
	public static SwingConsole createAppender(@PluginAttribute("name") String name, @PluginElement("Layout") Layout<? extends Serializable> layout, @PluginElement("Filter") final Filter filter, @PluginAttribute("otherAttribute") String otherAttribute) {
		if (name == null) {
			LOGGER.error("No name provided for MyCustomAppenderImpl");
			return null;
		}
		if (layout == null) {
			layout = PatternLayout.createDefaultLayout();
		}
		return new SwingConsole(name, filter, layout, true);
	}
}
