import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class Server extends UnicastRemoteObject implements RMI_Interface {

	
	private static final long serialVersionUID = 1L;
	private short portNo;
	private static int news/**/;
	private int numOfAccesses;
	private ArrayList<String> readersLog/**/, writersLog/**/;
	private int seqNo/**/, rseq;
	private int countRequests;
	private int totalRequests;
	private String serverAddress;

	private Semaphore x, y, z, wSem, rSem;
	private static int readCount, writeCount;

	public Server(short portNo, int numOfAccesses, int rNum, int wNum,
			String serverAddress) throws RemoteException {
		super();
		this.portNo = portNo;
		this.numOfAccesses = numOfAccesses;

		readersLog = new ArrayList<String>(rNum * this.numOfAccesses);
		writersLog = new ArrayList<String>(wNum * this.numOfAccesses);

		news = -1;
		seqNo = 0;
		rseq = 0;
		countRequests = 0;
		totalRequests = (rNum + wNum) * numOfAccesses;
		this.serverAddress = serverAddress;
		x = new Semaphore(1);
		y = new Semaphore(1);
		z = new Semaphore(rNum * numOfAccesses);
		wSem = new Semaphore(1);
		rSem = new Semaphore(rNum * numOfAccesses);
		readCount = 0;
		writeCount = 0;

		/*
		 * initialize server with registry
		 */
		try {
			LocateRegistry.createRegistry(this.portNo);
			Naming.rebind("//" + serverAddress + "/Server", this);
			System.out.println("Server ready");
		} catch (Exception e) {
			System.err.println("Server exception: " + e.toString());
			e.printStackTrace();
		}

	}

	private String reader(String response, int id) {
		try {
			z.acquire();
			rSem.acquire();
			x.acquire();
			readCount++;
			if (readCount == 1)
				wSem.acquire();
			x.release();
			rSem.release();
			z.release();
			long random = (long) (Math.random() * 1000);
			while (random < 500)
				random = (long) (Math.random() * 1000);
			Thread.sleep(random);
			seqNo++;
			response = rseq + "\t" + seqNo + "\t" + news;
			readersLog.add("" + seqNo + "\t" + "" + news + "\t" + "" + id
					+ "\t" + readCount);
			String temp = response;
			x.acquire();
			readCount--;
			if (readCount == 0)
				wSem.release();
			x.release();
			return temp;
		} catch (Exception e) {
			System.err.println("Exception in reader");
			return "";
		}

	}

	private String writer(String response, int id) {
		try {
			y.acquire();
			writeCount++;
			if (writeCount == 1)
				rSem.acquire();
			y.release();
			wSem.acquire();
			long random = (long) (Math.random() * 1000);
			while (random < 500)
				random = (long) (Math.random() * 1000);
			Thread.sleep(random);
			news = id;
			seqNo++;
			response = rseq + "\t" + seqNo;
			writersLog.add("" + seqNo + "\t" + "" + news + "\t" + "" + id);
			String temp = response;
			wSem.release();
			y.acquire();
			writeCount--;
			if (writeCount == 0)
				rSem.release();
			y.release();
			return temp;
		} catch (Exception e) {
			System.err.println("Error in write");
			return "";
		}
	}

	//Modification New Interface Implementation
	@Override
	public String processRequest(String request) throws RemoteException {
		// TODO Auto-generated method stub
		rseq++;
		String response = "";
		String[] splitR = request.split(",");
		int id = Integer.parseInt(splitR[1]);
		String type = splitR[0];
		System.out.println("Client Invoke the Code"+id);
		if (type.equalsIgnoreCase("read")) {
			response = reader(response, id);
			System.out.println("Reader"+id);
		} else if (type.equalsIgnoreCase("write")) {
			response = writer(response, id);
			System.out.println("Writer"+id);
		}
		countRequests++;
		if (countRequests == totalRequests) {
			// write log
			new LogWriter(readersLog.toArray(new String[readersLog.size()]),
					writersLog.toArray(new String[writersLog.size()]));
			try {
				Naming.unbind("//" + serverAddress + "/Server");
				System.out.println("Unbinding the server");
			} catch (Exception e) {
				System.out.println("Error in Finishing Server");
				e.printStackTrace();
			}
		}
		return response;
	}

}
