package com.scut.jsj.conf;

import com.scut.jsj.conf.Bean;
import com.scut.jsj.conf.resolve.ConfigurationManager;
import org.junit.Test;

import java.util.Map;

public class TestConfigManager {

    @Test
    public void test() {
        String path = "/applicationcontext.xml";

        Map<String, Bean> map = ConfigurationManager.getBeanConfig(path);

        for (Map.Entry<String, Bean> entry : map.entrySet()) {
            System.out.println(entry.getKey());
            System.out.println(entry.getValue().toString());
        }
    }
}
