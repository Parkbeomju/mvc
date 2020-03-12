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

// ServletContext에 보관된 MemberDao 사용하기
@SuppressWarnings("serial")
@WebServlet("/member/update")
public class MemberUpdateServlet extends HttpServlet {
	@Override
	protected void doGet(
			HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			ServletContext sc = this.getServletContext();
			PostgreSqlMemberDao postgreSqlMemberDao = (PostgreSqlMemberDao)sc.getAttribute("memberDao");

			Member member = postgreSqlMemberDao.selectOne(
					Integer.parseInt(request.getParameter("no")));

			request.setAttribute("member", member);
			request.setAttribute("viewUrl", "/member/MemberUpdateForm.jsp");

		} catch (Exception e) {
			throw new ServletException(e);
		}
	}

	@Override
	protected void doPost(
			HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			ServletContext sc = this.getServletContext();
			PostgreSqlMemberDao postgreSqlMemberDao = (PostgreSqlMemberDao) sc.getAttribute("memberDao");

			Member member = (Member)request.getAttribute("member");
			postgreSqlMemberDao.update(member);

			request.setAttribute("viewUrl", "redirect:list.do");
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}
}
