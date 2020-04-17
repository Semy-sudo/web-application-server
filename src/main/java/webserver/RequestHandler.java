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
import java.util.Map;

import org.slf4j.Logger;	
import org.slf4j.LoggerFactory;

import model.User;
import util.HttpRequestUtils;
import util.IOUtils;	

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
                String[] arrFirstLine = firstLine.split(" ");
               
                String httpMethod     = arrFirstLine[0];
                String httpUrl        = arrFirstLine[1].equals("/")?"/index.html":arrFirstLine[1];
                String httpProtocol   = arrFirstLine[2];
               
               
              
                /*
                while(!firstLine.equals("")) {
                	firstLine = br.readLine();
                	System.out.println("log.debug 로 읽은것");
                	log.debug("body:{}",firstLine);
                }
              */
            	System.out.println("arrFistLine[0]: "+httpMethod);
            	System.out.println("arrFistLine[1]: "+httpUrl);
            	System.out.println("arrFistLine[2]: "+httpProtocol);
                
            	
            	//요구사항2번
            	
            	/*
            	HttpRequestUtils hr = new HttpRequestUtils();
            	Map<String, String> r = hr.parseQueryString(httpUrl);//{ 정보들...   }
            	r.forEach((k,v) -> System.out.println("key : " + k + ", value : " + v));
            	//key 로 value찾기
            	//System.out.println(getKey(r,"syi9595"));// /user/create?userId
            	//null
            
           
            	
            	//User클래스에 저장
            	
            	String getName = (String)r.get("name");
            	String getID = (String)r.get("/user/create?userId");
            	String getPassword = (String)r.get("password");
            	String getEmail = (String)r.get("email");
            	
            	User user = new User(getID,getPassword,getName,getEmail);
            	System.out.println(user.toString());
            	*/
            	//요구사항3번
            	
            	
            	int contentLength = 0;
            	while(!firstLine.equals("")) {
            		log.debug("body : {}", firstLine);
                	firstLine = br.readLine();
                	
            		 if(firstLine.contains("Content-Length")) {
            		
            			 contentLength = getContentLength(firstLine);//94
            			 System.out.println("contentLength 길이:"+contentLength);
            			
            		 }
            		
            	}
            	 // System.out.println("contentLength 길이:"+contentLength);
            	if("/user/create".equals(httpUrl)) {
            		String body = IOUtils.readData(br, contentLength);
            		Map<String, String> rr = HttpRequestUtils.parseQueryString(body);//{ 정보들...   }
                	rr.forEach((k,v) -> System.out.println("key : " + k + ", value : " + v));
                	
                	User user2 = new User(rr.get("userId"),rr.get("password"),rr.get("name"),rr.get("email"));
               
                	log.debug("User : {} ",user2);
                	
                	
            	}
            	DataOutputStream dos = new DataOutputStream(out);
            	byte[] body = Files.readAllBytes(new File("./webapp"+httpUrl).toPath());
                response200Header(dos, body.length);
                responseBody(dos, body);
            	
                
            }catch(IOException e) {
            	e.printStackTrace();
            }
            
    }




	private int getContentLength(String firstLine) {
	String[] Stringline = firstLine.split(":");
	
	return Integer.parseInt(Stringline[1].trim());//94
}
	private static <K,V> K getKey(Map<K, V> r, V string) {
	
		 for (K key : r.keySet()) {
	            if (string.equals(r.get(key))) {
	                return key;
	            }
	        }

	return null;
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
