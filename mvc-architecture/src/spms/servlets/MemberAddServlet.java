package spms.servlets;

import spms.dao.MemberDao;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import spms.vo.Member;

@WebServlet("/member/add")
public class MemberAddServlet extends HttpServlet {
  private static final long serialVersionUID = 1L;

  @Override
  protected void doGet(
      HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    RequestDispatcher rd = request.getRequestDispatcher(
        "/member/MemberForm.jsp");
    rd.forward(request, response);
  }

  @Override
  protected void doPost(
      HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
//		Connection conn = null;
//		PreparedStatement stmt = null;
//
//		try {
//			ServletContext sc = this.getServletContext();
//			conn = (Connection) sc.getAttribute("conn");
//			stmt = conn.prepareStatement(
//					"INSERT INTO MEMBERS(EMAIL,PWD,MNAME,CRE_DATE,MOD_DATE)"
//					+ " VALUES (?,?,?,NOW(),NOW())");
//			stmt.setString(1, request.getParameter("email"));
//			stmt.setString(2, request.getParameter("password"));
//			stmt.setString(3, request.getParameter("name"));
//			stmt.executeUpdate();

    try {
      ServletContext sc = this.getServletContext();
      MemberDao memberDao = (MemberDao) sc.getAttribute("memberDao");

      memberDao.insert(new Member()
          .setEmail(request.getParameter("email"))
          .setPassword(request.getParameter("password"))
          .setName(request.getParameter("name")));

      response.sendRedirect("list");

    } catch (Exception e) {
      e.printStackTrace();
      request.setAttribute("error", e);
      RequestDispatcher rd = request.getRequestDispatcher("/Error.jsp");
      rd.forward(request, response);
    }
  }
}
