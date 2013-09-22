package org.alin.algorithm.sort;

public class QuickSort implements Sort {

	public int[] sort(int[] data) {
		if (null == data) {
			return data;
		}
		sort(data, 0, data.length - 1);
		return data;
	}

	public static void sort(int[] data, final int start, final int end) {
		if (start >= end) {
			return;
		}
		final int pivot = data[(start + end) / 2];

		int cursorLeft = start;
		int cursorRight = end;

		while (cursorLeft < cursorRight) {
			while (cursorLeft <= cursorRight && data[cursorLeft] <= pivot) {
				cursorLeft++;
			}
			if (data[cursorLeft] == pivot) {
				cursorLeft--;
			}
			while (cursorLeft <= cursorRight && data[cursorRight] > pivot) {
				cursorRight--;
			}
			if (data[cursorLeft] == pivot) {
				cursorLeft--;
			}
			if (cursorLeft < cursorRight) {
				data[cursorLeft] = data[cursorLeft] ^ data[cursorRight];
				data[cursorRight] = data[cursorLeft] ^ data[cursorRight];
				data[cursorLeft] = data[cursorLeft] ^ data[cursorRight];
			} else {
				break;
			}
		}
		sort(data, start, cursorLeft - 1);
		sort(data, cursorRight + 1, end);
	}
}
