package spms.controls;

import spms.annotaiton.Component;
import spms.bind.DataBinding;
import spms.dao.MemberDao;

import java.util.Map;

@Component("/member/delete.do")
public class MemberDeleteController implements Controller, DataBinding {

//  PostgreSqlMemberDao postgreSqlMemberDao;
  MemberDao memberDao;

  public MemberDeleteController setMemberDao(MemberDao memberDao) {
    this.memberDao = memberDao;
    return this;
  }

  public Object[] getDataBinders() {
    return new Object[]{
        "no", Integer.class
    };
  }

  @Override
  public String execute(Map<String, Object> model) throws Exception {
//    MemberDao memberDao = (MemberDao)model.get("memberDao");

    Integer no = (Integer)model.get("no");
    memberDao.delete(no);

    return "redirect:list.do";
  }

}
