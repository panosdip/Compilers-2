class DerivedCall {
	public static void main(String[] x) {
		System.out.println(i);
	}
}

class A {
	public int add(int a){
		return 1;
	}
	
}

class F extends A{
	public boolean foo(int a) {
		int m;
		F b;

		int c;

		return b.add();
	}
}
