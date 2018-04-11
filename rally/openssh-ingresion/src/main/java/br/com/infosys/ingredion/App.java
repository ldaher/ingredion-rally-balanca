package br.com.infosys.ingredion;

import java.io.IOException;
import java.util.Properties;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

/**
 * Hello world!
 *
 */
public class App {
	private static ChannelSftp c;
	private static Session session;
	private static String home;

	public static void main(String[] args) throws IOException {
		String user = args[0];
		String password = args[1];
		String host = args[2];
		String port = args[3];
		String path = args[4];

		connectDisconnect(user, password, host, port);

		System.out.printf("Successfully connected to: " + host);

		changeWorkingDirectory(path);

	}

	private static void connectDisconnect(String user, String password, String host, String port) {
		JSch jsch = new JSch();

		c = null;
		session = null;

		try {
			System.out.printf("Criando Sessão com o usuário %s e host %s\n", user, host);
			session = jsch.getSession(user, host);
			Properties hash = new Properties();
			hash.put("StrictHostKeyChecking", "no");
			System.out.printf("Configurando a Sessão com o valor de propriedade %s igual à %s\n", "StrictHostKeyChecking", hash.getProperty("StrictHostKeyChecking"));
			session.setConfig(hash);
			System.out.printf("Informando a porta %s e a senha %s para Sessão\n", port, password);
			session.setPort(Integer.parseInt(port));
			session.setPassword(password);
			System.out.printf("Conectando a Sessão...\n", port, password);
			session.connect();
			String fingerPrint = null;
			if ((fingerPrint != null) && (!session.getHostKey().getFingerPrint(jsch).equals(fingerPrint))) {
				throw new RuntimeException("Invalid Fingerprint");
			}
			Channel channel = session.openChannel("sftp");
			System.out.printf("Conectando a Canal sFTP...\n", port, password);
			channel.connect();
			c = ((ChannelSftp) channel);
			home = c.pwd();
		} catch (JSchException e) {
			if ((c != null) && (c.isConnected())) {
				c.disconnect();
			}
			if ((session != null) && (session.isConnected())) {
				session.disconnect();
			}
			System.err.printf("Erro de login para o usuário %s\n", user);
			e.printStackTrace();
		}
	}

	private static boolean changeWorkingDirectory(String wd) {
		System.out.printf("Acessando o sistema de arquivo...\n");
		try {
			if (!wd.startsWith(home)) {
				wd = home + wd;
			}
			if (wd.startsWith("/~")) {
				wd = home + wd.substring(2, wd.length());
			}
			System.out.printf("Acessando o diretório especificado...\n");
			c.cd(wd);
		} catch (SftpException e) {
			if ((c != null) && (c.isConnected())) {
				c.disconnect();
			}
			if ((session != null) && (session.isConnected())) {
				session.disconnect();
			}

			System.err.printf("Erro no CWD %s", wd);
			e.printStackTrace();
		}
		
		return true;
	}
}
