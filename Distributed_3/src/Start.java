import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Scanner;

public class Start {

	public static void main(String[] args) {

		final Configuration config = new Configuration("config.txt");

		// run the servers
		Runnable r = new Runnable() {
			@Override
			public void run() {
				try {
					new Server((short) config.getServerPort(),
							config.getNumOfAccesses(),
							config.getNumberOfReaders(),
							config.getNumberOfWriters(),
							config.getServerAddrs());
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		};
		Thread t = new Thread(r);
		t.start();
		// run clients
		String path = "/tmp/Distributed_3";
		// run readers
		ArrayList<ClientObj> allClients = config.getClients();
		String argss = "";
		try {
			for (int i = 0; i < allClients.size(); i++) {
				ClientObj client = allClients.get(i);
				argss = "-Djava.security.manager -Djava.security.policy=rmi.policy -Djava.rmi.server.hostname="
						+ client.getAddress().split("@")[1];
					
				System.out.println(config.getServerPort());
				Process pro= Runtime.getRuntime().exec(
						"ssh " + client.getAddress() + " cd \"" + path
								+ "/bin\" ;java " + argss + " Client "
								+ client.getId() + " "
								+ config.getServerAddrs() + " "
								+ config.getServerPort() + " "
								+ client.getType() + " "
								+ config.getNumOfAccesses());
				Scanner scr1 = new Scanner(pro.getErrorStream());
				 while (scr1.hasNextLine())
				 System.out.println(scr1.nextLine());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
