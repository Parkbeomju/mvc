package spms.listeners;

import spms.context.ApplicationContext;
import spms.controls.*;
import spms.dao.MemberDao;
import spms.dao.PostgreSqlMemberDao;

import javax.naming.InitialContext;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.sql.DataSource;


@WebListener
public class ContextLoaderListener implements ServletContextListener {

//  Connection conn;
//  DBConnectionPool connPool;
//  BasicDataSource ds;

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

      /*
      Class.forName(sc.getInitParameter("driver"));
      conn = DriverManager.getConnection(
          sc.getInitParameter("url"),
          sc.getInitParameter("username"),
          sc.getInitParameter("password"));

      connPool = new DBConnectionPool(
          sc.getInitParameter("driver"),
          sc.getInitParameter("url"),
          sc.getInitParameter("username"),
          sc.getInitParameter("password"));

      ds = new BasicDataSource();
      ds.setDriverClassName(sc.getInitParameter("driver"));
      ds.setUrl(sc.getInitParameter("url"));
      ds.setUsername(sc.getInitParameter("username"));
      ds.setPassword(sc.getInitParameter("password"));

      InitialContext initialContext = new InitialContext();
      DataSource ds = (DataSource)initialContext.lookup(
          "java:comp/env/jdbc/postgresql");

      PostgreSqlMemberDao memberDao = new PostgreSqlMemberDao();
      memberDao.setDataSource(ds);
      memberDao.setConnection(conn);
      memberDao.setDbConnectionPool(connPool);


     sc.setAttribute("memberDao", memberDao);  //별도로 꺼내서 사용할 일이 없기 때문에 ServletContext에 저장안함
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
          */
    } catch (Throwable e) {
      e.printStackTrace();
    }
  }

  @Override
  public void contextDestroyed(ServletContextEvent event) {
//    try {
//      conn.close();
//    } catch (Exception e) {}
//    connPool.closeAll();
//    try {if (ds != null) ds.close();} catch (SQLException e) {}
  }

}
