
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
public class DisjointSets {
	private static Random random = new Random();
	List<Integer> similars[];
	int[] p;
	public DisjointSets(int size) {
		p = new int[size];
		similars = new ArrayList[size];
		for(int i = 0; i < size; ++i){
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
/*		if (random.nextInt() % 2 == 1) {
			p[a] = b;
		} else {
			p[b] = a;
		}*/
		similars[a].addAll(similars[b]);

		similars[b].clear();
	}
}