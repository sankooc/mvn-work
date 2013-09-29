package org.alin.algorithm.ui;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Panel;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.ImageObserver;
import java.text.AttributedCharacterIterator;
import java.util.LinkedList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import org.alin.algorithm.routing.NodeIterator;
import org.alin.algorithm.routing.StaticGraphic;

public class GridGraphic extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	StaticGraphic graphic = new StaticGraphic();
	final StaticGraphic sg = new StaticGraphic();
	class OptionBar extends Panel {
		OptionBar() {
			LayoutManager layout = new GridLayout(5, 2, 10, 3);
			this.setLayout(layout);
			add(new JLabel("h size", SwingConstants.CENTER));
			final JTextField text1 = new JTextField("20", 0);
			add(text1);
			add(new JLabel("v size", SwingConstants.CENTER));
			final JTextField text2 = new JTextField("20", 0);
			add(text2);
			add(new JLabel("argorithm", SwingConstants.CENTER));
			add(new JComboBox(new Object[] { "A*" }));
			JButton ramdom = new JButton("Random create");
			ramdom.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					int x = Integer.parseInt(text1.getText());
					int y = Integer.parseInt(text2.getText());
					graphic.setWidth(x);
					graphic.setHeight(y);
					init();
				}
			});
			start = new JButton("path finding");
			start.setEnabled(false);
			start.addActionListener(new ActionListener() {
				
				public void actionPerformed(ActionEvent e) {
					Container container = GridGraphic.this.getContentPane();
					Grid start = list.get(0);
					Grid end = list.get(1);
					System.out.println(start.x+":"+start.y);
					System.out.println(end.x+":"+end.y);
					NodeIterator iterator = graphic.search(start.x, start.y, end.x, end.y);
					GridGraphic.this.printRoute(iterator);
				}
			});
			add(ramdom);
			add(start);
			this.setSize(this.getWidth(), 180);
//			optionBar.layout();
			this.doLayout();
			this.setBackground(Color.CYAN);
//			this.setColor(Color.CYAN);
//			JLabel status_label = new JLabel("",SwingConstants.CENTER));
//			JProgressBar progress = new JProgressBar();
//			add(progress,BorderLayout.SOUTH);
		}

		
		@Override
		public int getHeight() {
			return 180;
		}

		
		
	}

	public void init() {
		canvas.removeAll();
		graphic.init();
		canvas.setLayout(new GridLayout(graphic.width, graphic.height));
		for (int i = 0; i < graphic.height; i++) {
			for (int j = 0; j < graphic.width; j++) {
				if (graphic.matrix[j][i] == 1) {
					canvas.add(new Grid(Color.BLACK,i,j));
				} else {
					canvas.add(new Grid(Color.BLUE,i,j));
				}
			}
		}
		canvas.doLayout();
		flesh();
	}

	Panel canvas;
	OptionBar optionBar;
	JProgressBar progress;
	
	final LinkedList<Grid> list = new LinkedList<Grid>();
	JButton start;
	public void flesh(){
		
	}
	
	public void setGrid(Grid grid ,boolean flag){
		if(flag){
			list.add(grid);
			if(list.size() == 2){
				start.setEnabled(true);
			}
		}else{
			list.remove(grid);
			start.setEnabled(false);
		}
	}
	
	
	public GridGraphic() throws HeadlessException {
		super();
		setSize(500, 500);
		Container container = this.getContentPane();
		BoxLayout layout = new BoxLayout(container,BoxLayout.Y_AXIS);
		container.setLayout(layout);
		optionBar = new OptionBar();
		optionBar.setBounds(0, 0, 500, 200);
		 container.add(optionBar);
		canvas = new Panel();
		progress = new JProgressBar();
		progress.setValue(50);
		container.add(progress);
		canvas.setBackground(Color.WHITE);
		container.add(canvas);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				dispose();
				System.exit(0);
			}
		});
	}

	public void path(int x, int y) {
		Container container = this.getContentPane();
		Grid component = (Grid) ((Container)(container.getComponent(2))).getComponent(y * 20 + x);
		component.color = Color.red;
		component.repaint();
	}

	class Grid extends Canvas {
		private static final long serialVersionUID = -3660331000321506014L;
		Color color;
		boolean flag;
		int x;
		int y;
		Grid(Color color,int x,int y) {
			this.color = color;
			this.x =x;
			this.y = y;
			this.addMouseListener(new MouseAdapter() {

				@Override
				public void mouseClicked(MouseEvent e) {
					if(list.size() >1 & !flag){
						return;
					}
					flag = !flag;
					setGrid(Grid.this, flag);
					if(flag){
						setColorCircle(Grid.this.getGraphics(),Color.red);
					}else{
						setColorCircle(Grid.this.getGraphics(),Grid.this.color);
					}
				}
			});
		}

		void setColorCircle(Graphics g,Color color){
			g.setColor(color);
			Dimension demension = getSize();
			g.fillOval(demension.width / 2, demension.height / 2, demension.width / 2, demension.height / 2);
		
		}
		
		@Override
		public void paint(Graphics g) {
			super.paint(g);
			setColorCircle(g,color);
		}

	}

//	public void addGrid(Color color) {
//		Container container = this.getContentPane();
//		container.add(new Grid(color));
//		container.repaint();
//
//	}

	public void printRoute(NodeIterator path)  {
		if (null != path.getLastNode()) {
			printRoute(path.getLastNode());
		}
		path(path.getX(), path.getY());
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		GridGraphic graphic = new GridGraphic();
		graphic.setVisible(true);
		// graphic.printRoute(iterator);
	}

}
