package org.alin.algorithm.routing;
import org.junit.Test;
public class RoutingTest {
	@Test
	public void testAstar(){
		int a = 2000;
		int b = 2000;
		Astar algro = new Astar();
		algro.setWidth(a);
		algro.setHeight(b);
		algro.createRandomMatrix();
		long start = System.currentTimeMillis();
		NodeIterator path = algro.search(0, 0, a-1, b-1);
		long end = System.currentTimeMillis();
		System.out.println("compute "+a +" * " +b+" matrix cost time :"+(end-start)+"nanotime");
		StringBuilder builder = new StringBuilder();
		printRoute(path,builder);
		System.out.println(builder.toString());
	}
	void printRoute(NodeIterator path,StringBuilder builder){
		if(null != path.getLastNode()){
			printRoute(path.getLastNode(),builder);
		}
//		builder.append("("+path.getX()+","+path.getY()+")\r");
	}
}
