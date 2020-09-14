package test06.board;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

import test05.singleton.dbconnection.MyDBConnection;

public class TotalController {

	// DAO(Data Access Object) ==> 데이터베이스에 연결하여 관련된 업무(DDL, DML, DQL)를 처리해주는 객체 
	InterMemberDAO mdao = new MemberDAO();
	InterBoardDAO  bdao = new BoardDAO();
	
	
	// **** 시작메뉴 **** //
	public void menu_Start(Scanner sc) {
		
		MemberDTO member = null;
		
		String sChoice = "";
		do {
			String loginName = (member==null)?"":"["+member.getName()+" 로그인중..]"; 
			String login_logout	= (member==null)?"로그인":"로그아웃";	
			
			System.out.println("\n >>> ----- 시작메뉴 "+loginName+"----- <<< \n"
					+ "1.회원가입   2."+login_logout+"   3.프로그램종료\n"
					+ "-----------------------------\n");
			
			System.out.print("▷ 메뉴번호 선택: ");
			sChoice = sc.nextLine();
			
			switch (sChoice) {
				case "1":  // 회원가입 
					memberRegister(sc);
					break;
					
				case "2":  // 로그인  또는 로그아웃
					if("로그인".equals(login_logout)) {
						member = login(sc);  // 로그인 시도하기
						
						if(member != null) { // 로그인이 성공인 경우
						   menu_Board(member, sc); // 게시판 메뉴에 들어간다.
						}
						else { // 로그인이 실패한 경우 
							System.out.println("~~~ 로그인 실패!! ~~~");
						}
					}
					
					else
						member = null;      // 로그아웃 하기
					
					break;
					
				case "3":  // 프로그램종료
					appExit(); // Connection 자원반납 
					break;					
	
				default:
					System.out.println(">>> 메뉴에 없는 번호 입니다. 다시 선택하세요!! <<< \n");         
					break;
			}// end of switch()-------------------------
			
			
		} while (!("3".equals(sChoice)));
		
	}// end of public void menu_Start(Scanner sc)--------------	
	


