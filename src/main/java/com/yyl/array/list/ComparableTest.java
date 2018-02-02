package com.yyl.array.list;

/**
 * 在 Java 中我们常使用 Comparable 接口来实现排序，其中 compareTo 是实现该接口方法。我们知道 compareTo 返回 0 表示两个对象相等，返回正数表示大于，返回负数表示小于
 */
public class ComparableTest {










    public class Student implements Comparable<Student>{
        private String id;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        private String name;
        private int age;

        public Student(String id,String name,int age){
            this.id = id;
            this.name = name;
            this.age = age;
        }

        public boolean equals(Object obj){
            if(obj == null){
                return false;
            }

            if(this == obj){
                return true;
            }

            if(obj.getClass() != this.getClass()){
                return false;
            }

            Student student = (Student)obj;
            if(!student.getName().equals(getName())){
                return false;
            }

            return true;
        }
        public int compareTo(Student student) {
            return this.age - student.age;
        }
    }
}
