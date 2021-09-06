package Set2;

import java.io.File;
import java.io.FileNotFoundException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Scanner;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.SecretKeySpec;

public class Chal10 {
    /*
     * take a file read all of the input decode it from base64 into a byte[] take a
     * key, convert it to a byte[] xor the key and the plaintext into a new
     * ListArray<Byte[]>
     * 
     */
    /**
     * @param args
     * @throws FileNotFoundException
     * @throws NoSuchPaddingException 
     * @throws NoSuchAlgorithmException 
     * @throws InvalidKeyException 
     * @throws BadPaddingException 
     * @throws IllegalBlockSizeException 
     */

    /*
     * take a file read all of the input decode it from base64 into a byte[] take a
     * key, convert it to a byte[] xor the key and the plaintext into a new
     * ListArray<Byte[]>
     * 
     */
    public static void main(String[] args)
            throws FileNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException, ShortBufferException {
        File file = new File("C:\\Users\\nickm\\git\\Cryptopals-Java\\Crypto Pals\\src\\Set2\\Chal10Data");
        Scanner scanner = new Scanner(file);
        StringBuilder cipherText = new StringBuilder();
        while(scanner.hasNext()){
            cipherText.append(scanner.next());
        }
        byte[] msg = (Base64.getDecoder().decode(cipherText.toString()));
        byte[] IV = new byte[16];
        for (int i = 0; i < IV.length; i++){
            IV[i] = (byte)0;
        }
        SecretKey secretKey = new SecretKeySpec("YELLOW SUBMARINE".getBytes(), "AES");
        System.out.println( new String(decryptAESCBC(msg, secretKey, IV)));
        scanner.close();
    }


        public static byte[] encryptAESCBC(byte[] pt, SecretKey key, byte[] IV)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException,
            BadPaddingException, ShortBufferException {
        Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] ct = new byte[Chal9.padArr(pt).length];
        pt = Chal9.padArr(pt);
        for(int i =0;i<pt.length; i+=16){
            for(int j = i; j < i+ 16; j++){ // create the temporary array to use for all of this
                if(i == 0){
                    pt[j] = (byte) (pt[j] ^ IV[j]); //XOR'S ll of the bytes against the IV
                }
                else{
                    pt[j] = (byte) (pt[j] ^ ct[j-16]); //XOR'S ll of the bytes against the
                                                                    //previously encrypted block of text
                }
            }
            cipher.doFinal(pt, i, 16, ct, i);
        }
        return ct;
    }


        public static byte[] decryptAESCBC(byte[] ct, SecretKey key, byte[] IV)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException,
            BadPaddingException, ShortBufferException {
        Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] pt = new byte[ct.length];
        for(int i =0;i<ct.length; i+=16){
            cipher.doFinal(ct,i,16,pt,i);
            for(int k = i; k < i+16; k++)
                if(i == 0){
                    pt[k] = (byte) (pt[k] ^ IV[k]); //XOR'S ll of the bytes against the IV
                }
                else{
                    pt[k] = (byte) (pt[k] ^ ct[k-16]); //XOR'S ll of the bytes against the
                                                    //previously encrypted block of text
                }
        }
        return Chal9.unPadArr(pt);
    }        }