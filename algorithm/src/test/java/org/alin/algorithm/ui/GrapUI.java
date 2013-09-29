package org.alin.algorithm.ui;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;

import javax.swing.JFrame;

import org.alin.algorithm.routing.NodeIterator;
import org.alin.algorithm.routing.StaticGraphic;

public class GrapUI {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int a = 20;
		int b = 20;
		final StaticGraphic sg = new StaticGraphic(a, b);
		sg.init();
		final Canvas canvas = new Canvas() {
			public void paint(Graphics g) {
				for (int i = 0; i < sg.height; i++) {
					for (int j = 0; j < sg.width; j++) {
						if (sg.matrix[i][j] == 1) {
							g.setColor(Color.BLUE);
							g.fillOval(i * 15, j * 15, 10, 10);
						} else {
							g.setColor(Color.BLACK);
							g.fillOval(i * 15, j * 15, 10, 10);
						}
					}
				}
			}

			@Override
			public void update(Graphics g) {
				paint(g);
			}

		};
		Graph graph = new Graph(canvas);
		graph.setVisible(true);
		NodeIterator iterator = sg.search(0, 0, a - 1, b - 1);
		graph.repaint();
	}

	static class Graph extends JFrame {
		Canvas canvas;

		Graph(Canvas canvas) {
			Container container = this.getContentPane();
			setSize(500, 500);
			container.add(canvas);
		}
	}
}
