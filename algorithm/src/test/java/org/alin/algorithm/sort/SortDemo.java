package org.alin.algorithm.sort;

import org.junit.Test;

public class SortDemo {
	
	private Sort getSort() {
		return new MergeSort();
	}

	private int[] getSortData() {
		return new int[] { 2, 4, 3, 1, 5, 3, 2, 8, 7, 5, 8, 5, 4, 45, 34, 23, 54 };
	}

	@Test
	public void emptySort() {
		Sort sort = getSort();
		sort.sort(null);
	}

	@Test
	public void commonSort() {
		Sort sort = getSort();
		int[] data = getSortData();
		sort.sort(data);
	}

	@Test
	public void mutilSort() {
		int[] data = new int[] { 1, 1, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3, 2, 2, 2, 2, 1, 1, 1, 3, 3, 3, 2, 2, 2 };
		Sort sort = getSort();
		sort.sort(data);
	}

	@Test
	public void singleSort() {
		int[] data = new int[] { 1 };
		Sort sort = getSort();
		sort.sort(data);
	}
}
