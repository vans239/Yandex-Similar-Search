import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DisjointSets {
	private static Random random = new Random();
	List<Integer> similars[];
	int unique;
	private int[] p;

	public DisjointSets(int size) {
		unique = size;
		p = new int[size];
		similars = new ArrayList[size];
		for (int i = 0; i < size; ++i) {
			similars[i] = new ArrayList<Integer>();
			similars[i].add(i);
		}
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
		a = root(a);
		b = root(b);
		if (a != b) {
			similars[a].addAll(similars[b]);
			similars[b].clear();
			p[b] = a;
			--unique;
		}
	}
}