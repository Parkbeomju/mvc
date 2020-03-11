

## Chap 5.  MVC 아키텍처

#### 5.1 MVC(model-view-controller) 아키텍처

![image-20200228145949064](/Users/beomju/Library/Application Support/typora-user-images/image-20200228145949064.png)

#### MVC의 각 컴포넌트의 역할

##### 컨트롤러 컴포넌트의 역할

1. 클라이언트의 요청을 받았을 때 그 요청에 대해 실제 업무를 수행하는 **모델 컴포넌트를 호출**하는 일
2. 클라이언트가 보낸 데이터가 있다면, 모델을 호출할 때 전달하기 쉽게 데이터를 적절히 가공
3. 모델이 업무 수행을 완료하면, 그 결과를 가지고 화면을 생성하도록 뷰에게 전달

- ##### 클라이언트 요청에 대해 모델과 뷰를 결정하여 전달하는 일

##### 모델 컴포넌트의 역할

1. 데이터 저장소와 연동하여 사용자가 입력한 데이터나 사용자에게 출력할 데이터를 다루는 일

##### 뷰 컴포넌트의 역할

1. 모델이 처리한 데이터나 그 작업 결과를 가지고 사용자에게 출력할 화면을 만드는 일
   - HTML과 CSS. JavaScript를 사용하여 웹 브라우저가 출력할 UI를 만듦



#### MVC 이점

##### 1. 높은 재사용성, 넓은 융통성

- 화면 생성 부분을 별도의 컴포넌트로 분리하였기 때문에, 컨트롤러나 모델에 상관없이 뷰 교체만으로 배경색이나 모양, 레이아웃, 글꼴 등 사용자 화면을 손쉽게 변경 가능
- **원 소스 멀티 유즈 구현**
  - 모델 컴포넌트가 작업한 결과를 다양한 뷰 컴포넌트를 통하여 PDF나 HTML, XML, JSON 등 클라인언트가 원하는 형식으로 출력 가능
- 코드의 재사용 가능

##### 2. 빠른 개발, 저렴한 비용



#### MVC 구동 원리

![image-20200228151029612](/Users/beomju/Library/Application Support/typora-user-images/image-20200228151029612.png)

1. 웹 브라우저가 웹 애플리케이션 실행을 요청하면, 웹 서버가 그 요청을 받아서 서블릿 컨테이너에 넘겨 줌

   - 서블릿 컨테이너는 URL을 확인하여 그 요청을 처리할 서블릿을 찾아서 실행

2. 서블릿은 실제 업무를 처리하는 모델 자바 객체의 메서드를 호출

   - 만약 웹 브라우저가 보낸 데이터를 저장하거나 변경해야 한다면 그 데이터를 가공하여 값 객체(DTO)를 생성하고, 모델 객체의 메서드를 호출할 때 인자값으로 넘김

3. 모델 객체는 JDBC를 사용하여 매개변수로 넘어온 값 객체를 데이터베이스에 저장하거나, 데이터베이스로부터 질의 결과를 가져와서 값 객체로 만들어 반환함

   ##### - 값 객체는 객체와 객체 사이에 데이터를 전달하는 용도로 사용하기 때문에 데이터 전송 객체(DTO)라고 부름

4. 서블릿은 모델 객체로부터 반환받은 값을 JSP에 전달

5. JSP는 서블릿으로부터 전달받은 값 객체를 참조하여 웹 브라우저가 출력할 결과 화면을 만듦

   - 웹 브라우저에 출력함으로써 요청 처리를 완료

6. 웹 브라우저는 서버로부터 받은 응답 내용을 화면에 출력함



### 뷰 컴포넌트와 JSP

- MVC 아키텍처에서 뷰 컴포넌트를 만들 때 보통 JSP를 사용함

- 뷰 컴포넌트의 역할은 웹 브라우저가 출력할 화면을 만드는 일

  - ##### JSP는 화면 생성을 쉽게 해주는 기술

#### JSP를 사용하는 이유

- ##### JSP를 사용하지 않으면 출력문을 사용하여 일일이 HTML을 출력해야 함

``` java
public class MemberAddServlet extends HttpServlet {
	…	
	protected void doGet(…) … {
		response.setContentType("text/html; charset=UTF-8");
		PrintWriter out = response.getWriter();
		out.println("<html><head><title>회원 등록</title></head>");
		out.println("<body><h1>회원 등록</h1>");
		out.println("<form action='add' method='post'>");
		out.println("이름: <input type='text' name='name'><br>");
		out.println("이메일: <input type='text' name='email'><br>");
		out.println("암호: <input type='password' name='password'><br>");
		out.println("<input type='submit' value='추가'>");
		out.println("<input type='reset' value='취소'>");
		out.println("</form>");
		out.println("</body></html>");
	}
```

##### JSP 사용 후

``` java
<%@ page language="java” contentType="text/html; charset=UTF-8” …>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" …>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>회원 등록</title>
</head>
<body>
<jsp:include page="/Header.jsp"/>
<h1>회원 등록</h1>
<form action='add' method='post'>
이름: <input type='text' name='name'><br>
이메일: <input type='text' name='email'><br>
암호: <input type='password' name='password'><br>
<input type='submit' value='추가'>
<input type='reset' value='취소'>
</form>
<jsp:include page="/Tail.jsp"/>
</body>
</html>
```



#### JSP 구동 원리

##### JSP 기술의 가장 중요한 목적은 코딩을 단순화 하는 것

![image-20200228153133310](/Users/beomju/Library/Application Support/typora-user-images/image-20200228153133310.png)

> .java = java 규칙에 맞게 작성한 모든 소스코드 파일
>
> .class = .java를 컴파일 하여 생성된 파일

1. 개발자는 서버에 JSP 파일을 작성해 두고
   - 클라이언트가 JSP를 실행해 달라고 요청하면, 서블릿 컨테이너는 JSP 파일에 대응하는 자바 서블릿을 찾아서 실행
2. 만약 JSP에 대응하는 서블릿이 없거나 JSP 파일이 변경되었다면, JSP 엔진을 통해 JSP 파일을 해석해서 서블릿 자바 소스를 생성
3. 서블릿 자바 소스는 자바 컴파일러를 통해 서블릿 클래스 파일로 컴파일
   - JSP를 바꿀 때마다 이 과정을 반복
4. JSP로부터 생성된 서블릿은 서블릿 구동 방식에 따라 실행
   - 서블릿의 service() 메서드가 호출되고, 출력 메서드를 통해 서블릿이 생성한 HTML 화면을 웹 브라우저로 보냄

##### JSP를 사용하면 개발자는 자바로 출력문을 작성할 필요가 없음

#### JSP가 직접 실행되는 것이 아니라 JSP로부터 만들어진 서블릿이 실행됨

#### HttpJspPage 인터페이스 

##### HttpJspPage 인터페이스의 상속 관계

![image-20200229203021477](/Users/beomju/Library/Application Support/typora-user-images/image-20200229203021477.png)

##### jspinit()

- JspPage에 선언된 jspInit()는 JSP 객체(JSP로부터 만들어진 서블릿 객체)가 생성될 때 호출됨
- 만약 JSP 페이지에서 init()을 오버라이딩할 일이 있다면 init() 대신 jspInit()를 오버라이딩

##### jspDestroy()

- JSP 객체가 언로드(Unload) 될 때 호출됨
- 만약 JSP 페이지에서 destroy()을 오버라이딩할 일이 있다면 destroy() 대신 jspDestroy()를 오버라이딩

##### _jspService()

