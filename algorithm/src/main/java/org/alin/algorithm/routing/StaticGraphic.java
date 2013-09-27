package org.alin.algorithm.routing;

import java.util.Random;

public class StaticGraphic {
	int width;
	int height;
	int[][] matrix; 
	
	
	public StaticGraphic(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public void init(){
		Random random = new Random();
		matrix = new int[width][height];
		for(int i=0;i<height;i++){
			for(int j=0;j<width;j++){
				if(random.nextInt(7) == 6){
					matrix[j][i] = 1;
					System.out.print("X");
				}else{
					System.out.print("O");
				}
				System.out.print(" ");
			}
			System.out.println();
		}
	}
	
	//f(a,b) >= f(a,k) + g(k,b)
	
	public void search(int startX,int startY,int endX,int endY){
		
	}
	//g()
	public void compute(int ax,int ay,int bx,int by){
		
	}
	
//	public void display(){
//		for(int i=0;i<width;i++){
//			for(int j=0;j<height;j++){
//				if(){
//					
//				}
//				System.out.print();
//			}
//			System.out.println();
//		}
//	}
	public static void main(String[] args){
		StaticGraphic sg = new StaticGraphic(15,10);
		sg.init();
	}
}
