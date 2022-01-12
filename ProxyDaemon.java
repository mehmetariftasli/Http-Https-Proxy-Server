package networkProject;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class ProxyDaemon {

	final static ArrayList<String> forbiddenAddresses = new ArrayList<>();

	public static void main(String args[]) throws Exception {


		ServerSocket welcomeSocket = new ServerSocket(8080);

		while (true) {
			Socket connectionSocket = welcomeSocket.accept();
			new ServerHandler(connectionSocket,null);
		}

	}

}

class ServerHandler implements Runnable {

	Socket clientSocket;

	DataInputStream inFromClient;
	DataOutputStream outToClient;

	String host;
	String path;

	PrinterClass pC;

	public ServerHandler(Socket s, String request) {
		try {
			clientSocket = s;
			pC = new PrinterClass();

			pC.add("A connection from a client is initiated...");

			inFromClient = new DataInputStream(s.getInputStream());
			outToClient = new DataOutputStream(s.getOutputStream());

			String hd = request;
			int sp1 = hd.indexOf(' ');
			int sp2 = hd.indexOf(' ', sp1 + 1);
			int eol = hd.indexOf('\r');

			String reqHeaderRemainingLines = hd.substring(eol + 2);

			MimeHeader reqMH = new MimeHeader(hd);

			String url = hd.substring(sp1 + 1, sp2);
			System.out.println(url);

			String method = hd.substring(0, sp1);

			host = reqMH.get("Host");

			reqMH.put("Connection", "close");

			URL u = new URL(url);

			String tmpPath = u.getPath();

			String tmpHost = u.getHost();
			path = ((tmpPath == "") ? "/" : tmpPath);
			
			host = tmpHost;
			if (host.equals(tmpHost)) 
			{
				if (method.equalsIgnoreCase("get")) {
					pC.add("Client requests...\r\nHost: " + host + "\r\nPath: " + path);
					handleProxy(url, reqMH,"GET");
				}
				else if(method.equalsIgnoreCase("post"))
				{
					pC.add("Client requests...\r\nHost: " + host + "\r\nPath: " + path);
					handleProxy(url, reqMH,"POST");
				}
				else if(method.equalsIgnoreCase("head"))
				{
					pC.add("Client requests...\r\nHost: " + host + "\r\nPath: " + path);
					handleProxy(url, reqMH,"HEAD");
				}
				else 
				{
					pC.add("Requested method " + method + " is not allowed on proxy server");
					outToClient.writeBytes(createErrorPage(405, "Method Not Allowed", method));
				}
			} 
			else 
			{
				pC.add("Error for request: " + url);
			}
			pC.removeThread();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			String hd = getHeader(inFromClient);

			int sp1 = hd.indexOf(' ');
			int sp2 = hd.indexOf(' ', sp1 + 1);
			int eol = hd.indexOf('\r');

			String reqHeaderRemainingLines = hd.substring(eol + 2);

			MimeHeader reqMH = new MimeHeader(reqHeaderRemainingLines);

			String url = hd.substring(sp1 + 1, sp2);
			System.out.println(url);

			String method = hd.substring(0, sp1);

			host = reqMH.get("Host");

			reqMH.put("Connection", "close");

			URL u = new URL(url);

			String tmpPath = u.getPath();

			String tmpHost = u.getHost();

			path = ((tmpPath == "") ? "/" : tmpPath);

			if (ProxyDaemon.forbiddenAddresses.contains(tmpHost) == true) {
				pC.add("Connection blocked to the host due to the proxy policy");
				outToClient.writeBytes(createErrorPage(401, "Not Allowed", method));
			} else if (host.equals(tmpHost)) {
				if (method.equalsIgnoreCase("get")) {
					pC.add("Client requests...\r\nHost: " + host + "\r\nPath: " + path);
					handleProxy(url, reqMH,"GET");
				} else {
					pC.add("Requested method " + method + " is not allowed on proxy server");
					outToClient.writeBytes(createErrorPage(405, "Method Not Allowed", method));
				}
			} else {
				pC.add("Error for request: " + url);
			}
			pC.removeThread();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void handleProxy(String url, MimeHeader reqMH,String method) {
		try {
			pC.add("\r\nInitiating the server connection");
			Socket sSocket = new Socket(host, 80);
			DataInputStream inFromServer = new DataInputStream(sSocket.getInputStream());
			DataOutputStream outToServer = new DataOutputStream(sSocket.getOutputStream());

			reqMH.put("User-Agent", reqMH.get("User-Agent") + " via CSE471 Proxy");

			pC.add("\r\nSending to server...\r\n" + method+ " " + path + " HTTP/1.1\r\n" + reqMH + "\r\n");

			outToServer.writeBytes(method+" " + path + " HTTP/1.1\r\n" + reqMH + "\r\n");

			pC.add("HTTP request sent to: " + host);

			ByteArrayOutputStream bAOS = new ByteArrayOutputStream(10000);

			int a;

			byte[] buffer = new byte[1024];

			while ((a = inFromServer.read(buffer)) != -1) {
				bAOS.write(buffer, 0, a);
			}

			byte[] response = bAOS.toByteArray();

			String rawResponse = new String(response);

			String responseHeader = rawResponse.substring(0, rawResponse.indexOf("\r\n\r\n"));

			pC.add("\r\nResponse Header\r\n" + responseHeader);

			pC.add("\r\n\r\nGot " + response.length + " bytes of response data...\r\n"
					+ "Sending it back to the client...\r\n");

			outToClient.write(response);

			outToClient.close();

			sSocket.close();

			pC.add("Served http://" + host + path + "\r\nExiting ServerHelper thread...\r\n"
					+ "\r\n----------------------------------------------------" + "\r\n");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String createErrorPage(int code, String msg, String address) {
		String html_page = "";
		html_page = code + " " + msg + " " + address + "\n";
		
		MimeHeader mh = makeMimeHeader("text/html", html_page.length());
		HttpResponse hr = new HttpResponse(code, msg, mh);
		return hr + html_page;
	}

	private MimeHeader makeMimeHeader(String type, int length) {
		MimeHeader mh = new MimeHeader();
		Date d = new Date();
		TimeZone gmt = TimeZone.getTimeZone("GMT");
		SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy hh:mm:ss zzz");
		sdf.setTimeZone(gmt);
		String sdf_date = sdf.format(d);
		
		mh.put("Date:", sdf_date);
		mh.put("Type:", type);
		
		if (length >= 0)
			mh.put("Content-Length", String.valueOf(length));
		return mh;
	}

	public String getHeader(DataInputStream in) throws Exception {
		byte[] header = new byte[1024];

		int data;
		int h = 0;

		while ((data = in.read()) != -1) {
			header[h++] = (byte) data;

			if (header[h - 1] == '\n' && header[h - 2] == '\r' && header[h - 3] == '\n' && header[h - 4] == '\r') {
				break;
			}
		}

		return new String(header, 0, h);
	}

}