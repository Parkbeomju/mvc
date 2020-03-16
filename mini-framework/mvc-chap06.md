## Chap 6. 미니 MVC 프레임워크 만들기

 

### 6.1 프런트 컨트롤러의 도입

###### 컨트롤러에서 요청 데이터를 처리하는 코드나 모델과 뷰가 제어하는 코드가 중복되는 경우가 발생

##### 프런트 컨트롤러 패턴

![image-20200310202919414](/Users/beomju/Library/Application Support/typora-user-images/image-20200310202919414.png)

###### 프런트 컨트롤러를 적용한 MVC 구조도

1. 웹 브라우저에서 요청이 들어오면, 제일 먼저 프런트 컨트롤러에서 그 요청을 받음
   - 프런트 컨트롤러는 VO 객체를 생성하여 클라이언트가 보낸 데이터를 담음
   - 그리고 ServletRequest 보관함에 VO 객체를 저장
   - 요청 URL에 따라 페이지 컨트롤러를 선택하여 실행을 위임
2. 페이지 컨트롤러는 DAO를 사용하여 프런트 컨트롤러로부터 받은 VO 객체를 처리
3. DAO는 페이지 컨트롤러로부터 받은 데이터를 처리
4. DAO 호출이 끝나면, 페이지 컨트롤러는 화면을 만들 때 사용할 데이터를 준비
   - 그리고 JSP가 사용할 수 있도록 ServletRequest 보관소에 저장
   - 프런트 컨트롤러에게 화면 출력을 담당할 뷰 정보(JSP의 URL)를 반환
5. 프런트 컨트롤러는 페이지 컨트롤러가 알려준 JSP로 실행을 위임
   - 만약 오류가 발생한다면 '/Error.jsp'로 실행을 위임
6. JSP는 페이지 컨트롤러에서 준비한 데이터를 가지고 화면을 생성하여 출력
7. 프런트 컨트롤러는 웹 브라우저의 요청에 대한 응답을 완료

##### 프런트 컨트롤러 역할

- VO 객체의 준비, 뷰 컴포넌트로의 위임, 오류 처리 등과 같은 공통 작업

##### 페이지 컨트롤러 역할

- 요청한 페이지만을 위한 작업 수행



##### 프런트 컨트롤러 만들기

``` java
@SuppressWarnings("serial")
@WebServlet("*.do")
public class DispatcherServlet extends HttpServlet {
  @Override
  protected void service(
      HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    response.setContentType("text/html; charset=UTF-8");
    String servletPath = request.getServletPath();
    try {
      String pageControllerPath = null;

      if ("/member/list.do".equals(servletPath)) {
        pageControllerPath = "/member/list";
      } else if ("/member/add.do".equals(servletPath)) {
        pageControllerPath = "/member/add";
        if (request.getParameter("email") != null) {
          request.setAttribute("member", new Member()
              .setEmail(request.getParameter("email"))
              .setPassword(request.getParameter("password"))
              .setName(request.getParameter("name")));
        }
      } else if ("/member/update.do".equals(servletPath)) {
        pageControllerPath = "/member/update";
        if (request.getParameter("email") != null) {
          request.setAttribute("member", new Member()
              .setNo(Integer.parseInt(request.getParameter("no")))
              .setEmail(request.getParameter("email"))
              .setName(request.getParameter("name")));
        }
      } else if ("/member/delete.do".equals(servletPath)) {
        pageControllerPath = "/member/delete";
      } else if ("/auth/login.do".equals(servletPath)) {
        pageControllerPath = "/auth/login";
      } else if ("/auth/logout.do".equals(servletPath)) {
        pageControllerPath = "/auth/logout";
      }

      RequestDispatcher rd = request.getRequestDispatcher(pageControllerPath);
      rd.include(request, response);

      String viewUrl = (String) request.getAttribute("viewUrl");
      if (viewUrl.startsWith("redirect:")) {
        response.sendRedirect(viewUrl.substring(9));
        return;
      } else {
        rd = request.getRequestDispatcher(viewUrl);
        rd.include(request, response);
      }

    } catch (Exception e) {
      e.printStackTrace();
      request.setAttribute("error", e);
      RequestDispatcher rd = request.getRequestDispatcher("/Error.jsp");
      rd.forward(request, response);
    }
  }
}
```

- 프런트 컨트롤러도 서블릿이기 때문에 HttpServlet을 상속받음

- ##### 오버라이딩 하는 메서드가 doGet(), doPost()가 아니라 service()

  - ``` java
    protected void service(
      HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException { ...
    ```

  - 매개변수를 보면 서블릿 인터페이스 메서드가 아님!

    - ##### 서블릿 컨테이너(톰캣)가 직접 호출하지 않음

  - service( HttpServletRequest, HttpServletResponse)은 HTTP 요청 프로토콜을 분석하여 다시 doGet(), doPost()호출

  - ##### 이렇게 한 이유는, GET, POST뿐만 아니라 다양한 요청 방식에도 대응하기 위해서

###### 요청 URL에서 서블릿 경로 알아내기

- 프런트 컨트롤러의 역할은 클라이언트에게 전달하는 것

- ##### 클라이언트가 요청한 서블릿의 경로를 알고 싶으면 getServletPath()를 사용

> ##### HttpServletRequest의 URL 정보 추출 메서드

###### getRequestURL()

- 요청 URL 리턴(단, 매개변수 제외)
- 반환값 : http://localhost:9999/web06/member/list.do

###### getRequestURI()

- 서버 주소를 제외한 URL
- 반환값 : /web06/member/list.do

###### getContextPath()

- 웹 애플리케이션 경로
- 반환값 : /web06

###### getServletPath()

- 서블릿 경로
- 반환값 : /member/list.do



###### 요청 매개변수로부터 VO 객체 준비

``` java
request.setAttribute("member", new Member()
                     .setEmail(request.getParameter("email"))
                     .setPassword(request.getParameter("password"))
                     .setName(request.getParameter("name")));
```

###### JSP로 위임

``` java
String viewUrl = (String) request.getAttribute("viewUrl");
if (viewUrl.startsWith("redirect:")) {
  response.sendRedirect(viewUrl.substring(9));
  return;
} else {
  rd = request.getRequestDispatcher(viewUrl);
  rd.include(request, response);
}
```

- 페이지 컨트롤러의 실행이 끝나면, 화면 출력을 위해 ServletRequest에 보관된 뷰 URL로 실행을 위임

###### 오류 처리

- 프런트 컨트롤러에서 오류처리

###### 프런트 컨트롤러의 배치

- @WebServlet 애노테이션을 사용하여 프런트 컨트롤러의 배치 URL을 ' *.do '로 지정
  - 클라이언트의 요청 중에서 서블릿 경로 이름이 do로 끝나는 경우는 DispatcherServlet이 처리

##### MemberListServlet을 페이지 컨트롤러로 만들기

``` java
@WebServlet("/member/list")
public class MemberListServlet extends HttpServlet {

  @Override
  public void doGet(
      HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    try {
      ServletContext sc = this.getServletContext();

      MemberDao memberDao = (MemberDao)sc.getAttribute("memberDao");

      request.setAttribute("members", memberDao.selectList());
      request.setAttribute("viewUrl", "/member/MemberList.jsp");

//      request.setAttribute("members", memberDao.selectList());
//
//      response.setContentType("text/html; charset=UTF-8");
//      
//      RequestDispatcher rd = request.getRequestDispatcher(
//          "/member/MemberList.jsp");
//      rd.include(request, response);

    } catch (Exception e) {
      throw new ServletException(e);
    }

  }
}
```

- 응답 데이터의 문자 집합은 프런트 컨트롤러에서 이미 설정하였기 때문에 페이지 컨트롤러에서 다음 코드를 제거
- 화면 출력을 위해 JSP로 실행을 위임하는 것도 프런트 컨트롤러가 처리하게 때문에 제거
  - 대신, JSP URL 정보를 프런트 컨트롤러에게 알려주고자 ServletRequest 보관소에 저장
- 오류 처리 페이지로 실행을 위임하는 것도 제거
  - 대신 Dao를 실행하다가 오류가 발생한다면, 기존의 오류를 ServletException 객체에 담아서 던짐
  - service() 메서드는 ServletException을 던지도록 선언되어 있어서 객체를 생성 후 던짐

###### 프런트 컨트롤러가 적용된 회원 목록 페이지의 실행 결과

![image-20200310214035659](/Users/beomju/Library/Application Support/typora-user-images/image-20200310214035659.png)



##### MemberAddServlet을 페이지 컨트롤러로 만들기

```java
package spms.servlets;

import spms.dao.MemberDao;
import spms.vo.Member;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/member/add")
public class MemberAddServlet extends HttpServlet {
  private static final long serialVersionUID = 1L;

  @Override
  protected void doGet(
      HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
//    RequestDispatcher rd = request.getRequestDispatcher(
//        "/member/MemberForm.jsp");
    request.setAttribute("viewUrl", "/member/MemberForm.jsp");
  }

  @Override
  protected void doPost(
      HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    try {
      ServletContext sc = this.getServletContext();
      MemberDao memberDao = (MemberDao)sc.getAttribute("memberDao");

      Member member = (Member)request.getAttribute("member");
      memberDao.insert(member);

      request.setAttribute("viewUrl", "redirect:list.do");
//      request.setAttribute("viewUrl", "/member/MemberForm.jsp"); ->이거 썼다가 nullPointer 뜸 
      
//      MemberDao memberDao = (MemberDao) sc.getAttribute("memberDao");
//
//      memberDao.insert(new Member()
//          .setEmail(request.getParameter("email"))
//          .setPassword(request.getParameter("password"))
//          .setName(request.getParameter("name")));
//      response.sendRedirect("list");

    } catch (Exception e) {
      throw new ServletException(e);
    }
  }
}
```

