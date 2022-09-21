package com.song.common;

import lombok.Data;
import java.util.HashMap;
import java.util.Map;

/**
 * 通用返回值类型,服务器响应的数据最终都会封装成此对象
 * @param <T>
 */
@Data
public class R<T> {

    private Integer code; //编码：1成功，0和其它数字为失败

    private String msg; //错误信息

    private T data; //数据

    private Map map = new HashMap(); //动态数据



    //
    /*
    public static <Employee> R<Employee> success(Employee object){}

    扩展：泛型方法的定于和使用  具体见：https://blog.csdn.net/qq_39505245/article/details/120925331
        1.定义
            泛型方法 是在调用方法的时候指明泛型的具体类型。
            【泛型方法 能够使方法独立于类的处理指定的类型。】
        2.语法：
            修饰符 <T,E,…> 返回值类型 方法名（形参列表）{
                Java代码
            }

            修饰符与返回值类型中间的 泛型标识符 <T,E,…>,是 泛型方法的标志，只有这种格式声明的方法才是泛型方法。
            泛型方法声明时的 泛型标识符 <T,E,…> 表示在方法可以使用声明的泛型类型。
            与泛型类相同，泛型标识符可以是任意类型,常见的如T,E,K,V 等。
            泛型方法可以声明为 static 的，并且与普通的静态方法是一样的。

     */
    public static <T> R<T> success(T object) {
        R<T> r = new R<T>();
        r.data = object;
        r.code = 1;
        return r;
    }

    //不成功返回的值
    public static <T> R<T> error(String msg) {
        R r = new R();
        r.msg = msg;
        r.code = 0;
        return r;
    }


    public R<T> add(String key, Object value) {
        this.map.put(key, value);
        return this;
    }

}
