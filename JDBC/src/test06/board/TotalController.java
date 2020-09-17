package test06.board;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

import my.util.MyUtil;
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
					boardList();
					break;

				case "2":  // 글내용보기
					viewContents(loginMember, sc);  // 조회하는 사람이 누구인지 알아야 조회수가 올라갈지 말지 알 수 있기 때문에 로그인 정보 전달해야 한다.
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
					
				case "4":  // 댓글쓰기(jdbc_comment 테이블에 insert 하기)
					n = writeComment(loginMember, sc);
					
					if(n==1) {
						System.out.println(">> 댓글쓰기 성공!! <<");
					} else {
						System.out.println(">> 댓글쓰기 실패!! <<");
					}
					break;
					
				case "5":  // 글수정하기
					n = updateBoard(loginMember, sc);
					// n 값은 개발자 임의로 설정하여 출력 결과를 케이스별로 나눈다.
					if(n==0) {
						System.out.println(">> 수정할 글번호가 글목록에 존재하지 않습니다. <<\n");
					} else if(n==1) {
						System.out.println(">> 다른 사용자의 글은 수정불가 합니다!! <<\n");
					} else if(n==2) {
						System.out.println(">> 글암호가 올바르지 않습니다. <<\n");
					} else if(n==3) {
						System.out.println(">> 글 수정 실패!! <<\n");
					} else if(n==4) {
						System.out.println(">> 글 수정 취소!! <<\n");
					} else if(n==5) {
						System.out.println(">> 글 수정 성공!! <<\n");
					}
					break;
					
				case "6":  // 글삭제하기
					
					n = deleteBoard(loginMember, sc);
					
					if(n==0) {
						System.out.println(">> 삭제할 글번호가 글목록에 존재하지 않습니다. <<\n");
					} else if(n==1) {
						System.out.println(">> 다른 사용자의 글은 삭제불가 합니다!! <<\n");
					} else if(n==2) {
						System.out.println(">> 글암호가 올바르지 않습니다. <<\n");
					} else if(n==3) {
						System.out.println(">> 글 삭제 실패!! <<\n");
					} else if(n==4) {
						System.out.println(">> 글 삭제 취소!! <<\n");
					} else if(n==5) {
						System.out.println(">> 글 삭제 성공!! <<\n");
					}
					
					break;
					
				case "7":  // 최근1주일간 일자별 게시글 작성건수
					
					statisticsByWeek();
					
					break;
					
				case "8":  // 이번달 일자별 게시글 작성건수
					
					statisticsByCurrentMonth();
					
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
	
	
	// 글목록 보기 //
	private void boardList() {
		List<BoardDTO> boardList = bdao.boardList();  // 결과물로 출력되는 하나의 행이 DTO이다 ==> 여러 행(DTO)을 담을 List가 반환되어야할 것이다.
		
		StringBuilder sb = new StringBuilder();
		
		if(boardList.size() > 0) {  // 게시글이 존재하는 경우 
			// select 결과가 없을 경우 nullPointException을 피하기 위해 조건문 추가
			// boardList는 null 값을 가지지 않는다. 메소드 생성 시 객체 생성을 기본적으로 해주었기 때문이다.
			// 따라서 list 내부에 데이터가 있는지 없는지를 조건으로 따져야한다.
			for(int i=0; i<boardList.size(); i++) {
				sb.append(boardList.get(i).listInfo()+"\n");
			}// end of for-----------------------------------
			
			System.out.println("\n-------------------- [ 게시글 목록 ] --------------------");
			System.out.println("글번호\t글제목\t\t작성자\t작성일자\t조회수");
			System.out.println("----------------------------------------------------------------");
			System.out.println(sb.toString());
		} else {  // 게시글이 존재하지 않는 경우
			System.out.println(">>> 글목록이 없습니다 <<<\n");
		}
		
	}// end of boardList()
	
	
	// 글내용보기 //
	private void viewContents(MemberDTO loginMember, Scanner sc) {
		System.out.println("\n>>> 글 내용 보기 <<<");
		
		System.out.print("▷ 글번호 : ");
		String boardNo = sc.nextLine();
		
		Map<String, String> paraMap = new HashMap<String, String>();
		paraMap.put("boardNo", boardNo);
		
		BoardDTO bdto = bdao.viewContents(paraMap);
		// 특정 글번호(고유)에 대한 내용을 보여주고(select --> where boardno = paraMap.get("boardNo"))
		// 해당 글이 다른 사람이 쓴 글이라면 조회수를 1 증가하도록 한다(update viewcount + 1 --> where boardno = paraMap.get("boardNo"))
		
		if(bdto != null) {
			// 존재하는 글번호를 입력한 경우
			System.out.println("[글내용] "+bdto.getContents());
			
			if(!bdto.getFk_userid().equals(loginMember.getUserid())) {
				// 현재 로그인한 사용자가 자신의 글이 아닌 다른 사용자가 쓴 글을 조회했을 경우에만 조회수 1 증가시키기
				bdao.updateViewCount(boardNo);
			}
			
			System.out.println("[댓글]\n----------------------------------------");
			
			List<BoardCommentDTO> commentList = bdao.commentList(boardNo);  // 원글에 대한 댓글을 가져오는 것(특정 게시글 글번호에 대한jdbc_comment 테이블과 jdbc_member 테이블을 JOIN해서 보여준다.)
			
			if(commentList != null) {
				System.out.println("댓글내용\t작성자\t작성일자");
				System.out.println("----------------------------------------");
				
				StringBuilder sb = new StringBuilder();
				
				for(BoardCommentDTO comment : commentList) {  // 개선된 for문, 확장 for문, for each문
					sb.append(comment.commentInfo()+"\n");
				}// end of for-------------------------------
				
				System.out.println(sb.toString());
			} else {
				System.out.println(">> 댓글 내용 없음 <<\n");
			}
			
			
		} else {
			// 존재하지 않는 글번호를 입력한 경우
			System.out.println(">> 글번호 "+boardNo+"은 글목록에 존재하지 않습니다 <<\n");
		}
		
	}// end of viewContents(MemberDTO loginMember, Scanner sc)-----


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
	
	
	// 댓글쓰기(jdbc_comment 테이블에 insert) //
	private int writeComment(MemberDTO loginMember, Scanner sc) {
		int result = 0;
		
		System.out.println("\n>>> 댓글쓰기 <<<");
		
		System.out.println("1. 작성자명 : " + loginMember.getName());
		
		System.out.print("2. 원글의 글번호 : ");
		String boardno = sc.nextLine();  // 존재하지 않는 원글의 글번호를 입력할 수 있다.
		
		String contents = null;
		do {
			System.out.print("3. 댓글내용 : ");
			contents = sc.nextLine();
			
			if(contents==null || contents.trim().isEmpty()) {
				System.out.println(">> 댓글내용은 필수로 입력해야 합니다 <<\n");
			} else {
				break;
			}
		} while(true);
		
		BoardCommentDTO cmdto = new BoardCommentDTO();
		cmdto.setFk_boardno(boardno); 				  // 원글의 글번호
		cmdto.setFk_userid(loginMember.getUserid());  // 댓글을 작성한 사용자의 id ==> 현재 로그인한 사용자의 id
		cmdto.setContents(contents);  				  // 댓글내용
		
		result = bdao.writeComment(cmdto);  // 댓글쓰기(jdbc_comment 테이블에 insert)
		
		return result;
	}
	
	
	// 글 수정하기 //
	private int updateBoard(MemberDTO loginMember, Scanner sc) {
		int result = 0;
		
		System.out.println("\n>>> 글 수정 하기 <<<");
		
		System.out.print("▷ 수정할 글번호 : ");
		String boardNo = sc.nextLine();
		
		Map<String, String> paraMap = new HashMap<String, String>();  // 여러 개의 파라미터(인자)를 넘겨줄 때 Map을 사용한다.
		paraMap.put("boardNo", boardNo);
		
		BoardDTO bdto = bdao.viewContents(paraMap);  // 수정할 글번호가 있는지 확인하기 위해 글내용보기 메소드 재활용
		// 현재 paraMap에는 boardPasswd라는 것이 없는 상태이다.
		
		if(bdto != null) {
			// 수정할 글번호가 글목록에 존재하는 경우라면
			
			if(bdto.getFk_userid().equals(loginMember.getUserid())) {
				// 로그인한 사용자가 쓴 글을 수정할 경우
				System.out.print("▷ 글암호 : ");
				String boardPasswd = sc.nextLine();
				
				paraMap.put("boardPasswd", boardPasswd);
				
				bdto = bdao.viewContents(paraMap);  // 글번호 뿐만 아니라 글암호까지 파라미터로 넘겨주기 위해 메소드 변경
				// 현재 paraMap에는 boardPasswd라는 것이 있는 상태이다.
				
				if(bdto != null) {
					// 글암호가 올바른 경우
					// 수정할 글을 보여주고, 글수정 작업에 들어가도록 한다.
					System.out.println("------------------------------");
					System.out.println("글제목 : " + bdto.getSubject());
					System.out.println("글내용 : " + bdto.getContents());
					System.out.println("------------------------------\n");
					
					System.out.print("▷ 글제목[변경하지 않으려면 엔터]: ");
					String subject = sc.nextLine();
					if(subject!=null && subject.trim().isEmpty()) {
						subject = bdto.getSubject();
					}
					
					System.out.print("▷ 글내용[변경하지 않으려면 엔터]: ");
					String contents = sc.nextLine();
					if(contents!=null && contents.trim().isEmpty()) {
						contents = bdto.getContents();
					}
					
					paraMap.put("subject", subject);
					paraMap.put("contents", contents);  // 글번호는 이미 paraMap에 들어가 있다.
					
					int n = bdao.updateBoard(paraMap);
					
					if(n==1) {
						
						Connection conn = MyDBConnection.getConn();
						
						do {
							System.out.print("▷ 정말로 수정하시겠습니까?[Y/N] ");
							String yn = sc.nextLine();
							
							try {
								if("y".equalsIgnoreCase(yn)) {
									conn.commit();
									result = 5;
									break;
								} else if("n".equalsIgnoreCase(yn)) {
									conn.rollback();
									result = 4;
									break;
								} else {
									System.out.println(">> Y 또는 N 만 입력하세요!! <<");
								}
							} catch(SQLException e) {
								e.printStackTrace();
								break;
							}
						} while (true);
						
					} else {
						// sql문에 장애가 발생한 경우
						result = 3;
					}
					
				} else {
					// 글암호가 틀린 경우
					result = 2;
				}
				
			} else {
				// 로그인한 사용자가 쓴 글이 아닌 다른 사용자가 쓴 글을 수정할 경우
				result = 1;
			}
		}
		
		return result;
	}
	
	
	// 글 삭제하기 //
	private int deleteBoard(MemberDTO loginMember, Scanner sc) {

		int result = 0;
		
		System.out.println("\n>>> 글 삭제 하기 <<<");
		
		System.out.print("▷ 삭제할 글번호 : ");
		String boardNo = sc.nextLine();
		
		Map<String, String> paraMap = new HashMap<String, String>();
		paraMap.put("boardNo", boardNo);
		
		BoardDTO bdto = bdao.viewContents(paraMap);
		
		if(bdto != null) {
			
			if(loginMember.getUserid().equals(bdto.getFk_userid())) {
				
				System.out.print("▷ 글암호 : ");
				String boardPasswd = sc.nextLine();
				
				paraMap.put("boardPasswd", boardPasswd);
				
				bdto = bdao.viewContents(paraMap);
				
				if(bdto != null) {
					
					System.out.println("------------------------------");
					System.out.println("글제목 : " + bdto.getSubject());
					System.out.println("글내용 : " + bdto.getContents());
					System.out.println("------------------------------\n");
					
					int n = bdao.deleteBoard(boardNo);
					
					if(n==1) {
						
						Connection conn = MyDBConnection.getConn();
						
						do {
							
							System.out.print("▷ 정말로 삭제하시겠습니까?[Y/N] ");
							String yn = sc.nextLine();
							
							try {
								if("y".equalsIgnoreCase(yn)) {
									conn.commit();
									result = 5;
									break;
								} else if("n".equalsIgnoreCase(yn)) {
									conn.rollback();
									result = 4;
									break;
								} else {
									System.out.println(">> Y 또는 N 만 입력하세요!! <<");
								}
							} catch(SQLException e) {
								e.printStackTrace();
								break;
							}
							
						} while (true);
						
					} else {
						result = 3;
					}
					
				} else {
					result = 2;
				}
				
			} else {
				result = 1;
			}
		}
		
		return result;
	}
	
	
	// 최근1주일간 일자별 게시글 작성건수 //
	private void statisticsByWeek() {
		
		System.out.println("------------ [최근 1주일간 일자별 게시글 작성건수] ------------");
		
		String result = "전체\t";
		for(int i=0; i<7; i++) {
			result += MyUtil.getDay(-(6-i)) + " ";
		}// end of for----------
		
		System.out.println(result);
		System.out.println("------------------------------------------------------");
		
		Map<String, Integer> resultMap = bdao.statisticsByWeek();  // select 결과물 --> DTO가 아닌 Map으로 받을 수도 있다. 하나의 행 결과물을 Map으로 받는다(Map = DTO라고 생각).
		
		String str = resultMap.get("TOTAL")+"\t"+
				resultMap.get("PREVIOUS6")+"\t"+
				resultMap.get("PREVIOUS5")+"\t"+
				resultMap.get("PREVIOUS4")+"\t"+
				resultMap.get("PREVIOUS3")+"\t"+
				resultMap.get("PREVIOUS2")+"\t"+
				resultMap.get("PREVIOUS1")+"\t"+
				resultMap.get("TODAY");
		
		System.out.println(str);
			
	}
	
	
	// 이번달 일자별 게시글 작성건수
	private void statisticsByCurrentMonth() {

		Calendar currentDate = Calendar.getInstance();  // 현재 날짜와 시간을 얻어온다.
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy년 MM월");
		String currentMonth = dateFormat.format(currentDate.getTime());
		
		System.out.println(">>> ["+currentMonth+" 일자별 게시글 작성건수] <<<");
		System.out.println("-----------------------------------");
		System.out.println("작성일자\t작성건수");
		System.out.println("-----------------------------------");
		
		List<Map<String, String>> mapList = bdao.statisticsByCurrentMonth();
		
		if(mapList.size() > 0) {
			
			StringBuilder sb = new StringBuilder();
			
			for(Map<String, String> map : mapList) {
				sb.append(map.get("WRITEDAY") + "\t" + map.get("CNT") + "\n");
			}// end of for----------
			
			System.out.println(sb.toString());
			
		} else {
			
			System.out.println(">> 작성된 게시글이 없습니다. <<");
			
		}
			
	}


	// **** Connection 자원반납  **** //
	private void appExit() {
		MyDBConnection.closeConnection();
	}	
	
}