###### 뷰로 포워딩하는 코드를 제거

- 대신 MemberForm.jsp의 URL을 ServletRequest에 저장

###### 요청 매개변수의 값을 꺼내는 코드를 제거

- 클라이언트가 보낸 회원 정보를 꺼내기 위해 getParameter()를 호출하는 대신, 프런트 컨트롤러가 준비해 놓은 Member객체를 ServletRequest 보관소에서 꺼내도록 doPost() 메서드를 변경

###### 리다이렉트를 위한 뷰 URL 설정

- 회원 정보를 데이터베이스에 저장한 다음, 회원 목록 페이지로 리다이렉트 해야 하는데, 기존 코드를 제거하고 대신에 ServletRequest에 리다이렉트 URL을 지정
- 뷰 URL이 "redirect:" 문자열로 시작할 경우, 프런트 컨트롤러는 그 URL로 리다이렉트 함

###### 오류 처리 코드 제거



### 6.2 페이지 컨트롤러의 진화

###### 프런트 컨트롤러를 도입하면 페이지 컨트롤러를 굳이 서블릿으로 만들어야 할 이유가 없음

##### 일반 클래스로 만들면 서블릿 기술에 종속되지 않기 때문에 재사용성이 더 높아짐

- 이제 프런트 컨트롤러에서 페이지 컨트롤러로 작업을 위임할 때는 포워딩이나 인클루딩 대신 메서드를 호출해야 함

#### 프런트 컨트롤러와 페이지 컨트롤러의 호출 규칙 정의

- 프런트 컨트롤러와 페이지 컨트롤러 사이의 호출 규칙을 문법으로 정의해 두면 개발자들은 그 규칙에 따라 해당 클레스를 작성하고 호출하면 되기 때문에 프로그래밍의 일관성을 확보 가능
- 또한 페이지 컨트롤러를 서블릿이 아닌 일반 클래스로 만들면 web.xml 파일에 등록할 필요가 없어 유지보수가 쉬워짐

##### 호출 규칙 정의

- **인터페이스**를 사용해 규칙 정의

###### 일반 클래스로 만든 페이지 컨트롤러의 사용 시나리오

1. 웹 브라우저는 회원 목록 페이지를 요청함
2. 프런트 컨트롤러는 회원 목록 요청 처리를 담당하는 페이지 컨트롤러를 호출
   - 이 때, 데이터를 주고받을 바구니 역할을 할 Map 객체를 넘김
3. 페이지 컨트롤러는 Dao에게 회원 목록 데이터를 요청
4. MemberDao는 데이터베이스로부터 회원 목록 데이터를 가져와서, Member 객체에 담아 반환
5. 페이지 컨트롤러는 Dao가 반환한 회원 목록 데이터를 Map 객체에 저장
   - 그리고 프런트 컨트롤러에게 뷰 URL(jsp URL)을 반환
6. 프런트 컨트롤러는 Map 객체에 저장된 페이지 컨트롤러의 작업 결과물을 JSP가 사용할 수 있도록 ServletRequest로 옮김

##### 프런트 컨트롤러가 페이지 컨트롤러에게 작업을 위임할 때 더 이상 포워딩이나 인클루딩 방식을 사용하지 않음

- ##### 페이지 컨트롤러에게 일을 시키기 위해 execute() 메서드를 호출

##### 프런트 컨트롤러와 페이지 컨트롤러 사이에 데이터를 주고받기 위해 Map 객체를 사용

- 페이지 컨트롤러가 Servlet API를 직접 사용하지 않도록 하기 위함



##### 페이지 컨트롤러를 위한 인터페이스 정의

``` java
public interface Controller {
  String execute(Map<String, Object> model) throws Exception;
}
```

- execute()는 프런트 컨트롤러가 페이지 컨트롤러에게 일을 시키기 위해 호출하는 메서드

##### 페이지 컨트롤러 MemberListServlet을 일반 클래스로 전환

``` java
public class MemberListController implements Controller {

  @Override
  public String execute(Map<String, Object> model) throws Exception {
    MemberDao memberDao = (MemberDao)model.get("memberDao");
    model.put("members", memberDao.selectList());
    return "/member/MemberList.jsp";
  }

}
```

###### Controller 인터페이스 구현

- 페이지 컨트롤러가 되려면 Controller 규칙에 따라 클래스를 작성해야 함
- 페이지 컨트롤러는 더 이상 예외처리 X

###### 페이지 컨트롤러에서 사용할 객체를 Map에서 꺼내기

``` java
MemberDao memberDao = (MemberDao)model.get("memberDao");
```

###### 페이지 컨트롤러가 작업한 결과물을 Map에 담기

``` java
model.put("members", memberDao.selectList());
```

- Map 객체에 저장된 값은 프런트 컨트롤러가 꺼내서 ServletRequest 보관소로 옮김
- ServletRequest 보관소에 저장된 값은 다시 JSP가 꺼내서 사용할 것

###### 뷰 URL 반환

- 페이지 컨트롤러의 반환값은 화면을 출력할 JSP의 URL



##### 프런트 컨트롤러 변경

``` java
@SuppressWarnings("serial")
@WebServlet("*.do")
public class DispatcherServlet extends HttpServlet {
  @Override
  protected void service(
      HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    response.setContentType("text/html; charset=UTF-8");
    String servletPath = request.getServletPath();
    try {
      ServletContext sc = this.getServletContext();

      HashMap<String, Object> model = new HashMap<String, Object>();
      model.put("memberDao", sc.getAttribute("memberDao"));
      
      String pageControllerPath = null;
      Controller pageController = null;

      if ("/member/list.do".equals(servletPath)) {
        pageControllerPath = "/member/list";
      } else if ("/member/add.do".equals(servletPath)) {
        pageControllerPath = "/member/add";
        if (request.getParameter("email") != null) {
          request.setAttribute("member", new Member()
              .setEmail(request.getParameter("email"))
              .setPassword(request.getParameter("password"))
              .setName(request.getParameter("name")));
        }
      } else if ("/member/update.do".equals(servletPath)) {
        pageControllerPath = "/member/update";
        if (request.getParameter("email") != null) {
          request.setAttribute("member", new Member()
              .setNo(Integer.parseInt(request.getParameter("no")))
              .setEmail(request.getParameter("email"))
              .setName(request.getParameter("name")));
        }
      } else if ("/member/delete.do".equals(servletPath)) {
        pageControllerPath = "/member/delete";
      } else if ("/auth/login.do".equals(servletPath)) {
        pageControllerPath = "/auth/login";
      } else if ("/auth/logout.do".equals(servletPath)) {
        pageControllerPath = "/auth/logout";
      }

      String viewUrl = pageController.execute(model);
      
      for (String key : model.keySet()) {
        request.setAttribute(key, model.get(key));
      }
      
      if (viewUrl.startsWith("redirect:")) {
        response.sendRedirect(viewUrl.substring(9));
        return;
      } else {
        RequestDispatcher rd = request.getRequestDispatcher(viewUrl);
        rd.include(request, response);
      }

    } catch (Exception e) {
      e.printStackTrace();
      request.setAttribute("error", e);
      RequestDispatcher rd = request.getRequestDispatcher("/Error.jsp");
      rd.forward(request, response);
    }
  }
}
```

###### Map 객체 준비

- 프런트 컨트롤러와 페이지 컨트롤러 사이에 데이터나 객체를 주고 받을 때 사용할 Map 객체를 준비
- MemberListController는 회원 목록을 가져오기 위해 MemberDao 객체가 필요함
  - 그래서 ServletContext 보관소에 저장된 MemberDao 객체를 꺼내서 Map 객체에 담음

###### 회원 목록을 처리할 페이지 컨트롤러 준비

- 페이지 컨트롤러는 Controller의 구현체이기 때문에, 인터페이스 타입의 참조 변수 선언

``` java
Controller pageController = null;
```

- 회원 목록 요청을 처리할 페이지 컨트롤러를 준비

``` java
if ("/member/list.do".equals(servletPath)) {
        pageControllerPath = "/member/list";
}
```

###### 페이지 컨트롤러의 실행

- 이제는 MemberListController가 일반 클래스이기 때문에 메서드를 호출해야 함

``` java
String viewUrl = pageController.execute(model);
```

###### Map 객체에 저장된 값을 ServletRequest에 복사

- Map 객체는 페이지 컨트롤러에게 데이터나 객체를 보낼 때 사용되기도 하지만 페이지 컨트롤러의 실행 결과물을 받을 때도 사용함
  - 따라서 페이지 컨트롤러의 실행이 끝난 다음, Map 객체에 보관되어 있는 데이터나 객체를 JSP가 사용할 수 이도록 ServletRequest에 복사

``` java
for (String key : model.keySet()) {
  request.setAttribute(key, model.get(key));
}
```



##### 회원 등록 페이지 컨트롤러에 Controller 규칙 적용하기

