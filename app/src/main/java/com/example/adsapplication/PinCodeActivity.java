package com.example.adsapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.ArrayList;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;


public class PinCodeActivity extends AppCompatActivity implements View.OnClickListener{

    public static final String PBKDF2_DERIVATION_ALGORITHM = "PBKDF2WithHmacSHA1";
    private static final String CIPHER_ALGORITHM = "AES/CBC/PKCS7Padding";
//private static final String CIPHER_ALGORITHM = "AES/GCM/NoPadding";

    private static String DELIMITER = "]";
    private static final int PKCS5_SALT_LENGTH = 8;
    private static SecureRandom random = new SecureRandom();
    private static final int ITERATION_COUNT = 1000;
    private static final int KEY_LENGTH = 256;

    View view01,view02,view03,view04;
    Button btn01,btn02,btn03,btn04,btn05,btn06,btn07,btn08,btn09,btn00,btnback,btnclear;

    ArrayList<String> numberList = new ArrayList<>();
    String passCode = "";
    String num01,num02,num03,num04;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_code);
        initializeComponent();
    }

    private void initializeComponent() {

        view01 = findViewById(R.id.view01);
        view02 = findViewById(R.id.view02);
        view03 = findViewById(R.id.view03);
        view04 = findViewById(R.id.view04);

        btn01 = findViewById(R.id.btn1);
        btn02 = findViewById(R.id.btn2);
        btn03 = findViewById(R.id.btn3);
        btn04 = findViewById(R.id.btn4);
        btn05 = findViewById(R.id.btn5);
        btn06 = findViewById(R.id.btn6);
        btn07 = findViewById(R.id.btn7);
        btn08 = findViewById(R.id.btn8);
        btn09 = findViewById(R.id.btn9);
        btn00 = findViewById(R.id.btn0);
        btnback = findViewById(R.id.btnback);
        btnclear = findViewById(R.id.btnclear);

        btn01.setOnClickListener(this);
        btn02.setOnClickListener(this);
        btn03.setOnClickListener(this);
        btn04.setOnClickListener(this);
        btn05.setOnClickListener(this);
        btn06.setOnClickListener(this);
        btn07.setOnClickListener(this);
        btn08.setOnClickListener(this);
        btn09.setOnClickListener(this);
        btn00.setOnClickListener(this);
        btnback.setOnClickListener(this);
        btnclear.setOnClickListener(this);

    }


    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.btn1:
                numberList.add("1");
                passNumber(numberList);
                break;
            case R.id.btn2:
                numberList.add("2");
                passNumber(numberList);
                break;
            case R.id.btn3:
                numberList.add("3");
                passNumber(numberList);
                break;
            case R.id.btn4:
                numberList.add("4");
                passNumber(numberList);
                break;
            case R.id.btn5:
                numberList.add("5");
                passNumber(numberList);
                break;
            case R.id.btn6:
                numberList.add("6");
                passNumber(numberList);
                break;
            case R.id.btn7:
                numberList.add("7");
                passNumber(numberList);
                break;
            case R.id.btn8:
                numberList.add("8");
                passNumber(numberList);
                break;
            case R.id.btn9:
                numberList.add("9");
                passNumber(numberList);
                break;
            case R.id.btn0:
                numberList.add("0");
                passNumber(numberList);
                break;
            case R.id.btnback:
                passNumber(numberList);
                break;
            case R.id.btnclear:
                numberList.clear();
                passNumber(numberList);
                break;
        }
    }

    private void passNumber(ArrayList<String> numberList) {
        if(numberList == null){
            view01.setBackgroundResource(R.drawable.bg_view_gray_oval);
            view02.setBackgroundResource(R.drawable.bg_view_gray_oval);
            view03.setBackgroundResource(R.drawable.bg_view_gray_oval);
            view04.setBackgroundResource(R.drawable.bg_view_gray_oval);
        }else{
            switch(numberList.size()){
                case 1:
                    num01 = numberList.get(0);
                    view01.setBackgroundResource(R.drawable.bg_view_fill_oval);
                    break;
                case 2:
                    num02 = numberList.get(1);
                    view02.setBackgroundResource(R.drawable.bg_view_fill_oval);
                    break;
                case 3:
                    num03 = numberList.get(2);
                    view03.setBackgroundResource(R.drawable.bg_view_fill_oval);
                    break;
                case 4:
                    num04 = numberList.get(3);
                    view04.setBackgroundResource(R.drawable.bg_view_fill_oval);
                    passCode = num01 + num02 + num03 + num04;

                    break;
            }
        }
    }
    private SharedPreferences.Editor savePassCode(String passCode){

        SharedPreferences preferences = getSharedPreferences("passcode_pef", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("passcode",passCode);
        editor.commit();

        return editor;
    }
    private String getPassCode(){
        SharedPreferences preferences = getSharedPreferences("passcode_pef",Context.MODE_PRIVATE);
        return preferences.getString("passcode","");
    }
    public static String encrypt(String plaintext, String pwd) {
        byte[] salt = generateSalt();

        SecretKey key = deriveKey(pwd, salt);

        try {
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            byte[] iv = generateIv(cipher.getBlockSize());
            IvParameterSpec ivParams = new IvParameterSpec(iv);
            cipher.init(Cipher.ENCRYPT_MODE, key, ivParams);
            byte[] cipherText = cipher.doFinal(plaintext.getBytes("UTF-8"));

            return String.format("%s%s%s%s%s", toBase64(salt), DELIMITER, toBase64(iv), DELIMITER, toBase64(cipherText));

        } catch (GeneralSecurityException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String decrypt(String ciphertext, String pwd) {
        String[] fields = ciphertext.split(DELIMITER);
        if (fields.length != 3) {
            throw new IllegalArgumentException("Invalid encypted text format");
        }
        byte[] salt = fromBase64(fields[0]);
        byte[] iv = fromBase64(fields[1]);
        byte[] cipherBytes = fromBase64(fields[2]);
        SecretKey key = deriveKey(pwd, salt);

        try {
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            IvParameterSpec ivParams = new IvParameterSpec(iv);
            cipher.init(Cipher.DECRYPT_MODE, key, ivParams);
            byte[] plaintext = cipher.doFinal(cipherBytes);
            return new String(plaintext, "UTF-8");
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] generateSalt() {
        byte[] b = new byte[PKCS5_SALT_LENGTH];
        random.nextBytes(b);
        return b;
    }

    private static byte[] generateIv(int length) {
        byte[] b = new byte[length];
        random.nextBytes(b);
        return b;
    }

    private static SecretKey deriveKey(String pwd, byte[] salt) {

        try {
            KeySpec keySpec = new PBEKeySpec(pwd.toCharArray(), salt, ITERATION_COUNT, KEY_LENGTH);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(PBKDF2_DERIVATION_ALGORITHM);
            byte[] keyBytes = keyFactory.generateSecret(keySpec).getEncoded();
            return new SecretKeySpec(keyBytes, "AES");
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }

    private static String toBase64(byte[] bytes) {
        return Base64.encodeToString(bytes, Base64.NO_WRAP);
    }

    public static byte[] fromBase64(String base64) {
        return Base64.decode(base64, Base64.NO_WRAP);
    }

}