	// ****  회원가입  **** //
	private void memberRegister(Scanner sc) {
		
		System.out.println("\n >>> --- 회원가입 --- <<<");
		
		System.out.print("1. 아이디: ");
		String userid = sc.nextLine();
		
		System.out.print("2. 암호: ");
		String passwd = sc.nextLine();
		
		System.out.print("3. 회원명: ");
		String name = sc.nextLine();
		
		System.out.print("4. 연락처(휴대폰): ");
		String mobile = sc.nextLine();
		
		MemberDTO member = new MemberDTO();
		member.setUserid(userid);
		member.setPasswd(passwd);
		member.setName(name);
		member.setMobile(mobile);
		
		int n = mdao.memberRegister(member,sc);
		
		if(n==1) {
			System.out.println("\n >>> 회원가입을 축하드립니다. <<<");
		}
		else {
			System.out.println("\n >>> 회원가입을 취소합니다!! <<<");
		}
		
	}// end of private void memberRegister(Scanner sc)-----------
	
	
	// **** 로그인 **** //
	private MemberDTO login(Scanner sc) {
		
		MemberDTO member = null;
		
		System.out.println("\n >>> --- 로그인 --- <<< ");
		
		System.out.print("▷ 아이디 : ");
		String userid = sc.nextLine();
		
		System.out.print("▷ 암호 : ");
		String passwd = sc.nextLine();
		
		Map<String, String> paraMap = new HashMap<>();
		paraMap.put("userid", userid);
		paraMap.put("passwd", passwd);
				
		member = mdao.login(paraMap);
		
		if(member != null) {
			System.out.println("\n >>> 로그인 성공 !! <<<\n");
		}
		else {
			System.out.println("\n >>> 로그인 실패 !! <<<\n");
		}
		
		return member;
	}// end of private MemberDTO login(Scanner sc)----------------	
	

	
	// **** 게시판 메뉴 **** //
	public void menu_Board(MemberDTO loginMember, Scanner sc) {
		
		String adminMenu = ("admin".equals(loginMember.getUserid()))?"10.모든회원정보조회":"";
		String menuNo = "";
		
		do {
			System.out.println("\n---------- 게시판메뉴["+ loginMember.getName() +"님 로그인중..]----------\n"
					+ " 1.글목록보기  2.글내용보기  3.글쓰기  4.댓글쓰기 \n"
					+ " 5.글수정하기  6.글삭제하기  7.최근1주일간 일자별 게시글 작성건수 \n"
					+ " 8.이번달 일자별 게시글 작성건수  9.나가기  "+ adminMenu +"\n"
					+ "-----------------------------------------------------");
			
			System.out.print("▷ 메뉴번호 선택 : ");
			menuNo = sc.nextLine();
			
			switch (menuNo) {
				case "1":  // 글목록보기
					
					break;

				case "2":  // 글내용보기
					
					break;

				case "3":  // 글쓰기
					int n = write(loginMember, sc);
					
					if(n==1)
						System.out.println(">> 글쓰기 성공!! <<\n");
					else if(n==-1)
						System.out.println(">> 글쓰기를 취소하셨습니다!! <<\n");
					else
						System.out.println(">> 장애가 발생하여 글쓰기가 실패되었습니다!! <<\n");
					
					break;
					
				case "4":  // 댓글쓰기
					
					break;
					
				case "5":  // 글수정하기
					
					break;
					
				case "6":  // 글삭제하기
					
					break;
					
				case "7":  // 최근1주일간 일자별 게시글 작성건수
					
					break;
					
				case "8":  // 이번달 일자별 게시글 작성건수
					
					break;
					
				case "9":  // 나가기
					
					break;
					
				case "10": // 모든회원정보조회(관리자 전용 메뉴) 
					
					if( "admin".equals(loginMember.getUserid()) ) {
						
					}
					else {
						System.out.println(">> 메뉴에 없는 번호 입니다 << \n");
					}
					
					break;					
					
				default:
					System.out.println(">> 메뉴에 없는 번호 입니다 << \n");
					break;
			}
			
		} while (!"9".equals(menuNo));
				
	}// end of public void menu_Board(MemberDTO member, Scanner sc)----------
	
	
	// 글쓰기(글쓰기[jdbc_board 테이블에 insert] + 글쓴 회원의 포인트를 10증가[jdbc_member 테이블에 update] ==> Transaction 처리)
	private int write(MemberDTO loginMember, Scanner sc) {
		int result = 0;
		
		System.out.println("\n>>> 글쓰기 <<<");
		
		System.out.println("1. 작성자명 : " + loginMember.getName());
		
		System.out.print("2. 글제목 : ");
		String subject = sc.nextLine();
		
		System.out.print("3. 글내용 : ");
		String contents = sc.nextLine();
		
		System.out.print("4. 글암호 : ");
		String boardpasswd = sc.nextLine();
		
		BoardDTO bdto = new BoardDTO();
		bdto.setFk_userid(loginMember.getUserid());
		bdto.setSubject(subject);
		bdto.setContents(contents);
		bdto.setBoardpasswd(boardpasswd);
		
		int n1 = bdao.write(bdto);  // insert가 성공이면 n1에는 1이 들어올 것이다.
		
		int n2 = mdao.updateMemberPoint(loginMember);  // 회원테이블에서 글을 작성한 회원의 point를 10 증가하는 update
													   // --> update 성공이면 n2에는 1이 들어올 것이다.
		
		Connection conn = MyDBConnection.getConn();  // Controller에서 commit 또는 rollback하기 위해서는 Connect을 불러와야 한다.
		
		if(n1==1 && n2==1) {
			do {
				System.out.print(">> 정말로 글쓰기를 하시겠습니까?[Y/N] => ");
				String yn = sc.nextLine();
				
				try {
					if("y".equalsIgnoreCase(yn)) {
						conn.commit();
						result = 1;  // 글쓰기(insert) 및 회원의 point 10 증가(update)가 모두 성공인 경우
						break;
					} else if("n".equalsIgnoreCase(yn)) {
						conn.rollback();
						result = -1;  // 사용자가 글쓰기(insert) 및 회원의 point 10 증가(update)를 취소한 경우
						break;
					} else {
						System.out.println(">> Y 또는 N만 입력하세요!! <<\n");
					}
				} catch(SQLException e) {
					e.printStackTrace();
				}
			} while(true); // end of do~while
		} else {
			try {
				conn.rollback();  // bdao.write(bdto); 에 대한 롤백
			} catch(SQLException e) {
				e.printStackTrace();
			}
		} // end of if(n1==1 && n2==1)~else
		
		return result;
	}// end of write(MemberDTO loginMember, Scanner sc)
	// 깃 수



	// **** Connection 자원반납  **** //
	private void appExit() {
		MyDBConnection.closeConnection();
	}	
	
}
