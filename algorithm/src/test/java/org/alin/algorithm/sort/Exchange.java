package org.alin.algorithm.sort;

import org.junit.Test;

public class Exchange {

	@Test
	public void tmp() {
		long a = 2;
		long b = 3;
		int times = 20000;
		long start = System.nanoTime();
		for (int i = 0; i < times; i++) {
			for (int j = 0; j < times; j++) {
				exchangeBy(a, b);
			}
		}
		long end = System.nanoTime();
		long wei = end - start;
		System.out.println(wei);
		start = System.nanoTime();
		for (int i = 0; i < times; i++) {
			for (int j = 0; j < times; j++) {
				exchangeTmp(a, b);
			}
		}
		end = System.nanoTime();
		long tm = end - start;
		System.out.println(tm);
		System.out.println((tm * 100) / wei);

	}

	public void exchangeBy(long a, long b) {
		 a = a ^ b;
		 b = a ^ b;
		 a = a ^ b;
	}

	public void exchangeTmp(long a, long b) {
		long tmp;
		tmp = a;
		a = b;
		b = tmp;
	}

}
