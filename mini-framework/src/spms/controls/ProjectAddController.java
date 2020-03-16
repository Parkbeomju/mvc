package spms.controls;

import spms.annotaiton.Component;
import spms.bind.DataBinding;
import spms.dao.MemberDao;
import spms.dao.PostgreSqlMemberDao;
import spms.dao.ProjectDao;
import spms.vo.Project;

import java.util.Map;

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
    if (project.getTitle() == null) {
      return "/project/ProjectForm.jsp";

    } else {
      projectDao.insert(project);
      return "redirect:list.do";
    }
  }

}