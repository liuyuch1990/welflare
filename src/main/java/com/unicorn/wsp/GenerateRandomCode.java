package com.unicorn.wsp;

import java.util.HashSet;
import java.util.UUID;

public class GenerateRandomCode {

    public static void main(String[] args) {
        int size = 1200;
        int length = 6;
        HashSet<String> set = new HashSet<>();
        for(int i=0; size>set.size(); i++){
            String code = UUID.randomUUID().toString().replace("-", "").substring(0, length);
            System.out.println(code);
            set.add(code);
        }
        System.out.println("长度-" + set.size());
    }
}
