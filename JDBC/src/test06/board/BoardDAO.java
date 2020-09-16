package test06.board;

import java.sql.*;
import java.util.*;

import test05.singleton.dbconnection.MyDBConnection;

public class BoardDAO implements InterBoardDAO {
	
	private Connection conn = null;
	private PreparedStatement pstmt = null;
	private ResultSet rs = null;
	
	// 자원반납을 해주는 메소드 생성하기(자원생성의 역순으로 반납) //
	private void close() {
		try {
			if(rs != null) {
				rs.close();
				rs = null;
			}
			
			if(pstmt != null) {
				pstmt.close();
				pstmt = null;
			}
			// Connection은 싱글톤이기 때문에 전체 프로그램 종료 시에 반납해줘야 하므로 따로 반납하지 않는다.
		} catch (SQLException e) {
			
		}
	}// end of close()

	// 게시판 글쓰기(jdbc_board 테이블에 insert)
	@Override
	public int write(BoardDTO bdto) {
		int result = 0;
		
		try {
			conn = MyDBConnection.getConn();
			
			String sql = "insert into jdbc_board(boardno, fk_userid, subject, contents, boardpasswd)\n"+
					"values(board_seq.nextval, ?, ?, ?, ?)";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, bdto.getFk_userid());
			pstmt.setString(2, bdto.getSubject());
			pstmt.setString(3, bdto.getContents());
			pstmt.setString(4, bdto.getBoardpasswd());
			
			result = pstmt.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
		
		return result;
	}

	// 글목록보기 //
	@Override
	public List<BoardDTO> boardList() {
		List<BoardDTO> boardList = new ArrayList<BoardDTO>();
		
		try {
			conn = MyDBConnection.getConn();
		/*	
			String sql = "select B.boardno,\n"+
				//	"       case when length(B.subject) > 10 then substr(B.subject, 1, 10) || '..' else B.subject end AS subject,\n"+
					"		B.subject,\n"+
					"       M.name,\n"+
					"       to_char(B.writeday, 'yyyy-mm-dd hh24:mi:ss') AS writeday,\n"+
					"       B.viewcount\n"+
					"from jdbc_board B inner join jdbc_member M\n"+
					"on B.fk_userid = M.userid\n"+
					"order by 1 desc";
		*/
			// 댓글 수 포함 sql
			String sql = "select B.boardno, B.subject, M.name,\n"+
					"       to_char(B.writeday, 'yyyy-mm-dd hh24:mi:ss') AS writeday,\n"+
					"       B.viewcount,\n"+
					"       nvl(C.commentcnt, 0) AS COMMENTCNT\n"+
					"from jdbc_board B inner join jdbc_member M\n"+
					"on B.fk_userid = M.userid\n"+
					"left join\n"+
					"(\n"+
					"select fk_boardno, count(*) AS COMMENTCNT\n"+
					"from jdbc_comment\n"+
					"group by fk_boardno\n"+
					") C\n"+
					"on B.boardno = C.fk_boardno\n"+
					"order by 1 desc";
			
			pstmt = conn.prepareStatement(sql);
			
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				BoardDTO bdto = new BoardDTO();
				
				bdto.setBoardno(rs.getInt(1));    // 또는 bdto.setBoardno(rs.getInt("boardno"));
				bdto.setSubject(rs.getString(2)); // 또는 bdto.setBoardno(rs.getString("subject"));
				
				MemberDTO member = new MemberDTO();
				member.setName(rs.getString(3));
				bdto.setMember(member);
				
				bdto.setWriteday(rs.getString(4));
				bdto.setViewcount(rs.getInt(5));
				
				bdto.setCommentcnt(rs.getInt(6));  // 댓글의 개수
				
				boardList.add(bdto);
			}// end of while
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
		return boardList;
	}

	
	// 글내용보기 //
	@Override
	public BoardDTO viewContents(Map<String, String> paraMap) {
		
		BoardDTO bdto = null;  // 입력한 글번호가 없을 수 있기 때문에 초기값을 null로 지정하여 null 반환
		
		try {
			
			conn = MyDBConnection.getConn();
			
			String sql = "select contents, fk_userid, subject\n"+  // 보여주는 건 글내용이지만, 작성자가 누구인지도 알아야 조회수 카운팅이 가능하기 때문
					"from jdbc_board\n";
			
			// map을 사용하면 메소드 내에서 필요한 인자를 조건절로 나누어 재활용성을 높인다.
			if(paraMap.get("boardPasswd") != null) {
				// 글번호 및 글암호를 입력받아서 글내용을 보여주는 것
				sql += "where boardno = ? and boardpasswd = ? ";
				
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, paraMap.get("boardNo"));
				pstmt.setString(2, paraMap.get("boardPasswd"));
				
			} else {
				// 글번호만 입력받아서 글내용을 보여주는 것
				sql += "where boardno = ? ";
				
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, paraMap.get("boardNo"));
				
			}
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				bdto = new BoardDTO();
				bdto.setContents(rs.getString(1));
				bdto.setFk_userid(rs.getString(2));
				bdto.setSubject(rs.getString(3));
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
		
