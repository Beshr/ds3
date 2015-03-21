import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class LogWriter {

	public LogWriter(String data, String file) {
		File f = new File(file);
		try {
			if (!f.exists())
				f.createNewFile();
			FileWriter dOut = new FileWriter(f);
			dOut.write(data);
			dOut.close();
		} catch (IOException e) {
			System.err.println("Error in Client logWriter");
		}
	}

	public LogWriter(String[] readerLog, String[] writerLog) {
		File f = new File("serverLog.txt");
		try {
			if (!f.exists())
				f.createNewFile();

			FileWriter dOut = new FileWriter(f);

			dOut.write("Readers:\n");
			dOut.write("sSeq" + "\t" + "oVal" + "\t" + "rID" + "\t" + "rNum\n");
			write(readerLog, dOut);
			dOut.write("Writers:\n");
			dOut.write("sSeq" + "\t" + "oVal" + "\t" + "wID\n");
			write(writerLog, dOut);
			dOut.close();
		} catch (IOException e) {
			System.err.println("Error in logWriter");
		}
	}

	private void write(String[] x, FileWriter dOut) {
		for (int i = 0; i < x.length; i++) {
			try {
				dOut.write(x[i] + "\n");
			} catch (IOException e) {
				System.err.println("Error in writing");
			}
		}
	}
}
