class DerivedCall {
	public static void main(String[] x) {
		int i;
		B b;
		F f;
		f = new F();
		b = new B();
		i = f.foo(b);
		System.out.println(i);
	}
}

class A {
	int q;
}

class B extends A {
	int b;
    A a;
}

class F extends A {
	public boolean foo(A a) {
		int[] m;
		F b;

		boolean c;

		return f.foo();
	}
}
