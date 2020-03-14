package spms.controls;

import spms.dao.ProjectDao;

import java.util.Map;

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