``` java
public class MemberAddController implements Controller {
  @Override
  public String execute(Map<String, Object> model) throws Exception {
    if (model.get("member") == null) {
      return "/member/MemberForm.jsp";
    } else {
      MemberDao memberDao = (MemberDao)model.get("memberDao");
      Member member = (Member)model.get("member");
      memberDao.insert(member);

      return "redirect:list.do";
    }
  }
}
```

- Map 객체에 VO rorcp "Member"가 들어 있으면 POST 요청으로 간주하고, 그렇지 않으면 GET 요청으로 간주

- 데이터를 저장한 후엔믄 회원 목록 페이지로 리다이렉트 할 수 있도록 반환 URL 앞에 "redirect:"를 붙임

##### 회원 등록 요청을 처리하기 위해 DispatcherServlet 변경

``` java
else if ("/member/add.do".equals(servletPath)) {
  pageController = new MemberAddController();
  if (request.getParameter("email") != null) {
    model.put("member", new Member()
              .setEmail(request.getParameter("email"))
              .setPassword(request.getParameter("password"))
              .setName(request.getParameter("name")));
  }
}
```



### 6.3 DI를 이용한 빈 의존성 관리

###### MemberListController가 작업을 수행하려면 데이터베이스로부터 회원 정보를 가져다줄 MemberDao가 필요함

- ###### 이렇게 특정 작업을 수행할 때 사용하는 객체를 '의존 객체'라고 하고, 이런 관계를 '의존관계'라고 함

> #####  MemberListController ----의존관계--->> MemberDao

#### 의존 객체의 관리

#### <고전적인 방법>

###### 1. 의존 객체가 필요하면 즉시 생성

(기존 MemberListServlet 코드 일부)

``` java
public void doGet(
      HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    try {
      ServletContext sc = this.getServletContext();
      Connection conn = (Connection) sc.getAttribute("conn");

      MemberDao memberDao = new MemberDao();
      memberDao.setConnection(conn);
      
      request.setAttribute("members", memberDao.selectList());
```

- 회원 목록 데이터를 가져오기 위해 직접 MemberDao 객체를 생성하고 있음
- 비효율적

###### 2. 의존 객체를 미리 생성해 두었다가 필요할 때 사용

(기존 MemberListController 코드 일부)

``` java
public String execute(Map<String, Object> model) throws Exception {
  MemberDao memberDao = (MemberDao)model.get("memberDao");
  model.put("members", memberDao.selectList());
  return "/member/MemberList.jsp";
}
```

- 웹 애플리케이션이 시작될 때 MemberDao 객체르 미리 생성하여 ServletContext에 보관해 두었다가 필요할 때마다 꺼내 씀

##### 고전적인 방법의 문제점 

1. 코드의 잦은 변경
   - 의존 객체를 사용하는 쪽과 의존 객체(또는 보관소) 사이의 결합도가 높아져서 의존 객체나 보관소에 변경이 발생하면 바로 영향
2. 대체가 어려움
   - 데이터베이스에 따라 코드도 그에 맞게 (SQL) 변경해야 함

#### <의존 객체를 외부에서 주입하는 방법>

![image-20200311144227894](/Users/beomju/Library/Application Support/typora-user-images/image-20200311144227894.png)

- 의존 객체를 위한 인스턴스 변수와 셋터 메서드를 준비

###### 의존 객체의 관리

- 의존 객체의 관리와 주입은 빈 컨테이너가 관리
- ContextLoaderListener가 빈 컨테이너 역할 수행
- 인터페이스를 통해 의존 객체에 대한 결합도 낮춤

##### MemberDao와 DataSource

- MemberDao가 작업을 수행하려면 데이터베이스와 연결을 수행하는 DataSource가 필요
  - 이 DataSource 객체를 MemberDao가 직접 생성하는 것이 아니라 외부에서 주입 받음

``` java
public void contextInitialized(ServletContextEvent event) {
    try {
      ServletContext sc = event.getServletContext();

      InitialContext initialContext = new InitialContext();
      DataSource ds = (DataSource)initialContext.lookup(
          "java:comp/env/jdbc/postgresql"
      );

      MemberDao memberDao = new MemberDao();
      memberDao.setDataSource(ds);
```

##### MemberListController에 MemberDao 주입

``` java
public class MemberListController implements Controller {

  MemberDao memberDao;

  public MemberListController setMemberDao(MemberDao memberDao) {
    this.memberDao = memberDao;
    return this;
  }

  @Override
  public String execute(Map<String, Object> model) throws Exception {
   // MemberDao memberDao = (MemberDao)model.get("memberDao");
    model.put("members", memberDao.selectList());
    return "/member/MemberList.jsp";
  }
  
}
```

###### 의존 객체 주입을 위한 인스턴스 변수와 셋터 메서드

###### 의존 객체를 스스로 준비하지 않음

``` java
//MemberDao memberDao = (MemberDao)model.get("memberDao"); 제거
```

##### 페이지 컨트롤러 객체들을 준비

ContextLoaderListener

``` java
  public void contextInitialized(ServletContextEvent event) {
    try {
      ServletContext sc = event.getServletContext();

      InitialContext initialContext = new InitialContext();
      DataSource ds = (DataSource)initialContext.lookup(
          "java:comp/env/jdbc/postgresql"
      );

      MemberDao memberDao = new MemberDao();
      
      memberDao.setDataSource(ds);
			
      //sc.setAttribute("memberDao", memberDao);
      
      sc.setAttribute("/auth/login.do",
          new LogInController().setMemberDao(memberDao));
      sc.setAttribute("/auth/logout.do", new LogOutController());
      sc.setAttribute("/member/list.do",
          new MemberListController().setMemberDao(memberDao));
      sc.setAttribute("/member/add.do",
          new MemberAddController().setMemberDao(memberDao));
      sc.setAttribute("/member/update.do",
          new MemberUpdateController().setMemberDao(memberDao));
      sc.setAttribute("/member/delete.do",
          new MemberDeleteController().setMemberDao(memberDao));
      
    } catch (Throwable e) {
      e.printStackTrace();
    }
  }
```

###### 페이지 컨트롤러 객체를 준비

- 페이지 컨트롤러 객체를 생성하고 나서 MemberDao가 필요한 객체에 대해서는 셋터 메서드를 호출하여 주입해줌

``` java
new LogInController().setMemberDao(memberDao)
```

- 이렇게 생성된 페이지 컨트롤러를 ServletContext에 저장함. 단, 저장할 때 서블릿 요청 URL을 키(key)로 저장

##### 프런트 컨트롤러의 변경

``` java
  protected void service(
      HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    response.setContentType("text/html; charset=UTF-8");
    String servletPath = request.getServletPath();
    try {
      ServletContext sc = this.getServletContext();

      HashMap<String, Object> model = new HashMap<String, Object>();
//      model.put("memberDao", sc.getAttribute("memberDao"));
      model.put("session", request.getSession());

      Controller pageController = (Controller) sc.getAttribute(servletPath);

      if ("/member/list.do".equals(servletPath)) {
//        pageController = new MemberListController();
      } else if ("/member/add.do".equals(servletPath)) {
//        pageController = new MemberAddController();
        if (request.getParameter("email") != null) {
          model.put("member", new Member()
              .setEmail(request.getParameter("email"))
              .setPassword(request.getParameter("password"))
              .setName(request.getParameter("name")));
        }
      } else if ("/member/update.do".equals(servletPath)) {
//        pageController = new MemberUpdateController();
        if (request.getParameter("email") != null) {
          model.put("member", new Member()
              .setNo(Integer.parseInt(request.getParameter("no")))
              .setEmail(request.getParameter("email"))
              .setName(request.getParameter("name")));
        } else {
          model.put("no", new Integer(request.getParameter("no")));
        }
      } else if ("/member/delete.do".equals(servletPath)) {
//        pageController = new MemberDeleteController();
        model.put("no", new Integer(request.getParameter("no")));
      } else if ("/auth/login.do".equals(servletPath)) {
//        pageController = new LogInController();
        if (request.getParameter("email") != null) {
          model.put("loginInfo", new Member()
              .setEmail(request.getParameter("email"))
              .setPassword(request.getParameter("password")));
        }
      } //else if ("/auth/logout.do".equals(servletPath)) {
//        pageController = new LogOutController();
    //  }
```

- MemberDao 객체는 더 이상 Map 객체에 담을 필요가 없어서 제거

  ###### ServletContext에서 페이지 컨트롤러 꺼내기

  ``` java
  Controller pageController = (Controller) sc.getAttribute(servletPath);
  ```

  - 페이지 컨트롤러는 ServletContext 보관소에 저장되어 있음.
  - 이 보관소에서 페이지 컨트롤러를 꺼낼 때는 서블릿 URL을 사용



#### 인터페이스를 활용하여 공급처를 다변화

###### 다양한 데이터베이스에 대응하기 위해 인터페이스를 활용

##### MemberDao 인터페이스 정의

``` java
public interface MemberDao {

  List<Member> selectList() throws Exception;
  int insert(Member member) throws Exception;
  Member selectOne(int no) throws Exception;
  int update(Member member) throws Exception;
  Member exist(String email, String password) throws Exception;

}
```

``` java
public class PostgreSqlMemberDao implements MemberDao{
  DataSource ds;

  public void setDataSource(DataSource ds) {
    this.ds = ds;
  }
  
  public List<Member> selectList() throws Exception {
    Connection connection = null;
    Statement stmt = null;
    ResultSet rs = null;

    try {
      ...
```



