package com.mrtr.aslib;

import com.mrtr.aslib.core.encoders.Base58;

import org.junit.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class Base58UnitTest {
  @Test
  public void encodeBase58() {
    assertEquals("vTaMKLSeLWR3", Base58.encode("Hi there."));
    assertTrue(Base58.encode("").isEmpty());
  }

  @Test
  public void decodeBase58() {
    assertEquals(
      "Hi there.",
      new String(Base58.decode("vTaMKLSeLWR3"), StandardCharsets.UTF_8)
    );
    assertEquals(0, Base58.decode("").length);
  }
}