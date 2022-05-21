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
import static org.junit.Assert.assertTrue;

/**
 * This test class is responsible for testing {@link KeyProvider} functionality
 *
 * @author Mohammadreza-Torkaman
 * @version 1.0
 * @since 1.0
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

      firstKeyProvider = KeyProvider.provide(appContext, ALIAS_1);
      secondKeyProvider = KeyProvider.provide(appContext, ALIAS_2);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Test
  public void isNotNull_KeyProvider() {
    assertNotNull(firstKeyProvider);
    assertNotNull(firstKeyProvider.getKey());
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
      final String testVariable = "this is a test";
      String firstEncrypted = firstKeyProvider.encrypt(testVariable);
      String secondEncrypted = firstKeyProvider.encrypt(testVariable);
      assertNotEquals(firstEncrypted, secondEncrypted);
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void canEncrypt_keyProviders() {
    try {
      final String testVariable = "this is a test";
      String firstEncrypted = firstKeyProvider.encrypt(testVariable);
      byte[] firstBytesEncrypted = firstKeyProvider.encrypt(testVariable.getBytes());
      String secondEncrypted = secondKeyProvider.encrypt(testVariable);
      byte[] secondBytesEncrypted = secondKeyProvider.encrypt(testVariable.getBytes());
      assertNotNull(firstEncrypted);
      assertNotNull(secondEncrypted);
      assertNotEquals(0, firstBytesEncrypted.length);
      assertNotEquals(0, secondBytesEncrypted.length);
      assertFalse(firstEncrypted.isEmpty());
      assertFalse(secondEncrypted.isEmpty());

    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void canDecrypt_keyProviders() {
    try {
      final String testVariable = "this is a test";

      String firstEncrypted = firstKeyProvider.encrypt(testVariable);
      String firstDecrypted = firstKeyProvider.decrypt(firstEncrypted);

      byte[] firstEncryptedBytes = firstKeyProvider.encrypt(testVariable.getBytes());
      byte[] firstDecryptBytes = firstKeyProvider.decrypt(firstEncryptedBytes);

      String secondEncrypted = secondKeyProvider.encrypt(testVariable);
      String secondDecrypted = secondKeyProvider.decrypt(secondEncrypted);

      byte[] secondEncryptedBytes = secondKeyProvider.encrypt(testVariable.getBytes());
      byte[] secondDecryptedBytes = secondKeyProvider.decrypt(secondEncryptedBytes);

      assertEquals(testVariable, firstDecrypted);
      assertEquals(testVariable, secondDecrypted);
      assertNotEquals(0, firstDecryptBytes.length);
      assertNotEquals(0, secondDecryptedBytes.length);

    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void canRemove_keyProviders() {
    Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
    final String testAlias = "CAN_REMOVE_TEST_1";
    final String notExistAlias = "CAN_REMOVE_TEST_2";
    try {
      KeyProvider.provide(appContext, testAlias);
      boolean testAliasRemoved1 = KeyProvider.removeKey(appContext, testAlias);
      KeyProvider instance = KeyProvider.provide(appContext, testAlias);
      boolean testAliasRemoved2 = KeyProvider.removeKey(appContext, instance);
      boolean notExistAliasRemoved = KeyProvider.removeKey(appContext, notExistAlias);
      assertTrue(testAliasRemoved1);
      assertTrue(testAliasRemoved2);
      assertTrue(notExistAliasRemoved);
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void keyExists_keyProviders() {
    Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
    String existsCheckAlias = "EXIST_KEY";
    String notExistsCheckAlias = "NOT_EXIST_KEY";
    try {
      KeyProvider.provide(context, existsCheckAlias);
      boolean exists = KeyProvider.keyExists(existsCheckAlias);
      boolean notExists = KeyProvider.keyExists(notExistsCheckAlias);
      assertTrue(exists);
      assertFalse(notExists);
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail(e.getMessage());
    }

  }
}