### 6.4 리플랙션 API를 이용하여 프런트 컨트롤러 개선하기

###### 현재까지 작업한 프런트 컨트롤러는 페이지 컨트롤러를 추가할 때마다 코드를 변경해야 함

###### 리플랙션 API를 활용하여 인스턴스를 자동 생성하고, 메서드를 자동으로 호출하는 방법을 배워보자



##### 신규 회원 정보 추가 자동화

###### 프런트 컨트롤러에서 VO 객체 생성 자동화 시나리오

1. 웹 브라우저는 회원 등록을 요청. 사용자가 입력한 매개변수 값을 서블릿에 전달
2. 프런트 컨트롤러는 회원 등록을 처리하는 페이지 컨트롤러에게 어떤 데이터가 필요한지 물어봄. 페이지 컨트롤러는 작업하는 데 필요한 데이터의 이름과 타입 정보를 담은 배열을 리턴
3. 프런트 컨트롤러는 ServletRequestDataBinder를 이용하여, 요청 매개변수에게 페이지 컨트롤러가 원하는 형식의 값 객체 (예 : Member, Integer, Data 등)를 만듦
4. 프런트 컨트롤러는 ServletRequestDataBinder가 만들어 준 값 객체를 Map에 저장
5. 프런트 컨트롤러는 페이지 컨트롤러를 실행. 페이지 컨트롤러의 execute()를 호출할 때, 값이 저장된 Map 객체를 매개변수로 넘김



##### DataBinding 인터페이스 정의

- 프런트 컨트롤러 입장에서는 규칙을 준수하는 페이지 컨트롤러를 호출할 때만 VO 객체를 준비하면 됨

``` java
public interface DataBinding {
  Object[] getDataBinders();
}
```

- 페이지 컨트롤러 중에서 클라이언트가 보낸 데이터가 필요한 경우 이 DataBinding 인터페이스를 구현
- getDataBinders()의 반환값은 데이터의 이름과 타입 정보를 담은  Object의 배열
  - new Object[] {"데이터이름", "데이터타입", "데이터이름", "데이터타입", ...}
  - 데이터 이름과 데이터 타입이 한 쌍으로 순서대로 오도록 작성 (배열의 크기는 항상 짝수)

##### 페이지 컨트롤러의 DataBinding 구현

``` java
public class MemberAddController implements Controller {

//  PostgreSqlMemberDao postgreSqlMemberDao;
  MemberDao memberDao;

  public MemberAddController setPostgreSqlMemberDao(MemberDao memberDao) {
    this.memberDao = memberDao;
    return this;
  }

  public Object[] getDataBinders() {
    return new Object[] {
        "member", spms.vo.Member.class
    };
  }

  @Override
  public String execute(Map<String, Object> model) throws Exception {
    Member member = (Member)model.get("member");
    if (member.getEmail() == null {
      return "/member/MemberForm.jsp";
    } else {
//      MemberDao memberDao = (MemberDao)model.get("memberDao");
//      Member member = (Member)model.get("member");
//      postgreSqlMemberDao.insert(member);
      memberDao.insert(member);

      return "redirect:list.do";
    }
  }
}
```

###### DataBinding 인터페이스 선언

###### getDataBinders() 메서드 구현

``` java
  public Object[] getDataBinders() {
    return new Object[] {
        "member", spms.vo.Member.class
    };
  }
```

- MemberAddController가 원하는 데이터는 사용자가 회원 등록폼에 입력한 회원 이름과 이메일, 암호값임
- 프런트 컨트롤러는 Object 배열에 지정된 대로 Member 인스턴스를 준비하여 Map 객체에 저장하고, execute()를 호출할 때 매개변수로 이 Map 객체를 넘길 것

###### execute 메서드의 변화

- 이전 코드에서는 Map 객체에 Member가 들어 있는지 없는지에 따라 작업을 분기

- ##### 이제부터는 getDataBinders()에서 지정한 대로 프런트 컨트롤러가 VO 객체를 무조건 생성할 것이기 때문에 Member가 있는지 여부로 판단하지 않고, Member에 이메일이 들어 있는지를 여부로 검사

``` java
if (member.getEmail() == null) {
```



##### 프런트 컨트롤러의 변경

``` java
@SuppressWarnings("serial")
@WebServlet("*.do")
public class DispatcherServlet extends HttpServlet {
  @Override
  protected void service(
      HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    response.setContentType("text/html; charset=UTF-8");
    String servletPath = request.getServletPath();
    try {
      ServletContext sc = this.getServletContext();

      HashMap<String, Object> model = new HashMap<String, Object>();
//      model.put("memberDao", sc.getAttribute("memberDao"));
      model.put("session", request.getSession());

      Controller pageController = (Controller) sc.getAttribute(servletPath);

      if (pageController instanceof DataBinding) {
        prepareRequestData(request, model, (DataBinding)pageController);
      }

      String viewUrl = pageController.execute(model);

      for (String key : model.keySet()) {
        request.setAttribute(key, model.get(key));
      }

      if (viewUrl.startsWith("redirect:")) {
        response.sendRedirect(viewUrl.substring(9));
        return;
      } else {
        RequestDispatcher rd = request.getRequestDispatcher(viewUrl);
        rd.include(request, response);
      }

    } catch (Exception e) {
      e.printStackTrace();
      request.setAttribute("error", e);
      RequestDispatcher rd = request.getRequestDispatcher("/Error.jsp");
      rd.forward(request, response);
    }
  }

  private void prepareRequestData(HttpServletRequest request,
                                  HashMap<String, Object> model, DataBinding dataBinding)
      throws Exception {
    Object[] dataBinders = dataBinding.getDataBinders();
    String dataName = null;
    Class<?> dataType = null;
    Object dataObj = null;
    for (int i = 0; i < dataBinders.length; i+=2) {
      dataName = (String)dataBinders[i];
      dataType = (Class<?>) dataBinders[i+1];
      dataObj = ServletRequestDataBinder.bind(request, dataType, dataName);
      model.put(dataName, dataObj);
    }
  }
}
```

###### service() 메서드

- service() 메서드에서 조건문이 사라짐
  - 매개변수 값을 사용하는 페이지 컨트롤러를 추가하더라도 조건문을 삽입할 필요가 없어짐
  - 대신, 데이터 준비를 자동으로 수행하는 prepareRequest()를 호출

``` java
if (pageController instanceof DataBinding) {
  prepareRequestData(request, model, (DataBinding)pageController);
}
```

###### prepareRequestData() 메서드

-  ///



##### ServletRequestDataBinder 클래스 생성

``` java
public class ServletRequestDataBinder {
  public static Object bind(
      ServletRequest request, Class<?> dataType, String dataName)
      throws Exception {
    if (isPrimitiveType(dataType)) {
      return createValueObject(dataType, request.getParameter(dataName));
    }

    Set<String> paramNames = request.getParameterMap().keySet();
    Object dataObject = dataType.newInstance();
    Method m = null;

    for (String paramName : paramNames) {
      m = findSetter(dataType, paramName);
      if (m != null) {
        m.invoke(dataObject, createValueObject(m.getParameterTypes()[0],
            request.getParameter(paramName)));
      }
    }
    return dataObject;
  }

  private static boolean isPrimitiveType(Class<?> type) {
    if (type.getName().equals("int") || type == Integer.class ||
        type.getName().equals("long") || type == Long.class ||
        type.getName().equals("float") || type == Float.class ||
        type.getName().equals("double") || type == Double.class ||
        type.getName().equals("boolean") || type == Boolean.class ||
        type == Date.class || type == String.class) {
      return true;
    }
    return false;
  }

  private static Object createValueObject(Class<?> type, String value) {
    if (type.getName().equals("int") || type == Integer.class) {
      return new Integer(value);
    } else if (type.getName().equals("float") || type == Float.class) {
      return new Float(value);
    } else if (type.getName().equals("double") || type == Double.class) {
      return new Double(value);
    } else if (type.getName().equals("long") || type == Long.class) {
      return new Long(value);
    } else if (type.getName().equals("boolean") || type == Boolean.class) {
      return new Boolean(value);
    } else if (type == Date.class) {
      return java.sql.Date.valueOf(value);
    } else {
      return value;
    }
  }

  private static Method findSetter(Class<?> type, String name) {
    Method[] methods = type.getMethods();

    String propName = null;
    for (Method m : methods) {
      if (!m.getName().startsWith("set")) continue;

      propName = m.getName().substring(3);
      if (propName.toLowerCase().equals(name.toLowerCase())) {
        return m;
      }
    }
    return null;
  }
}
```

######  bind() 메서드

- 프런트 컨트롤러에서 호출하는 메서드
- 요청 매개변수의 값과 데이터 이름, 데이터 타입을 받아서 데이터 객체(예 : Member, String, Date, Integer 등)를 만드는 일을 함

``` java
  public static Object bind(
      ServletRequest request, Class<?> dataType, String dataName)
      throws Exception {
    ...
    return dataObject;
  }
```

``` java
if (isPrimitiveType(dataType)) {
  return createValueObject(dataType, request.getParameter(dataName));
}
```

- 이 메서드는 첫 번째 명령문은 dataType이 기본 타입인지 아닌지 검사하는 일
  - 만약 기본 타입이라면 즉시 객체를 생성하여 반환



