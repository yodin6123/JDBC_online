package test06.board;

import java.util.List;

public interface InterBoardDAO {
	
	int write(BoardDTO bdto);        // 게시판 글쓰기(jdbc_board 테이블에 insert)
	
	List<BoardDTO> boardList();      // 글목록보기
	
	BoardDTO viewContents(String boardno);  // 글내용보기
	
	void updateViewCount(String boardNo); // 조회수 1 증가시키기
	
	int writeComment(BoardCommentDTO cmdto);  // 댓글쓰기(jdbc_comment 테이블에 insert)

}
