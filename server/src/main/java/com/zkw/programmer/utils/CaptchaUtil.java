package com.zkw.programmer.utils;
import java.util.Random;

/**
 * 验证码生成器
 */
public class CaptchaUtil {

    /**
     * 验证码来源
     */
    final private char[] code = {
            '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
            'k', 'm', 'n', 'p', 'q', 'r', 's', 't', 'u', 'v',
            'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F',
            'G', 'H', 'J', 'K', 'L', 'M', 'N', 'P', 'Q', 'R',
            'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
    };

    /**
     * 验证码长度
     * 默认4个字符
     */
    private int vcodeLen = 4;

    public CaptchaUtil(){}


    /**
     * 生成验证码
     * @return 验证码
     */
    public String generatorVCode(){
        int len = code.length;
        Random ran = new Random();
        StringBuffer sb = new StringBuffer();
        for(int i = 0;i < vcodeLen;i++){
            int index = ran.nextInt(len);
            sb.append(code[index]);
        }
        return sb.toString();
    }

}
