package utils;

import java.io.IOException;
import java.util.List;

import br.com.bksolutionsdomotica.modelo.SocketBase;

public class TimeOut extends Thread {
	List<SocketBase> socketsBase;

	public TimeOut(List<SocketBase> socketsBase) {
		this.socketsBase = socketsBase;
	}

	@Override
	public void run() {
		do {
			try {
				sleep(6000);
			} catch (InterruptedException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			for (SocketBase sb : socketsBase) {
				try {
					sb.sendCommand("\0");
					sleep(0, 100);
				} catch (IOException | InterruptedException e) {

					e.printStackTrace();
					try {
						sb.closeResouces();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		} while (true);
	}
}

//package utils;
//
//import java.io.IOException;
//
//import br.com.bksolutionsdomotica.modelo.SocketBase;
//
//public class TimeOut extends Thread {
//	SocketBase socketBase = null;
//
//	public TimeOut(SocketBase socketBase) {
//		this.socketBase = socketBase;
//	}
//
//	@Override
//	public void run() {
//		do {
//			try {
//				sleep(6000);
//				socketBase.sendCommand("\0");
//				sleep(1000);
//			} catch (IOException | InterruptedException e) {
//
//				e.printStackTrace();
//				try {
//					socketBase.closeResouces();
//				} catch (IOException e1) {
//					e1.printStackTrace();
//				}
//				break;
//			}
//		} while (true);
//	}
//}
