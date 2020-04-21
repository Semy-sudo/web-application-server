
package webserver;
import java.io.BufferedReader;
import java.io.DataOutputStream;	
import java.io.File;

import java.io.IOException;	
import java.io.InputStream;	
import java.io.InputStreamReader;
import java.io.OutputStream;	
import java.net.Socket;	
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;	
import org.slf4j.LoggerFactory;

import db.DataBase;
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
            	
            	String firstLine = br.readLine();
            	if(firstLine == null) {
            		return ;
            	}
             
                String[] arrFirstLine = firstLine.split(" ");
               
                String httpMethod     = arrFirstLine[0];
                String httpUrl        = arrFirstLine[1].equals("/")?"/index.html":arrFirstLine[1];
                String httpProtocol   = arrFirstLine[2];
                log.debug("httpUrl: {}", httpUrl);
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
                
            	
            	//요구사항2번//3번
            	//contentLength 길이 알아내기
            	int contentLength = 0;
            	while(!firstLine.equals("")) {
            		log.debug("body : {}", firstLine);
                	firstLine = br.readLine();
                	
            		 if(firstLine.contains("Content-Length")) {
            			 contentLength = getContentLength(firstLine);//94
            			 System.out.println("contentLength 길이:"+contentLength);
            			
            		 }
            		
            	}
            	
           	 	
            	if(("/user/create".equals(httpUrl))) {
            		String body = IOUtils.readData(br, contentLength);
            		log.debug("body:{}",body);
            		Map<String, String> r  = HttpRequestUtils.parseQueryString(body);//{ 정보들...   }
                	
                	User user = new User(r.get("userId"),r.get("password"),r.get("name"),r.get("email"));
                	//System.out.println("user"+user);
                	log.debug("User : {} ",user);
                	
                	//요구사항4번
                	//httpUrl = "/index.html";
                	DataOutputStream dos = new DataOutputStream(out);
                	 
                	
                	response302Header(dos);
                   
                    
            	}else {

                	DataOutputStream dos = new DataOutputStream(out);
                	byte[] body = Files.readAllBytes(new File("./webapp"+httpUrl).toPath()); 
                	
                	response200Header(dos, body.length);
                    responseBody(dos, body);
                    
            		
            		
            	}
            	
            	
            	
            	/*
            	if(("/user/login.html".equals(httpUrl))) {
            		if() {
            			
            			httpUrl = "/index.html";
            		}else {
            			
            			httpUrl = "/user/login_failed.html";
            		}
            		
            	}
               */
            	
            	
            	
                
            }catch(IOException e) {
            	e.printStackTrace();
            }
            
    }


	Map<String,String> headers = new HashMap<String, String>();

	private int getContentLength(String firstLine) {
		
	String[] Stringline = firstLine.split(":");
	
	if(Stringline.length == 2) {
		headers.put(Stringline[0],Stringline[1]);
	}
	
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
	//요구사항 4번
	private void response302Header(DataOutputStream dos) {
        try {
             dos.writeBytes("HTTP/1.1 302 OK \r\n");
             dos.writeBytes("Location: /index.html\r\n");
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