- HttpJspPage에 선언된 _jspService()는 JSP 페이지가 해야 할 작업이 들어 있는 메서드
- 서블릿 컨테이너가 service()를 호출하면 service() 메서드 내부에서는 바로 이 메서드를 호출함으로써 JSP 페이지에 작성했던 코드들이 실행되는 것



#### JSP 객체의 실체 분석

##### 자동 생성된 서블릿 클래스의 이름

- 톰캣 서버에서는 Hello_jsp 같은 형식으로 클래스 이름을 짓고있지만, 이름 짓는 방식은 서블릿 컨테이너마다 다름

##### HttpJspBase 클래스

- Hello_jsp는 HttpJspBase를 상속받고 있는데(책 참고), HttpJspBase는 톰캣 서버에서 제공하는 클래스임
  - 이 클래스의 소스 코드를 분석해 보면 결국 HttpServlet 클래스를 상속받았고 HttpJspPage 인터페이스를 구현함
  - 즉, HttpJspBase를 상속받은 Hello_jsp는 서블릿이라는 뜻

##### JSP 내장 객체

- _jspService()의 매개변수는 HttpServletRequest와 HttpServletResponse 객체임
  - 매개변수의 이름은 반드시 request, response로 해야 함

``` java
public void _jspService(
  final javax.servlet.http.HttpServletRequest request,
  final javax.servlet.http.HttpServletResponse response,  
)
```

##### JSP의 출력문

- Hello_jsp 소스를 보면 Hello.jsp에서 작성했던 HTML 문장이 그대로 출력문으로 만들어진 것을 알 수 있음

``` html
out.write("<title>Hello</title>\n")
out.write("</head>\n")
out.write("<body>\n")
out.write("<p>안녕하세요</p>\n")
```

이런 점 때문에 JSP를 사용함



#### HttpJspBase 클래스의 소스

###### Hello_jsp의 슈퍼 클래스인 HttpJspBase 클래스에 대해 간단히 알아보자

- HttpJspBase는 HttpServlet을 상속받기 때문에 이 클래스의 자식 클래스는 당연히 서블릿 클래스가 됨



#### JSP 프리컴파일

실무에서는 웹 애플리케이션을 서버에 배치할 때 모든 JSP 파일에 대해 자바 서블릿 클래스를 미리 생성하기도 함

- 그 이유는 JSP 실행 요청이 들어 왔을 때, 곧바로 서블릿을 호출할 수 있기 때문
- 즉, 이렇게 미리 자바 서블릿을 만들게 되면, JSP를 실행할 때마다 JSP 파일이 변경되었는지, JSP 파일에 대해 서블릿 파일이 있는지 매번 검사할 필요가 없음
- 또한, JSP 파일에 대해 자바 서블릿 소스를 생성하고 컴파일하는 과정이 없어서 실행 속도를 높일 수 있음

##### 이 방식의 문제는 JSP를 편집하면 서버를 다시 시작해야 함

##### 시스템을 도입한 후 안정화 단계까지는 오류교정이나 기능 변경이 수시로 발생하므로, 초기단계에는 프리컴파일 설정을 피하는 것이 좋음



### 5.3 JSP의 주요 구성 요소

##### JSP를 구성하는 요소는 크게 두 가지로 나눌 수 있음.

1. 템플릿 데이터
2. JSP 전용 태그

##### 템플릿 데이터

- 클라이언트로 출력되는 콘텐츠( 예 : HTML, 자바스크립트, 스타일 시트, JSON 형식 문자열, XML, 일반 텍스트 등 )

##### JSP 전용 태그

- 특정 자바 명령문으로 바뀌는 태그



#### JSP로 만드는 계산기 실습

``` jsp
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
String v1 = "";
String v2 = "";
String result = "";
String[] selected = {"", "", "", ""};

if (request.getParameter("v1") != null) {
    v1 = request.getParameter("v1");
    v2 = request.getParameter("v2");
    String op = request.getParameter("op");

    result = calculate(
            Integer.parseInt(v1),
            Integer.parseInt(v2),
            op);

    if ("+".equals(op)){
      selected[0] = "selected";
    } else if ("-".equals(op)) {
      selected[1] = "selected";
    } else if ("*".equals(op)) {
      selected[2] = "selected";
    } else if ("/".equals(op)) {
      selected[3] = "selected";
    }
}
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN""http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
<meta http-equiv="Content-Type" content="text/html"; charset="UTF-8">
<title>계산기</title>
</head>
<body>
<h2>JSP 계산기</h2>
<form action="Calculator.jsp" method="get">
    <input type="text" name="v1" size="4" value="<%=v1%>">
    <select name="op">
        <option value="+" <%=selected[0]%>>+</option>
        <option value="-" <%=selected[1]%>>+</option>
        <option value="*" <%=selected[2]%>>+</option>
        <option value="/" <%=selected[3]%>>+</option>
    </select>
    <input type="text" name="v2" size="4" value="<%=v2%>">
    <input type="submit" value="=">
    <input type="text" size="8" value="<%=result%>"><br>
</form>
</body>
</html>
<%!
    private String calculate(int a, int b, String op) {
      int r = 0;

      if ("+".equals(op)) {
        r = a + b;
      } else if ("-".equals(op)) {
          r = a - b;
      } else if ("*".equals(op)) {
          r = a * b;
      } else if ("/".equals(op)) {
          r = a / b;
      }

      return Integer.toString(r);
    }
%>

```

![image-20200229211954136](/Users/beomju/Library/Application Support/typora-user-images/image-20200229211954136.png)

![image-20200229212057131](/Users/beomju/Library/Application Support/typora-user-images/image-20200229212057131.png)

값을 계산하기 위해 "=" 버튼을 누르면 다시 Calculator.jsp를 요청하도록 함

##### 탬블릿 데이터

- 클라이언트로 출력되는 콘텐츠 (앞 부분에서 설명)

``` jsp
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN""http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
<meta http-equiv="Content-Type" content="text/html"; charset="UTF-8">
<title>계산기</title>
</head>
<body>
<h2>JSP 계산기</h2>
<form action="Calculator.jsp" method="get">
  ...
</form>
</body>
</html>
```

템플릿 데이터는 서블릿 코드를 생성할 때 출력문으로 바뀜

#### JSP 전용 태그 - 지시자

> ##### <%@ 지시자 속성="값" 속성="깂" ... %>

<%@ 지시자 --- %>는 JSP 전용 태그로 '지시자'나 '속성'에 따라 특별한 자바 코드를 생성

JSP 지시자에는 **page, taglib, include**가 있음

##### page 지시자

- Page 지시자는 JSP 페이지와 관련된 속성을 정의할 때 사용하는 태그
- 별도의 자바 명령문을 생성하지 않음

``` jsp
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
```

1. language 속성
   - 스크립트릿이나, 표현식, 선언부를 작성할 때 사용할 프로그래밍 언어를 지정
   - 즉, JSP 페이지에 삽입되는 코드의 스크립팅 언어를 지정
   - Language 속성을 생략하면 기본으로 'java'

2. contentType 속성
   - 출력할 데이터의 MIME 타입과 문자 집합을 지정
   - 예제 코드에서 값 'charset=UTF-8'은 출력할 데이터를 UTF-8로 변환할 것을 지시

3. pageEncoding 속성
   - 출력할 데이터의 문자 집합을 지정
   - 기본값은 'ISO-8859-1'
   - 이 속성을 생략하면 contentType에 설정된 값을 사용함

#### JSP 전용 태그 - 스크립트릿

> ##### <% 자바 코드 %>

JSP 페이지 안에 자바 코드를 넣을 때는 스크립트릿(scriptlet Elements) 태그 <% %> 안에 작성

