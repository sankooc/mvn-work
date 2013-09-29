package org.alin.algorithm.routing;

import java.util.Random;
import java.util.TreeMap;
import java.util.TreeSet;

public class StaticGraphic {
	public int width;
	public int height;
	public int[][] matrix;
	boolean[][] visit;
	int endx;
	int endy;

	public StaticGraphic(int width, int height) {
		this.width = width;
		this.height = height;
	}
	public StaticGraphic() {
	}
	
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public void init() {
		Random random = new Random();
		matrix = new int[width][height];
		gf = new int[width][height];
		visit = new boolean[width][height];
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				gf[j][i] = Integer.MAX_VALUE;
				if (random.nextInt(7) == 6) {
					matrix[j][i] = 1;
//					System.out.print("X");
				} else {
//					System.out.print("O");
				}
			}
//			System.out.println();
		}
	}

	// f(a,b) >= f(a,k) + g(k,b)
	int[][] gf;

	public void push(int x, int y, Cnode from) {

		if(visit[x][y]){
			return;
		}
		if (matrix[x][y] == 0) {
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

	TreeSet<Cnode> set = new TreeSet<Cnode>();
	Cnode lastNode; 
	public Cnode find() {
		if (set.isEmpty()) {
			return null;
		}
		Cnode node = set.first();
		lastNode = node;
		set.remove(node);

		if (node.x == endx && node.y == endy) {
			return node;
		}

		if (node.x > 0) {
			push(node.x - 1, node.y, node);
		}
		if (node.x < endx) {
			push(node.x + 1, node.y, node);
		}
		if (node.y > 0) {
			push(node.x, node.y - 1, node);
		}
		if (node.y < endy) {
			push(node.x, node.y + 1, node);
		}
		return find();
	}

	public NodeIterator search(int startX, int startY, int endX, int endY) {
		this.endx = endX;
		this.endy = endY;
		matrix[startX][startY] = 0;
		matrix[endX][endY] = 0;
		set.clear();
		push(startX, startY, null);
		return find();
	}

	class Cnode implements Comparable<Cnode>, NodeIterator {
		Cnode(int x, int y, Cnode from) {
			this.x = x;
			this.y = y;
			this.lastNode = from;
			g = compute(x, y, endx, endy);
			if (null != from) {
				g += from.getLength();
			}else{
				setLength(0);
			}
			visit[x][y] = true;
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
			if(equals(o)){
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
			result = prime * result + getOuterType().hashCode();
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
			if (getClass() != obj.getClass())
				return false;
			Cnode other = (Cnode) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (x != other.x)
				return false;
			if (y != other.y)
				return false;
			return true;
		}

		private StaticGraphic getOuterType() {
			return StaticGraphic.this;
		}
		
	}

	// g()
	public double compute(double ax, double ay, double bx, double by) {
		return Math.sqrt((ax - ay) * (ax - ay) + (bx - by) * (bx - by));
	}

	// public void display(){
	// for(int i=0;i<width;i++){
	// for(int j=0;j<height;j++){
	// if(){
	//
	// }
	// System.out.print();
	// }
	// System.out.println();
	// }
	// }
	public static void main(String[] args) {
		int a = 120;
		int b = 100;
		StaticGraphic sg = new StaticGraphic(a, b);
		sg.init();
		long start = System.nanoTime();
		NodeIterator path = sg.search(0, 0, a-1, b-1);
		long end = System.nanoTime();
		System.out.println("compute "+a +" * " +b+" matrix cost time :"+(end-start)+"nanotime");
		sg.set.clear();
		StringBuilder builder = new StringBuilder();
		printRoute(path,builder);
		System.out.println(builder.toString());
		
	}
	
	public static void printRoute(NodeIterator path,StringBuilder builder){
		if(null != path.getLastNode()){
			printRoute(path.getLastNode(),builder);
		}
		builder.append("("+path.getX()+","+path.getY()+")\r");
	}
}
