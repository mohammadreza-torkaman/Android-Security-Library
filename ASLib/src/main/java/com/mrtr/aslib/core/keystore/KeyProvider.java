package com.mrtr.aslib.core.keystore;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.security.KeyPairGeneratorSpec;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;

import com.mrtr.aslib.helper.Algorithms;
import com.mrtr.aslib.helper.Modes;
import com.mrtr.aslib.helper.Providers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.UnrecoverableEntryException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.security.auth.x500.X500Principal;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

public class KeyProvider {

  private static final String SHARED_PREFERENCE_NAME_PREFIX = "KEY_STORE_";
  private static final String AES_KEY_ALIAS_PREFIX = "AES_ALIAS_";
  private static final String RSA_KEY_ALIAS_PREFIX = "RSA_ALIAS_";
  private static final String ENCRYPTED_KEY = "EN_KEY";

  private static final String ANDROID_KEY_STORE = "AndroidKeyStore";
  private static final String ANDROID_OPEN_SSL = "AndroidOpenSSL";


  private static final int AUTH_TAG_LEN = 128;

  private final String AESAlias;
  private final String RSAAlias;
  private final String sharedPreferenceFileName;
  private final KeyStore keyStore;
  private final Key key;

  private final SecureRandom secureRandom = new SecureRandom();


  @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
  public static KeyProvider getInstance(@NonNull Context context, @NonNull String alias) throws Exception {
    return new KeyProvider(context, alias);
  }