		return bdto;
	}

	
	// 조회수 1 증가시키기 //
	@Override
	public void updateViewCount(String boardNo) {
		try {
			conn = MyDBConnection.getConn();
			
			String sql = "update jdbc_board set viewcount = viewcount + 1\n"+
					"where boardno = ?";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, boardNo);
			
			int n = pstmt.executeUpdate();
			
			if(n == 1) {
				conn.commit();
			} else {
				conn.rollback();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
		
	}

	
	// 댓글쓰기(jdbc_comment 테이블에 insert) //
	@Override
	public int writeComment(BoardCommentDTO cmdto) {
		int n = 0;
		
		try {
			conn = MyDBConnection.getConn();
			
			String sql = "insert into jdbc_comment(commentno, fk_boardno, fk_userid, contents)\n"+
					"values(seq_comment.nextval, ?, ?, ?)";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, cmdto.getFk_boardno());  // 존재하지 않는 원글의 글번호를 입력할 수 있다.
			pstmt.setString(2, cmdto.getFk_userid());
			pstmt.setString(3, cmdto.getContents());
			
			n = pstmt.executeUpdate();
			
			if(n==1) {
				conn.commit();
			}
		} catch (SQLIntegrityConstraintViolationException e) {
			System.out.println(">>> 원글번호 " + cmdto.getFk_boardno() + "은 존재하지 않습니다 <<<");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
		
		return n;
	}

	
	// 원글에 대한 댓글을 가져오는 것(특정 게시글 글번호에 대한jdbc_comment 테이블과 jdbc_member 테이블을 JOIN해서 보여준다.)
	@Override
	public List<BoardCommentDTO> commentList(String boardNo) {
		List<BoardCommentDTO> commentList = null;  // 댓글이 없을 수 있기 때문에 초기값 null
		
		try {
			conn = MyDBConnection.getConn();
			
			String sql = "select C.contents, M.name, to_char(C.writeday, 'yyyy-mm-dd hh24:mi:ss') AS writeday\n"+
					"from jdbc_comment C join jdbc_member M\n"+
					"on C.fk_userid = M.userid\n"+
					"where C.fk_boardno = ?\n"+
					"order by C.commentno desc";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, boardNo);
			
			rs = pstmt.executeQuery();
			
			int cnt = 0;
			while(rs.next()) {
				BoardCommentDTO cmdto = new BoardCommentDTO();
				cmdto.setContents(rs.getString(1));
				
				MemberDTO member = new MemberDTO();
				member.setName(rs.getString(2));
				cmdto.setMember(member);
				
				cmdto.setWriteday(rs.getString(3));
				
				cnt++;  // 1 ==> 2 ==> ...
				if(cnt==1) {
					commentList = new ArrayList<BoardCommentDTO>();  // while문 안에서 한 번만 객체를 생성하도록 한다.
				}
				commentList.add(cmdto);
			}// end of while--------
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
		
		return commentList;
	}

	@Override
	public int updateBoard(Map<String, String> paraMap) {
		
		int result = 0;
		
		try {
			conn = MyDBConnection.getConn();
			
			String sql = "update jdbc_board set subject = ? , contents = ?\n"+
					"where boardno = ?";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, paraMap.get("subject"));
			pstmt.setString(2, paraMap.get("contents"));
			pstmt.setString(3, paraMap.get("boardNo"));
			
			int n = pstmt.executeUpdate();
			
			if(n == 1) {
				conn.commit();
			} else {
				conn.rollback();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
		
		return result;
		
	}

}
