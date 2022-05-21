package com.mrtr.aslib;

import android.content.Context;

import com.mrtr.aslib.core.encoders.Base58;
import com.mrtr.aslib.core.keystore.KeyProvider;

import org.junit.Test;

import androidx.test.platform.app.InstrumentationRegistry;

import static org.junit.Assert.*;


public class Base58UnitTest {
  @Test
  public void encodeBase58() {
    assertEquals("vTaMKLSeLWR3", Base58.encode("Hi there."));
    assertTrue(Base58.encode("").isEmpty());
  }

  @Test
  public void decodeBase58() {
    assertEquals("Hi there.", Base58.encode("vTaMKLSeLWR3"));
    assertEquals(0, Base58.decode("").length);
  }
}