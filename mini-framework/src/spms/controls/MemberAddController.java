package spms.controls;

import spms.annotaiton.Component;
import spms.bind.DataBinding;
import spms.dao.MemberDao;

import java.util.Map;

import spms.vo.Member;

@Component("/member/add.do")

public class MemberAddController implements Controller, DataBinding {

//  PostgreSqlMemberDao postgreSqlMemberDao;

  MemberDao memberDao;

  public MemberAddController setMemberDao(MemberDao memberDao) {
    this.memberDao = memberDao;
    return this;
  }

  public Object[] getDataBinders() {
    return new Object[]{
        "member", spms.vo.Member.class
    };
  }

  @Override
  public String execute(Map<String, Object> model) throws Exception {
    Member member = (Member) model.get("member");
    if (member.getEmail() == null) {
      return "/member/MemberForm.jsp";
    } else {
//      MemberDao memberDao = (MemberDao)model.get("memberDao");
//      Member member = (Member)model.get("member");
//      postgreSqlMemberDao.insert(member);
      memberDao.insert(member);

      return "redirect:list.do";
    }
  }
}
