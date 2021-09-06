package Set2;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Random;
import java.util.Scanner;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.SecretKeySpec;

public class Chal11 {
	
	private static int encryptionType = 1;
	private static int encryptionGuess = -2;
	private static int ECBCount = 0;
	private static int CBCCount =0;
	private static int wrongGuess =0;
	public static void main(String[] args)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException, ShortBufferException, IOException {
        File file = new File("C:\\Users\\nickm\\git\\Cryptopals-Java\\Crypto Pals\\src\\Set2\\Chal11");
        Scanner scanner = new Scanner(file);
        StringBuilder cipherText = new StringBuilder();
        while(scanner.hasNext()){
            cipherText.append(scanner.next());
        }
        byte[] msg = cipherText.toString().getBytes();
//        byte[] msg = (Base64.getDecoder().decode(cipherText.toString()));

		for(int i = 0; i < 10000; i++) aesDetectionOracle(randomEncrypt(msg));
		
		System.out.println("The number of times ECB was used is: " + ECBCount);
		System.out.println("The number of times CBC was used is: " + CBCCount);
        System.out.println("The number of wrong guesses was: " + wrongGuess);
		scanner.close();
    }
	
	/*
	 * This Function generates a random 16-byte SecretkeySpec to be used for AES encryption
	 */
	public static SecretKey randKey() {
		Random rand = new Random();
		byte[]key = new byte[16];
		rand.nextBytes(key);
		SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
		return secretKey;
	}
	
	/*
	 * This function will take a byte array, append 5-10 bytes to the beginning and end of the
	 * byte array. It will then randomly encrypt the input array using AES ECB or AES CBC
	 * encryption and return the encrypted byte array
	 * @param pt The plain-text byte array to be encrypted
	 * @return A byte array that has been randomly encrypted with either AES ECB or AES CBC
	 */
	public static byte[] randomEncrypt(byte[] pt) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, ShortBufferException, IOException {
		SecretKey key = randKey();
		Random rand = new Random();
		encryptionType = -1;
		int randKey = rand.nextInt(2);
		ByteArrayOutputStream newPT = new ByteArrayOutputStream();
		byte[] appendBytes = new byte[5+ rand.nextInt(6)];
		byte[] dependBytes = new byte[5+ rand.nextInt(6)];
		for(int i = 0; i< appendBytes.length; i++) appendBytes[i] = (byte)rand.nextInt(255);
		for(int j = 0; j< dependBytes.length; j++) dependBytes[j] = (byte)rand.nextInt(255);
		newPT.write(appendBytes);
		newPT.write(pt);
		newPT.write(dependBytes);
		
		if(rand.nextInt(2) == 0) {
			encryptionType = 1;
			ECBCount++;
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(cipher.ENCRYPT_MODE, key);
			return cipher.doFinal(newPT.toByteArray());
		}
		else {
			encryptionType = 0;
			CBCCount++;
			byte[] iv = new byte[16];
			for(int k = 0; k< iv.length; k++) iv[k] = (byte)rand.nextInt(255);			
			rand.nextBytes(iv);
			return Chal10.encryptAESCBC(newPT.toByteArray(), key, iv);
		}
	}
	/*
	 * This will intake a byte array that has been encrypted using AES and determine
	 * whether or not it has been encrypted using AES ECB or AES CBC. It accomplishes
	 * this task by checking each keyLength 'chunk' against all other keyLength chunks
	 * in the byte array to see if they match. If there is a match, then it is likely 
	 * encrypted using AES ECB. The intent is to feed the encryption a known byte array
	 * of identical chunks, I.E. 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa' in order to determine
	 * the type of encryption.
	 * 
	 * This function can be modified to use average wordlength of five. The creator determined
	 * that with about 70% accuracy it will determine the type of encryption on files of text.
	 */
	public static void aesDetectionOracle(byte[] ct) {
		int duplicateCount = 0;
		int keyLength = 16; //average length of an english word
		for(int i = 0; i < ct.length; i+=keyLength) {
			test:
			for(int j = i+1; j < ct.length-keyLength; j+=1) {
				for(int k = 0; k < keyLength; k++) {
					if(ct[i+k] != ct[j+k]) {
						continue test;
					}
					else if (k ==keyLength-1) duplicateCount++;
				}
			}
		}
		if (duplicateCount > 0) {
			encryptionGuess = 1;
		}
		else encryptionGuess = 0;
		
		if(encryptionType != encryptionGuess) wrongGuess++;
	}
}
