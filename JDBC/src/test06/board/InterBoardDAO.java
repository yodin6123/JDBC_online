package test06.board;

import java.util.List;
import java.util.Map;

public interface InterBoardDAO {
	
	int write(BoardDTO bdto);        // 게시판 글쓰기(jdbc_board 테이블에 insert)
	
	List<BoardDTO> boardList();      // 글목록보기
	
	BoardDTO viewContents(Map<String, String> paraMap);  // 글내용보기
	
	void updateViewCount(String boardNo); // 조회수 1 증가시키기
	
	int writeComment(BoardCommentDTO cmdto);  // 댓글쓰기(jdbc_comment 테이블에 insert)
	
	List<BoardCommentDTO> commentList(String boardNo);  // 원글에 대한 댓글을 가져오는 것(특정 게시글 글번호에 대한jdbc_comment 테이블과 jdbc_member 테이블을 JOIN해서 보여준다.)
	
	int updateBoard(Map<String, String> paraMap);  // 글 수정하기
	
	int deleteBoard(String boardNo);  // 글 삭제하기
	
	Map<String, Integer> statisticsByWeek();  // 최근 1주일간 일자별 게시글 작성건수 조회하기 ==> select 결과를 Map으로 받는 메소드
	
	List<Map<String, String>> statisticsByCurrentMonth();  // 이번달 일자별 게시글 작성건수 조회하기

}