- Member 클래스처럼  dataType이 기본 타입이 아닌 경우는 요청 매개변수의 이름과 일치하는 셋터 메서드를 찾아서 호출
  - 먼저 요청 매개변수의 이름 목록을 얻음

``` java
Set<String> paramNames = request.getParameterMap().ketSet();
```

request.getParameterMap()은 매개변수의 이름과 값을 맵 객체에 담아서 반환

- 우리가 필요한 것은 매개변수의 이름이기 때문에 Map의 keySet()을 호출하여 이름 목록만 꺼냄

그리고 값을 저장할 객체를 생성.

``` java
Object dataObject = dataType.newInstance();
```

요청 매개변수의 이름 목록이 준비되었으면 for 반복문을 실행

``` java
    for (String paramName : paramNames) {
      m = findSetter(dataType, paramName);
      if (m != null) {
        m.invoke(dataObject, createValueObject(m.getParameterTypes()[0],
            request.getParameter(paramName)));
      }
    }
```

- 데이터 타입 클래스에서 매개변수 이름과 일치하는 프로퍼티(셋터 메서드)를 찾음

###### isPrimitiveType() 메서드

- 매개변수로 주어진 타입이 기본 타입인지 검사하는 메서드

###### createValueObject() 메서드

- 기본 타입의 경우 셋터 메서드가 없기 때문에 값을 할당할 수 없음
  - 이 메서드는 셋터로 값을 할당할 수 없는 기본 타입에 대해 객체를 생성하는 메서드

###### findSetter() 메서드

``` java
  private static Method findSetter(Class<?> type, String name) {
    Method[] methods = type.getMethods(); //제일 먼저 데이터 타입에서 메서드 목록을 얻음

    String propName = null;
    for (Method m : methods) {    
      if (!m.getName().startsWith("set")) continue; //메서드 이름이 set으로 시작하지 않으면 무시

      propName = m.getName().substring(3);
      if (propName.toLowerCase().equals(name.toLowerCase())) {
        return m;  //일치하는 셋터메서드를 찾으면 즉시 반환
      }
    }
    return null;
  }
```

- 클래스(type)을 조사하여 주어진 이름(name)과 일치하는 셋터 메서드를 찾음



### 6.5 프로퍼티를 이용한 객체 관리

###### 리플랙션 API를 사용하여 프런트 컨트롤러를 개선하였지만, ContextLoaderListener는 변경해야 한다.

###### 페이지 컨트롤러뿐만 아니라 DAO를 추가하는 경우에도 ContextLoaderListener 클래스에 코드를 추가해야 하는데, 객체를 생성하고 의존 객체를 주입하는 부분을 자동화 해보자



###### 프로퍼티 파일을 이용한 객체 자동 생성 시나리오

1. 웹 애플리케이션이 시작되면 서블릿 컨테이너는 ContextLoaderListener의 contextInitialized() 메서드를 호출함
2. contextInitialized() 메서드에서는  ApplicationContext를 생성함. 이때 생성자에 프로퍼티 파일의 경로를 매개변수로 넘겨줌
3. ApplicationContext는 프로퍼티 파일의 내용을 읽어들임
4. 프로퍼티 파일에 선언된 대로 객체를 생성하여 객체 테이블에 저장
5. 객체 테이블에 저장된 각 객체에 대해 의존 객체를 찾아서 할당해 줌

##### 프로퍼티 파일 작성

``` 
jndi.dataSource=java:comp/env/jdbc/postgresql
memberDao=spms.dao.PostgreSqlMemberDao
/auth/login.do=spms.controls.LogInController
/auth/logout.do=spms.controls.LogOutController
/member/list.do=spms.controls.MemberListController
/member/add.do=spms.controls.MemberAddController
/member/update.do=spms.controls.MemberUpdateController
/member/delete.do=spms.controls.MemberDeleteController
```

###### 톰캣 서버에서 제공하는 객체

- DataSource처럼 톰캣 서버에서 제공하는 객체는 ApplicationContext에서 생성할 수 없음

  - 대신 InitialContext를 통해 해당 객체를 얻어야 함

  - ##### Jodi.{객체이름}={JNDI 이름}

``` 
jndi.dataSource=java:comp/env/jdbc/postgresql
```

###### 일반 객체

- ##### {객체이름} = {패키지 이름을 포함한 클래스 이름}

- 프로퍼티의 키는 객체를 알아보는 데 도움이 되는 이름을 사용, 단 다른 이름과 중복되어서는 안됨

- 프로퍼티의 값은 패키지 이름을 포함한 전체 클래스 이름이어야 함

``` 
memberDao=spms.dao.PostgreSqlMemberDao
```

###### 페이지 컨트롤러 객체

- ##### {서블릿 URL} = {패키지 이름을 포함한 클래스 이름}

``` 
/auth/login.do=spms.controls.LogInController
...
```



##### ApplicationContext 클래스

- 페이지 컨트롤러나 DAO가 추가되더라도  ContextLoaderListener를 변경하지 않기 위함

``` java
public class ApplicationContext {
  Hashtable<String,Object> objTable = new Hashtable<String,Object>();

  public Object getBean(String key) {
    return objTable.get(key);
  }

  public ApplicationContext(String propertiesPath) throws Exception {
    Properties props = new Properties();
    props.load(new FileReader(propertiesPath));

    prepareObjects(props);
    injectDependency();
  }

  private void prepareObjects(Properties props) throws Exception {
    Context ctx = new InitialContext();
    String key = null;
    String value = null;

    for (Object item : props.keySet()) {
      key = (String)item;
      value = props.getProperty(key);
      if (key.startsWith("jndi.")) {
        objTable.put(key, ctx.lookup(value));
      } else {
        objTable.put(key, Class.forName(value).newInstance());
      }
    }
  }

  private void injectDependency() throws Exception {
    for (String key : objTable.keySet()) {
      if (!key.startsWith("jndi.")) {
        callSetter(objTable.get(key));
      }
    }
  }

  private void callSetter(Object obj) throws Exception {
    Object dependency = null;
    for (Method m : obj.getClass().getMethods()) {
      if (m.getName().startsWith("set")) {
        dependency = findObjectByType(m.getParameterTypes()[0]);
        if (dependency != null) {
          m.invoke(obj, dependency);
        }
      }
    }
  }

  private Object findObjectByType(Class<?> type) {
    for (Object obj : objTable.values()) {
      if (type.isInstance(obj)) {
        return obj;
      }
    }
    return null;
  }
}
```

###### 객체의 보관

- 프로퍼티에 설정된 대로 객체를 준비하면, 객체를 저장할 보관소가 필요한데 이를 위해 해시 테이블을 준비함
  - 또한, 해시 테이블에서 객체를 꺼낼(getter) 메서드도 정의

``` java
  Hashtable<String,Object> objTable = new Hashtable<String,Object>();

  public Object getBean(String key) {
    return objTable.get(key); }
```

###### 프로터피 파일의 로딩

- ApplicationContext 생성자가 호출되면 매개변수로 지정된 프로퍼티 파일의 내용을 로딩해야 함
  - 이를 위해  java.util.Properties 클래스 사용

``` java
Properties props = new Properties();
props.load(new FileReader(propertiesPath));
```

- Properties는 '이름 = 값' 형태로 된 파일을 다룰 때 사용하는 클래스
  - load메서드는 FileReader를 통해 읽어들인 프로퍼티 내용을 키-값 형태로 내부 맵에 보관

###### prepareObjects() 메서드

- 프로퍼티 파일의 내용을 로딩했으면, 그에 따라 객체를 준비해야 함
  - prepareObjects()가 바로 그 일을 수행하는 메서드
- 먼저 JNDI 객체를 찾을 때 사용할 InitialContext를 준비

``` java
Context ctx = new InitialContext();
```

그리고 반복문을 통해 프로퍼티에 들어있는 정보를 꺼내서 객체를 생성

``` java
for (Object item : props.keySet()) {...}
```

###### injectDependency() 메서드

- 톰캣 서버로부터 객체를 가져오거나 직접 객체를 생성했으면 이제는 각 객체가 필요로 하는 의존 객체를 할당해 주어야 함

``` java
  private void injectDependency() throws Exception {
    for (String key : objTable.keySet()) {
      if (!key.startsWith("jndi.")) {
        callSetter(objTable.get(key));
      }}}
```

- 객체 이름이 'jndi.'로 시작하는 경우 톰캣 서버에서 제공한 객체이므로 의존 객체를 주입해서는 안됨. 그래서 제외
  - 나머지 객체에 대해서는 셋터 메서드를 호출

###### callSetter() 메서드

- callSetter()는 매개변수로 주어진 객체에 대해 셋터 메서드를 찾아서 호출하는 일을 함

``` java
 for (Method m : obj.getClass().getMethods()) {
      if (m.getName().startsWith("set")) {
```

- 셋터 메서드를 찾았으면 셋터 메서드의 매개변수와 타입이 일치하는 객체를  objTable에서 찾음

``` java
dependency = findObjectByType(m.getParameterTypes()[0]);
```

- 의존 객체를 찾았다면, 셋터 메서드를 호출

``` java
if (dependency != null) {
  m.invoke(obj, dependency);
```

###### findObjectByType() 메서드

- 이 메서드는 셋터 메서드를 호출할 때 넘겨줄 의존 객체를 찾는 일을 함



##### ContextLoaderListener 변경

