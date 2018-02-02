package com.yyl.annotation;

import java.lang.annotation.*;
import java.lang.reflect.Method;

public class DemoTest {

    public static void main(String[] args) throws Exception {
        Object obj = Class.forName("com.yyl.annotation.DemoTest$Student").newInstance();
        Method[] methods = obj.getClass().getDeclaredMethods();

        for (Method method : methods) {
            if (method.isAnnotationPresent(ValueBind.class)) {
                ValueBind valueBind = method.getAnnotation(ValueBind.class);

                ValueBind.FieldType type = valueBind.type();
                String value = valueBind.value();

                if (valueBind.type() == ValueBind.FieldType.INT) {
                    method.invoke(obj, Integer.parseInt(value));
                } else if (valueBind.type() == ValueBind.FieldType.STRING) {
                    method.invoke(obj, value);
                }
            }
        }

        System.out.println((Student) obj);
    }
    @Inherited
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface ValueBind {
        enum FieldType {
            STRING, INT
        }

        FieldType type();

        String value();

    }

    public static class Student {
        private String name;
        private int age;
        private String studentID;

        public Student() {
        }

        public String getName() {
            return name;
        }

        @ValueBind(type = ValueBind.FieldType.STRING, value = "wgc")
        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        @ValueBind(type = ValueBind.FieldType.INT, value = "22")
        public void setAge(int age) {
            this.age = age;
        }

        public String getStudentID() {
            return studentID;
        }

        @ValueBind(type = ValueBind.FieldType.STRING, value = "12")
        public void setStudentID(String studentID) {
            this.studentID = studentID;
        }

        @Override
        public String toString() {
            return "(" + getName() + "," + getAge() + "," + getStudentID() + ")";
        }
    }
}