- 스크립트릿 태그 안에 작성한 내용은 서블릿 파일을 만들 때 그대로 복사됨

``` jsp
<%
String v1 = "";
String v2 = "";
String result = "";
String[] selected = {"", "", "", ""};

if (request.getParameter("v1") != null) {
    v1 = request.getParameter("v1");
    v2 = request.getParameter("v2");
    String op = request.getParameter("op");

    result = calculate(
            Integer.parseInt(v1),
            Integer.parseInt(v2),
            op);
  ...
}
%>
```

이렇게 작성한 스크립트릿의 내용은 서블릿 파일을 생성할 때 그대로 복사됨

#### JSP 내장 객체

JSP 페이지에서 스크립트릿 <% %>이나 표현식 <%= %>을 작성할 때 별도의 선언 없이 사용하는 자바 객체가 있음

- 이런 객체를 **JSP 내장 객체 (Implicit Objects)**라고 함
- 예제 코드의 request 객체가 바로 이것

``` jsp
v1 = request.getParameter("v1");
v2 = request.getParameter("v2");
String op = request.getParameter("op");
```

##### JSP 기술 사양서에는 스크립트릿이나 표현식에서 JSP 내장 객체의 사용을 보장한다고 되어 있음

- 그래서 객체를 선언하지 않고 바로 사용할 수 있음

##### JSP 페이지 작성자가 별도 선언 없이 즉시 이용할 수 있는 9개의 객체

- request, response, pageContext, session, application, config, out, page, exception



#### JSP 전용 태그 - 선언문

> ##### <%! 멤버 변수 및 메서드 선언 %>

##### 서블릿 클래스의 멤버(변수나 메서드)를 선언할 때 사용하는 태그

JSP 페이지에서 선언문 <%! %>을 작성하는 위치는 어디든 상관없음

- 선언문은 _jspService() 밖의 클래스 블록 안에 복사되기 때문

``` jsp
<%!
    private String calculate(int a, int b, String op) {
      int r = 0;

      if ("+".equals(op)) {
        ...
      }

      return Integer.toString(r);
    }
%>
```

calculate()는 클라이언트가 보낸 매개변수 값을 계산하여 그 결과를 문자열로 바꾸어 반환



#### JSP 전용 태그 - 표현식

> ##### <%= 결과를 반환하는 자바 표현식 %>

``` jsp
<input type="text" name="v1" size="4" value="<%=v1%>">
    <select name="op">
        <option value="+" <%=selected[0]%>>+</option>
        <option value="-" <%=selected[1]%>>+</option>
        <option value="*" <%=selected[2]%>>+</option>
        <option value="/" <%=selected[3]%>>+</option>
    </select>
```

##### 표현식 태그는 문자열을 출력할 때 사용

- 따라서 표현식 안에는 결과를 반환하는 자바 코드가 와야 함

- 표현식도 스크립트립과 같이 _jspService() 안에 순서대로 복사함

- ##### 표현식 안의 자바 코드는 출력문으로 만들어짐



### 5.4 서블릿에서 뷰 분리하기

![image-20200229220953158](/Users/beomju/Library/Application Support/typora-user-images/image-20200229220953158.png)

클라이언트로부터 요청이 들어오면 서블릿은 데이터를 준비(모델 역할)하여 JSP에 전달 (컨트롤러 역할)함

JSP는 서블릿이 준비한 데이터를 가지고 웹 브라우저로 출력할 화면을 만듦

#### 값 객체(VO) = 데이터 수송 객체 (DTO)

###### 데이터베이스에서 가져온 정보를 JSP 페이지에 전달하려면 그 정보를 담을 객체가 필요함

- 이렇게 값을 담는 용도로 사용하는 객체를 **값 객체**라고 부름
- 값 객체는 계층 간 또는 객체 간에 데이터를 전달하는 데 이용하므로 **데이터 수송객체(data transfer object)**라고도 부름
- 또한 값 객체는 업무영역의 데이터를 표현하기 때문에 객체지향 분석 및 설계 분야에서는 **도메인 객체**라고도 부름

##### 값 객체 생성

``` java
package spms.vo;

import java.util.Date;

public class Member {
  protected int no;
  protected String name;
  protected String email;
  protected String password;
  protected Date createDate;
  protected Date modifiedDate;

  public int getNo() {
    return no;
  }

  public Member setNo(int no) {
    this.no = no;
    return this;
  }

  public String getName() {
    return name;
  }

  public Member setName(String name) {
    this.name = name;
    return this;
  }

  public String getEmail() {
    return email;
  }

  public Member setEmail(String email) {
    this.email = email;
    return this;
  }

  public String getPassword() {
    return password;
  }

  public Member setPassword(String password) {
    this.password = password;
    return this;
  }

  public Date getCreateDate() {
    return createDate;
  }

  public Member setCreateDate(Date createDate) {
    this.createDate = createDate;
    return this;
  }

  public Date getModifiedDate() {
    return modifiedDate;
  }

  public Member setModifiedDate(Date modifiedDate) {
    this.modifiedDate = modifiedDate;
    return this;
  }
}
```

##### 셋터(Setter) 메서드의 리턴값이 void가 아니라 Member

- ##### 이유는 셋터 메서드를 연속으로 호출하여 값을 할당할 수 있게 하기 위함

- > New Member().setNo(1).setName("홍길동").setEmail("hong@test.com");

- ##### 셋터 메서드의 연속 호출



#### 서블릿에서 뷰 관련 코드 제거

``` java
@WebServlet("/member/list")
public class MemberListServlet extends HttpServlet {
  private static final long serialVersionUID = 1L;

  @Override
  public void doGet(
      HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    Connection conn = null;
    Statement stmt = null;
    ResultSet rs = null;

    try {
      ServletContext sc = this.getServletContext();
      Class.forName(sc.getInitParameter("driver"));
      conn = DriverManager.getConnection(
          sc.getInitParameter("url"),
          sc.getInitParameter("username"),
          sc.getInitParameter("password"));
      stmt = conn.createStatement();
      rs = stmt.executeQuery(
          "SELECT MNO,MNAME,EMAIL,CRE_DATE" +
              " FROM MEMBERS" +
              " ORDER BY MNO ASC");

      response.setContentType("text/html; charset=UTF-8");
      ArrayList<Member> members = new ArrayList<Member>();

			// 데이터베이스에서 회원 정보를 가져와 Member에 담는다.
			// 그리고 Member객체를 ArrayList에 추가한다.
      while (rs.next()) {
        members.add(new Member()
						.setNo(rs.getInt("MNO"))
						.setName(rs.getString("MNAME"))
						.setEmail(rs.getString("EMAIL"))
						.setCreateDate(rs.getDate("CRE_DATE")));
      }
      // request에 회원 목록 데이터 보관한다.
      request.setAttribute("members", members);
      // JSP로 출력을 위임한다.
      RequestDispatcher rd = request.getRequestDispatcher(
					"/member/MemberList.jsp");
			rd.include(request, response);

    } catch (Exception e) {
      throw new ServletException(e);
    } finally {
      try {
        if (rs != null) rs.close();
      } catch (Exception e) {
      }
      try {
        if (stmt != null) stmt.close();
      } catch (Exception e) {
      }
      try {
        if (conn != null) conn.close();
      } catch (Exception e) {
      }
    }

  }
}
```

##### HTML 출력 코드 제거

- 앞으로 회원 목록 화면을 생성하고 출력하는 것은 MemberList.jsp가 담당

##### JSP에 전달할 회원 목록 데이터 준비

``` java
ArrayList<Member> members = new ArrayList<Member>();
```

