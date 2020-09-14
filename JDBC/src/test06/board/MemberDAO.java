package test06.board;

import java.sql.*;
import java.util.*;

import test05.singleton.dbconnection.MyDBConnection;

public class MemberDAO implements InterMemberDAO {

	Connection conn;
	PreparedStatement pstmt;
	ResultSet rs;
	
	
	// *** 자원반납 메소드 *** //
	private void close() {
	
		try {
			if(rs != null)     rs.close();
			if(pstmt != null)  pstmt.close();
		} catch(SQLException e) {
			e.printStackTrace();
		}
		
	}// end of public void close()---------------	
	
	
	// *** DB에 회원가입 메소드 *** //
	@Override
	public int memberRegister(MemberDTO member, Scanner sc) {
		
		int result = 0;
		
		try {
			conn = MyDBConnection.getConn();
			
			String sql = "insert into jdbc_member(userseq, userid, passwd, name, mobile)\n"+
					"values(userseq.nextval, ?, ?, ?, ?)";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, member.getUserid());
			pstmt.setString(2, member.getPasswd());
			pstmt.setString(3, member.getName());
			pstmt.setString(4, member.getMobile());
			
			result = pstmt.executeUpdate();
			
			if(result == 1) {
				String yn = "";
				do {
					System.out.print(">> 회원가입을 정말로 하시겠습니까?[Y/N] ");
					yn = sc.nextLine();
					
					if("Y".equalsIgnoreCase(yn)) {
						conn.commit(); // 커밋
					}
					else if("N".equalsIgnoreCase(yn)) {
						conn.rollback(); // 롤백
						result = 0;
					}
					else {
						System.out.println(">>> Y 또는 N 만 입력하세요!! \n");
					}
					
				} while (!("Y".equalsIgnoreCase(yn) || "N".equalsIgnoreCase(yn)));
			}
			
		} catch (SQLIntegrityConstraintViolationException e) {
			System.out.println("에러메시지 : " + e.getMessage());
			System.out.println("에러코드번호 : " + e.getErrorCode());
			System.out.println(">>> 아이디가 중복되었습니다. 새로운 아이디를 입력하세요!! ");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
		
		return result;
	}// end of public int memberRegister(MemberDTO member)---------------	
	

	
	// *** 로그인처리 메소드 *** //
	@Override
	public MemberDTO login(Map<String, String> paraMap) {
		
		MemberDTO member = null;
		
		try {
			conn = MyDBConnection.getConn();
			
			String sql = "select userseq, userid, passwd, name, mobile, point\n"+
				//	"select name \n"+
					"     , to_char(registerday, 'yyyy-mm-dd') AS registerday, status \n"+
					"from jdbc_member \n"+
					"where userid = ? and passwd = ?";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, paraMap.get("userid"));
			pstmt.setString(2, paraMap.get("passwd"));
						
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				member = new MemberDTO();
				member.setUserseq(rs.getInt("userseq"));
				member.setUserid(rs.getString("userid"));
				member.setPasswd(rs.getString("passwd"));
				member.setName(rs.getString("name"));
				member.setMobile(rs.getString("mobile"));
				member.setPoint(rs.getInt("point"));
				member.setRegisterday(rs.getString("registerday"));
				member.setStatus(rs.getInt("status"));
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}		
		
		return member;
	}// end of public MemberDTO login(Map<String, String> paraMap)---------	


	// 게시판에서 글을 쓴 작성자에게 포인트 10 올려주기
	@Override
	public int updateMemberPoint(MemberDTO loginMember) {
		int result = 0;
		
		try {
			conn = MyDBConnection.getConn();
			
			String sql = "update jdbc_member set point = point + 10\n"+
					"where userid = ?";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, loginMember.getUserid());
			
			result = pstmt.executeUpdate();  // sql문이 성공이면 result 변수에는 1이 들어간다.
						
		} catch (SQLException e) {
			// e.printStackTrace();
			System.out.println(loginMember.getName() + "님의 포인트는 현재 20이라서 30으로 증가가 불가합니다.");
		} finally {
			close();
		}
		
		return result;
	}// end of updateMemberPoint(String userid)
	
	
	
}
