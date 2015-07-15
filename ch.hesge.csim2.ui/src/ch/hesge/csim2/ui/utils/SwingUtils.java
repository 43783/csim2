package ch.hesge.csim2.ui.utils;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.AncestorEvent;

import ch.hesge.csim2.core.utils.Console;
import ch.hesge.csim2.core.utils.StringUtils;

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
	 * Set focus on a component when visible.
	 * 
	 * @param component
	 *        the component to put focus on
	 */
	public static void setFocusWhenVisible(JComponent component) {

		invokeWhenVisible(component, new Runnable() {
			@Override
			public void run() {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						component.requestFocus();
					}
				});
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

	/**
	 * Open the file dialog to select a folder.
	 * 
	 * @param owner
	 *        dialog owner
	 * @param startFolder
	 *        initial folder to display
	 * @return a string if a folder is selected, otherwise null
	 */
	public static String selectFolder(Component owner, String startFolder) {

		JFileChooser dialog = new JFileChooser();
		dialog.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		
		if (startFolder != null) {
			dialog.setCurrentDirectory(new File(startFolder));
		}

		if (dialog.showOpenDialog(owner) == JFileChooser.APPROVE_OPTION) {
			return dialog.getSelectedFile().getAbsolutePath();
		}

		return null;
	}

	/**
	 * Open file dialog to select a single file to open.
	 * 
	 * @param owner
	 *        dialog owner
	 * @param startFolder
	 *        initial folder to display
	 * @return a string if a file is selected, otherwise null
	 */
	public static String selectOpenFile(Component owner, String startFolder) {

		JFileChooser dialog = new JFileChooser();
		dialog.setFileSelectionMode(JFileChooser.FILES_ONLY);

		if (startFolder != null) {
			dialog.setCurrentDirectory(new File(startFolder));
		}

		if (dialog.showOpenDialog(owner) == JFileChooser.APPROVE_OPTION) {
			return dialog.getSelectedFile().getAbsolutePath();
		}

		return null;
	}

	/**
	 * Open file dialog to select a single file to open.
	 * 
	 * @param owner
	 *        dialog owner
	 * @param startFolder
	 *        initial folder to display
	 * @return a string if a file is selected, otherwise null
	 */
	public static String selectSaveFile(Component owner, String startFolder) {

		JFileChooser dialog = new JFileChooser();
		dialog.setFileSelectionMode(JFileChooser.FILES_ONLY);

		if (startFolder != null) {
			dialog.setCurrentDirectory(new File(startFolder));
		}

		if (dialog.showSaveDialog(owner) == JFileChooser.APPROVE_OPTION) {
			return dialog.getSelectedFile().getAbsolutePath();
		}

		return null;
	}

	/**
	 * Open a file with default system editor.
	 * The file can be located in all the hierarchy.
	 * 
	 * @param rootFolder
	 *        root folder to start lookup up
	 * @param filename
	 *        the filename to open
	 */
	public static void openFile(String rootFolder, String filename) {

		if (rootFolder != null) {

			try {
				Files.walkFileTree(Paths.get(rootFolder), new SimpleFileVisitor<Path>() {

					@Override
					public FileVisitResult visitFile(Path filepath, BasicFileAttributes attrs) throws IOException {

						String fileFound = filepath.getFileName().toString().toLowerCase();

						if (fileFound.equals(filename.toLowerCase())) {
							Desktop.getDesktop().open(filepath.toFile());
						}

						return FileVisitResult.CONTINUE;
					}
				});
			}
			catch (IOException e1) {
				Console.writeError(SwingUtils.class, "error while scanning file: '" + filename + "', error = " + StringUtils.toString(e1));
			}
		}
	}
}
