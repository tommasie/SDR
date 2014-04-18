package it.sp4te.util;

public class Utils {

	public static int mcd(int a, int b) {
		if (a == b)
			return a;
		if(a > b) {
			if (b==0) return a;
			return mcd(b, a % b);
		}
		else return mcd(b,a);
	}
}
