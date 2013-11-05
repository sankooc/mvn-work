package org.alin.algorithm.routing;

import java.util.TreeSet;

public class Astar extends PathAlgorithm {
	protected Astar(int width, int height) {
		super(width, height);
	}

	boolean[][] visit;
	int[][] gf;

	public Astar() {
		super(20, 20);
	}

	public void init() {
		gf = new int[width][height];
		visit = new boolean[width][height];
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				gf[j][i] = Integer.MAX_VALUE;
			}
		}
	}

	TreeSet<Cnode> set = new TreeSet<Cnode>();

	@Override
	public NodeIterator search(int startX, int startY, int endX, int endY) {
		init();
		this.endx = endX;
		this.endy = endY;
		matrix[startX][startY] = false;
		matrix[endX][endY] = false;
		set.clear();
		push(startX, startY, null);
		// return find();
		while (true) {
			if (set.isEmpty()) {
				return null;
			}
			Cnode node = set.first();
			lastNode = node;
			set.remove(node);
			visit[node.x][node.y] = true;
			if (node.x == endx && node.y == endy) {
				return node;
			}

			if (node.x > 0) {
				push(node.x - 1, node.y, node);
			}
			if (node.x < width - 1) {
				push(node.x + 1, node.y, node);
			}
			if (node.y > 0) {
				push(node.x, node.y - 1, node);
			}
			if (node.y < height - 1) {
				push(node.x, node.y + 1, node);
			}
		}
	}

	Cnode lastNode;

	public Cnode find() {
		if (set.isEmpty()) {
			return null;
		}
		Cnode node = set.first();
		lastNode = node;
		set.remove(node);
		visit[node.x][node.y] = true;
		if (node.x == endx && node.y == endy) {
			return node;
		}

		if (node.x > 0) {
			push(node.x - 1, node.y, node);
		}
		if (node.x < width - 1) {
			push(node.x + 1, node.y, node);
		}
		if (node.y > 0) {
			push(node.x, node.y - 1, node);
		}
		if (node.y < height - 1) {
			push(node.x, node.y + 1, node);
		}
		return find();
	}

	public double compute(double ax, double ay, double bx, double by) {
		return Math.sqrt((ax - ay) * (ax - ay) + (bx - by) * (bx - by));
	}

	public void push(int x, int y, Cnode from) {

		if (visit[x][y]) {
			return;
		}
		if (!matrix[x][y]) {
			Cnode node = new Cnode(x, y, from);
			set.add(node);
			if (null == from) {
				return;
			}
			int length = from.getLength();
			length++;
			if (node.getLength() > length) {
				node.setLength(length);
				node.lastNode = from;
			}
		}
	}

	class Cnode implements Comparable<Cnode>, NodeIterator {
		Cnode(int x, int y, Cnode from) {
			this.x = x;
			this.y = y;
			this.lastNode = from;
			g = compute(x, y, endx, endy);
			if (null != from) {
				g += from.getLength();
			} else {
				setLength(0);
			}
		}

		int x;
		int y;
		double g;
		Cnode lastNode;

		public Cnode getLastNode() {
			return lastNode;
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}

		public int compareTo(Cnode o) {
			if (equals(o)) {
				return 0;
			}
			double r = o.g - g;
			return r < 0 ? 1 : -1;
		}

		public int getLength() {
			return gf[x][y];
		}

		public void setLength(int length) {
			gf[x][y] = length;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result;
			result = prime * result + x;
			result = prime * result + y;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			Cnode other = (Cnode) obj;
			if (x != other.x)
				return false;
			if (y != other.y)
				return false;
			return true;
		}

	}
}
