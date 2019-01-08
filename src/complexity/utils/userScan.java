package complexity.utils;

import java.util.Scanner;

public class userScan {
	public static String scanStr() {
		Scanner sc=new Scanner(System.in);
		String s = sc.nextLine();
		sc.close();
		return s;
	}
}