``` java
@WebListener
public class ContextLoaderListener implements ServletContextListener {
  
  static ApplicationContext applicationContext;

  public static ApplicationContext getApplicationContext() {
    return applicationContext;
  }

  @Override
  public void contextInitialized(ServletContextEvent event) {
    try {
      ServletContext sc = event.getServletContext();

      String propertiesPath = sc.getRealPath(
          sc.getInitParameter("contextConfigLocation"));
      applicationContext = new ApplicationContext(propertiesPath);
    } catch (Throwable e) {
      e.printStackTrace();
    }
  }

  @Override
  public void contextDestroyed(ServletContextEvent event) {}

}

```

- 이제 더 이상 이 클래스를 변경할 필요가 없음
- 페이지 컨트롤러나 DAO 등을 추가할 때는 프로퍼티 파일에 그 클래스에 대한 정보를 한 줄 추가하면 자동으로 그 객체가 생성됨

###### 프로퍼티 파일의 경로

- 프로퍼티 파일의 이름과 경로 정보도  web.xml 파일로부터 읽어 오게 처리함

``` java
String propertiesPath = sc.getRealPath(
  sc.getInitParameter("contextConfigLocation"));
```

- 그리고 ApplicationContext 객체를 생성할 때 생성자의 매개변수로 넘겨줌

``` java
applicationContext = new ApplicationContext(propertiesPath);
```

###### getApplicationContext() 클래스 메서드

- 이 메서드는  ContextLoaderListener에서 만든 ApplicationContext 객체를 얻을 때 사용함

##### DispatcherServlet 변경

``` java
@SuppressWarnings("serial")
@WebServlet("*.do")
public class DispatcherServlet extends HttpServlet {
  @Override
  protected void service(
      HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    response.setContentType("text/html; charset=UTF-8");
    String servletPath = request.getServletPath();
    try {
      //ServletContext sc = this.getServletContext();
      ApplicationContext ctx = ContextLoaderListener.getApplicationContext();

      HashMap<String, Object> model = new HashMap<String, Object>();
      model.put("session", request.getSession());

      //Controller pageController = (Controller) sc.getAttribute(servletPath);
      Controller pageController = (Controller) ctx.getBean(servletPath);
      if (pageController instanceof DataBinding) {
        prepareRequestData(request, model, (DataBinding)pageController);
      }

      String viewUrl = pageController.execute(model);

      for (String key : model.keySet()) {
        request.setAttribute(key, model.get(key));
      }

      if (viewUrl.startsWith("redirect:")) {
        response.sendRedirect(viewUrl.substring(9));
        return;
      } else {
        RequestDispatcher rd = request.getRequestDispatcher(viewUrl);
        rd.include(request, response);
      }

    } catch (Exception e) {
      e.printStackTrace();
      request.setAttribute("error", e);
      RequestDispatcher rd = request.getRequestDispatcher("/Error.jsp");
      rd.forward(request, response);
    }
  }

  private void prepareRequestData(HttpServletRequest request,
                                  HashMap<String, Object> model, DataBinding dataBinding)
      throws Exception {
    Object[] dataBinders = dataBinding.getDataBinders();
    String dataName = null;
    Class<?> dataType = null;
    Object dataObj = null;
    for (int i = 0; i < dataBinders.length; i+=2) {
      dataName = (String)dataBinders[i];
      dataType = (Class<?>) dataBinders[i+1];
      dataObj = ServletRequestDataBinder.bind(request, dataType, dataName);
      model.put(dataName, dataObj);
    }
  }
}
```

- 이전에 페이지 컨트롤러가 ServletContext에 저장되었기 떄문에 이 객체를 준비해야 했지만, ApplicationContext를 도입하면서 필요가 없어짐
  - 대신 ContextLoaderListener의 getApplicationContext()를 호출하여  ApplicationContext 객체를 꺼냄
- 페이지 컨트롤러를 찾을 때도 ServletContext를 찾지 않기 때문에 제거
  - 대신 ApplicationContext의  getBean()을 호출하여 페이지 컨트롤러를 찾고, 못찾으면 오류를 발생시킴



### 6.6. 애노테이션을 이용한 객체 관리

###### 이전 절에는 프로퍼티 파일을 이용하여 DAO나 페이지 컨트롤러 등을 관리하였음.

###### 예전보다는 DAO나 페이지 컨트롤러를 추가하더라도 손이 덜 가는 구조지만, 그럼에도 이런 객체들을 추가할 때마다 프로퍼티 파일에 한 줄 추가해야 하는 약간의 번거로움이 남아있음

###### 이 번거로움마저 자바'애노테이션'을 사용하여 없애보자

##### '애노테이션'

- 컴파일이나 배포, 실행할 때 참조할 수 있는 아주 특별한 주석
- 애노테이션을 사용하면 클래스나 필드, 메서드에 대해 부가 정보를 등록할 수 있음

###### 애노테이션이 적용된 객체 관리 시나리오

1. 웹 애플리케이션이 시작되면 서블릿 컨테이너 ContextLoaderListener에 대해 ContextInitialized()를 호출
2. contextInitialized()는  ApplicationContext를 생성함. 생성자의 매개변수 값으로 프로퍼티 파일의 경로를 넘김
3. ApplicationContext 생성자는 프로퍼티 파일을 로딩하여 내부 맵에 보관
4. ApplicationContext는 맵에 저장된 정보를 꺼내 인스턴스를  생성하거나 또는 톰캣 서버에서 객체를 가져옴
5. 또한, 자바 classpath를 뒤져서 애노테이션이 붙은 클래스를 찾음. 그리고 애노테이션에 지정된 정보에 따라 인스턴스를 생성
6. 객체가 모두 준비되었으면, 각 객체에 대해 의존 객체를 찾아서 할당

##### 애노테이션 정의

###### @Component 애노테이션의 사용 예

``` java
@Component("memberDao")
class MemberDao {
  ...
}
```



``` java
@Retention(RetentionPolicy.RUNTIME)
public @interface Component {
  String value() default "";
}
```

- 애노테이션 문법은 인터페이스 문법과 비슷함. interface 키워드 앞에  @가 붙음
- 객체 이름을 저장하는 용도로 사용 할 'value'라는 기본속성을 정의
  - value 속성은 값을 설정할 때 이름을 생략할 기능이 있음

###### 애노테이션 유지 정책

- 애노테이션을 정의할 때 잊지 말아야 할 것은 애노테이션의 유지 정책을 지정하는 것
- 애노테이션 유지 정책이란, 애노테이션 정보를 언제까지 유지할 것인지를 설정하는 문법

``` java
@Retention(RetentionPolicyl.RUNTIME)
```

- 위 애노테이션의 유지 정책을  RUNTIME으로 지정했기 때문에, 실행 중에도 언제든지 @Component 애노테이션의 속성값을 참조할 수 있음

##### 애노테이션 유지 정책 정리

###### RetentionPolicy.SOURCE

- 소스 파일에서만 유지, 컴파일할 때 제거됨, 즉 클래스 파일에 애노테이션 정보가 남아있지 않음

###### RetentionPolicy.CLASS

- 클래스 파일에 기록됨. 실행 시에는 유지되지 않음. 즉 실행 중에서는 클래스에 기록된 애노테이션 값을 꺼낼 수 없음 (기본 정책)

###### RetentionPolicy.RUNTIME

- 클래스 파일에 기록됨. 실행 시에도 유지됨 즉, 실행 중에 클래스에 기록된 애노테이션 값을 참조할 수 있음.



##### 애노테이션 적용

``` java
@Component("memberDao")
public class PostgreSqlMemberDao implements MemberDao{
```

> ##### @Component(value="객체이름")

- 페이지 컨트롤러의 경우, 서블릿 URL을 지정.

``` java
@Component ("/member/list.do")
public class MemberListController implements Controller {
```

##### 프로퍼티 파일 변경

- 우리가 만든 클래스에 대해서는 애노테이션을 적용할 수 있지만, DataSource와 같은 톰캣 서버가 제공하는 객체에는 애노테이션을 적용할 수 없음

``` properties
jndi.dataSource=java:comp/env/jdbc/postgresql
#memberDao=spms.dao.PostgreSqlMemberDao
#/auth/login.do=spms.controls.LogInController
#/auth/logout.do=spms.controls.LogOutController
#/member/list.do=spms.controls.MemberListController
#/member/add.do=spms.controls.MemberAddController
#/member/update.do=spms.controls.MemberUpdateController
#/member/delete.do=spms.controls.MemberDeleteController
```

- DAO와 페이지 컨트롤러는 애노테이션으로 객체 정보를 관리하기 때문에 프로퍼티 파일에서 제거
- 톰캣 서버가 관리하는  JNDI 객체나 외부 라이브러리에 들어 있는 객체는 우리가 만든 애노테이션을 적용할 수 없기 때문에 프로퍼티 파일에 등록해야 함

##### ApplicationContext 변경 (추가)

``` java
ublic class ApplicationContext {
  Hashtable<String,Object> objTable = new Hashtable<String,Object>();

  public Object getBean(String key) {
    return objTable.get(key);
  }

  public ApplicationContext(String propertiesPath) throws Exception {
    Properties props = new Properties();
    props.load(new FileReader(propertiesPath));

    prepareObjects(props);
    prepateAnnotationObjects();
    injectDependency();
  }

  private void prepateAnnotationObjects() throws Exception {
    Reflections reflector = new Reflections("");

    Set<Class<?>> list = reflector.getTypesAnnotatedWith(Component.class);
    String key = null;
    for(Class<?> clazz : list) {
      key = clazz.getAnnotation(Component.class).value();
      objTable.put(key, clazz.newInstance());
    }
  }
```

