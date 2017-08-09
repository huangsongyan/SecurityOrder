# 一个基于TOTP的密码二次认证工具

### TOTP

基于时间的加密算法
应用范围：对安全性要求比较高的场合，可以进行密码的二次认证，例如：游戏，银行等业务。

### 代码分析
加密工具类主要参考[google-authenticator](https://github.com/google/google-authenticator-android/ "google-authenticator")
加密原理：使用HMAC算法，利用哈希算法，以一个密钥和一个消息为输入，生成一个消息摘要作为输出。
这里的密钥参数:base32生成，消息参数：时间戳(秒单位)/时间间隔(TimeStep)  (tips: 例如 TimeStep=30 30内生成密码一样，就是误差30s)

###关键代码

### 生成随机密码
```
    /*PasscodeGenerator.java*/

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
 
```

### 获取当前时间所处的时间段值
```
      /**
      * 获取当前时间所处的时间段值  
      *
      * @param time
      * @return
      */
     public static long getValueAtTime(long time) {
         long timeSinceStartTime = time - mStartTime;
         if (timeSinceStartTime >= 0) {
             return timeSinceStartTime / mTimeStep;
         } else {
             return (timeSinceStartTime - (mTimeStep - 1)) / mTimeStep;
         }
     }
```

### long to byte[] 算法
```
   /**
   *    PasscodeGenerator.java
   *    long to byte[] 算法
   */
   private byte[] long2bytes(long state)
            throws GeneralSecurityException {
        byte[] value = ByteBuffer.allocate(8).putLong(state).array();
        return value;
    }
```

### byte[] to int 算法
```
    /**
     *    PasscodeGenerator.java
     *    byte[] to int 算法
     */
    
    private int bytes2int(byte[] hash){
    
        int offset = hash[hash.length - 1] & 0xF;
        int truncatedHash = hashToInt(hash, offset) & 0x7FFFFFFF;
        return truncatedHash;
    }
       
```

### 不足n位补0处理
```
   //不足n位补0处理
       private String padOutput(int value) {
           String result = Integer.toString(value);
           //根据判断result补0
           for (int i = result.length(); i < PASS_CODE_LENGTH; i++) {
               result = "0" + result;
           }
           return result;
      }
```
 



### 运行效果图
![live](gif/live.gif "live")