import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class LogParser {
	
	private final static String DIR_OPTION = "-d";
	private final static String FILE_EXTENSION = ".log";
	private final static String SEARCH_WORDS ="(ERROR|WARN|INFO|DEBUG)";
	private HashMap<String, List<String>> fileListWithErrors;
		
	public HashMap<String, List<String>> getFileListWithErrors() {
		return fileListWithErrors;
	}

	public void setFileListWithErrors(
			HashMap<String, List<String>> fileListWithErrors) {
		this.fileListWithErrors = fileListWithErrors;
	}

	public static void main(String[] args) {

		LogParser logParser = new LogParser();

		// Parser Args
		logParser.parseArgs(args);
		HashMap<String, List<String>> fileListWithErrors = logParser.getFileListWithErrors();
		if (fileListWithErrors != null && fileListWithErrors.size() > 0) {
			for (Entry<String, List<String>> entry : fileListWithErrors.entrySet()){
				System.out.println("File Name: " + entry.getKey());
				System.out.println("====================================");
				System.out.println("Error List as follows................");
				for (String str : entry.getValue()) {
					System.out.println(str);
				}
				System.out.println(".....................................................");
			}
		}
	}

	private void parseArgs(String[] args) {
		
		if (args.length == 2) {
			if (args[0].equalsIgnoreCase(DIR_OPTION)) {
				parseDirectoryForFiles(args[0 + 1]);

			} else {
				System.out.println("Invalid usage");
				System.out.println("Usage: " + this.getClass().getName() + " <-d dirname>");
			}
		} else {
			System.out.println("Invalid usage");
			System.out.println("Usage: " + this.getClass().getName() + " <-d dir name>");
		}

	}

	private void parseDirectoryForFiles(String dirName) {
		
		File dir = new File(dirName);
		
		if (dir.isDirectory()){
			if (dir.canRead()) {
				fileListWithErrors = new HashMap<String, List<String>>();
				for (File file : dir.listFiles()) {
					if (file.isFile()) {
						if (file.getName().contains(FILE_EXTENSION)) {
							parseLogFile(file);
						}
					}
				}
			} else  {
				System.out.println(dir.getAbsoluteFile() + ": Permission denied!");
			}
			
		} else {
			System.out.println(dir.getAbsoluteFile() + " is not a directory!");
		}

	}

	private void parseLogFile(File logFile) {
		
		try {
			List<String> lines = new ArrayList<String>();
			String line = null;
			int lineCount = 0;
			FileReader fr = new FileReader(logFile);
			BufferedReader br = new BufferedReader(fr);
			while((line = br.readLine()) != null) {
				Pattern p = Pattern.compile(SEARCH_WORDS);
				Matcher m = p.matcher(line);
				if(m.find()) {
					lines.add((lineCount + 1) + ": " + line);
				}

				lineCount++;
			}
			
			if (lines.size() > 0) {
				fileListWithErrors.put(logFile.getAbsolutePath(), lines);
			}
			
		} catch (IOException e) {
			System.out.println(logFile.getAbsoluteFile() + " is unable to read!");
		}
			
		}
}




