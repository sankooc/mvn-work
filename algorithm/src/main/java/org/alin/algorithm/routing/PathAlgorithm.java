package org.alin.algorithm.routing;

import java.util.Random;

public abstract class PathAlgorithm {
	public int width = 20;
	public int height = 20;
	public boolean[][] matrix;
	int endx;
	int endy;
	int startx;
	int stopx;
	final Random random = new Random();

	protected PathAlgorithm(int width, int height) {
		this.width = width;
		this.height = height;
		createEmptyMatrix();
	}

	public boolean isBlock(int x, int y) {
		return matrix[x][y];
	}

	public void setMatrix(int x, int y, boolean value) {
		matrix[x][y] = value;
	}

	public void createEmptyMatrix() {
		matrix = new boolean[width][height];
	}

	public void createRandomMatrix() {
		createEmptyMatrix();
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				if (random.nextInt(7) == 6) {
					matrix[j][i] = true;
				} else {
				}
			}
		}
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

	public int getEndx() {
		return endx;
	}

	public void setEndx(int endx) {
		this.endx = endx;
	}

	public int getEndy() {
		return endy;
	}

	public void setEndy(int endy) {
		this.endy = endy;
	}

	public int getStartx() {
		return startx;
	}

	public void setStartx(int startx) {
		this.startx = startx;
	}

	public int getStopx() {
		return stopx;
	}

	public void setStopx(int stopx) {
		this.stopx = stopx;
	}

	public abstract NodeIterator search(int startX, int startY, int endX, int endY);
}
