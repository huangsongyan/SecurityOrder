/*
 * Copyright 2009 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tk.securityorder.totp;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class PasscodeGenerator {

    //加密秘钥
    private final static String AUTH_KEY = "LFLFMU2SGVCUIUCZKBMEKRKLIQ";

    /**
     * 默认密码长度
     */
    private static final int PASS_CODE_LENGTH = 8;

    private static PasscodeGenerator mInstance;

    public static PasscodeGenerator getInstance() {
        if (mInstance == null) {
            mInstance = new PasscodeGenerator();
        }
        return mInstance;
    }

    public static String generateTotpNum() {
        return getInstance().generateResponseCode();
    }

    //不足n位补0处理
    private String padOutput(int value) {
        String result = Integer.toString(value);
        //根据判断result补0
        for (int i = result.length(); i < PASS_CODE_LENGTH; i++) {
            result = "0" + result;
        }
        return result;
    }

    /**
     * long to byte[] 算法
     */
    private byte[] long2bytes(long state)
            throws GeneralSecurityException {
        byte[] value = ByteBuffer.allocate(8).putLong(state).array();
        return value;
    }

    /**
     * byte[] to int 算法
     */

    private int bytes2int(byte[] hash) {
        int offset = hash[hash.length - 1] & 0xF;
        int truncatedHash = hashToInt(hash, offset) & 0x7FFFFFFF;
        return truncatedHash;
    }

    /**
     * hash to int
     */
    private int hashToInt(byte[] bytes, int start) {
        DataInput input = new DataInputStream(
                new ByteArrayInputStream(bytes, start, bytes.length - start));
        int val;
        try {
            val = input.readInt();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        return val;
    }

    private String generateResponseCode() {
        try {
            byte[] KEYBYTES = Base32String.decode(AUTH_KEY);
            Mac mac = Mac.getInstance("HMACSHA1");
            mac.init(new SecretKeySpec(KEYBYTES, ""));


            //执行加密 doFinal 传的是加密源，类型是byte[] 所以我们需要把 当前时间段的值转为byte[]
            long time = TimeUtils.getValueAtTime();
            byte[] data = long2bytes(time);

            //加密后的类型是byte[],由于我们要的数字密码，这个用了个转换算法 byte[]转int算法
            byte[] hash = mac.doFinal(data);

            //这个转成int，不是我们要的n位密码，这值长度我们未知，不符合我们密码要求，这里用了个取n位取余法，(例如计算的结果：123456789，假设我们要的是8位密码，就是 123456789%10000000 =23456789)
            int truncatedHash = bytes2int(hash);

            //对truncatedHash进行取余得到数字,可能小于n位. 所以我们对不足n位的进行补0处理
            int pinValue = truncatedHash % Double.valueOf(Math.pow(10, PASS_CODE_LENGTH)).intValue();

            return padOutput(pinValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


}
