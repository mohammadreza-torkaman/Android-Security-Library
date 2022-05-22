package com.mrtr.aslib;

import com.mrtr.aslib.core.encoders.ROT;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ROTUnitTest {

  @Test
  public void encode_rotN() {
    assertEquals(ROT.Companion.encode("Hello There 1", 5, false), "Mjqqt Ymjwj 1");
    assertEquals(ROT.Companion.encode("Hello There 1", 13, false), "Uryyb Gurer 1");
    assertEquals(ROT.Companion.encode("Hello There 1", 16, false), "Xubbe Jxuhu 1");
    assertEquals(ROT.Companion.encode("Hello There 1", 19, false), "Axeeh Maxkx 1");
    assertEquals(ROT.Companion.encode("Hello There 1", 23, false), "Ebiil Qebob 1");
    assertEquals(ROT.Companion.encode("Hello There 1", 9, true), "Qnuux Cqnan 0");
    assertEquals(ROT.Companion.encode("Hello There 1", 19, true), "Axeeh Maxkx 0");
  }

  @Test
  public void decode_rotN() {
    assertEquals(ROT.Companion.decode("Mjqqt Ymjwj 1", 5, false), "Hello There 1");
    assertEquals(ROT.Companion.decode("Uryyb Gurer 1", 13, false), "Hello There 1");
    assertEquals(ROT.Companion.decode("Xubbe Jxuhu 1", 16, false), "Hello There 1");
    assertEquals(ROT.Companion.decode("Axeeh Maxkx 1", 19, false), "Hello There 1");
    assertEquals(ROT.Companion.decode("Ebiil Qebob 1", 23, false), "Hello There 1");
    assertEquals(ROT.Companion.decode("Qnuux Cqnan 0", 9, true), "Hello There 1");
    assertEquals(ROT.Companion.decode("Axeeh Maxkx 0", 19, true), "Hello There 1");
  }

}
