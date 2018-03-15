package com.scut.jsj.util;

import com.scut.jsj.bean.Pet;
import com.scut.jsj.bean.User;
import org.junit.Test;

public class TestBeanUtil {

    @Test
    public void testSetField() {
        Object object = new User();
        String feildName = "name";
        String feildValue = "猪猪侠";
        BeanUtil.setField(object, feildName, feildValue);
        User user = (User) object;
        System.out.println(user.toString());
    }

    @Test
    public void testSetField2() {
        Object object = new User();
        String feildName = "pet";
        Object feildValue = new Pet();
        BeanUtil.setField(object, feildName, feildValue);
        User user = (User) object;
        System.out.println(user.toString());
    }


}

