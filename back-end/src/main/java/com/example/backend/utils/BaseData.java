package com.example.backend.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Consumer;

public interface BaseData {
    /*
    首先，这个方法调用了 this.asbject(clazz)，这个方法会将当前对象转换为一个新的对象，
    新对象的类型是由 clazz 参数指定的。然后，这个新对象被赋值给了变量 v。
    接着，这个方法调用了 consumer.accept(v)。Consumer 是一个函数式接口，它的 accept 方法接受一个参数并且没有返回值。
    在这里，accept 方法对新对象 v 进行了一些操作。这个操作是什么，完全取决于你如何定义 Consumer 对象。
    最后，这个方法返回了新对象 v。
    所以，这个方法的作用就是将当前对象转换为一个新的对象，并且在这个过程中，你可以对新对象进行一些自定义的操作。
     */
    default <V> V asViewObject(Class<V> clazz, Consumer<V> consumer) {
        V v = this.asViewObject(clazz);
        consumer.accept(v);
        return v;
    }



    /*
    通过反射
    获取目标对象的所有字段，创建对象
    遍历这些字段，通过convert方法，获取源对象中的对应字段(如果没有字段会抛出异常，但忽略)，并赋值给目标字段
     */
    default <V> V asViewObject(Class<V> clazz) {
        try {
            //获取字段 构造器
            Field[] fields = clazz.getDeclaredFields();
            Constructor<V> constructor = clazz.getConstructor();
            V v = constructor.newInstance();
            for (Field field : fields) {
                convert(field, v);
            }
            return v;
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                 IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void convert(Field field, Object o) {
        try {
            //this这里指的是实现了BaseData的对象，也就是源对象
            Field source = this.getClass().getDeclaredField(field.getName());
            field.setAccessible(true);
            source.setAccessible(true);
            //设置Object o 的值
            field.set(o, source.get(this));
        } catch (NoSuchFieldException | IllegalAccessException e) {
//            throw new RuntimeException(e);
        }
    }
}
