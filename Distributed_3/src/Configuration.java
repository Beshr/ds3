import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class Configuration {

	private String serverAddrs;
	private int serverPort;
	private int numberOfReaders, numberOfWriters;
	private ArrayList<ClientObj> allClients;
	private int numOfAccesses;

	public Configuration(String fileName) {

		File file = new File(fileName);
		if (file.exists()) {
			allClients = new ArrayList<ClientObj>();
			// reading the file
			Scanner sc = null;
			try {
				sc = new Scanner(file);
				byte count = 0;
				while (sc.hasNextLine()) {
					String line = sc.nextLine().trim();
					String[] data = line.split("=");
					if (count == 0) {// server address
						serverAddrs = data[1].trim();
					} else if (count == 1) {
						serverPort = Integer.parseInt(data[1].trim());
					} else {
						// get number of readers
						numberOfReaders = Integer.parseInt(data[1].trim());
						// get addresses of all readers
						fill(numberOfReaders, sc, "read", 0);
						// get number of writers
						numberOfWriters = Integer.parseInt(sc.nextLine().trim()
								.split("=")[1].trim());
						fill(numberOfWriters, sc, "write", numberOfReaders);
						// read number of accesses
						numOfAccesses = Integer.parseInt(sc.nextLine().trim()
								.split("=")[1].trim());
						break;

					}
					count++;
				}
				sc.close();

			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("Error in reading file");
			}
		} else
			System.err.println("File Not Found");
	}

	private void fill(int count, Scanner sc, String type, int base) {
		for (int i = 0; i < count; i++)
			allClients.add(new ClientObj(sc.nextLine().trim().split("=")[1]
					.trim(), type, base + i));
	}

	public String getServerAddrs() {
		return serverAddrs;
	}

	public int getServerPort() {
		return serverPort;
	}

	public int getNumberOfReaders() {
		return numberOfReaders;
	}

	public int getNumberOfWriters() {
		return numberOfWriters;
	}

	public int getNumOfAccesses() {
		return numOfAccesses;
	}

	public ArrayList<ClientObj> getClients() {
		Collections.shuffle(allClients);
		return allClients;
	}

	public static void main(String[] args) {
		Configuration configuration = new Configuration("config.txt");
		System.out.println(configuration.getServerAddrs());
		System.out.println(configuration.getServerPort());
		System.out.println(configuration.getNumberOfReaders());

	}
}
