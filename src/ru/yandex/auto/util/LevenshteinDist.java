package ru.yandex.auto.util;

public class LevenshteinDist {
	public static int getDist(String a, String b) {
		a = a.replaceAll("[, '\\u00A0''\\u2007''\\u202F'-]", "");
		b = b.replaceAll("[, '\\u00A0''\\u2007''\\u202F'-]", "");
		int sizeA = a.length();
		int sizeB = b.length();
		int dist[][] = new int[sizeA + 1][];
		for (int i = 0; i <= sizeA; ++i)
			dist[i] = new int[sizeB + 1];
		for (int i = 0; i <= sizeA; ++i)
			for (int j = 0; j <= sizeB; ++j) {
				if (i == 0 || j == 0) {
					dist[i][j] = Math.max(i, j);
				} else {
					if (a.charAt(i - 1) == b.charAt(j - 1)) {
						dist[i][j] = dist[i - 1][j - 1];
					} else {
						dist[i][j] = dist[i - 1][j] + 1;
						dist[i][j] = Math.min(dist[i][j], dist[i][j - 1] + 1);
						dist[i][j] = Math.min(dist[i][j], dist[i - 1][j - 1] + 1);
					}
				}
			}
		return dist[sizeA][sizeB];
	}

	public static void main(String argv[]) {
		String b = "Mercedes-Benz E-Класс";
		String a = "Mercedes-Benz E-Klasse";
		String str = a.substring(0, b.length());
		/*
			mercedes benz e-class  mercedes-benz e-220
			mercedes benz e-superclass  mercedes-benz e-220
		 */

		System.out.println(a + " " + b);
		System.out.println(LevenshteinDist.getDist(a, b) + "  " + Math.max(a.length(), b.length()) / 4
				+ " " + LevenshteinDist.getDist(str, b) + " " + b.length() / 4);
		System.out.println(getDist(a, b));
	}
}
