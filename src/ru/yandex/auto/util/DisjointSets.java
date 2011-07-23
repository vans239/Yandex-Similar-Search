package ru.yandex.auto.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DisjointSets {
	private static Random random = new Random();
	private int[] p;

	public DisjointSets(int size) {
		p = new int[size];
		for (int i = 0; i < size; i++) {
			p[i] = i;
		}
	}

	public int root(int x) {
		while (x != p[x])
			x = p[x];
		return x;
	}

	public void unite(int a, int b) {
		int rootA = root(a);
		int rootB = root(b);
		if (rootA != rootB) {
			p[rootB] = rootA;
			//compression heuristics
			p[a] = rootA;
			p[b] = rootA;
		}
	}
}