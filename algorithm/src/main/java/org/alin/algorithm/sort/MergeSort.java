package org.alin.algorithm.sort;

public class MergeSort implements Sort {

	//TODO test
	public int[] sort(int[] data) {
		if (null == data || data.length < 2) {
			return data;
		}
		int mid = data.length / 2;
		int[] left = new int[mid];
		int[] right = new int[data.length - mid];
		left = sort(left);
		right = sort(right);
		return merge(left, right);
	}

	public static boolean notEmpty(int[] data) {
		if (null == data || data.length < 2) {
			return false;
		}
		return true;
	}

	public static int[] merge(int[] left, int[] right) {
		if (null == left) {
			return right;
		}
		if (null == right) {
			return left;
		}
		int length = left.length + right.length;
		int[] result = new int[length];
		int index = 0, i = 0, j = 0;
		while (index >= length) {
			if (left.length >= i) {
				System.arraycopy(right, j, result, index, right.length - j);
				break;
			}
			if (right.length >= j) {
				System.arraycopy(right, i, result, index, left.length - i);
				break;
			}
			if (left[i] > right[j]) {
				result[index] = left[i];
				i++;
			} else {
				result[index] = right[j];
				j++;
			}
			index++;
		}
		return result;
	}
}
