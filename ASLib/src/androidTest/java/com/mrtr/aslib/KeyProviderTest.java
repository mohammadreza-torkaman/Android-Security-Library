package com.mrtr.aslib;

import android.content.Context;

import com.mrtr.aslib.core.keystore.KeyProvider;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class KeyProviderTest {

  private static final String ALIAS_1 = "alias_1";
  private static final String ALIAS_2 = "alias_2";
  KeyProvider firstKeyProvider;
  KeyProvider secondKeyProvider;

  @Before
  public void initializeKeyProviders() {
    Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
    try {

      firstKeyProvider = KeyProvider.getInstance(appContext, ALIAS_1);
      secondKeyProvider = KeyProvider.getInstance(appContext, ALIAS_2);
      secondKeyProvider.remove();
      secondKeyProvider = KeyProvider.getInstance(appContext, ALIAS_2);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Test
  public void isNull_firstKeyProvider() {
    assertNotNull(firstKeyProvider);
    assertNotNull(firstKeyProvider.getKey());
  }

  @Test
  public void isNull_secondKeyProvider() {
    assertNotNull(secondKeyProvider);
    assertNotNull(secondKeyProvider.getKey());
  }

  @Test
  public void isDifferent_sharedPreferences() {
    assertNotEquals(firstKeyProvider.getSharedPreferenceFileName(), secondKeyProvider.getSharedPreferenceFileName());
  }

  @Test
  public void isRandom_encryptedData() {
    try {
      String firstEncrypted = firstKeyProvider.encrypt("this is a test");
      String secondEncrypted = firstKeyProvider.encrypt("this is a test");
      assertNotEquals(firstEncrypted, secondEncrypted);
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void canEncrypt() {
    try {
      String firstEncrypted = firstKeyProvider.encrypt("this is a test");
      String secondEncrypted = secondKeyProvider.encrypt("this is a test");
      assertNotNull(firstEncrypted);
      assertNotNull(secondEncrypted);
      assertFalse(firstEncrypted.isEmpty());
      assertFalse(secondEncrypted.isEmpty());
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void canDecrypt() {
    try {
      final String testVariable = "this is a test";
      String firstEncrypted = firstKeyProvider.encrypt(testVariable);
      String firstDecrypted = firstKeyProvider.decrypt(firstEncrypted);

      String secondEncrypted = secondKeyProvider.encrypt(testVariable);
      String secondDecrypted = secondKeyProvider.decrypt(secondEncrypted);

      assertEquals(testVariable, firstDecrypted);
      assertEquals(testVariable, secondDecrypted);

    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail(e.getMessage());
    }
  }
}