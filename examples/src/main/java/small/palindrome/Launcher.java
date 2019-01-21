package small.palindrome;
import gasp.Main;
import gasp.Options;

public class Launcher {
	public static void main(String[] s) {
		final Options o = new Options();
		
		//Pietro's settings
		o.setClasspath("/Users/pietro/Development/eclipse-workspaces/gasp/gasp-examples/bin");
		o.setJBSEPath("/Users/pietro/git/gasp/master/build/libs/gasp-master-0.1.0-SNAPSHOT-shaded.jar");
		o.setZ3Path("/opt/local/bin/z3");

		o.setMethodSignature("small/palindrome/Palindrome:([I)Z:isPalindrome");
		
		final Main m = new Main(o);
		m.run();
	}
}
