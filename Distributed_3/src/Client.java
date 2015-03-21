import java.rmi.Naming;

public class Client {

	public Client(String ID, String serverAddr, String portNo, String type,
			String numOfaccess) {
		// initialization
		String message = "";
		String log = "Client type: ";
		if (type.equals("write")) {
			message = "write," + ID;
			log += "Writer\nClient Name: " + ID + "\nrSeq\tsSeq\n";
		} else {
			message = "read," + ID;
			log += "Reader\nClient Name: " + ID + "\nrSeq\tsSeq\toVal\n";
		}
		//Modification New Look-Up
		// initialize RMI
		RMI_Interface stub = null;
		try {
			if (System.getSecurityManager() == null)
				System.setSecurityManager(new SecurityManager());
			stub = (RMI_Interface) Naming.lookup("//" + serverAddr + ":"
					+ portNo + "/Server");
			System.out.println("Looking up is done");
		} catch (Exception e1) {
			e1.printStackTrace();
			return;
		}
		try {
			long random = (long) (Math.random() * 500);
			while (random < 250)
				random = (long) (Math.random() * 500);
			for (int i = 0; i < Integer.parseInt(numOfaccess); i++) {
				String resp = "";
				resp = stub.processRequest(message);
				log += resp + "\n";
				while (random < 250)
					random = (long) (Math.random() * 500);
				Thread.sleep(random);
			}
			new LogWriter(log, "log" + ID + ".txt");
		} catch (Exception e) {
			System.err.println("Error in sending request");
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new Client(args[0], args[1], args[2], args[3], args[4]);
	}
}
