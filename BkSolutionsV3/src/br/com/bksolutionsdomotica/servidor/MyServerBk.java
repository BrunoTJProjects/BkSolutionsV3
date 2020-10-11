package br.com.bksolutionsdomotica.servidor;

import java.io.IOException;
import java.sql.SQLException;

import org.json.JSONObject;

import br.com.bksolutionsdomotica.conexaobd.BKClienteDAO;
import br.com.bksolutionsdomotica.conexaobd.BKHardwareDAO;
import br.com.bksolutionsdomotica.manager.ClientsManager;
import br.com.bksolutionsdomotica.modelo.Cliente;
import br.com.bksolutionsdomotica.modelo.Hardware;
import br.com.bksolutionsdomotica.modelo.SocketBase;

public class MyServerBk implements ServerCoreBK.InterfaceCommand {

	private ServerCoreBK server;
	private static BKHardwareDAO bkHardwareDAO = new BKHardwareDAO();
	private static BKClienteDAO bkClienteDAO = new BKClienteDAO();
	private static ClientsManager gerenciador = new ClientsManager();

	public MyServerBk(int port) {
		server = new ServerCoreBK(port, this);
		try {
			server.init();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// M�todo criado para Debug juntamente com o Servidor.java
	public static ClientsManager getGerenciador() {
		return gerenciador;
	}

	@Override
	public boolean hardwareLogado() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onHardwareSignIn(SocketBase socketBase, String login, String password)
			throws ClassNotFoundException, SQLException, IOException {
		Hardware hardware = bkHardwareDAO.getHardware(login, password);
		if (hardware != null) {
			socketBase.setHardware(hardware);
			socketBase.sendCommand("Hardware login was Successful");
			gerenciador.addHardware(socketBase);
		}
	}

	@Override
	public void onHardwareSignOut(SocketBase socketBase) throws ClassNotFoundException, SQLException, IOException {
		
	}

	@Override
	public void onHardwareCommand(SocketBase socketBase, JSONObject jsonObject) throws IOException, ClassNotFoundException, SQLException {
		gerenciador.onHardwareCommand(socketBase, jsonObject);
	}

	@Override
	public boolean clienteLogado() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onClienteSignIn(SocketBase socketBase, String login, String password)
			throws ClassNotFoundException, SQLException, IOException {
		Cliente cliente = bkClienteDAO.logarCliente(login, password);
		if (cliente != null) {
			socketBase.setCliente(cliente);
			socketBase.sendCommand("Cliente login was Successful");
			gerenciador.addCliente(socketBase);
		}
	}

	@Override
	public void onClienteSignOut(SocketBase socketBase) throws ClassNotFoundException, SQLException, IOException {
		// TODO Auto-generated method stub
		System.out.println("Logout Cliente");
	}

//		ATEN��O AQUI
	@Override
	public void onClienteCommand(SocketBase socketBase, JSONObject comando)
			throws IOException, ClassNotFoundException, SQLException {

		gerenciador.onClienteCommand(socketBase, comando);
	}

	public static synchronized int getCodCliente(Hardware hardware) throws SQLException, ClassNotFoundException {
		int codCliente = bkHardwareDAO.getCodCliente(hardware);
		return codCliente;
	}

}