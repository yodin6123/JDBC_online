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
			
			String sql = "select B.boardno,\n"+
				//	"       case when length(B.subject) > 10 then substr(B.subject, 1, 10) || '..' else B.subject end AS subject,\n"+
					"		B.subject,\n"+
					"       M.name,\n"+
					"       to_char(B.writeday, 'yyyy-mm-dd hh24:mi:ss') AS writeday,\n"+
					"       B.viewcount\n"+
					"from jdbc_board B inner join jdbc_member M\n"+
					"on B.fk_userid = M.userid\n"+
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
	public BoardDTO viewContents(String boardno) {
		BoardDTO bdto = null;  // 입력한 글번호가 없을 수 있기 때문에 초기값을 null로 지정하여 null 반환
		
		try {
			conn = MyDBConnection.getConn();
			
			String sql = "select contents, fk_userid\n"+  // 보여주는 건 글내용이지만, 작성자가 누구인지도 알아야 조회수 카운팅이 가능하기 때문
					"from jdbc_board\n"+
					"where boardno = ?";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, boardno);
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				bdto = new BoardDTO();
				bdto.setContents(rs.getString(1));
				bdto.setFk_userid(rs.getString(2));
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

}
