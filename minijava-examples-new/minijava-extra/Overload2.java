class Overload2 {

    public static void main(String[] args){ }

}


class A {
    int foo(int x) { }
}

class B extends A {
    boolean foo(int x) { } // illegal
}
