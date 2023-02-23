package bamboo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Bamboo {

	private static final int THREAD_POOL_SIZE = 10;
    private static ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

	public void grownUp(int port) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8080);
        System.out.println("Server started at port 8080");

        while (true) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connected: " + clientSocket.getInetAddress().getHostAddress());

            executorService.submit(() -> {
                try {
                    handleClient(clientSocket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    private static void handleClient(Socket clientSocket) throws IOException {
    	InputStream in = clientSocket.getInputStream();
    	OutputStream out = clientSocket.getOutputStream();

    	byte[] buffer = new byte[1024];
    	int bytesRead;
    	while ((bytesRead = in.read(buffer)) != -1) {
    	    String request = new String(buffer, 0, bytesRead);
    	    if (request.startsWith("GET")) {
    	        String responseBody = "<html><body><h1>Hello, World!</h1></body></html>";
    	        String httpResponseBody = responseBody;
    	        String httpStatusLine = "HTTP/1.1 200 OK\r\n";
    	        String httpHeaders = "Content-Type: text/html; charset=UTF-8\r\n" +
    	                "Content-Length: " + httpResponseBody.length() + "\r\n" +
    	                "Connection: close\r\n\r\n";
    	        String httpResponse = httpStatusLine + httpHeaders + httpResponseBody;
    	        out.write(httpResponse.getBytes());
    	        out.flush();
    	    } else {
    	        String responseBody = "Unsupported request method.";
    	        String httpResponseBody = responseBody;
    	        String httpStatusLine = "HTTP/1.1 400 Bad Request\r\n";
    	        String httpHeaders = "Content-Type: text/plain; charset=UTF-8\r\n" +
    	                "Content-Length: " + httpResponseBody.length() + "\r\n" +
    	                "Connection: close\r\n\r\n";
    	        String httpResponse = httpStatusLine + httpHeaders + httpResponseBody;
    	        out.write(httpResponse.getBytes());
    	        out.flush();
    	    }
    	}
    	clientSocket.close();
    	System.out.println("Client disconnected: " + clientSocket.getInetAddress().getHostAddress());
    }
}