package spms.servlets;

import spms.bind.DataBinding;
import spms.bind.ServletRequestDataBinder;
import spms.context.ApplicationContext;
import spms.controls.Controller;
import spms.listeners.ContextLoaderListener;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;

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
//      model.put("memberDao", sc.getAttribute("memberDao"));

      Controller pageController = (Controller) ctx.getBean(servletPath);
      if (pageController == null) {
        throw new Exception("요청한 서비스를 찾을 수 없습니다.");
      }

      if (pageController instanceof DataBinding) {
        prepareRequestData(request, model, (DataBinding)pageController);
      }

/*      if ("/member/list.do".equals(servletPath)) {
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
*/
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