##### 셋터 메서드를 연속으로 호출하여 할당 (스크립팅 언어에서 흔히 쓰이는 코딩 스타일)

``` java
while (rs.next()) {
  members.add(new Member()
              .setNo(rs.getInt("MNO"))
              .setName(rs.getString("MNAME"))
              .setEmail(rs.getString("EMAIL"))
              .setCreateDate(rs.getDate("CRE_DATE")));
}
```

- Member 클래스에서 셋터 메서드의 반환 값을 this라고 한 이유.
  - 객체를 생성하고 바로  초기화 할 수 있기 때문에

##### RequestDispatcher를 이용한 forward, include

- 회원 목록 데이터가 준비가 되었다면, 화면 생성을 위해 JSP로 작업을 위임해야 함

  - ##### 이렇게, 다른 서블릿이나 JSP로 작업을 위임할 때 사용하는 객체가 RequestDispatcher

``` java
RequestDispatcher rd = request.getRequestDispatcher(
  "/member/MemberList.jsp");
rd.include(request, response);
```

//Include할 것인지 forward할 것인지 판단

##### ServletRequest(HttpServletRequest)를 통한 데이터 전달

- MemberList.jsp를 인클루드 할 때 MemberListServlet.doGet() 메서드의 매개변수 값을 그대로 넘김
- 즉, MemberListServletrhk MemberList.jsp는 request와 response를 공유함
  - 바로 이 부분을 이용하여 데이터를 전달

ServletRequest는 클라이언트의 요청을 다루는 기능 외에 어떤 값을 보관하는 **보관소 기능**도 있음

- setAttribute()를 호출하여 값을 보관할 수도 있고, getAttribute를 호출하여 보관된 값을 꺼낼 수도 있음

``` java
request.setAttribute("members", members);
```



#### 뷰 컴포넌트 만들기

``` jsp
<%@page import="spms.vo.Member"%>
<%@page import="java.util.ArrayList"%>
<%@ page
        language="java"
        contentType="text/html; charset=UTF-8"
        pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>회원 목록</title>
</head>
<body>
<h1>회원목록</h1>
<p><a href='add'>신규 회원</a></p>
<%
    ArrayList<Member> members = (ArrayList<Member>)request.getAttribute(
            "members");
    for(Member member : members) {
%>
<%=member.getNo()%>,
<a href='update?no=<%=member.getNo()%>'><%=member.getName()%></a>,
<%=member.getEmail()%>,
<%=member.getCreatedDate()%>
<a href='delete?no=<%=member.getNo()%>'>[삭제]</a><br>
<%} %>
</body>
</html>
```

> ##### 여기서 에러 떴음!
>
> ![image-20200302214241641](/Users/beomju/Library/Application Support/typora-user-images/image-20200302214241641.png)

### 5.5 포워딩과 인클루딩

![image-20200302211158293](/Users/beomju/Library/Application Support/typora-user-images/image-20200302211158293.png)

##### 포워딩을 통한 예외처리

![image-20200302220802279](/Users/beomju/Library/Application Support/typora-user-images/image-20200302220802279.png)

- 예외가 발생하면 안내 문구를 출력하는 JSP로 위임

##### 인쿨루딩을 통한 예외처리

###### Header.jsp 만들기

``` jsp
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div style="background-color: #00008b;color: #ffffff;height: 20px;padding: 5px;">
    SPMS(Simple Project Management System)
</div>
```

###### Tail.jsp 만들기

``` jsp
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div style="background-color:#f0fff0;height:20px;padding:5px; margin-top:10px">
    SPMS &copy; 2013
</div>
```

###### MemberList.jsp에서 Header.jsp와 Tail.jsp를 포함하기

``` jsp
...
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>회원 목록</title>
</head>
<body>
<jsp:include page="/Header.jsp"/>  //
<h1>회원목록</h1>
<p><a href='add'>신규 회원</a></p>
...
<%=member.getNo()%>,
<a href='update?no=<%=member.getNo()%>'><%=member.getName()%></a>,
<%=member.getEmail()%>,
<%=member.getCreatedDate()%>
<a href='delete?no=<%=member.getNo()%>'>[삭제]</a><br>
<%} %>
<jsp:include page="/Tail.jsp"/>  //
</body>
</html>
```



### 데이터 보관소

![image-20200302221426209](/Users/beomju/Library/Application Support/typora-user-images/image-20200302221426209.png)

#####  ServletContext 보관소

- 웹 애플리케이션이 시작될 때 생성되어 웹 애플리케이션이 종료될 때까지 유지
- 어떤 객체를 웹 애플리케이션이 실행되는 동안 모든 서블릿들과 공유하고 싶을 때 사용
- JSP에서는 application 변수를 통해 이 보관소를 참조할 수 있음

##### HttpSession 보관소

- 클라이언트의 최초 요청 시 생성되어 브라우저를 닫을 때까지 유지됨
- 보통 로그인할 때 이 보관소를 초기화하고, 로그아웃하면 이 보관소에 저장된 값을 비움
- JSP에서는 session 변수를 통해 이 보관소를 참조 가능

##### ServletRequest 보관소

- 클라이언트의 요청이 들어올 때 생성되어, 클라이언트에게 응답할 때까지 유지됨
- 포워딩이나 인클루딩하는 서블릿들 사이에서 값을 공유할 때 유용함
- JSP에서는 request 변수를 통해 이 보관소를 참조 가능

##### JspContext 보관소

- JSP 페이지를 실행하는 동안에만 유지됨
- JSP에서는 pageContext 변수를 통해 이 보관소를 참조 가능



##### 보관소에 넣고 빼는 방법

> ##### 보관소 객체.setAttribute(키, 값);  // 값 저장
>
> ##### 보관소 객체.getAttribute(키);      // 값 조회



#### ServletContext의 활용

###### 지금까지 데이터베이스를 사용하는 서블릿들은 모두, 호출될 때마다 데이터베이스 커넥션을 생성함

###### 이 데이터베이스 커넥션 객체를 웹 애플리케이션이 시작될 때 생성하여 ServletContext에 저장하면, 데이터베이스를 이용하는 모든 서블릿은 ServletContext에서 DB 커넥션 객체를 얻을 수 있음

##### 공유 자원을 준비하는 서블릿 작성

``` java
public class AppInitServlet extends HttpServlet {

  @Override
  public void init(ServletConfig config) throws ServletException {
    System.out.println("AppInitServlet 준비...");
    super.init(config);

    try {
      ServletContext sc = this.getServletContext();
      Class.forName(sc.getInitParameter("driver"));
      Connection conn = DriverManager.getConnection(sc.getInitParameter("url"),sc.getInitParameter("username"),sc.getInitParameter("password"));
      sc.setAttribute("conn", conn);
    } catch (Throwable e) {
      throw new ServletException(e);
    }
  }

  @Override
  public void destroy() {
    System.out.println("AppInitServlet 마무리 ...");
    super.destroy();
    Connection conn = (Connection)this.getServletContext().getAttribute("conn");
    try {
      if(conn != null && conn.isClosed() == false) {
        conn.close();
      }
    } catch (Exception e) {}
  }

}
```

- 지금까지 서블릿을 만들 때, service()를 직접 오버라이딩 하거나 doGet(), doPost()를 오버라이딩 했지만, 

  - 이번 서블릿은 클라이언트에서 호출할 서블릿이 아니므로 이런 메서드들을 오버라이딩 하지 않음
  - 대신, init()을 오버라이딩

  

- 데이터베이스 커넥션 객체를 준비한 다음, **모든 서블릿들이 사용할 수 있도록 ServletContext 객체에 저장**

