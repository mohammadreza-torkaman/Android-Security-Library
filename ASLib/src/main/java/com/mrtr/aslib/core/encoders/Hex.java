package com.mrtr.aslib.core.encoders;

import java.nio.charset.StandardCharsets;

public class Hex {
  public static String toHex(String input){
    return new String(org.spongycastle.util.encoders.Hex.encode(input.getBytes(StandardCharsets.UTF_8)),StandardCharsets.UTF_8).toUpperCase();
  }
  public static String toHex(byte[] input){
    return new String(org.spongycastle.util.encoders.Hex.encode(input),StandardCharsets.UTF_8).toUpperCase();
  }
  public static String fromHex(String input){
    return new String(org.spongycastle.util.encoders.Hex.decode(input), StandardCharsets.UTF_8);
  }
  public static String fromHex(byte[] input){
    return new String(org.spongycastle.util.encoders.Hex.decode(input), StandardCharsets.UTF_8);
  }
}
