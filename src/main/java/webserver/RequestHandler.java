package webserver;

import java.io.BufferedReader;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.Flushable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }
////코드 수정
   
    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());
        	
            try(InputStream in = connection.getInputStream(); OutputStream out= connection.getOutputStream()){
            	BufferedReader br = new BufferedReader(new InputStreamReader(in,"UTF-8"));
            	
            	String firstLine      = br.readLine();
                String arrFirstLine[] = firstLine.split(" ");
                if(arrFirstLine.length==3){
                String httpMethod     = arrFirstLine[0];
                String httpUrl        = arrFirstLine[1].equals("/")?"/index.html":arrFirstLine[1];
                String httpProtocol   = arrFirstLine[2];
                
            
            	System.out.println("arrFistLine[0]: "+httpMethod);
            	System.out.println("arrFistLine[1]: "+httpUrl);
            	System.out.println("arrFistLine[2]: "+httpProtocol);
                
            	DataOutputStream dos = new DataOutputStream(out);
            	byte[] body = Files.readAllBytes(new File("./webapp"+httpUrl).toPath());
                response200Header(dos, body.length);
                responseBody(dos, body);
                
                }
            }catch(IOException e) {
            	e.printStackTrace();
            }
            
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
             dos.writeBytes("HTTP/1.1 200 OK \r\n");
             dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
             dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
             dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
             dos.write(body, 0, body.length);
             dos.writeBytes("\r\n");
             dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