``` java
sc.setAttribute("conn", conn)
```

##### 서블릿 배치와 <load-on-startup>태그

``` xml
<servlet>
  <servlet-name>AppInitServlet</servlet-name>
  <servlet-class>spms.servlets.AppInitServlet</servlet-class>
  <load-on-startup>1</load-on-startup>
</servlet>
```

###### Load-on-startup 태그

- 서블릿 객체는 클라이언트의 최초 요청 시 생성되지만,

  - ##### AppInitServlet 처럼 다른 서블릿이 생성되기 전에 미리 준비 작업을 해야 하는 서블릿이라면, 클라이언트 요청이 없더라도 생성되어야 함

  - 그럴 때 사용하는 태그임

- ##### 서블릿을 배치할 때 <load-on-startup> 태그를 지정하면, 해당 서블릿은 웹 애플리케이션이 시작될 때 자동으로 생성됨



##### ServletContext에 저장된 DB 커넥션 사용

이제 서블릿에서 DB 커넥션을 직접 준비할 필요가 없음

- ServletContext 보관소에 저장된 DB커넥션 객체를 꺼내 쓰면 됨

회원 목록을 처리하는 MemberListServlet의 일부분을 다음과 같이 수정

``` java
  @Override
  public void doGet(
      HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    Connection conn = null;
    Statement stmt = null;
    ResultSet rs = null;

    try {
      ServletContext sc = this.getServletContext();
//      Class.forName(sc.getInitParameter("driver"));
//      conn = DriverManager.getConnection(
//          sc.getInitParameter("url"),
//          sc.getInitParameter("username"),
//          sc.getInitParameter("password"));
      conn = (Connection) sc.getAttribute("conn");  //추가
      stmt = conn.createStatement();
      rs = stmt.executeQuery(
          "SELECT MNO,MNAME,EMAIL,CRE_DATE" +
              " FROM MEMBERS" +
              " ORDER BY MNO ASC");
```



#### HttpSession의 활용 - 로그인

###### HttpSession 객체는 클라이언트 당 한 개가 생성됨

###### 웹 브라우저로부터 요청이 들어오면, 그 웹 브라우저를 위한 HttpSession 객체가 있는지 검사하고, 없다면 새로 HttpSession 객체를 만듦

###### 이렇게 생성된 HttpSession 객체는 그 웹 브라우저로부터 일정 시간 동안 Timeout 요청이 없으면, 삭제됨



##### 로그인을 수행하는 시나리오

1. 웹 브라우저에서 '/auth/login' 서블릿을 요청
2. LogInServlet은 LoginForm.jsp로 화면 출력 작업을 위임
3. LogInForm.jsp는 로그인 입력폼을 만들어 출력
4. 사용자가 입력한 정보를 가지고 다시 '/auth/login'서블릿을 요청
   - 단 이번에는 POST요청
5. LogInServlet은 이메일과 암호가 일치하는 회원 정보를 데이터베이스에서 찾아서 값 객체 'Member'에 담음
   - 또한, 다른 서블릿들도 참조할 수 있드록 HttpSession 객체에 보관.
   - 만약 이메일과 암호가 일치하는 회원을 찾지 못한다면, LogInFail.jsp로 작업을 위임
6. 로그인 성공일 때, 회원 목록 페이지로 리다이렉트. 
   - 로그인 실패 시, 로그인 실패 메시지를 출력한 후 다시 로그인 입력폼으로 리프래시

##### 로그인 컨트롤러 만들기

``` java
@WebServlet("/auth/login")
public class LogInServlet extends HttpServlet {

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    RequestDispatcher rd = request.getRequestDispatcher("/auth/LogInForm.jsp");
    rd.forward(request, response);
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;

    try {
      ServletContext sc = this.getServletContext();
      conn = (Connection) sc.getAttribute("conn");
      stmt = conn.prepareStatement(
          "SELECT MNAME, EMAIL FROM  MEMBERS"
          + " WHERE EMAIL=? AND PWD=?");
      stmt.setString(1, request.getParameter("email"));
      stmt.setString(2, request.getParameter("password"));
      rs = stmt.executeQuery();
      if (rs.next()) {
        Member member = new Member()
            .setEmail(rs.getString("EMAIL"))
            .setName(rs.getString("MNAME"));
        HttpSession session = request.getSession();
        session.setAttribute("member", member);
      } else {
        RequestDispatcher rd = request.getRequestDispatcher("/auth/LogInFail.jsp");
        rd.forward(request, response);
      }
    } catch (Exception e) {
      throw new ServletException(e);
    } finally {
      try { if (rs != null) rs.close();} catch (Exception e) {}
      try { if (stmt != null) stmt.close();} catch (Exception e) {}
    }
  }
}
```

코드 분석

1. 웹 브라우저로부터 GET 요청이 들어오면 doGet()이 호출되어 LogInForm.jsp로 포워딩

``` java
rd.forward(request, response);
```

- JSP에서 다시 서블릿으로 돌아올 필요가 없어서 인클루딩 대신 포워딩으로 처리

2. 사용자가 이메일과 암호를 입력한 후 POST 요청을 하면 doPost()가 호출됨.
   - doPost()에서는 데이터베이스로부터 회원 정보를 조회. 만약 이메일과 암호가 일치하지 않는 회원을 찾는다면 값 객체 Member에 회원 정보를 담음

``` java
Member member = new Member()
            .setEmail(rs.getString("EMAIL"))
            .setName(rs.getString("MNAME"));
```

3. 그리고 Member 객체를 HttpSession에 보관

``` java
HttpSession session = request.getSession();
session.setAttribute("member", member);
```

4.1 로그인 성공이면, /member/lsit로 리다이렉트

``` java
rd.forward(request, response);
```

4.2 로그인 실패면, /auth/LogInFail.jsp로 포워딩

``` java
RequestDispatcher rd = request.getRequestDispatcher("/auth.LogInFail.jsp");
rd.forward(request, response);
```

![image-20200303214119831](/Users/beomju/Library/Application Support/typora-user-images/image-20200303214119831.png)



#### HttpSession의 활용 - 로그인 정보 사용

##### 로그인 정보 사용 시나리오

1. 로그인 성공일 떄, 서버로부터 리다이렉트 응답을 받음. 즉시 웹 브라우저는 톰캣 서버에 리다이렉트 URL(/member/lsit)을 요청
2. MemberListServlet은 데이터베이스에서 회원 목록을 가져온 후, MemberList.jsp에게 화면 출력 작업을 위임
3. MemberList.jsp는 화면 상당의 내용을 출력하기 위해 Header.jsp를 인클루딩
4. Header.jsp는 HttpSession 객체에 보관된 로그인 회원의 정보 (Member 객체)를 꺼냄
5. 또한 Header.jsp는 Member 객체로부터 이름을 추출하여 로그인 사용자 정보를 출력
6. MemberList.jsp는 화면 하단의 내용을 출력하기 위해 Tail.jsp를 인클루딩
7. MemberListServlet은 MemberList.jsp가 작업한 내용을 최종적으로 출력함으로써 응답을 완료

##### 페이지 헤더에 로그인 사용자 이름 출력

- Header.jsp를 다음과 같이 수정

``` jsp
<%@page import="spms.vo.Member"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%
    Member member = (Member)session.getAttribute("members");
%>
<div style="background-color:#00008b;color:#ffffff;height:20px;padding: 5px;">
    SPMS(Simple Project Management System)
    <% if (member.getEmail() != null) { %>
<span style="float:right;">
  <%=member.getName()%>
  <a style="color:white;"
     href="<%=request.getContextPath()%>/auth/logout">로그아웃</a>
</span>
</div>
```

