package org.yzh.jt808.codec.local;

import java.util.LinkedList;

public class GeneCheckCode {
    public static void main(String[] args) {
//        String hexStr = "007d01002a018276468888007d01000000014850204d414b45522050524f424f4f4b7a68616e36362d616d64204137014e422038383838388f";
        String hexStr = "0100002e064762924825000300000000484f4f5000bfb5b4ef562d31000000000000000000000000000000015a0d5dff02bba644503939393800";
        LinkedList<String> byteValues = new LinkedList<>();
        for (int i = 0; i < hexStr.length(); i++) {
            if (i % 2 == 0) {
                //防越界&保留最高位
                if (i + 2 > hexStr.length()) {
                    byteValues.add(hexStr.substring(i));
                    break;
                }
                byteValues.add(hexStr.substring(i, i + 2));
            }
        }

        int result = Integer.parseInt(byteValues.get(0), 16);
        for (int i = 1; i < byteValues.size(); i++) {
            int tmp = Integer.parseInt(byteValues.get(i), 16);
            result = result ^ tmp;
        }
        System.out.println(Integer.toString(result, 16));
    }
}
