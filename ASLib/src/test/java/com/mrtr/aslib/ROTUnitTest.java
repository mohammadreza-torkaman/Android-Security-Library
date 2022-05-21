package com.mrtr.aslib;

import com.mrtr.aslib.core.encoders.ROT;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ROTUnitTest {

  @Test
  public void rotN() {
    assertEquals(ROT.Companion.encode("HELLO1", 11, false), "SPWWZ1");

    assertEquals("asdfjoiwejflksdalkfjsdlkfj", ROT.Companion.decode("meprvauiqvrxwepmxwrvepxwrv", 12, false));

    assertEquals(ROT.Companion.encode("HELLO", 11, false), "SPWWZ");
  }
}
