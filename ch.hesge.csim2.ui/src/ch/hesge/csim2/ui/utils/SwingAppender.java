package ch.hesge.csim2.ui.utils;

import java.io.Serializable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

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
@Plugin(name = "SwingAppender", category = "Core", elementType = "appender", printObject = true)
public class SwingAppender extends AbstractAppender {

	// Private attributes
	private static JTextArea textArea;
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
	protected SwingAppender(String name, Filter filter, Layout<? extends Serializable> layout, final boolean ignoreExceptions) {
		super(name, filter, layout, ignoreExceptions);
	}

	/**
	 * Set the target JTextArea for the logging information to appear.
	 */
	/**
	 * Define on which swing component logs should be redirected.
	 * 
	 * @param textArea
	 *        the textArea where logs a append
	 */
	public static void setTextArea(JTextArea textArea) {
		SwingAppender.textArea = textArea;
	}

	/**
	 * Format event with current layout a apped log message to the JTextArea
	 * previously registered.
	 */
	@Override
	public void append(LogEvent event) {

		if (textArea != null) {

			try {
				readLock.lock();

				String message = new String(getLayout().toByteArray(event));

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						textArea.append(message);
					}
				});
			}
			catch (Exception ex) {
				if (!ignoreExceptions()) {
					throw new AppenderLoggingException(ex);
				}
			}
			finally {
				readLock.unlock();
			}

		}
	}

	@PluginFactory
	public static SwingAppender createAppender(@PluginAttribute("name") String name, @PluginElement("Layout") Layout<? extends Serializable> layout, @PluginElement("Filter") final Filter filter, @PluginAttribute("otherAttribute") String otherAttribute) {

		if (name == null) {
			LOGGER.error("No name provided for SwingConsole");
			return null;
		}

		if (layout == null) {
			layout = PatternLayout.createDefaultLayout();
		}

		return new SwingAppender(name, filter, layout, true);
	}
}
