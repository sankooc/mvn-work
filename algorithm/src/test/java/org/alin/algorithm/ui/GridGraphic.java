package org.alin.algorithm.ui;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.LayoutManager;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import org.alin.algorithm.routing.NodeIterator;
import org.alin.algorithm.routing.StaticGraphic;

public class GridGraphic extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	final StaticGraphic sg = new StaticGraphic();
	class OptionBar extends Panel {
		OptionBar() {
			LayoutManager layout = new GridLayout(5, 2, 10, 10);

			this.setLayout(layout);
			add(new JLabel("h size", SwingConstants.CENTER));
			final JTextArea text1 = new JTextArea("20", 1, 4);
			add(text1);
			add(new JLabel("v size", SwingConstants.CENTER));
			final JTextArea text2 = new JTextArea("20", 1, 4);
			add(text2);
			add(new JLabel("argorithm", SwingConstants.CENTER));
			add(new JComboBox(new Object[] { "A*" }));
			JButton ramdom = new JButton("Random create");
			ramdom.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					
				}
			});
			JButton start = new JButton("path finding");
			add(ramdom);
			add(start);
			add(new JProgressBar());
		}
	}

	public void init(int x, int y, int[][] matrix) {
		canvas.setLayout(new GridLayout(x, y));
		for (int i = 0; i < y; i++) {
			for (int j = 0; j < x; j++) {
				if (matrix[i][j] == 1) {
					canvas.add(new Grid(Color.BLACK));
				} else {
					canvas.add(new Grid(Color.BLUE));
				}
			}
		}

	}

	Panel canvas;

	public GridGraphic(int[][] matrix) throws HeadlessException {
		super();
		setSize(500, 500);
		Container container = this.getContentPane();
		container.setLayout(new BorderLayout());
		container.add(new OptionBar(), BorderLayout.NORTH);
		canvas = new Panel();
		container.add(canvas, BorderLayout.SOUTH);

		// for (int i = 0; i < 20; i++) {
		// for (int j = 0; j < 20; j++) {
		// if (matrix[i][j] == 1) {
		// container.add(new Grid(Color.BLACK));
		// } else {
		// container.add(new Grid(Color.BLUE));
		// }
		// }
		// }
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				dispose();
				System.exit(0);
			}
		});
	}

	public void path(int x, int y) {
		Container container = this.getContentPane();
		Grid component = (Grid) container.getComponent(y * 20 + x);
		component.color = Color.red;
		component.repaint();
	}

	class Grid extends Canvas {
		private static final long serialVersionUID = -3660331000321506014L;
		Color color;

		Grid(Color color) {
			this.color = color;
		}

		@Override
		public void paint(Graphics g) {
			g.setColor(color);
			super.paint(g);
			Dimension demension = getSize();
			g.fillOval(demension.width / 2, demension.height / 2, demension.width / 2, demension.height / 2);
		}

	}

	public void addGrid(Color color) {
		Container container = this.getContentPane();
		container.add(new Grid(color));
		container.repaint();

	}

	public void printRoute(NodeIterator path) throws InterruptedException {
		if (null != path.getLastNode()) {
			printRoute(path.getLastNode());
		}
		path(path.getX(), path.getY());
		Thread.sleep(100);
	}

	/**
	 * @param args
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException {
		int a = 20;
		int b = 20;
		final StaticGraphic sg = new StaticGraphic(a, b);
		sg.init();
		// NodeIterator iterator = sg.search(0, 0, a - 1, b - 1);
		GridGraphic graphic = new GridGraphic(sg.matrix);
		graphic.setVisible(true);
		// graphic.printRoute(iterator);
	}

}
