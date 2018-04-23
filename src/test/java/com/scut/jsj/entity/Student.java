package com.scut.jsj.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Student {

    Person person;
    int age;
    String name;
}
