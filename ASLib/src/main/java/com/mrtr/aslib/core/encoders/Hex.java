package com.mrtr.aslib.core.encoders;

import java.nio.charset.Charset;

public class Hex {

  public static String toHexString(String input) {
    return new String(org.spongycastle.util.encoders.Hex
      .encode(input.getBytes(Charset.forName("UTF-8"))), Charset.forName("UTF-8"))
      .toUpperCase();
  }

  public static String toHexString(byte[] input) {
    return new String(org.spongycastle.util.encoders.Hex
      .encode(input), Charset.forName("UTF-8"))
      .toUpperCase();
  }

  public static String fromHexString(String input) {
    return new String(org.spongycastle.util.encoders.Hex.decode(input), Charset.forName("UTF-8"));
  }

  public static String fromHexBytes(byte[] input) {
    return new String(org.spongycastle.util.encoders.Hex.decode(input), Charset.forName("UTF-8"));
  }

}
