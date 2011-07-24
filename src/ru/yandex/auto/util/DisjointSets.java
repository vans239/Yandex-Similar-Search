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
		//compression heuristics
		if (x != p[x])
			p[x] = root(p[x]);
		return p[x];
	}

	public void unite(int a, int b) {
		int rootA = root(a);
		int rootB = root(b);
		// rank heuristics
		if (random.nextInt() % 2 == 0) {
			p[rootB] = rootA;
		} else {
			p[rootA] = rootB;
		}
	}
}