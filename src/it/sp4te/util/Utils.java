package it.sp4te.util;

public class Utils {

	public static int mcd(int a, int b) {
		if(a > b) {
			if (b==0) return a;
			return mcd(b, a % b);
		}
		else return mcd(b,a);
	}
	
	public static void main(String[] args) {
		System.out.println(mcd(42,56));
	}
}
