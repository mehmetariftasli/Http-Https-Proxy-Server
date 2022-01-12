package networkProject;
import java.io.*;

import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class TestProxyServer extends Thread {
	static ArrayList<String> avoidedList = new ArrayList<String>();
	static HashMap<String, String> report = new HashMap<String, String>();
    public static void main(String[] args) {
        (new TestProxyServer()).run();
    }

    public TestProxyServer() {
        super("Server Thread");
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(8080)) {
            Socket socket;
            try {
                while ((socket = serverSocket.accept()) != null) {
                    (new Handler(socket)).start();
                }
            } catch (IOException e) {
                e.printStackTrace();  // TODO: implement catch
            }
        } catch (IOException e) {
            e.printStackTrace();  // TODO: implement catch
            return;
        }
    }

    public static class Handler extends Thread {
    	boolean readFlag = false;
        public static final Pattern CONNECT_PATTERN = Pattern.compile("CONNECT (.+):(.+) HTTP/(1\\.[01])",
                Pattern.CASE_INSENSITIVE);
        private final Socket clientSocket;
        private boolean previousWasR = false;

        public Handler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try {
                String request = readLine(clientSocket);
                Matcher matcher = CONNECT_PATTERN.matcher(request);
             
                if (matcher.matches()) {
                	System.out.println(request);
                	boolean avoidFlag = false;
                	for (var str : avoidedList) {
                		if(matcher.group(1).contains(str))
                		{
                			avoidFlag = true;
                			//System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
                			break;
                		}
                	    // fruit is an element of the `fruits` array.
                	}
                    if(avoidFlag)
                    {
                    	System.out.println("401 Unauthorized");  
                    	
                    	
                    }
                    else
                    {
                    	//System.out.println("Tried to connect: "+ matcher.group(1));
                    
	                    String header;
	                    do {
	                    	if(!report.containsKey(clientSocket.getLocalAddress().toString()))
	                    	{
	                    		//System.out.println(clientSocket.getLocalAddress().toString());
	                    		report.put(clientSocket.getLocalAddress().toString(), "Start of report for" + clientSocket.getLocalAddress().toString()+"\n");
	                    	}
	                    	LocalTime myObj = LocalTime.now();
	                    	String tmpStr = report.get(clientSocket.getLocalAddress().toString());
		                    report.put(clientSocket.getLocalAddress().toString(),tmpStr +"CONNECT "+ matcher.group(1) + " " + myObj.getHour()+":"+myObj.getMinute()+":"+myObj.getSecond()+"\n");
	                    	//String tmpReport = report.get(clientSocket.getLocalAddress().toString());
	                    	//tmpReport = tmpReport + "\n";
	                    	//report.replace(clientSocket.getLocalAddress().toString(), tmpReport);
	                        header = readLine(clientSocket);
	                        //System.out.println(clientSocket);
	 
	                    } while (!"".equals(header));
	                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(clientSocket.getOutputStream(),
	                            "ISO-8859-1");
	
	                    final Socket forwardSocket;
	                    try {
	                        forwardSocket = new Socket(matcher.group(1), Integer.parseInt(matcher.group(2)));
	                        System.out.println(forwardSocket);
	                    } catch (IOException | NumberFormatException e) {
	                        e.printStackTrace();  // TODO: implement catch
	                        outputStreamWriter.write("HTTP/" + matcher.group(3) + " 502 Bad Gateway\r\n");
	                        outputStreamWriter.write("Proxy-agent: Simple/0.1\r\n");
	                        outputStreamWriter.write("\r\n");
	                        outputStreamWriter.flush();
	                        return;
	                    }
	                    try {

	                        outputStreamWriter.write("HTTP/" + matcher.group(3) + " 200 Connection established\r\n");
	                        outputStreamWriter.write("Proxy-agent: Simple/0.1\r\n");
	                        outputStreamWriter.write("\r\n");
	                        outputStreamWriter.flush();
	
	                        Thread remoteToClient = new Thread() {
	                            @Override
	                            public void run() {
	                                forwardData(forwardSocket, clientSocket);
	                            }
	                        };
	                        remoteToClient.start();
	                        try {
	                            if (previousWasR) 
	                            {
	                                int read = clientSocket.getInputStream().read();
	                                if (read != -1) 
	                                {
	                                    if (read != '\n') 
	                                    {
	                                        forwardSocket.getOutputStream().write(read);
	                                    }
	                                    forwardData(clientSocket, forwardSocket);
	                                } 
	                                else 
	                                {
	                                    if (!forwardSocket.isOutputShutdown()) 
	                                    {
	                                        forwardSocket.shutdownOutput();
	                                    }
	                                    if (!clientSocket.isInputShutdown()) 
	                                    {
	                                        clientSocket.shutdownInput();
	                                    }
	                                }
	                            } 
	                            else 
	                            {
	                                forwardData(clientSocket, forwardSocket);
	                            }
	                        } 
	                        finally 
	                        {
	                            try 
	                            {
	                                remoteToClient.join();
	                            } catch (InterruptedException e) 
	                            {
	                                e.printStackTrace();  // TODO: implement catch
	                            }
	                        }
	                    } finally 
	                    {
	                        forwardSocket.close();
	                    }
                }
                }
                else
                {
                	//request = request +"\n" +readLine(clientSocket) +"\n" +readLine(clientSocket)+"\n" + readLine(clientSocket)+"\n" + readLine(clientSocket)+"\n" +readLine(clientSocket)+"\n" +readLine(clientSocket)
                	//+"\n"+readLine(clientSocket)+"\n"+readLine(clientSocket)+"\n"+readLine(clientSocket)+"\r\n"+"\r\n";
                	boolean avoidFlag = false;
                	byte[] header = new byte[1024];
            		char data;
            		int h = 0;
            		String line ="";
            		while ((data = (char)clientSocket.getInputStream().read()) != -1) {
            			line = line + data;
            			h++;
            			if(line.charAt(line.length()-1) == '\n' && h>4)
            			{
            				if(line.charAt(line.length()-2) == '\r')
                			{
            					if(line.charAt(line.length()-3) == '\n')
                    			{
            						if(line.charAt(line.length()-4) == '\r')
                        			{
                        				break;
                        			}
                    			}
                			}
            			}
            		}
                	request = request+line;
                	String lines[] = request.split("\n");
                	for (var str : avoidedList) {
                		if(lines[0].contains(str))
                		{
                			avoidFlag = true;
                			break;
                		}
                	    // fruit is an element of the `fruits` array.
                	}
                    if(avoidFlag)
                    {
                    	System.out.println("401 Unauthorized");  
                    }
                    else
                    {
                    	if(!report.containsKey(clientSocket.getLocalAddress().toString()))
                    	{
                    		//System.out.println(clientSocket.getLocalAddress().toString());
                    		report.put(clientSocket.getLocalAddress().toString(), "Start of report for" + clientSocket.getLocalAddress().toString()+"\n");
                    	}
                    	LocalTime myObj = LocalTime.now();
                    	String tmpStr = report.get(clientSocket.getLocalAddress().toString());
	                    report.put(clientSocket.getLocalAddress().toString(),tmpStr + lines[0] + " " + myObj.getHour()+":"+myObj.getMinute()+":"+myObj.getSecond()+"\n");
                    	new ServerHandler(clientSocket,request);
                    }
                }
            } catch (IOException e) 
            {
                e.printStackTrace();  // TODO: implement catch
            } 
            finally 
            {
                try 
                {
                    clientSocket.close();
                } catch (IOException e) 
                {
                    e.printStackTrace();  // TODO: implement catch
                }
            }
        }

        private static String forwardData(Socket inputSocket, Socket outputSocket) {
            try 
            {
                InputStream inputStream = inputSocket.getInputStream();
                try 
                {
                    OutputStream outputStream = outputSocket.getOutputStream();
                    try 
                    {
                        byte[] buffer = new byte[4096];
                        int read;
                        do 
                        {
                            read = inputStream.read(buffer);
                            if (read > 0) 
                            {  

                                outputStream.write(buffer, 0, read);
                                if (inputStream.available() < 1) 
                                {
                                    outputStream.flush();
                                }
                            }
                        } while (read >= 0);
                    } 
                    finally 
                    {
                        if (!outputSocket.isOutputShutdown()) 
                        {
                            outputSocket.shutdownOutput();
                        }
                    }
                } 
                finally 
                {
                    if (!inputSocket.isInputShutdown()) 
                    {
                        inputSocket.shutdownInput();
                    }
                }
                
            } 
            catch (IOException e) 
            {
                e.printStackTrace();  // TODO: implement catch
            }
            String tmpReport = null;
			return tmpReport;
        }

        private String readLine(Socket socket) throws IOException {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            int next;
            readerLoop:
            while ((next = socket.getInputStream().read()) != -1) 
            {
                if (previousWasR && next == '\n') 
                {
                    previousWasR = false;
                    continue;
                    
                }
                previousWasR = false;
                switch (next) {
                    case '\r':
                        previousWasR = true;
                        break readerLoop;
                    case '\n':
                        break readerLoop;
                    default:
                        byteArrayOutputStream.write(next);
                        break;
                }
            }
            return byteArrayOutputStream.toString("ISO-8859-1");
        }
    }
}