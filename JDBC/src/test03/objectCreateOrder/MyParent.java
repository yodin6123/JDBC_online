package test03.objectCreateOrder;

public class MyParent {

	int age = 20; // field 생성이 제일 먼저임.
	
	public MyParent() {
		System.out.println("~~~ 2. 부모클래스 MyParent 의 default 생성자  MyParent() 실행됨 ~~~ \n");
		age = 30;
	}
	
}
