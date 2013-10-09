package org.alin.algorithm.ui;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.LayoutManager;
import java.awt.Panel;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.alin.algorithm.routing.Astar;
import org.alin.algorithm.routing.NodeIterator;
import org.alin.algorithm.routing.PathAlgorithm;

public class GridGraphic extends JFrame {
	
	private static final long serialVersionUID = 1L;

	private PathAlgorithm graphic = new Astar();

	boolean created;

	class OptionBar extends Panel {

		private static final long serialVersionUID = 5419234652517916636L;

		int getValue(String v, int dv) {
			if (null == v) {
				return dv;
			}
			try {
				return Integer.parseInt(v);
			} catch (NumberFormatException e) {
				return dv;
			}
		}

		JLabel startLabel;
		JLabel endLabel;

		OptionBar() {
			LayoutManager layout = new GridLayout(6, 2, 8, 8);
			this.setLayout(layout);
			add(new JLabel("horizontal size :", SwingConstants.RIGHT));
			final JTextField text1 = new JTextField("10", 0);
			add(text1);
			add(new JLabel("vertical size :", SwingConstants.RIGHT));
			final JTextField text2 = new JTextField("10", 0);
			add(text2);
			add(new JLabel("argorithm", SwingConstants.RIGHT));
			final JComboBox combo = new JComboBox(new Object[] { "A*" });
			combo.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					int inx = combo.getSelectedIndex();
					if (inx == 0) {
						// TODO
					}

				}
			});
			add(combo);
			JButton ramdom = new JButton("Random create");
			ramdom.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					int x = getValue(text1.getText(), 10);
					int y = getValue(text2.getText(), 10);
					graphic.setWidth(x);
					graphic.setHeight(y);
					startLabel.setText("");
					endLabel.setText("");
					init();
				}
			});
			start = new JButton("path finding");
			start.setEnabled(false);
			start.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Point start = path.get(0);
					Point end = path.get(1);
					long start_time = System.currentTimeMillis();
					NodeIterator iterator = graphic.search(start.x, start.y, end.x, end.y);
					long cost_time = System.currentTimeMillis() - start_time;
					startLabel.setText(String.format("(x%s:y%s)->(x%s:y%s)", start.x, start.y, end.x, end.y));
					endLabel.setText((cost_time + "ms"));
					if (null != iterator) {
						GridGraphic.this.printRoute(iterator);
						created = false;
					} else {
						JDialog dialog = new JDialog(GridGraphic.this, "cannot find path", true);
						dialog.setVisible(true);
					}
				}
			});
			add(ramdom);
			add(start);
			add(new JLabel("Path", SwingConstants.RIGHT));
			startLabel = new JLabel("", SwingConstants.CENTER);
			add(startLabel);
			add(new JLabel("cost time", SwingConstants.RIGHT));
			endLabel = new JLabel("", SwingConstants.CENTER);
			add(endLabel);
		}

	}

	int pix = 10;

	public void init() {
		graphic.createRandomMatrix();
		path.clear();
		int oh = canvas.getSize().width;
		int ov = canvas.getSize().height;
		int offset = getSize().height - ov;
		int hHint = graphic.getWidth();
		int vHint = graphic.getHeight();
		oh = Math.max(500, (hHint * pix));
		ov = Math.max(500 - offset, (vHint * pix));
		oh = Math.max((hHint * pix), oh);
		ov = Math.max((vHint * pix), ov);
		setSize(oh + 5, ov + offset);
		doLayout();
		canvas.repaint();
		created = true;
	}

	Canvas canvas;
	OptionBar optionBar;
	JProgressBar progress;
	final LinkedList<Point> path = new LinkedList<Point>();
	JButton start;

	public void drawFlagPoint(Point point) {
		drawFlagPoint(point.x, point.y);
	}

	public void drawFlagPoint(int x, int y) {
		drawPoint(x, y, Color.RED);
	}

	public void drawPoint(int x, int y, Color color) {
		Graphics g = canvas.getGraphics();
		g.setColor(color);
		g.fillOval(x * pix, y * pix, pix, pix);
	}

	public void createPoint(int j, int i) {
		if (graphic.isBlock(j, i)) {
			drawPoint(j, i, Color.BLACK);
		} else {
			drawPoint(j, i, Color.BLUE);
		}
	}

	public GridGraphic() throws HeadlessException {
		super();
		setSize(500, 500);
		setResizable(false);
		Container container = this.getContentPane();
		LayoutManager layout = new BorderLayout();
		container.setLayout(layout);
		optionBar = new OptionBar();
		container.add(optionBar, BorderLayout.NORTH);
		canvas = new Canvas() {
			private static final long serialVersionUID = 1156764343758198602L;

			public void paint(Graphics g) {
				super.paint(g);
				for (int i = 0; i < graphic.height; i++) {
					for (int j = 0; j < graphic.width; j++) {
						if ((i + j) % 2 > 0) {
							g.setColor(Color.CYAN);
							g.fillRect(j * pix, i * pix, pix, pix);
						} else {
							g.setColor(Color.WHITE);
							g.fillRect(j * pix, i * pix, pix, pix);
						}
						createPoint(j, i);

					}
				}

			}

		};

		canvas.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (!created) {
					return;
				}

				int hhint = e.getX() / pix;
				int vhint = e.getY() / pix;
				Point p = new Point(hhint, vhint);

				if (path.size() > 1 && !path.contains(p)) {
					return;
				}
				if (path.contains(p)) {
					path.remove(p);
					createPoint(p.x, p.y);
				} else {
					path.add(p);
					drawFlagPoint(p);
				}
				if (path.size() == 2) {
					start.setEnabled(true);
				} else {
					start.setEnabled(false);
				}
			}
		});
		container.add(canvas, BorderLayout.CENTER);

		progress = new JProgressBar();
		progress.setValue(50);
		container.add(progress, BorderLayout.SOUTH);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				dispose();
				System.exit(0);
			}
		});
	}

	public void printRoute(NodeIterator path) {
		if (null != path.getLastNode()) {
			printRoute(path.getLastNode());
		}
		drawFlagPoint(path.getX(), path.getY());
	}

	/**
	 * @param args
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException {
		GridGraphic graphic = new GridGraphic();
		graphic.setVisible(true);
	}

}