- JSP 내장 객체 session을 사용하여 "member"라는 키로 저장된 값을 꺼냄

``` jsp
<% Member member = (Member)session.getAttribute("members"); %>
```

- HttpSession 보관소에서 꺼낸 Member 객체로부터 이름을 알아내어 사용자 로그인 정보를 출력

``` jsp
<span style="float:right;">
<%=member.getName()%>
<a style="color:white;"
   href="<%=request.getContextPath()%>/auth/logout">로그아웃</a>
</span>
```



#### HttpSession의 활용 - 로그아웃

1. '로그아웃' 링크를 클릭하면, 웹 브라우저는 LogOutServlet을 요청
2. LogOutServlet은 HttpSession 객체를 없애기 위해 invalidate()를 호출.
   
   - HttpSession 객체는 제거됨
3. 그리고 로그인 입력폼으로 리다이렉트 함
   
   - 다시 로그인 입력폼을 출력할 때 HttpSession객체는 새로 생성됨
4. 새로 생성된 HttpSession을 가지고 회원 목록 서블릿을 실행함
   
- MemberListServlet은 데이터베이스에서 회원 목록 정보를 가져옴
   
5. 그리고 MemberListServlet은 MemberList.jsp에 화면 출력을 위임

6. MemberList.jsp는 화면 상단의 내용을 출력하기 위해 Header.jsp를 인클루딩

7. ##### Header.jsp는 HttpSession 보관소에서 'member'라는 이름으로 저장된 객체를 꺼냄.

   - ##### 하지만, 로그아웃하면서 기존의 HttpSession 객체가 제거되고 새로 만들어졌기 때문에 이 세션 객체에는 어떠한 값도 들어있지 않음. 

   - ##### 따라서, HttpSession 객체는 null을 반환

   - ##### 당연히 null에 대해 회원 이름을 꺼내려 하므로 오류가 발생할 것



##### 로그아웃 서블릿 생성

``` java
public class LogOutServlet extends HttpServlet {

  @Override
  protected void doGet(
      HttpServletRequest request, HttpServletResponse response
  ) throws ServletException, IOException {
    HttpSession session = request.getSession();
    session.invalidate();

    response.sendRedirect("login");
  }
  
}
```

- HttpSession 객체를 무효화 하기 위해 invalidate()를 호출
- HttpSession 객체를 무효화시킨 후, 새로운 요청이 들어오면 HttpSession 객체가 새로 만들어짐

#### 

#### ServletRequest의 활용

###### ServletRequest 객체에 데이터를 보관하면 포워딩이나 인클루딩을 통해 협업하는 서블릿끼리 데이터를 공유할 수 있음.

- ###### request와 request를 같이 사용하기 때문

``` java
protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
  RequestDispatcher rd = request.getRequestDispatcher("/auth/LogInForm.jsp");
  rd.forward(request, response);
}
```

##### 협업을 하는 서블릿들 끼리의 ServletRequest의 공유

![image-20200303224328860](/Users/beomju/Library/Application Support/typora-user-images/image-20200303224328860.png)



#### JspContext의 활용

###### JspContext 보관소는 JSP 페이지를 실행할 때 생성되고, 실행이 완료되면 이 객체는 제거됨, 따라서 JSP 페이지 내부에서만 사용될 데이터를 공유할 때 사용함

``` jsp
<body>
<jsp:include page="/Header.jsp"/>
<h1>회원목록</h1>
<p><a href='add'>신규 회원</a></p>
```

< jsp:include>같은 이런 태그들은 JSP 엔진이 서블릿 클래스를 생성할 때 특정 자바 코드로 변환됨

- 이때 이 태그의 값을 다루는 객체를 '태그 핸들러'라고 부름

- ##### 바로 이 태그 핸들러에게 데이터를 전달하고자 할 때 JspContext 보관소를 사용하는 것

- JSP 페이지에 선언된 로컬 변수는 태그 핸들러에서 접근 불가능

- ##### 태그 핸들러에게 전달할 데이터가 아니라면 JspContext에 값을 보관할 필요는 없음



### 5.7 JSP 액션 태그의 사용

###### JSP 액션 태그를 사용해서  JSP 페이지를 좀 더 개선해 보자!

##### JSP 페이지를 작성할 때, 가능한 자바 코드의 삽입을 최소화하는 것이 유지보수에 좋음

- ##### 이를 위해 JSP에서는 다양한 JSP 전용 태그를 제공하고, 이러한 태그들의 집합을 'JSP 액션'이라고 함

##### < jsp:useBean> (이하 띄워쓰기는 무시)

- 자바 인스턴스(빈)를 준비
- 자바 인스턴스를 꺼내거나, 자바 인스턴스를 새로 만들어 보관소에 저장하는 코드를 생성함

##### < jsp:setProperty>

- 자바 빈의 프로퍼티 값을 설정
- 자바 객체의 셋터 메서드를 호출하는 코드를 생성

##### < jsp:getProperty>

- 자바 빈의 프로퍼티 값을 꺼냄
- 자바 객체의 겟터 메서드를 호출하는 코드를 생성

##### < jsp:include>

- 정적(HTML, 텍스트 파일) 또는 동적 자원 (서블릿/JSP)을 인클루딩 하는 자바 코드를 생성

##### < jsp:forward>

- 현재 페이지의 실행을 멈추고 다른 정적 자원이나 동적 자원으로 포워딩하는 자바 코드를 생성

##### < jsp:param>

- ServletRequest 객체에 매개변수를 추가하는 코드를 생성

##### < jsp:elememt>

- 임의의 XML 태그나 HTML 태그를 생성



#### JSP 액션 태그 - < jsp:useBean >

- application, session, request, page 보관소에 저장된 자바 객체를 꺼낼 수 있음

##### Jsp:useBean 문법

> < jsp:useBean> id "이름" scope="page|request|session|application"  class="클래스명" type="타입명"/>

##### 예시

``` jsp
<jsp:useBean> id = "members" scope = "request" class = "java.util.Arraylist" type = "java.util.ArrayList<spms.vo.Member>"/>
```

##### 이 액션태그를 자바 코드로 바꾼다면?

``` java
능java.util ArrayList<spms.vo.Member> members = (java.util.ArrayList<spms.vo.Member>)request.getAttribute("members");
if (member == null ) {
  members = new java.util.ArrayList();
  request.setAttribute("members", members);
}
```

###### id 속성

- 객체의 이름을 설정
- 이 이름을 사용하여 보관소로부터 값을 꺼낼 것임

###### scope 속성

- 기존의 객체를 조회하거나 새로 만든 객체를 저장할 보관소를 지정함
- 네 가지 보관소 중 하나를 지정할 수 있음
  - 'page'는 JspContext
  - 'request'는 ServletRequest
  - 'session'은 HttpSession
  - 'application'은 ServletContext

- scope를 지정하지 않으면 자동으로 page

###### class 속성

- 자바 객체를 생성할 때 사용할 클래스 이름을 지정
- 따라서 클래스 이름은 반드시 패키지 이름을 포함해야 하고 인터페이스 이름은 불가능

###### type 속성

- 참조 변수를 선언할 때 사용할 타입의 이름
- 클래스 이름 또는 인터페이스 이름이 올 수 있음



##### JSP 액션 태그의 존재 의의

- 액션태그를 이용하면 자바 객체를 생성하거나 request 객체에서 값을 꺼내는 작업을 쉽게 처리할 수 있음
- 이렇게 JSP 액션 태그를 사용하면 자바 언어를 모르는 웹 디자이너나 웹 퍼블리셔들도 손쉽게 웰 페이지를 만들 수 있음.
  - 즉, 비즈니스 로직을 처리하는 부분과 화면을 처리하는 부분을 나눠서 개발할 수 있음