- 애노테이션이 붙은 클래스를 찾아서 객체를 준비해 주는  prepateAnnotationObjects() 메서드 추가

###### prepateAnnotationObjects() 메서드

- 이 메서드는 자바 classpath를 뒤져서 @Component 애노테이션이 붙은 클래스를 찾음. 그리고 그 객체를 생성하여 객체 테이블에 담는 일을 함
  - 이 작업을 위해 "Reflections"라는 오픈 소스 라이브러리를 활용

###### Reflections

- 우리가 원하는 클래스를 찾아 주는 도구
- 생성자에 넘겨 주는 매개 변수 값은 클래스를 찾을 때 출발하는 패키지
- 만약 매개변수 값이 'spms'라면 spms 패키지 및 그 하위 패키지를 모두 뒤짐
  - 예제에서는 "" 처럼 빈 문자열을 넘겼는데, 이는 자바 classpath에 있는 모든 패키지를 검색하라는 뜻
- Reflections의 getTypesAnnotatedWith() 메서드를 사용하면 애노테이션이 붙은 클래스들을 찾을 수 있음
- getAnnotation()을 통해 클래스로부터 애노테이션을 추출함
- @Component의 기본 속성값을 꺼내고 싶으면, 다음과 같이 value()를 호출

``` java
key = clazz.getAnnotation(Component.class).value();
```

- 이렇게 애노테이션을 통해 알아낸 객체 이름(key)으로 인스턴스를 지정

``` java
objTable.put(key, clazz.newInstance());
```



### 6.7 실력 향상 훈련

###### 간단한 프로젝트 관리 시스템을 만들어 보자

##### 프로젝트 테이블 생성

``` sql
CREATE TABLE PROJECTS (
	PNO SERIAL NOT NULL,
  PNAME VARCHAR NOT NULL,
  CONTENT TEXT NOT NULL,
  STA_DATE TIMESTAMP NOT NULL,
  END_DATE TIMESTAMP NOT NULL,
  STATE INTEGER NOT NULL,
  CRE_DATE TIMESTAMP NOT NULL,
  TAGS VARCHAR NULL
);
```

- PNO 컬럼에 대해 자동적으로 증가하는 일련번호가 저장될 수 있도록 설정함

###### PROJECTS 테이블의 기본 키 칼럼을 지정

``` sql
ALTER TABLE PROJECTS ADD CONSTRAINT PK_PROJECTS PRIMARY KEY (PNO);
```

##### 프로젝트 멤버 테이블 생성

``` sql
CREATE TABLE PRJ_MEMBS (
  PNO INTEGER NOT NULL,
  MNO INTEGER NOT NULL,
  LEVEL INTEGER NOT NULL,
 	STATE INTEGER NOT NULL,
  MOD_DATE TIMESTAMP NOT NULL,
);
```

###### PRJ_MEMBS 테이블의 기본 키 칼럼 지정

``` sql
ALTER TABLE PRJ_MEMBS ADD CONSTRAINT PK_PRJ_MEMBS PRIMARY KEY (PNO, MNO);
```

##### Project 값 객체 준비

``` java
public class Project {
  protected int     no;
  protected String  title;
  protected String  content;
  protected Date    startDate;
  protected Date    endDate;
  protected int     state;
  protected Date    createdDate;
  protected String  tags;

  public int getNo() {
    return no;
  }
  public Project setNo(int no) {
    this.no = no;
    return this;
  }
  public String getTitle() {
    return title;
  }
  public Project setTitle(String title) {
    this.title = title;
    return this;
  }
  public String getContent() {
    return content;
  }
  public Project setContent(String content) {
    this.content = content;
    return this;
  }
  public Date getStartDate() {
    return startDate;
  }
  public Project setStartDate(Date startDate) {
    this.startDate = startDate;
    return this;
  }
  public Date getEndDate() {
    return endDate;
  }
  public Project setEndDate(Date endDate) {
    this.endDate = endDate;
    return this;
  }
  public int getState() {
    return state;
  }
  public Project setState(int state) {
    this.state = state;
    return this;
  }
  public Date getCreatedDate() {
    return createdDate;
  }
  public Project setCreatedDate(Date createdDate) {
    this.createdDate = createdDate;
    return this;
  }
  public String getTags() {
    return tags;
  }
  public Project setTags(String tags) {
    this.tags = tags;
    return this;
  }
}
```

### 프로젝트 관리 시스템 만들기

##### 프로젝트 목록 페이지 구현

1. ###### DAO 인터페이스 생성

``` java
public interface ProjectDao {
  List<Project> selectList() throws Exception;
}
```

2. ###### DAO 구현체 생성

``` java
public class PostgreSqlProjectDao implements ProjectDao {

  DataSource ds;

  public void setDataSource(DataSource ds) {
    this.ds = ds;
  }

  public List<Project> selectList() throws Exception {
    Connection connection = null;
    Statement stmt = null;
    ResultSet rs = null;

    try {
      connection = ds.getConnection();
      stmt = connection.createStatement();
      rs = stmt.executeQuery(
          "SELECT PNO, PNAME, STA_DATE, END_DATE, STATE" +
              " FROM PROJECTS" +
              " ORDER BY PNO DESC");

      ArrayList<Project> projects = new ArrayList<Project>();

      while(rs.next()) {
        projects.add(new Project()
            .setNo(rs.getInt("PNO"))
            .setTitle(rs.getString("PNAME"))
            .setStartDate(rs.getDate("STA_DATE"))
            .setEndDate(rs.getDate("END_DATE"))
            .setCreatedDate(rs.getDate("STATE"))	);
      }

      return projects;

    } catch (Exception e) {
      throw e;

    } finally {
      try {if (rs != null) rs.close();} catch(Exception e) {}
      try {if (stmt != null) stmt.close();} catch(Exception e) {}
      try {if (connection != null) connection.close();} catch (Exception e) {}
    }
  }

}
```

###### 3. 페이지 컨트롤러 생성

``` java
public class ProjectListController implements Controller{

  ProjectDao projectDao;
  
  public ProjectListController setProjectDao(ProjectDao projectDao) {
    this.projectDao = projectDao;
    return this;
  }
  
  public String execute(Map<String, Object> model) throws Exception {
    model.put("project", projectDao.selectList());
    return "/project/ProjectList.jsp";
  }
  
}
```

###### 4. JSP 페이지 생성

```jsp
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>프로젝트목록</title>
</head>
<body>
<jsp:include page="/Header.jsp"/>
<h1>프로젝트 목록</h1>
<p><a href="add.do">신규 프로젝트</a></p>
<table border="1">
    <tr>
        <th>번호</th>
        <th>제목</th>
        <th>시작일</th>
        <th>종료일</th>
        <th>상태</th>
        <th></th>
    </tr>
    <c:forEach var="project" items="#{projects}">
        <tr>
            <td>${project.no}</td>
            <td><a href='update.do?no=${project.no}'>${project.title}</a></td>
            <td>${project.startDate}</td>
            <td>${project.endDate}</td>
            <td>${project.state}</td>
            <td><a href="delete.do?no=${project.no}">[삭제]</a></td>
        </tr>
    </c:forEach>
</table>
<jsp:include page="/Tail.jsp"/>
</body>
</html>
```

#### 프로젝트 등록 구현

###### DAO 인터페이스에 등록 메서드 추가

``` java
int insert(Project project) throws Exception;
```

###### DAO 구현체에 메서드 추가

``` java
public int insert(Project project) throws Exception {
  Connection connection = null;
  PreparedStatement stmt = null;

  try {
    connection = ds.getConnection();
    stmt = connection.prepareStatement(
      "INSERT INTO PROJECTS"
      + " (PNAME, CONTENT, STA_DATE, END_DATE, STATE, CRE_DATE, TAGS)"
      + " VALUE (?, ?, ?, ?, 0, NOW(), ?)");
    stmt.setString(1, project.getTitle());
    stmt.setString(2, project.getContent());
    stmt.setDate(3, new java.sql.Date(project.getStartDate().getTime()));
    stmt.setDate(4, new java.sql.Date(project.getEndDate().getTime()));
    stmt.setString(5, project.getTags());

    return stmt.executeUpdate();

  } catch (Exception e) {
    throw e;
  } finally {
    try {if (stmt != null) stmt.close();} catch (Exception e) {}
    try {if (connection != null) connection.close();} catch (Exception e) {}
  }
```

###### 페이지 컨트롤러 - ProjectAddController

``` java
@Component("/project/add.do")
public class ProjectAddController implements Controller, DataBinding {
  
  ProjectDao projectDao;
  
  public ProjectAddController setProjectDao(ProjectDao projectDao) {
    this.projectDao = projectDao;
    return this;
  }
  
  public Object[] getDataBinders() {
    return new Object[]{
        "project", spms.vo.Project.class
    };
  }
  
  @Override
  public String execute(Map<String, Object> model) throws Exception {
    Project project = (Project)model.get("project");
    if(project.getTitle() == null){
      return "/project/ProjectForm.jsp";
    } else {
      projectDao.insert(project);
      return "redirect:list.do";
    }
  }
  
}
```

