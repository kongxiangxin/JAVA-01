package study.java1.week07.readwrite.v1.datasource;

public class DataSourceHolder {
    private static ThreadLocal<Object> current = new ThreadLocal<>();
    private static ThreadLocal<Object> original = new ThreadLocal<>();

    public static void put(Object dataSource){
        original.set(get());
        current.set(dataSource);
    }

    public static Object get(){
        return current.get();
    }

    /**
     * 恢复
     */
    public static void restore(){
        put(original.get());
    }
}