- 실무에서는 웹 브라우저가 출력할 화면을 만들 때 jSP 액션 태그보다는 오히려 HTML, CSS, JavaScript 기술이 더 요구됨



### 5.10 DAO 만들기

##### Data access dbject

- 데이터 처리를 전문으로 하는 객체
- 데이터베이스나 파일, 메모리 등을 이용하여 애플리케이션 데이터를 생성, 조회, 변경, 삭제한느 역할을 수행함

##### DAO가 적용된 후의 회원 목록 조회 시나리오

1. 웹 브라우저에게 회원 목록을 요청
2. MemberListServlet은 회원 목록 데이터를 얻기 위해  MemberDao의 메서드를 호출
3. MemberDao는 데이터베이스로부터 회원 정보를 가져옴
4. MemberDao는 결과 레코드의 개수만큼 Member 객체를 만들어 서블릿에게 반환
5. MemberListServletdms MemerDao로부터 받은 회원 목록을 MemberList.jsp에게 전달
6. MemberList.jsp는 회원 목록 데이터를 가지고 화면을 생성. 그리고 다시 제어권을 MemberListServlet에게 넘김
7. MemberListServlet은 웹 브라우저의 요청에 대해 응답을 완료

##### DAO 생성

``` java
public class MemberDao {

  Connection connection;

  public void setConnection(Connection connection) {
    this.connection = connection;
  }

  public List<Member> selectList() throws Exception {
    Statement stmt = null;
    ResultSet rs = null;

    try {
      stmt = connection.createStatement();
      rs = stmt.executeQuery(
          "SELECT MNO, MNAME, EMAIL, CRE_DATE +" +
              " FROM MEMBERS" +
              " ORDER BY MNO ASC");

      ArrayList<Member> members = new ArrayList<Member>();

      while (rs.next()) {
        members.add(new Member()
        .setNo(rs.getInt("MNO"))
        .setName(rs.getString("MNAME"))
        .setEmail(rs.getString("EMAIL"))
        .setCreatedDate(rs.getDate("CRE_DATE")));
      }

      return members;
    } catch (Exception e) {
      throw e;
    } finally {
      try {if (rs != null) rs.close();} catch (Exception e) {}
      try {if (stmt != null) stmt.close();} catch (Exception e) {}
    }

  }

}

```

###### Connection 객체 주입

- MemberDao에서는 ServletContext에 접근할 수 없기 때문에, ServletContext에 보관된 DBConnection 객체를 꺼낼 수 없음.

- 이를 해결하기 위해, 외부로부터  Connection 객체를 주입받기 위한 셋터 메서드와 인스턴스 변수를 준비

- ``` java
  Connection connection;
  
  public void setConnection(Connection connection) {
    this.connection = connection;}
  ```



##### 서블릿에서  DAO 사용하기

``` java
package spms.servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import spms.dao.MemberDao;
import spms.vo.Member;

// UI 출력 코드를 제거하고, UI 생성 및 출력을 JSP에게 위임한다.
@WebServlet("/member/list")
public class MemberListServlet extends HttpServlet {
  private static final long serialVersionUID = 1L;

  @Override
  public void doGet(
      HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    Connection conn = null;
    Statement stmt = null;
    ResultSet rs = null;

    try {
      ServletContext sc = this.getServletContext();
      conn = (Connection) sc.getAttribute("conn");
      
      MemberDao memberDao = new MemberDao();
      memberDao.setConnection(conn);

      // request에 회원 목록 데이터 보관한다.
      request.setAttribute("members", memberDao.selectList());
      
      response.setContentType("text/html; charset=UTF-8");
      // JSP로 출력을 위임한다.
      RequestDispatcher rd = request.getRequestDispatcher(
          "/member/MemberList.jsp");
      rd.include(request, response);

    } catch (Exception e) {
      e.printStackTrace();
      request.setAttribute("error", e);
      RequestDispatcher rd = request.getRequestDispatcher("/Error.jsp");
      rd.forward(request, response);
    } finally {
      try {if (rs != null) rs.close();} catch(Exception e) {}
      try {if (stmt != null) stmt.close();} catch(Exception e) {}
      try {if (conn != null) conn.close();} catch(Exception e) {}
    }

  }
}
```

- 이제 MemberListServlet 클레스에는 더이상 데이터베이스와 관련된 코드가 존재하지 않음
  - 모두  MemberDao에 이관
- MemberListServlet이 할 일은, 클라이언트의 요청에 대해 어떤 Dao를 사용하고, 어느 JSP로 그 결과를 보내야 하는지 조정하는 것



### 5.11 ServletContextListener와 객체 공유

###### 서블릿 컨테이너는 웹 애플리케이션의 상태를 모니터링 할 수 있도록 웹 애플리케이션의 시작에서 종료까지 주요한 사건에 대해 알림 기능을 제공

###### 이러한 사건이 발생했을 때 알림을 받는 객체를 '리스너'라고 부름

이전 예제를 살펴보면 서블릿은 요청을 처리하기 위해 매번  DAO 인스턴스를 생성함. 이렇게 요청을 처리할 때마다 객체를 만들게 되면 많은 가비지가 생성되고 실행 시간이 길어짐.

DAO의 경우처럼 여러 서블릿이 사용하는 객체는 서로 공유하는 것이 메모리 관리나 실행 속도 측면에서 좋음.

- DAO를 공유하려면 ServletContext에 저장하는 것이 좋음

##### 그렇다면, 서블릿이 사용할 DAO 객체는 언제 준비해야 할까?

보통 웹 애플리케이션을 시작할 때 공유 객체들을 준비

AppInitServlet에서 DAO 객체를 준비해도 되지만, 웹 애플리케이션 이벤트를 이용하는 것이 더 좋음

##### 웹 애플리케이션이 시작되거나 종료되는 사건이 발생하면, 이를 알리고자 서블릿 컨테이너는 리스너의 메서드를 호출

- 바로 이 리스너에서 DAO를 준비하면 됨



##### ServletContextListener의 활용

- AppInitServlet이 하던 일을 이 리스너에 옮김
- 또한, MemberDao의 인스턴스 생성도 이 리스너에서 준비

###### 리스너의 구동과 서블릿들의 DAO 공유 과정

1. 웹 애플리케이션이 시작되면, 서블릿 컨테이너는 ServletContextListener의 구현체에 대해 contextInitalized() 메서드를 호출
2. 리스너는  DB 커넥션 객체를 생성
3. 리스너는 DAO 객체를 생성
4. 그리 DB 커넥션 객체를 DAO에 주입
5. 서블릿들이  DAO를 사용할 수 있도록 ServletContext 저장소에 저장
6. 만약 웹 애플리케이션이 종료되면, 서블릿 컨테이너는 리스너의 contextDestoryed() 메서드를 호출



##### 리스너  ServletContextListener 만들기

``` java
@WebListener
public class ContextLoaderListener implements ServletContextListener {
  
  Connection conn;
  
  @Override
  public void contextInitialized(ServletContextEvent event) {
    try {
      ServletContext sc = event.getServletContext();

      Class.forName(sc.getInitParameter("driver"));
      conn = DriverManager.getConnection(
          sc.getInitParameter("url"),
          sc.getInitParameter("username"),
          sc.getInitParameter("password"));

      MemberDao memberDao = new MemberDao();
      memberDao.setConnection(conn);
      
      sc.setAttribute("memberDao", memberDao);
    } catch (Throwable e) {
      e.printStackTrace();
    }
  }
  
  @Override
  public void contextDestroyed(ServletContextEvent event) {
    try {
      conn.close();
    } catch (Exception e) {}
  }
  
}
```

