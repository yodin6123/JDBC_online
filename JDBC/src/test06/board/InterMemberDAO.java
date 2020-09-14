package test06.board;

import java.util.*;

public interface InterMemberDAO {

	// 회원가입 메소드
	int memberRegister(MemberDTO member, Scanner sc);
	
	// 로그인처리 메소드
	MemberDTO login(Map<String, String> paraMap);	
	
	// 게시판에서 글을 쓴 작성자에게 포인트 10 올려주기
	int updateMemberPoint(MemberDTO loginMember);
	
}
