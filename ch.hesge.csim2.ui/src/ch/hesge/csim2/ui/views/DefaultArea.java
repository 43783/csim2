package ch.hesge.csim2.ui.views;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CGrid;
import bibliothek.gui.dock.common.CGridArea;
import bibliothek.gui.dock.common.DefaultSingleCDockable;

public class DefaultArea {

	public static final float LEFTRIGHT_SPLIT = 0.33f;
	public static final float TOPBOTTOM_SPLIT = 0.75f;

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(500, 500);
		frame.setLayout(new BorderLayout());
		final CControl control = new CControl(frame);

		frame.add(control.getContentArea(), BorderLayout.CENTER);

		final CGridArea area = control.createGridArea("area");
		final CGridArea leftArea = control.createGridArea("left");
		final CGridArea bottomRightArea = control.createGridArea("bottom");

		CGrid grid = new CGrid();
		grid.add(0, 0, LEFTRIGHT_SPLIT * (1 / LEFTRIGHT_SPLIT), 3, leftArea);
		grid.add(1, 0, (1 - LEFTRIGHT_SPLIT) * (1 / LEFTRIGHT_SPLIT), 2, area);
		grid.add(1, 1, (1 - LEFTRIGHT_SPLIT) * (1 / LEFTRIGHT_SPLIT), 1,
				bottomRightArea);
		control.getContentArea().deploy(grid);
		
		List<DefaultSingleCDockable> children = new ArrayList<>();

		JMenuBar bar = new JMenuBar();
		JMenu menu = new JMenu("Menu");
		bar.add(menu);
		JMenuItem item1 = new JMenuItem("Open");
		item1.addActionListener(new ActionListener() {
			private int i = 0;

			public void actionPerformed(ActionEvent e) {
				String id = String.valueOf(i++);
				DefaultSingleCDockable dockable = new DefaultSingleCDockable(
						id, id);
				control.addDockable(dockable);
				dockable.setLocation(area.getStationLocation());
				dockable.setVisible(true);
				children.add(dockable);
			}
		});

		menu.add(item1);

		JMenuItem item2 = new JMenuItem("Close All");
		item2.addActionListener(new ActionListener() {
			private int i = 0;

			public void actionPerformed(ActionEvent e) {
				
				for (DefaultSingleCDockable dockable : children) {
					dockable.setVisible(false);
				}
			}
		});
		menu.add(item2);

		frame.setJMenuBar(bar);

		frame.setVisible(true);
	}
}
