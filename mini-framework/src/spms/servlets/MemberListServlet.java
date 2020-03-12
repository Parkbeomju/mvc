package spms.servlets;

import spms.dao.PostgreSqlMemberDao;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

// UI 출력 코드를 제거하고, UI 생성 및 출력을 JSP에게 위임한다.
@WebServlet("/member/list")
public class MemberListServlet extends HttpServlet {

  @Override
  public void doGet(
      HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    try {
      ServletContext sc = this.getServletContext();

      PostgreSqlMemberDao postgreSqlMemberDao = (PostgreSqlMemberDao)sc.getAttribute("memberDao");

      request.setAttribute("members", postgreSqlMemberDao.selectList());
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