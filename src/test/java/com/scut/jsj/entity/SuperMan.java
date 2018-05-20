package com.scut.jsj.entity;

import com.scut.jsj.beans.factory.annotation.Autowired;
import com.scut.jsj.beans.factory.annotation.Component;
import com.scut.jsj.beans.factory.annotation.Qualifier;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Component
public class SuperMan extends Teacher {

    @Autowired
    private Person person;

    @Qualifier("jsj")
    private String name;

    @Qualifier("20")
    private int age;
}
