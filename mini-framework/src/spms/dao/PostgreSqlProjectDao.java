package spms.dao;

import spms.vo.Member;
import spms.vo.Project;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

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
