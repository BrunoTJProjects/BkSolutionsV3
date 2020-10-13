package utils;

import java.io.IOException;

import br.com.bksolutionsdomotica.modelo.SocketBase;

public class TimeOut extends Thread {
	SocketBase socketBase = null;

	public TimeOut(SocketBase socketBase) {
		this.socketBase = socketBase;
	}

	@Override
	public void run() {
		do {
			try {
				sleep(6000);
				socketBase.sendCommand("\0");
				sleep(1000);
			} catch (IOException | InterruptedException e) {

				e.printStackTrace();
				try {
					socketBase.closeResouces();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				break;
			}
		} while (true);
	}
}
