package spms.controls;

import spms.annotaiton.Component;
import spms.bind.DataBinding;
import spms.dao.MemberDao;
import spms.vo.Member;

import java.util.Map;

@Component("/member/update.do")
public class MemberUpdateController implements Controller, DataBinding {

  MemberDao memberDao;

  public MemberUpdateController setMemberDao(MemberDao memberDao) {
    this.memberDao = memberDao;
    return this;
  }

  public Object[] getDataBinders() {
    return new Object[]{
        "no", Integer.class,
        "member", spms.vo.Member.class
    };
  }

  @Override
  public String execute(Map<String, Object> model) throws Exception {
//    MemberDao memberDao = (MemberDao)model.get("memberDao");
    Member member = (Member)model.get("member");

    if (member.getEmail() == null) {
      Integer no = (Integer)model.get("no");
      Member detailInfo = memberDao.selectOne(no);
      model.put("member", detailInfo);
      return "/member/MemberUpdateForm.jsp";
    } else {
//      Member member = (Member)model.get("member");
      memberDao.update(member);
      return "redirect:list.do";
    }
  }

}