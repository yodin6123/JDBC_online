--- *** SYS 로 접속한다. *** ---

show user;
-- USER이(가) "SYS"입니다.

--- *** 오라클 일반 사용자 계정(계정명 : myorauser , 암호 : cclass)을 생성해준다. *** ---
create user myorauser identified by cclass;
-- User MYORAUSER이(가) 생성되었습니다.

--- *** 생성된 myorauser 에게 오라클서버에 접속해서 작업을 할 수 있도록 권한을 부여해 준다. *** ---
grant connect, resource to myorauser; 
-- Grant을(를) 성공했습니다.

---------------------------------------------------------------------------------------

--- *** MYORAUSER 로 접속한다. *** ---
show user;
-- USER이(가) "MYORAUSER"입니다.

select * from tab;

        ------ ***  회원 테이블 생성하기  *** ------
drop table jdbc_member purge; 
        
create table jdbc_member
(userseq       number        not null    -- 회원번호
,userid        varchar2(30)  not null    -- 회원아이디
,passwd        varchar2(30)  not null    -- 회원암호
,name          varchar2(20)  not null    -- 회원명
,mobile        varchar2(20)              -- 연락처
,point         number(10) default 0      -- 포인트
,registerday   date default sysdate      -- 가입일자 
,status        number(1) default 1       -- status 컬럼의 값이 1 이면 정상, 0 이면 탈퇴 
,constraint PK_jdbc_member primary key(userseq)
,constraint UQ_jdbc_member unique(userid)
,constraint CK_jdbc_member check( status in(0,1) )
);

create sequence userseq
start with 1
increment by 1
nomaxvalue
nominvalue
nocycle
nocache;


select *
from jdbc_member
order by userseq desc;

select userseq, userid, passwd, name, mobile, point
     , to_char(registerday, 'yyyy-mm-dd') AS registerday, status 
from jdbc_member 
where userid = 'leess' and passwd = '1234';

select userseq, userid, passwd, name, mobile, point
     , to_char(registerday, 'yyyy-mm-dd') AS registerday, status 
from jdbc_member 
where userid = 'superman' and passwd = '1234';

select userseq, userid, passwd, name, mobile, point
     , to_char(registerday, 'yyyy-mm-dd') AS registerday, status 
from jdbc_member 
where userid = 'leess' and passwd = '5678';

select *
from user_tables;

------ ****  도서대여 프로그램 테이블 생성하기 **** --------
------ 테이블 생성시 제약조건을 모두 만들어야 합니다. --------
--- 테이블명은 접두어로 hw_ 붙이도록 한다.
예: hw_member  hw_isbn 

회원 
ISBN
각각의도서
대여 

create table 










