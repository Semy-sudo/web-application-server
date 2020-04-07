# 실습을 위한 개발 환경 세팅
* https://github.com/slipp/web-application-server 프로젝트를 자신의 계정으로 Fork한다. Github 우측 상단의 Fork 버튼을 클릭하면 자신의 계정으로 Fork된다.
* Fork한 프로젝트를 eclipse 또는 터미널에서 clone 한다.
* Fork한 프로젝트를 eclipse로 import한 후에 Maven 빌드 도구를 활용해 eclipse 프로젝트로 변환한다.(mvn eclipse:clean eclipse:eclipse)
* 빌드가 성공하면 반드시 refresh(fn + f5)를 실행해야 한다.

# 웹 서버 시작 및 테스트
* webserver.WebServer 는 사용자의 요청을 받아 RequestHandler에 작업을 위임하는 클래스이다.
* 사용자 요청에 대한 모든 처리는 RequestHandler 클래스의 run() 메서드가 담당한다.
* WebServer를 실행한 후 브라우저에서 http://localhost:8080으로 접속해 "Hello World" 메시지가 출력되는지 확인한다.

# 각 요구사항별 학습 내용 정리
* 구현 단계에서는 각 요구사항을 구현하는데 집중한다. 
* 구현을 완료한 후 구현 과정에서 새롭게 알게된 내용, 궁금한 내용을 기록한다.
* 각 요구사항을 구현하는 것이 중요한 것이 아니라 구현 과정을 통해 학습한 내용을 인식하는 것이 배움에 중요하다. 

### 요구사항 1 - http://localhost:8080/index.html로 접속시 응답
* InputStream.OutputStream : 바이트 단위 입출력 위한 최상위 입출력 스트림 클래스
* Socket connection : 소켓이란 네트워크 환경에 연결할 수 있게 만들어진 연결부
* InputStream is = socket.getInputStream() // 입력스트림 얻기
* OutputStream os = socket.getOutputStream() // 출력스트림 얻기
* <BufferedReader 사용법> 
* 많은양의 데이터를 입력받는 경우 BufferedReader사용하는게 효율적!
* 메소드: BufferedReader(Reader rd) ->rd에 연결되는 문자입력 버퍼스트림 생성
* BufferedReader br = new BufferedReader(new inputStreamReader(System.in));
* String s = br.readLine() -> 스트림으로 부터 한줄을 읽어 문자열로 리턴
* int i = Integer.parseInt(br.readLine())
* <DataOutputStream 사용법>
* 자바의 기본 자료형 데이터를 바이트 스트림으로 입출력하는 기능을 제공하는
  ByteStream 클래스이다. 객체생성시에 InputStream 과 OutputStream을 매개변수 인자로 가진다
* 생성자:DataInputStream(InputStream in) ->inputStream 을 인자로 DataInputStream 을 생성한다
* 생성자:DataOutputStream(OutputStream out) ->OutputStream 을 인자로 DataOutputStream을 생성한다

### 요구사항 2 - get 방식으로 회원가입
* 

### 요구사항 3 - post 방식으로 회원가입
* 

### 요구사항 4 - redirect 방식으로 이동
* 

### 요구사항 5 - cookie
* 

### 요구사항 6 - stylesheet 적용
* 

### heroku 서버에 배포 후
* 
