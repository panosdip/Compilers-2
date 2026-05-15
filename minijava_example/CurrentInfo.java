public class CurrentInfo {
    ClassInfo currentClass;
    Method currentMethod;

    public CurrentInfo(ClassInfo c, Method m){
        currentClass = c;
        currentMethod = m;
    }
}