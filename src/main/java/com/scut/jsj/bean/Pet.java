package com.scut.jsj.bean;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Pet {

    private String name;

    private String type;

    public void bark() {
        System.out.println("我是一只可爱的宠物...");
    }

}
