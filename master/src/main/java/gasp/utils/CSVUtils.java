package gasp.utils;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

public class CSVUtils {

	private static final char SEPARATOR = ',';
	
	public static void writeLine(Writer w, List<String> values) throws IOException {
		writeLine(w, values, SEPARATOR, ' ');
	}
	
	public static void writeLine(Writer w, List<String> values, char separator) throws IOException {
		writeLine(w, values, separator, ' ');
	}
	
	private static String followCSVformat(String value) {
		return value.replace("\"", "\"\"");
	}
	
	public static void writeLine(Writer w, List<String> values, Character separator, Character customQuote) throws IOException {
		boolean first = true;
		
		if(separator == null) {
			separator = SEPARATOR;
		}
		
		StringBuilder sb = new StringBuilder();
		for(String value : values) {
			if(!first) {
				sb.append(separator);
			}
			if (customQuote == null) {
				sb.append(followCSVformat(value));
			}
			else {
				sb.append(customQuote).append(followCSVformat(value)).append(customQuote);
			}
			first = false;
		}
		
		sb.append("\n");
		w.append(sb.toString());
	}
	
}