###### ServletContextListener 인터페이스 구현

- 웹 애플리케이션의 시작과 종료 사건을 처리하려면, 리스너 클래스는 반드시 ServletContextListener 규칙에 따라 작성해야 함

###### contextInitialized() 메서드

``` java
      Class.forName(sc.getInitParameter("driver"));
      conn = DriverManager.getConnection(
          ...);

      MemberDao memberDao = new MemberDao();
      memberDao.setConnection(conn);
      
      sc.setAttribute("memberDao", memberDao);
```

- 이 리스너의 핵심은 웹 애플리케이션이 시작될 때 MemberDao 객체를 준비하여  ServletContext에 보관하는 것

###### contextDestroyed() 메서드

- DB 커넥션 객체의 참조 면수 'conn'은 리스너의 인스턴스 변수

###### ContextLoaderListener의 배치

- 리스너의 배치는 두 가지 방법이 있음

  1. 애노테이션을 사용하는 방법

     ``` java
     @WebListener
     public class ContextLoaderListener implements ServletContextListener {
     ```

  2. DD파일 (web.xml)에 XML 태그를 선언하는 방법

##### 기존의 서블릿 변경

``` java
public void doGet(
      HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    Connection conn = null;
    Statement stmt = null;
    ResultSet rs = null;

    try {
      ServletContext sc = this.getServletContext();
//      conn = (Connection) sc.getAttribute("conn");

      MemberDao memberDao = (MemberDao)sc.getAttribute("memberDao");
//      MemberDao memberDao = new MemberDao();
//      memberDao.setConnection(conn);
```

- ServletContext로부터 DB 커넥션 객체를 뽑아 오는 부분을 제거, MemberDao 객체를 생성하는 코드 제거
- 대신 MemberDao 객체는 ServletContext로부터 꺼냄

##### AppInitServlet 제거

- AppInitServlet이 하던 일은  ContextLoaderListener로 이관



### 5.12 DB 커넥션풀

- DB 커넥션 객체를 여러 개 생성하여 풀에 담아 놓고 필요할 때 꺼내 쓰는 방식

##### 싱글 커넥션 사용의 문제점

- 같은 Connection을 공유한 같이 작업한 것들에게도 영향을 미침
- 만약 어떤 요청을 처리하다가 예외가 발생하여 롤백한다면, 다른 요청에 대해 작업한 내용까지 모두 롤백 되는 치명적인 문제
- 이를 해결하기 위해 **DB 커넥션풀** 등장



##### DB 커넥션풀 만들기

``` java
public class DBConnectionPool {
  
  String url;
  String username;
  String password;
  ArrayList<Connection> connList = new ArrayList<Connection>();
  
  public DBConnectionPool(String driver, String url, String username, String password) throws Exception {
    this.url = url;
    this.username = username;
    this.password = password;
    
    Class.forName(driver);
  }
  
  public Connection getConnection() throws Exception {
    if (connList.size() > 0) {
      Connection conn = connList.get(0);
      if (conn.isValid(10)){
        return conn;
      }
    }
    return DriverManager.getConnection(url, username, password);
  }
  
  public void returnConnection(Connection conn) throws Exception {
    connList.add(conn);
  }
  
  public void closeAll() {
    for(Connection conn : connList) {
      try{conn.close();} catch (Exception e) {}
    }
  }
  
}
```

###### ArrayList<Connection> 인스턴스 변수

- Connection 객체를 보관할 ArrayList를 준비
- 생성자에는 DB 커넥션에 필요한 값을 매개변수로 받음

###### getConnection()  메서드

- DB 커넥션을 달라고 요청받으면,  ArrayList에 들어 있는 거을 꺼내 줌.

- DB 커넥션 객체도 일정 시간이 지나면 서버와의 연결이 끊어지기 때문에 유효성 체크를 한 다음에 반환

- ``` java
  Connection conn = connList.get(0);
  if (conn.isValid(10)){
    return conn;
  }
  ```

- ArrayList에 보관된 객체가 없다면,  DriverManager를 통해 새로 만들어 반환

###### returnConnection()  메서드

- 커넥션 객체를 쓰고 난 다음에는 이 메서들르 호출하여 커넥션 풀에 반환

###### closeAll() 메서드

- 웹 애플리케이션을 종료하기 전에 이 메서드를 호출하여 데이터베이스와 연결된 것을 모두 끊어야 함



### 5.13 DataSource와  JNDI 

##### DataSource

- ##### DataSource는 DriverManager를 통해  DB 커넥션을 얻는 것보다 더 좋은 기법을 제공

- Javax.sql 패키지에 들어 있음

##### javax.sql 확장 패키지의 주요기능

- DriverManager를 대체할 수 있는 DataSource 인터페이스 제공
- Connection 및  Statement 객체의 풀링
- 분산 트랜잭션 처리
- Rowsets의 지원

###### DataSource의 장점

1. DataSource는 서버에서 관리하기 때문에 데이터베이스나 JDBC 드라이버가 변경되더라도 애플리케이션을 바꿀 필요가 없음
   - DriverManager를 사용하는 경우, 웹 애플리케이션에서 관리하기 때문에  데이터베이스의 주소가 바뀐다거나 JDBC 드라이버가 변경될 경우 웹 애플리케이션의 코드도 변경해야 함
2. Connection과 Statement 객체를 풀링할 수 있으며, 분산 트랜잭션을 다룰 수 있음
   - DriverManager를 사용하면 애플리케이션 개발자가 커넥션풀을 별도로 준비해야함

##### 서버에서 제공하는 DataSource 사용하기

``` xml
<?xml version="1.0" encoding="UTF-8"?>
<Context>
    <WatchedResource>WEB-INF/web.xml</WatchedResource>
    <Resource name="" auth="Container" type="javax.sql.DataSource"
              maxActive="10" maxIdle="3" maxWait="10000" username="felkmqwt"
              password="PFz09wbu8QuuZvQk5yuNdC2qwj82XXAS" driverClassName="org.postgresql.Driver"
              url="jdbc:postgresql://arjuna.db.elephantsql.com:5432"
              closeMethod="close"/>
</Context>
```

#### JNDI

- Java Naming and Directory Interface API

##### ContextLoaderListener 클래스 변경

``` java
@WebListener
public class ContextLoaderListener implements ServletContextListener {

  @Override
  public void contextInitialized(ServletContextEvent event) {
    try {
      ServletContext sc = event.getServletContext();

      InitialContext initialContext = new InitialContext();
      DataSource ds = (DataSource)initialContext.lookup(
          "java:comp/env/jdbc/postgresql"
      );

      MemberDao memberDao = new MemberDao();

      memberDao.setDataSource(ds);

      sc.setAttribute("memberDao", memberDao);
    } catch (Throwable e) {
      e.printStackTrace();
    }
  }

  @Override
  public void contextDestroyed(ServletContextEvent event) {
  }

}
```

- 톰캣 서버에서 자원을 찾기 위해  InitialContext 객체를 생성
- InitialContext의  lookup() 메서드를 이용하면,  JNDI 이름으로 등록되어 있는 서버 자원을 찾을 수 있음
- lookup()이 리턴하는 자원이 DataSource이기 때문에 형변환

##### JNDI를 사용하면 프로그래머는 데이터베이스 백그라운드에서의 특별한 세팅에 대해 신경쓰지 않고 사용할 수 있음

