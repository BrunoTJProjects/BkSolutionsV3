package br.com.bksolutionsdomotica.manager;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import br.com.bksolutionsdomotica.modelo.SocketBase;

public class UserHardwares {
	
	private static final String LOG_OUT = "Você foi desconectado";

	private static final String CODE_REQUEST_GETKEYS = "getKeys";
	private static final String CODE_REQUEST_GETKEY = "getKey";
	private static final String CODE_REQUEST_SETKEYS = "setKeys";
	private static final String CODE_REQUEST_SETKEY = "setKey";
	private static final String CODE_REQUEST_DELETEKEY = "deleteKey";

	private static final String MAC_KEY = "mac";
	private static final String CHAVE_KEY = "key";
	private static final String VALUE_KEY = "value";
	private static final String CHAVE_KEYS = "keys";

	private static final String HARDWARE_NOT_CONNECTED = "Este dispositivo não está conectado";
	private static final String SUCESS_MSG = "ok";

	private List<SocketBase> clientes = new ArrayList<SocketBase>();
	private HashMap<String, SocketBase> hardwares = new HashMap<String, SocketBase>();

	public void onClienteCommand(SocketBase cliente, JSONObject comando)
			throws IOException, ClassNotFoundException, SQLException {
		String mac = comando.getString(MAC_KEY);

		if (mac == null || mac.isEmpty() || !contemHardware(mac)) {
			cliente.sendCommand(HARDWARE_NOT_CONNECTED);
			return;
		}

		String request = comando.getString("request");
		SocketBase hardware;
		String key;

		switch (request) {
		case CODE_REQUEST_GETKEYS:
			cliente.sendCommand(cliente.getCliente().getChaves(mac).toString());
			break;
		case CODE_REQUEST_GETKEY:
			key = comando.getString(CHAVE_KEY);
			cliente.sendCommand(cliente.getCliente().getChave(mac, key));
			break;
		case CODE_REQUEST_SETKEYS:
			JSONObject keys = comando.getJSONObject(CHAVE_KEYS);
			cliente.getCliente().setChaves(mac, keys);
			hardware = hardwares.get(mac);
			hardware.sendCommand(comando.toString());

			for (SocketBase sb : clientes) {
				if (sb == cliente)
					continue;
				sb.sendCommand(comando.toString());
			}

			cliente.sendCommand(SUCESS_MSG);
			break;
		case CODE_REQUEST_SETKEY:
			key = comando.getString(CHAVE_KEY);
			String value = comando.getString(VALUE_KEY);
			cliente.getCliente().setChave(mac, key, value);
			hardware = hardwares.get(mac);
			hardware.sendCommand(comando.toString());

			for (SocketBase sb : clientes) {
				if (sb == cliente)
					continue;
				sb.sendCommand(comando.toString());
			}

			cliente.sendCommand(SUCESS_MSG);
			break;
		case CODE_REQUEST_DELETEKEY:
			key = comando.getString(CHAVE_KEY);
			cliente.getCliente().excluirChave(mac, key);

			cliente.sendCommand(SUCESS_MSG);
			break;
		}

	}

	public void onHardwareCommand(SocketBase hardware, JSONObject comando) throws IOException, ClassNotFoundException, SQLException {
		
		String request = comando.getString("request");
		String key;

		switch (request) {
		case CODE_REQUEST_GETKEYS:
			hardware.sendCommand(hardware.getHardware().getChaves().toString());
			break;
		case CODE_REQUEST_GETKEY:
			key = comando.getString(CHAVE_KEY);
			hardware.sendCommand(hardware.getHardware().getChave(key));
			break;
		case CODE_REQUEST_SETKEYS:
			JSONObject keys = comando.getJSONObject(CHAVE_KEYS);
			hardware.getHardware().setChaves(keys);
			hardware.sendCommand(SUCESS_MSG);
			comando.put(MAC_KEY, hardware.getHardware().getMac());
			for (SocketBase sb : clientes) {
				sb.sendCommand(comando.toString());
			}
			break;
		case CODE_REQUEST_SETKEY:
			key = comando.getString(CHAVE_KEY);
			String value = comando.getString(VALUE_KEY);
			hardware.getHardware().setChave(key, value);
			hardware.sendCommand(SUCESS_MSG);
			comando.put(MAC_KEY, hardware.getHardware().getMac());
			for (SocketBase sb : clientes) {
				sb.sendCommand(comando.toString());
			}
			break;
		}

	}

	public void addClientes(SocketBase cliente) {
		if (!clientes.contains(cliente)) {
			clientes.add(cliente);
		}
	}

	public void removeCliente(SocketBase cliente) {
		if (clientes.contains(cliente)) {
			try {
				cliente.sendCommand(LOG_OUT);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			clientes.remove(cliente);
			cliente.setCliente(null);
		}
	}

	public boolean contemCliente(SocketBase cliente) {
		return clientes.contains(cliente);
	}

	public boolean naoContemClientes() {
		return clientes.isEmpty();
	}

	public void addHardware(SocketBase hardware) throws ClassNotFoundException, SQLException {
		if (!hardwares.containsKey(hardware.getHardware().getMac())) {
			hardwares.put(hardware.getHardware().getMac(), hardware);
		}
	}

	public void removeHardware(SocketBase hardware) {
		if (hardwares.containsKey(hardware.getHardware().getMac())) {
			try {
				hardware.sendCommand(LOG_OUT);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
			hardwares.remove(hardware.getHardware().getMac());
			hardware.setHardware(null);
		}
	}

	public boolean contemHardware(SocketBase hardware) {
		return contemHardware(hardware.getHardware().getMac());
	}

	private boolean contemHardware(String mac) {
		return hardwares.containsKey(mac);
	}

	public boolean naoContemHardwares() {
		return hardwares.isEmpty();
	}

	@Override
	public String toString() {
		return "Numero de clientes: " + clientes.size() + "/ Numero de Hardwares: " + hardwares.size();
	}

}