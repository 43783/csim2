package ch.hesge.csim2.ui.utils;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.AncestorEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

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
	 * Register an action to execute when a component become visible.
	 * 
	 * @param component
	 *        the component we are listening to
	 * @param doRun
	 *        the action to execute
	 */
	public static void onComponentVisible(JComponent component, SimpleAction<?> action) {

		component.addAncestorListener(new AncestorAdapter() {
			@Override
			public void ancestorAdded(AncestorEvent event) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						action.run(null);
					}
				});
			}
		});
	}

	/**
	 * Register an action to execute when a component is resized.
	 * 
	 * @param component
	 *        the component we are listening to
	 * @param doRun
	 *        the action to execute
	 */
	public static void onComponentResized(JComponent component, SimpleAction<ComponentEvent> action) {

		component.addComponentListener(new ComponentListener() {
			@Override
			public void componentResized(ComponentEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						action.run(e);
					}
				});
			}

			@Override
			public void componentShown(ComponentEvent e) {
			}

			@Override
			public void componentMoved(ComponentEvent e) {
			}

			@Override
			public void componentHidden(ComponentEvent e) {
			}
		});
	}

	/**
	 * Register an action to execute when a component lost focus.
	 * 
	 * @param component
	 *        the component we are listening to
	 * @param doRun
	 *        the action to execute
	 */
	public static void onFocusLost(JComponent component, SimpleAction<FocusEvent> action) {

		component.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
			}

			@Override
			public void focusLost(FocusEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						action.run(e);
					}
				});
			}
		});
	}

	/**
	 * Register an action to execute when a component gain focus.
	 * 
	 * @param component
	 *        the component we are listening to
	 * @param doRun
	 *        the action to execute
	 */
	public static void onFocusGained(JComponent component, SimpleAction<FocusEvent> action) {

		component.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						action.run(e);
					}
				});
			}

			@Override
			public void focusLost(FocusEvent e) {
			}
		});
	}

	/**
	 * Register an action to execute when a component received a key pressed
	 * event.
	 * 
	 * @param component
	 *        the component we are listening to
	 * @param doRun
	 *        the action to execute
	 */
	public static void onKeyPressed(JComponent component, SimpleAction<KeyEvent> action) {

		component.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						action.run(e);
					}
				});
			}
		});
	}

	/**
	 * Register an action to execute when a component received a mouse pressed
	 * event.
	 * 
	 * @param component
	 *        the component we are listening to
	 * @param doRun
	 *        the action to execute
	 */
	public static void onMousePressed(JComponent component, SimpleAction<MouseEvent> action) {

		component.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						action.run(e);
					}
				});
			}
		});
	}

	/**
	 * Register an action to execute when a component received a mouse pressed
	 * event.
	 * 
	 * @param component
	 *        the component we are listening to
	 * @param doRun
	 *        the action to execute
	 */
	public static void onMouseReleased(JComponent component, SimpleAction<MouseEvent> action) {

		component.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						action.run(e);
					}
				});
			}
		});
	}

	/**
	 * Register an action to execute when a component received a mouse move
	 * event.
	 * 
	 * @param component
	 *        the component we are listening to
	 * @param doRun
	 *        the action to execute
	 */
	public static void onMouseMoved(JComponent component, SimpleAction<MouseEvent> action) {

		component.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseMoved(MouseEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						action.run(e);
					}
				});
			}
		});
	}

	/**
	 * Register an action to execute when a component received a mouse drag
	 * event.
	 * 
	 * @param component
	 *        the component we are listening to
	 * @param doRun
	 *        the action to execute
	 */
	public static void onMouseDragged(JComponent component, SimpleAction<MouseEvent> action) {

		component.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						action.run(e);
					}
				});
			}
		});
	}

	/**
	 * Register an action to execute when a component received a mouse wheel
	 * event.
	 * 
	 * @param component
	 *        the component we are listening to
	 * @param doRun
	 *        the action to execute
	 */
	public static void onMouseWheeled(JComponent component, SimpleAction<MouseWheelEvent> action) {

		component.addMouseWheelListener(new MouseWheelListener() {
			public void mouseWheelMoved(MouseWheelEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						action.run(e);
					}
				});
			}
		});
	}

	/**
	 * Register an action to execute on table item selection.
	 * 
	 * @param table
	 *        the JTable we are listening to
	 * @param action
	 *        the action to execute
	 */
	public static void onTableSelection(JTable table, SimpleAction<ListSelectionEvent> action) {
		
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						action.run(e);
					}
				});	
			}
		});
	}
	
	/**
	 * Register an action to execute on table click.
	 * 
	 * @param table
	 *        the JTable we are listening to
	 * @param action
	 *        the action to execute
	 */
	public static void onTableSingleClick2(JTable table, SimpleAction<MouseEvent> action) {

		table.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {

				if (SwingUtilities.isLeftMouseButton(e)) {

					table.requestFocus();
					int clickedRow = table.rowAtPoint(e.getPoint());

					// Select row under the mouse
					if (clickedRow != -1) {
						table.setRowSelectionInterval(clickedRow, clickedRow);
					}

					// Detect double-click
					if (e.getClickCount() == 1) {

						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								action.run(e);
							}
						});
					}

					e.consume();
				}
			}
		});
	}

	/**
	 * Register an action to execute on table double-click.
	 * 
	 * @param table
	 *        the JTable we are listening to
	 * @param action
	 *        the action to execute
	 */
	public static void onTableDoubleClick(JTable table, SimpleAction<MouseEvent> action) {

		table.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {

				if (SwingUtilities.isLeftMouseButton(e)) {

					table.requestFocus();
					int clickedRow = table.rowAtPoint(e.getPoint());

					// Select row under the mouse
					if (clickedRow != -1) {
						table.setRowSelectionInterval(clickedRow, clickedRow);
					}

					// Detect double-click
					if (e.getClickCount() == 2) {

						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								action.run(e);
							}
						});
					}

					e.consume();
				}
			}
		});
	}

	/**
	 * Register an action to execute on table single-click.
	 * 
	 * @param table
	 *        the JTable we are listening to
	 * @param action
	 *        the action to execute
	 */
	public static void onTableRightClick(JTable table, SimpleAction<MouseEvent> action) {

		table.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {

				if (SwingUtilities.isRightMouseButton(e)) {

					table.requestFocus();
					int clickedRow = table.rowAtPoint(e.getPoint());

					// Select row under the mouse
					if (clickedRow != -1) {
						table.setRowSelectionInterval(clickedRow, clickedRow);
					}

					// Detect double-click
					if (e.getClickCount() == 1) {

						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								action.run(e);
							}
						});
					}

					e.consume();
				}
			}
		});
	}

	/**
	 * Retrieve the user object underlying a mouse coordinate.
	 * 
	 * @param tree
	 *        The JTree to analyze
	 * @param x
	 *        The x coordinate
	 * @param y
	 *        The y coordinate
	 * @return
	 *         a user object or null
	 */
	public static Object getTreeUserObject(JTree tree, int x, int y) {

		Object userObject = null;
		TreePath path = tree.getPathForLocation(x, y);

		if (path != null) {

			DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();

			if (node != null) {
				userObject = node.getUserObject();
			}
		}

		return userObject;
	}
	
	/**
	 * Retrieve the object, within a JTree, under the coordinates
	 * passed in argument.
	 * 
	 * @param path
	 *        The JTree path
	 * @return
	 *         a user object or null
	 */
	public static Object getTreeUserObject(TreePath path) {

		Object userObject = null;

		if (path != null) {

			DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();

			if (node != null) {
				userObject = node.getUserObject();
			}
		}

		return userObject;
	}
	
	/**
	 * Register an action to execute on tree item selection.
	 * 
	 * @param tree
	 *        the JTree we are listening to
	 * @param action
	 *        the action to execute
	 */
	public static void onTreeSelection(JTree tree, SimpleAction<TreeSelectionEvent> action) {
		
		tree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						action.run(e);
					}
				});	
			}
		});
	}
	
	/**
	 * Register an action to execute on tree click.
	 * 
	 * @param tree
	 *        the JTree we are listening to
	 * @param action
	 *        the action to execute
	 */
	public static void onTreeSingleClick2(JTree tree, SimpleAction<MouseEvent> action) {

		tree.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {

				if (SwingUtilities.isLeftMouseButton(e)) {

					tree.requestFocus();
					int clickedRow = tree.getRowForLocation(e.getX(), e.getY());

					// Select row under the mouse
					if (clickedRow != -1) {
						tree.setSelectionRow(clickedRow);
					}

					// Detect double-click
					if (e.getClickCount() == 1) {

						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								action.run(e);
							}
						});
					}

					e.consume();
				}
			}
		});
	}

	/**
	 * Register an action to execute on tree double-click.
	 * 
	 * @param tree
	 *        the JTree we are listening to
	 * @param action
	 *        the action to execute
	 */
	public static void onTreeDoubleClick(JTree tree, SimpleAction<MouseEvent> action) {

		tree.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {

				if (SwingUtilities.isLeftMouseButton(e)) {

					tree.requestFocus();
					int clickedRow = tree.getRowForLocation(e.getX(), e.getY());

					// Select row under the mouse
					if (clickedRow != -1) {
						tree.setSelectionRow(clickedRow);
					}

					// Detect double-click
					if (e.getClickCount() == 2) {

						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								action.run(e);
							}
						});
					}

					e.consume();
				}
			}
		});
	}

	/**
	 * Register an action to execute on tree single-click.
	 * 
	 * @param table
	 *        the JTree we are listening to
	 * @param action
	 *        the action to execute
	 */
	public static void onTreeRightClick(JTree tree, SimpleAction<MouseEvent> action) {

		tree.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {

				if (SwingUtilities.isRightMouseButton(e)) {

					tree.requestFocus();
					int clickedRow = tree.getRowForLocation(e.getX(), e.getY());

					// Select row under the mouse
					if (clickedRow != -1) {
						tree.setSelectionRow(clickedRow);
					}

					// Detect double-click
					if (e.getClickCount() == 1) {

						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								action.run(e);
							}
						});
					}

					e.consume();
				}
			}
		});
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
	public static boolean openFile(String rootFolder, String filename) {

		final List<Path> pathFound = new ArrayList<>();
		
		if (rootFolder != null) {
			
			try {
				
				// Scan file system for filename
				Files.walkFileTree(Paths.get(rootFolder), new SimpleFileVisitor<Path>() {

					@Override
					public FileVisitResult visitFile(Path filepath, BasicFileAttributes attrs) throws IOException {

						String visitedName = filepath.getFileName().toString().toLowerCase();

						if (visitedName.equals(filename.toLowerCase())) {
							pathFound.add(filepath);
						}

						return FileVisitResult.CONTINUE;
					}
				});
				
				// If a file has been found, try to open it
				if (!pathFound.isEmpty()) {
					Desktop.getDesktop().open(pathFound.get(0).toFile());
				}
				
			}
			catch (IOException e1) {
				Console.writeError(SwingUtils.class, "error while scanning file: '" + filename + "', error = " + StringUtils.toString(e1));
			}
		}
		
		return !pathFound.isEmpty();
	}
}
