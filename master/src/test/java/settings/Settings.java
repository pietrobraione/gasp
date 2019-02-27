package settings;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Settings {
	public static final Path GASP_HOME = Paths.get("/Users", "pietro", "git", "gasp");
	public static final List<Path> CLASSPATH = new ArrayList<>(); 
	static {
		CLASSPATH.add(GASP_HOME.resolve("master/build/classes/java/test"));
	}
	public static final Path JBSE_PATH = GASP_HOME.resolve("jbse/build/classes/java/main"); 
	public static final Path Z3_PATH = Paths.get("/opt", "local", "bin", "z3");

}
