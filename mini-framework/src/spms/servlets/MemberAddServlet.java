package spms.servlets;

import spms.dao.PostgreSqlMemberDao;
import spms.vo.Member;

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
      PostgreSqlMemberDao postgreSqlMemberDao = (PostgreSqlMemberDao)sc.getAttribute("memberDao");

      Member member = (Member)request.getAttribute("member");
      postgreSqlMemberDao.insert(member);

      request.setAttribute("viewUrl", "redirect:list.do");
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