###### 뷰 컴포넌트 - ProjectForm.jsp

``` jsp
<%@ page
        language="java"
        contentType="text/html; charset=UTF-8"
        pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>프로젝트 등록</title>
    <style>
        ul { padding: 0; }
        li { list-style:none; }

        label {
            float: left;
            text-align: right;
            width: 60px;
        }
    </style>
</head>
<body>
<jsp:include page="/Header.jsp"/>
<h1>프로젝트 등록</h1>
<form action='add.do' method='post'>
    <ul>
        <li><label for="title">제목</label>
            <input id="title"
                   type='text' name='title' size="50"></li>
        <li><label for="content">내용</label>
            <textarea id="content"
                      name='content' rows="5" cols="40"></textarea></li>
        <li><label for="sdate">시작일</label>
            <input id="sdate"
                   type='text' name='startDate' placeholder="예)2013-01-01"></li>
        <li><label for="edate">종료일</label>
            <input id="edate"
                   type='text' name='endDate' placeholder="예)2013-01-01"></li>
        <li><label for="tags">태그</label>
            <input id="tags"
                   type='text' name='tags' placeholder="예)태그1 태그2 태그3" size="50"></li>
    </ul>
    <input type='submit' value='추가'>
    <input type='reset' value='취소'>
</form>
<jsp:include page="/Tail.jsp"/>
</body>
</html>
```

##### 프로젝트 변경 구현

###### DAO 인터페이스에 변경 메서드 추가

``` java
int delete(int no) throws Exception;
```

###### DAO 구현체에 메서드 추가

``` java
  public Project selectOne(int no) throws Exception {
    Connection connection = null;
    Statement stmt = null;
    ResultSet rs = null;

    try {
      connection = ds.getConnection();
      stmt = connection.createStatement();
      rs = stmt.executeQuery(
          "SELECT PNO,PNAME,CONTENT,STA_DATE,END_DATE,CRE_DATE,TAGS"
          + " FORM PROJECTS"
          + " WHERE PNO="
          + no);
      if(rs.next()) {
        return new Project()
            .setNo(rs.getInt("PNO"))
            .setTitle(rs.getString("PNAME"))
            .setContent(rs.getString("CONTENT"))
            .setStartDate(rs.getDate("STA_DATE"))
            .setEndDate(rs.getDate("END_DATE"))
            .setState(rs.getInt("STATE"))
            .setCreatedDate(rs.getDate("CRE_DATE"))
            .setTags(rs.getString("TAGS"));
      } else {
        throw new Exception("해당 번호의 프로젝트를 찾을 수 없습니다.");
      }
    } catch (Exception e) {
      throw e;
    } finally {
      try {if (rs != null) rs.close();} catch(Exception e) {}
      try {if (stmt != null) stmt.close();} catch(Exception e) {}
      try {if (connection != null) connection.close();} catch(Exception e) {}    }
  }

  public int update(Project project) throws Exception {
    Connection connection = null;
    PreparedStatement stmt = null;
    try {
      connection = ds.getConnection();
      stmt = connection.prepareStatement(
          "UPDATE PROJECTS SET "
              + " PNAME=?,"
              + " CONTENT=?,"
              + " STA_DATE=?,"
              + " END_DATE=?,"
              + " CRE_DATE=?,"
              + " TAGS=?"
              + " WHERE PNO=?");
      stmt.setString(1, project.getTitle());
      stmt.setString(2, project.getContent());
      stmt.setDate(3, new java.sql.Date(project.getStartDate().getTime()));
      stmt.setDate(4, new java.sql.Date(project.getEndDate().getTime()));
      stmt.setInt(5, project.getState());
      stmt.setString(6, project.getTags());
      stmt.setInt(7, project.getNo());

      return stmt.executeUpdate();

    } catch (Exception e) {
      throw e;
    } finally {
      try {
        if (stmt != null) stmt.close();
      } catch (Exception e) {
      }
      try {
        if (connection != null) connection.close();
      } catch (Exception e) {
      }
    }
  }
```

###### 페이지 컨트롤러 - ProjectUpdateController

``` java
@Component ("/project/update.do")
public class ProjectUpdateController implements Controller, DataBinding {

  ProjectDao projectDao;

  public ProjectUpdateController setProjectDao(ProjectDao projectDao) {
    this.projectDao = projectDao;
    return this;
  }

  public Object[] getDataBinders() {
    return new Object[]{
        "no", Integer.class,
        "project", spms.vo.Project.class
    };
  }

  public String execute(Map<String, Object> model) throws Exception {
     Project project = (Project)model.get("project");

     if (project.getTitle() == null) {
       Integer no = (Integer)model.get("no");
       Project detailInfo = projectDao.selectOne(no);
       model.put("project", detailInfo);
       return "/project/ProjectUpdateForm.jsp";

     } else {
       projectDao.update(project);
       return "redirect:list.do";
     }
  }

}
```

###### 뷰 컴포넌트 - ProjectUpdateForm.jsp

``` jsp
<%@ page
	language="java"
	contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>프로젝트 정보</title>
<style>
ul { padding: 0; }
li { list-style:none; }

label {
  float: left;
  text-align: right;
  width: 60px;
}
</style>
</head>
<body>
<jsp:include page="/Header.jsp"/>
<h1>프로젝트 정보</h1>
<form action='update.do' method='post'>
<ul>
<li><label for="no">번호</label>
  <input id="no"
  type='text' name='no' size="5" value="${project.no}"
  readonly></li>
<li><label for="title">제목</label>
  <input id="title"
  type='text' name='title' size="50" value="${project.title}"></li>
<li><label for="content">내용</label>
  <textarea id="content"
  name='content' rows="5" cols="40">${project.content}</textarea></li>
<li><label for="sdate">시작일</label>
  <input id="sdate"
  type='text' name='startDate' placeholder="예)2013-01-01"
  value="${project.startDate}"></li>
<li><label for="edate">종료일</label>
  <input id="edate"
  type='text' name='endDate' placeholder="예)2013-01-01"
  value="${project.endDate}"></li>
<li><label for="state">상태</label>
  <select id="state" name="state">
    <option value="0" ${project.state == 0 ? "selected" : ""}>준비</option>
    <option value="1" ${project.state == 1 ? "selected" : ""}>진행</option>
    <option value="2" ${project.state == 2 ? "selected" : ""}>완료</option>
    <option value="3" ${project.state == 3 ? "selected" : ""}>취소</option>
  </select></li>
<li><label for="tags">태그</label>
  <input id="tags"
  type='text' name='tags' placeholder="예)태그1 태그2 태그3" size="50"
  value="${project.tags}"></li>
</ul>
<input type='submit' value='저장'>
<input type='button' value='삭제'
  onclick='location.href="delete.do?no=${project.no}";'>
<input type='button' value='취소' onclick='location.href="list.do"'>
</form>
<jsp:include page="/Tail.jsp"/>
</body>
</html>
```

##### 프로젝트 삭제 구현

###### DAO 인터페이스에 삭제 메서드 추가

``` java
int delete(int no) throws Exception;
```

###### DAO 구현체 - PostgreSqlProjectDao

``` java
public int delete(int no) throws Exception {
  Connection connection = null;
  Statement stmt = null;
  try {
    connection = ds.getConnection();
    stmt = connection.createStatement();
    return stmt.executeUpdate(
      "DELETE FROM PROJECTS WHERE PNO =" + no
    );
  } catch (Exception e) {
    throw e;
  } finally {
    try {
      if (stmt != null) stmt.close();
    } catch (Exception e) {
    }
    try {
      if (connection != null) connection.close();
    } catch (Exception e) {
    }
  }
}
```

###### DAO 구현체 - ProjectDeleteController

``` java
@Component ("/project/delete.do")
public class ProjectDeleteController implements Controller, DataBinding {

  ProjectDao projectDao;

  public ProjectDeleteController setProjectDao(ProjectDao projectDao) {
    this.projectDao = projectDao;
    return this;
  }

  public Object[] getDataBinders() {
    return new Object[]{
        "no", Integer.class
    };
  }
  @Override
  public String execute(Map<String, Object> model) throws Exception {
    Integer no = (Integer)model.get("no");
    projectDao.delete(no);

    return "redirect:list.do";
  }

}
```

###### Header2.jsp 변경

``` jsp
<%-- 메뉴 추가 --%>
<%@page import="spms.vo.Member"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<div style="background-color:#00008b;color:#ffffff;height:20px;padding: 5px;">
    SPMS(Simple Project Management System)

    <span style="float:right;">
<a style="color:white;"
   href="<%=request.getContextPath()%>/project/list.do">프로젝트</a>
<a style="color:white;"
   href="<%=request.getContextPath()%>/member/list.do">회원</a>

<c:if test="${empty sessionScope.member or
              empty sessionScope.member.email}">
<a style="color:white;"
   href="<%=request.getContextPath()%>/auth/login.do">로그인</a>
</c:if>

<c:if test="${!empty sessionScope.member and
              !empty sessionScope.member.email}">
    ${sessionScope.member.name}
    (<a style="color:white;"
    href="<%=request.getContextPath()%>/auth/logout.do">로그아웃</a>)
</c:if>
</span>
</div>
```



![image-20200316142500762](/Users/beomju/Library/Application Support/typora-user-images/image-20200316142500762.png)

