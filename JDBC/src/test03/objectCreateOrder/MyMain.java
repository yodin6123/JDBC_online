package test03.objectCreateOrder;

public class MyMain {

	public static void main(String[] args) {
		
		MyChild mc = new MyChild();
		
		System.out.println("주소 : " + MyChild.address);
		System.out.println("나이 : " + mc.age);
		System.out.println("성명 : " + mc.name);
		
		System.out.println("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
		
		MyChild mc2 = new MyChild();
		
	}

}
