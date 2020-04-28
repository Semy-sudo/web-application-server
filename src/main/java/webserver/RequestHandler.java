
package webserver;
import java.io.BufferedReader;

import java.io.DataOutputStream;	
import java.io.File;

import java.io.IOException;	
import java.io.InputStream;	
import java.io.InputStreamReader;
import java.io.OutputStream;

import java.net.Socket;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.util.Collection;
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
                
                Boolean what = null ;
                
               
                
                
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
                
            	String cookie = "";
            	//요구사항2번//3번
            	//contentLength 길이 알아내기
            	int contentLength = 0;
            	while(!firstLine.equals("")) {
            		log.debug("Header : {}", firstLine);
                	firstLine = br.readLine();
                	
            		 if(firstLine.contains("Content-Length")) {
            			 contentLength = getContentLength(firstLine);//94
            			 System.out.println("contentLength 길이:"+contentLength);
            			
            		 }
            		
            	}
            	
            	
            	
           	 	// /user/create 요청이 들어왔을때
            	if(("/user/create".equals(httpUrl))) {
            		String body = IOUtils.readData(br, contentLength);
            		log.debug("Header:{}",body);
            		Map<String, String> r  = HttpRequestUtils.parseQueryString(body);//{ 정보들...   }
                	
                	User user = new User(r.get("userId"),r.get("password"),r.get("name"),r.get("email"));
                	//System.out.println("user"+user);
                	log.debug("User : {} ",user);
                	DataBase.addUser(user);
                	
                	//요구사항4번
                	
                	DataOutputStream dos = new DataOutputStream(out);
                	response302Header(dos);
                	
                    
            	}
            	
            	if(("/user/login".equals(httpUrl))){
            		//요구사항5번
            		String body = IOUtils.readData(br, contentLength);
            		log.debug("user:{}",body);
            		Map<String, String> r  = HttpRequestUtils.parseQueryString(body);//{ 정보들...   }
                	
                	User existuser = DataBase.findUserById("syi9595");
                	
                	log.debug("existUser : {} ",existuser);
                	
                	String reurl="";
                	if(existuser == null || !existuser.getPassword().equals(r.get("password"))) {
                		cookie = "logined=false";
                		reurl = "/user/login_failed.html";
                		
                		DataOutputStream dos = new DataOutputStream(out);
                    	response302HeaderA(dos,reurl,cookie);
                	
                		
                	}else {
                		cookie = "logined=true";
                		reurl="/index.html";
                		
                		
                		System.out.println(what);
                		DataOutputStream dos = new DataOutputStream(out);
                    	response302HeaderA(dos,reurl,cookie);
                	}
              
            	
            
            	}
            	
            	if(("/user/list".equals(httpUrl))) {
            		String reurl = "";
            		
            		//요구사항 6번 
            		//로그인이 되면 list로 url 이동
            		
            		Map<String, String> UserList = HttpRequestUtils.parseCookies(cookie);
            		
            		
            		//if(Boolean.parseBoolean(UserList.get("logined")==true)) {
            		if(true) {
            			//사용자 목록 출력하는 HTML 동적 생성 후 응답 보내기
            			Collection<User> userList = DataBase.findAll();
          		        StringBuilder sb = new StringBuilder();//이용해 동적html
          		        
          		        sb.append("<table border='1'>");
          		        sb.append("<tr>");
          		        sb.append("<th>아이디</th>");
        		    	sb.append("<th>이름</th>");
        		    	sb.append("<th>이메일</th>");
        		    	sb.append("</tr>");
          		      for (User user: userList) {
          		    	sb.append("<tr>");
          		    	sb.append("<td>"+user.getUserId()+"</td>");
          		    	sb.append("<td>"+user.getName()+"</td>");
          		    	sb.append("<td>"+user.getEmail()+"</td>");
          		    	sb.append("</tr>");
          		      }
          		        sb.append("</table>");
          		        
                		byte[] body = sb.toString().getBytes();//문자열을 바이트코드로 인코딩
                		//byte[] body = Files.readAllBytes(new File("./webapp"+httpUrl).toPath()); 
                		//httpUrl에 해당하는 파일을 바이트로 읽어들인다
                		DataOutputStream dos = new DataOutputStream(out);	
                		response200Header(dos, body.length);
               		    responseBody(dos, body);
                   		
            		}else {
            			//필요없는
            			reurl = "/user/login.html";
            			
            			DataOutputStream dos = new DataOutputStream(out);	
            			response302HeaderB(dos, reurl);
            			
            			
            		}
            		
            		
            	}
            	
            	
            	
            	 if(httpUrl.endsWith("css")) {
             		//요구사항7번
             		DataOutputStream dos = new DataOutputStream(out);	
         			byte[] body = Files.readAllBytes(new File("./webapp"+httpUrl).toPath()); 
         			
         		 response200HeaderCss(dos, body.length);
         		    
         		 responseBody(dos, body);
             		
             	}
            	
            	 else {
            		DataOutputStream dos = new DataOutputStream(out);	
        			byte[] body = Files.readAllBytes(new File("./webapp"+httpUrl).toPath()); 
        			
        			
        		 response200Header(dos, body.length);
        		    
        		 responseBody(dos, body);
            	 
            	
            	 }
            	
            	
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
	//상태코드200과 content length 전달해서 정상적으로 요청이진행되었음을 알려주도록 처리하는 메소드
	
	private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
             dos.writeBytes("HTTP/1.1 200 OK \r\n");
             dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
             dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
             // dos.writeBytes("Set-Cookie: logined=true");
             dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
	//요구사항 7번 css 적용
	private void response200HeaderCss(DataOutputStream dos, int lengthOfBodyContent) {
        try {
             dos.writeBytes("HTTP/1.1 200 OK \r\n");
             dos.writeBytes("Content-Type: text/css;charset=utf-8\r\n");
             dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            
             dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
	//요구사항 4번 //url 임시이동가능
	private void response302Header(DataOutputStream dos) {
        try {
             dos.writeBytes("HTTP/1.1 302 OK \r\n");
			 dos.writeBytes("Location: /index.html\r\n");
             dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
	//요구사항 5번 //reurl,cookie
	private void response302HeaderA(DataOutputStream dos, String reurl, String cookie) {
        try {
             dos.writeBytes("HTTP/1.1 302 OK \r\n");
			 dos.writeBytes("Location:" +reurl+"\r\n");
			 dos.writeBytes("Set-Cookie:"+cookie+"\r\n");
             dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
	//요구사항 6번 //reurl
	private void response302HeaderB(DataOutputStream dos, String reurl) {
        try {
             dos.writeBytes("HTTP/1.1 302 OK \r\n");
			 dos.writeBytes("Location:" +reurl+"\r\n");
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