  @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
  private KeyProvider(Context context, String alias)
    throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException, NoSuchProviderException,
    InvalidAlgorithmParameterException, UnrecoverableEntryException, NoSuchPaddingException, InvalidKeyException {
    AESAlias = AES_KEY_ALIAS_PREFIX + alias.toUpperCase(Locale.ROOT);
    RSAAlias = RSA_KEY_ALIAS_PREFIX + alias.toUpperCase(Locale.ROOT);
    sharedPreferenceFileName = SHARED_PREFERENCE_NAME_PREFIX + alias.toUpperCase(Locale.ROOT);

    keyStore = KeyStore.getInstance(ANDROID_KEY_STORE);
    keyStore.load(null);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      key = generateKeyForAndroidAboveM();
    } else {
      key = generateKeyForAndroidBelowM(context);
    }
  }


  @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
  private Key generateKeyForAndroidBelowM(Context context)
    throws KeyStoreException, NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException,
    NoSuchPaddingException, InvalidKeyException, UnrecoverableEntryException, IOException {


    //this is for rtl issue date time for creating key
    //we change locale to english and then restore it
    // Set English locale as default (workaround)
    Locale initialLocale = Locale.getDefault();
    setLocale(context, Locale.ENGLISH);

    // Generate the RSA key pairs
    if (!keyStore.containsAlias(RSAAlias)) {
      // Generate an RSA key pair for encrypting AES key
      Calendar start = Calendar.getInstance();
      Calendar end = Calendar.getInstance();
      end.add(Calendar.YEAR, 30);
      X500Principal principal = new X500Principal("CN=" + RSAAlias);
      KeyPairGeneratorSpec spec = new KeyPairGeneratorSpec.Builder(context)
        .setAlias(RSAAlias)
        .setSubject(principal)
        .setSerialNumber(BigInteger.TEN)
        .setStartDate(start.getTime())
        .setEndDate(end.getTime())
        .build();
      KeyPairGenerator kpg = KeyPairGenerator.getInstance(Algorithms.RSA, ANDROID_KEY_STORE);
      kpg.initialize(spec);
      kpg.generateKeyPair();
      //generating a random AES Key and store it using an RSA generated key
      generateRSAProtectedSecretKey(context);

    }
    Key temp = getRSAProtectedSecretKey(context);
    setLocale(context, initialLocale);
    return temp;
  }

  @RequiresApi(api = Build.VERSION_CODES.M)
  private Key generateKeyForAndroidAboveM()
    throws KeyStoreException, NoSuchAlgorithmException, NoSuchProviderException,
    InvalidAlgorithmParameterException, UnrecoverableKeyException {

    if (!keyStore.containsAlias(AESAlias)) {

      KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE);

      KeyGenParameterSpec parameterSpec =
        new KeyGenParameterSpec.Builder(AESAlias, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
          .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
          .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
          .setRandomizedEncryptionRequired(false)
          .build();

      keyGenerator.init(parameterSpec);
      return keyGenerator.generateKey();

    }
    return keyStore.getKey(AESAlias, null);
  }

  public void remove() throws KeyStoreException {
    keyStore.deleteEntry(RSAAlias);
    keyStore.deleteEntry(AESAlias);
  }

  public String encrypt(String plainText) throws NoSuchPaddingException, NoSuchAlgorithmException,
    InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, IOException {
    byte[] iv = new byte[12];
    secureRandom.nextBytes(iv);
    Cipher encryptionCipher;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      encryptionCipher = Cipher.getInstance(Modes.AES_GCM_NO_PADDING);
      encryptionCipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(AUTH_TAG_LEN, iv));
      try (
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream)
      ) {
        dataOutputStream.write(iv);
        byte[] bytes = encryptionCipher.doFinal(plainText.getBytes());
        dataOutputStream.write(bytes);
        return Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
      }
    } else {
      encryptionCipher = Cipher.getInstance(Modes.AES_GCM_NO_PADDING, Providers.getBouncyCastleProvider());
      IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
      encryptionCipher.init(Cipher.ENCRYPT_MODE, key, ivParameterSpec);
      try (
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream)
      ) {
        dataOutputStream.write(iv);
        byte[] encodedBytes = encryptionCipher.doFinal(plainText.getBytes());
        dataOutputStream.write(encodedBytes);
        return Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
      }
    }
  }

  public byte[] encrypt(byte[] plainText) throws NoSuchPaddingException, NoSuchAlgorithmException,
    InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, IOException {
    byte[] iv = new byte[12];
    secureRandom.nextBytes(iv);
    Cipher encryptionCipher;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      encryptionCipher = Cipher.getInstance(Modes.AES_GCM_NO_PADDING);
      encryptionCipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(AUTH_TAG_LEN, iv));
      try (
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream)
      ) {
        dataOutputStream.write(iv);
        byte[] bytes = encryptionCipher.doFinal(plainText);
        dataOutputStream.write(bytes);
        return byteArrayOutputStream.toByteArray();
      }
    } else {
      encryptionCipher = Cipher.getInstance(Modes.AES_GCM_NO_PADDING, Providers.getBouncyCastleProvider());
      IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
      encryptionCipher.init(Cipher.ENCRYPT_MODE, key, ivParameterSpec);
      try (
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream)
      ) {
        dataOutputStream.write(iv);
        byte[] encodedBytes = encryptionCipher.doFinal(plainText);
        dataOutputStream.write(encodedBytes);
        return byteArrayOutputStream.toByteArray();
      }
    }
  }

  public String decrypt(String cipherText) throws NoSuchPaddingException, NoSuchAlgorithmException,
    InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
    byte[] decode = Base64.decode(cipherText, Base64.DEFAULT);
    byte[] iv = Arrays.copyOfRange(decode, 0, 12);
    byte[] cipher = Arrays.copyOfRange(decode, 12, decode.length);

    Cipher decryptionCipher;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      decryptionCipher = Cipher.getInstance(Modes.AES_GCM_NO_PADDING);
      decryptionCipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(AUTH_TAG_LEN, iv));
    } else {
      decryptionCipher = Cipher.getInstance(Modes.AES_GCM_NO_PADDING, Providers.getBouncyCastleProvider());
      decryptionCipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
    }
    return new String(decryptionCipher.doFinal(cipher));
  }

  public byte[] decrypt(byte[] cipherText) throws NoSuchPaddingException, NoSuchAlgorithmException,
    InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
    byte[] iv = Arrays.copyOfRange(cipherText, 0, 12);
    byte[] cipher = Arrays.copyOfRange(cipherText, 12, cipherText.length);

    Cipher decryptionCipher;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      decryptionCipher = Cipher.getInstance(Modes.AES_GCM_NO_PADDING);
      decryptionCipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(AUTH_TAG_LEN, iv));
    } else {
      decryptionCipher = Cipher.getInstance(Modes.AES_GCM_NO_PADDING, Providers.getBouncyCastleProvider());
      decryptionCipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
    }
    return decryptionCipher.doFinal(cipher);
  }

  private Key getRSAProtectedSecretKey(Context context)
    throws NoSuchPaddingException, InvalidKeyException, NoSuchAlgorithmException,
    KeyStoreException, NoSuchProviderException, UnrecoverableEntryException, IOException {
    SharedPreferences pref = context.getSharedPreferences(sharedPreferenceFileName, Context.MODE_PRIVATE);
    String encryptedKeyB64 = pref.getString(ENCRYPTED_KEY, null);// need to check null, omitted here
    byte[] encryptedKey = Base64.decode(encryptedKeyB64, Base64.DEFAULT);
    byte[] key = rsaDecrypt(encryptedKey);
    return new SecretKeySpec(key, Algorithms.AES);
  }

  private void generateRSAProtectedSecretKey(Context context) throws NoSuchPaddingException, NoSuchAlgorithmException,
    NoSuchProviderException, KeyStoreException, UnrecoverableEntryException, InvalidKeyException, IOException {
    SharedPreferences pref = context.getSharedPreferences(SHARED_PREFERENCE_NAME_PREFIX, Context.MODE_PRIVATE);
    String encryptedKeyB64 = pref.getString(ENCRYPTED_KEY, null);
    if (encryptedKeyB64 == null) {
      byte[] key = new byte[16];
      SecureRandom secureRandom = new SecureRandom();
      secureRandom.nextBytes(key);
      byte[] encryptedKey = rsaEncrypt(key);
      encryptedKeyB64 = Base64.encodeToString(encryptedKey, Base64.DEFAULT);
      SharedPreferences.Editor edit = pref.edit();
      edit.putString(ENCRYPTED_KEY, encryptedKeyB64);
      edit.apply();
    }
  }

  private byte[] rsaEncrypt(byte[] secret) throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException, KeyStoreException, UnrecoverableEntryException, InvalidKeyException, IOException {
    KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(RSA_KEY_ALIAS_PREFIX, null);
    Cipher encryptCipher = Cipher.getInstance(Modes.RSA_ECB_PKCS1_PADDING, ANDROID_OPEN_SSL);
    encryptCipher.init(Cipher.ENCRYPT_MODE, privateKeyEntry.getCertificate().getPublicKey());
    try (
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, encryptCipher)
    ) {
      cipherOutputStream.write(secret);
      cipherOutputStream.close();
      return outputStream.toByteArray();
    }
  }

  private byte[] rsaDecrypt(byte[] encrypted) throws UnrecoverableEntryException, NoSuchAlgorithmException, KeyStoreException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, IOException {
    KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(RSA_KEY_ALIAS_PREFIX, null);
    Cipher decryptCipher = Cipher.getInstance(Modes.RSA_ECB_PKCS1_PADDING, ANDROID_OPEN_SSL);
    decryptCipher.init(Cipher.DECRYPT_MODE, privateKeyEntry.getPrivateKey());
    try (
      CipherInputStream cipherInputStream = new CipherInputStream(new ByteArrayInputStream(encrypted), decryptCipher)
    ) {
      ArrayList<Byte> values = new ArrayList<>();
      int nextByte;
      while ((nextByte = cipherInputStream.read()) != -1) {
        values.add((byte) nextByte);
      }
      byte[] bytes = new byte[values.size()];
      for (int i = 0; i < bytes.length; i++) {
        bytes[i] = values.get(i);
      }
      return bytes;
    }
  }

  /**
   * Sets default locale.
   */
  private void setLocale(Context context, Locale locale) {
    Locale.setDefault(locale);
    Resources resources = context.getResources();
    Configuration config = resources.getConfiguration();
    config.locale = locale;
    resources.updateConfiguration(config, resources.getDisplayMetrics());
  }

  public String getSharedPreferenceFileName() {
    return sharedPreferenceFileName;
  }

  public String getRSAAlias() {
    return RSAAlias;
  }

  public String getAESAlias() {
    return AESAlias;
  }

  public Key getKey() {
    return key;
  }

}