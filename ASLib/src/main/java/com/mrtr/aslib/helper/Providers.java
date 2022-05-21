package com.mrtr.aslib.helper;

import org.spongycastle.jce.provider.BouncyCastleProvider;

import java.security.Provider;

public class Providers {

  public static Provider getBouncyCastleProvider(){
  return new BouncyCastleProvider();
  }
}
