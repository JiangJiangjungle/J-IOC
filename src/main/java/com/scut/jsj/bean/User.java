package com.scut.jsj.bean;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class User {

    private int age;

    private String name;

    private Pet pet;

    public void speak() {
        System.out.println("我是一只尊贵的用户...");
    }

}
