package ch.hesge.csim2.ui.utils;

import java.awt.Component;
import java.awt.Cursor;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.AncestorEvent;

import com.alee.utils.swing.AncestorAdapter;

/**
 * This is a general purpose utility class dedicated to swing utility functions.
 * 
 * Copyright HEG Geneva 2014, Switzerland
 * 
 * @author Eric Harth
 */
public class SwingUtils {

	/**
	 * Register a keystroke handler with its associated action.
	 * If the keystroke is detected, the action passed in argument is executed.
	 * 
	 * @param component
	 *        the component where the key will be performed
	 * @param key
	 *        the key to detect
	 * @param name
	 *        the name of the registered action handler
	 * @param action
	 *        the action the execute while key is pressed
	 */
	public static void setInputKeyAction(JComponent component, int key, final String name, AbstractAction action) {

		// Avoid NullPointerException in WindowBuilder
		if (component != null) {

			// Retrieve key to handle
			KeyStroke keyStroke = KeyStroke.getKeyStroke(key, 0);

			// Replace keyStroke in input map
			component.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(keyStroke, name);

			// Register action in actin map
			component.getActionMap().put(name, action);
		}
	}

	/**
	 * Register an action to execute after the component is made visible.
	 * 
	 * @param component
	 *        the component we are listening to
	 * @param doRun
	 *        the runnable to execute when component is visible
	 */
	public static void invokeWhenVisible(JComponent component, Runnable doRun) {

		component.addAncestorListener(new AncestorAdapter() {
			@Override
			public void ancestorAdded(AncestorEvent event) {
				doRun.run();
			}
		});
	}

	/**
	 * Execute a long operation with waiting cursor.
	 * 
	 * @param component
	 *        the component where the wait cursor will be displayed
	 * @param task
	 *        the long task to execute
	 */
	public static void invokeLongOperation(JComponent component, Runnable doRun) {

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					component.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					doRun.run();
				}
				finally {
					component.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				}
			}
		});
	}

	/**
	 * Returns first parent which is instance of specified class type or null if
	 * none found.
	 *
	 * @param component
	 *        component to look parent for
	 * @param parentClass
	 *        class of the parent to lookup for
	 * @return
	 *         the parent or null
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getFirstParent(final Component component, final Class<T> parentClass) {

		Component parent = component.getParent();

		while (!parentClass.isInstance(parent) && parent != null) {
			parent = parent.getParent();
		}

		return (T) parent;
	}
}
