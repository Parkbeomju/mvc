package spms.controls;

import spms.annotaiton.Component;
import spms.dao.ProjectDao;

import java.util.Map;

@Component("/project/list.do")
public class ProjectListController implements Controller {

  ProjectDao projectDao;

  public ProjectListController setMemberDao(ProjectDao projectDao) {
    this.projectDao = projectDao;
    return this;
  }

  @Override
  public String execute(Map<String, Object> model) throws Exception {
    model.put("projects", projectDao.selectList());
    return "/project/ProjectList.jsp";

  }
}
