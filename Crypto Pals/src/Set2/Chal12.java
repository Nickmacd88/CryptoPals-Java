package Set2;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
/*
 * Byte-at-a-time ECB decryption (Simple)
Copy your oracle function to a new function that encrypts buffers under ECB mode using a consistent but unknown key (for instance, 
assign a single random key, once, to a global variable).

Now take that same function and have it append to the plaintext, BEFORE ENCRYPTING, the following string:

Um9sbGluJyBpbiBteSA1LjAKV2l0aCBteSByYWctdG9wIGRvd24gc28gbXkg
aGFpciBjYW4gYmxvdwpUaGUgZ2lybGllcyBvbiBzdGFuZGJ5IHdhdmluZyBq
dXN0IHRvIHNheSBoaQpEaWQgeW91IHN0b3A/IE5vLCBJIGp1c3QgZHJvdmUg
YnkK
Spoiler alert.
Do not decode this string now. Don't do it.

Base64 decode the string before appending it. Do not base64 decode the string by hand; make your code do it. 
The point is that you don't know its contents.

What you have now is a function that produces:

AES-128-ECB(your-string || unknown-string, random-key)
It turns out: you can decrypt "unknown-string" with repeated calls to the oracle function!

Here's roughly how:

Feed identical bytes of your-string to the function 1 at a time --- start with 1 byte ("A"), then "AA", then "AAA" and so on. Discover the block size of the cipher. You know it, but do this step anyway.
Detect that the function is using ECB. You already know, but do this step anyways.
Knowing the block size, craft an input block that is exactly 1 byte short (for instance, if the block size is 8 bytes, make "AAAAAAA"). Think about what the oracle function is going to put in that last byte position.
Make a dictionary of every possible last byte by feeding different strings to the oracle; for instance, "AAAAAAAA", "AAAAAAAB", "AAAAAAAC", remembering the first block of each invocation.
Match the output of the one-byte-short input to one of the entries in your dictionary. You've now discovered the first byte of unknown-string.
Repeat for the next byte.
 */
public class Chal12 {
	static SecretKey KEY = null;
	static String CIPHERTEXT = "Um9sbGluJyBpbiBteSA1LjAKV2l0aCBteSByYWctdG9wIGRvd24gc28gbXkgaGFpciBjYW4gYmxvdwpUaGUgZ2lybGllcyBvbiBzdGFuZGJ5IHdhdmluZyBqdXN0IHRvIHNheSBoaQpEaWQgeW91IHN0b3A/IE5vLCBJIGp1c3QgZHJvdmUgYnkK";
	
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
	public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IOException, IllegalBlockSizeException, BadPaddingException {
		SecretKey key = genKey(128);
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		cipher.init(cipher.ENCRYPT_MODE, key);
		byte[] ct = (Base64.getDecoder().decode(CIPHERTEXT.getBytes()));
		decryptAES(ct, key, cipher);
	}
	
	public static SecretKey genKey(int keyLength) throws NoSuchAlgorithmException {
		KeyGenerator keyGen = KeyGenerator.getInstance("AES");
		keyGen.init(keyLength);
		return keyGen.generateKey();
	}	
			
	public static byte[] addCT (byte pt, int length) throws IOException {
		ByteArrayOutputStream newPt = new ByteArrayOutputStream();
		for(int i = 0; i < length; i++)newPt.write(pt);
		newPt.write(CIPHERTEXT.getBytes());
		return newPt.toByteArray();
	}

	/*
	 * Turns an ArrayList of type Byte into a byte[]. Same functionality as the Apache Commons ArrayUtils method.
	 * @param list the ArrayList<Byte> to be transformed into a primitive array
	 * @returns A primitive array of type byte
	 */
	public static byte[] toPrimitive(ArrayList<Byte> list) {
		byte[] fin = new byte[list.size()];
		for(int i = 0; i < fin.length; i++) {
			fin[i] = list.get(i).byteValue();
		}
		return fin;
	}
	
	private static int blockSizeDetector (SecretKey key, Cipher cipher) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException {
		int keySize = 0;
		byte[] testCT = null;
		for(int i = 2; i <= 64; i+=2) {
			testCT = addCT((byte)65, i);
			testCT = cipher.doFinal(testCT);
			
			if(testBlockSize(testCT, i/2)) {
				System.out.println("AES ECB has been detected, the key size is: " + (i/2));
				return (i/2); //this is the expected keysize
			}
		}
		return -1;
	}
	/*
	 * Creates a byte[] of blockSize length filled with the byte 65, which is the character A.
	 * @param blockSize The size of the byte array to be created.
	 * @returns a byte[] of size blockSize that is filled with the char 'A'
	 */
	private static byte[] makeDecryptBlock(int blockSize) {
		byte[] decryptBlock = new byte[blockSize];
		for(int i = 0; i<blockSize; i++) {decryptBlock[i] = (byte)65;}
		return decryptBlock;
	}
	
	/*
	 * Unsuccessful attempt to make a dictionary hashmap to be used for reference in the decrypt AES method.
	 * I have disocvered that the .equals function hashmaps use will not work correctly for what I need them 
	 * to do in this exercise.
	 */
	private static HashMap<ArrayList<Byte>, Byte> makeDict (int blockSize, SecretKey key, Cipher cipher) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException{
		HashMap<ArrayList<Byte>, Byte> dict = new HashMap<>();
		byte[] testBlock = makeDecryptBlock(blockSize);
		byte[] testBlock2 = null;
		byte[] tempBlock = null;
		ArrayList<Byte> tempBlock2 = new ArrayList<>();

		int doubles = 0;		
		for(int i = -128; i < 128; i++) {
			testBlock[blockSize-1] = (byte) i;
			tempBlock = cipher.doFinal(testBlock);
			for(int j =0 ; j< tempBlock.length; j++) {
				tempBlock2.add(tempBlock[j]);
			}
			dict.put(tempBlock2,(byte) i);
		}
		return dict;
		
	}

	/*
	 * Intakes a ciphertext byte array, a specific key, and a cipher and will decrypt the Ciphertext
	 * without using the key or the decoder. The method accomplishes this by creating a dummy cipher text
	 * with a known first blockSize-1 number of bytes. The function will then add a byte from the cipher text
	 * to the end of the known string. It will then iterate through the potential plaintext byte arrays until 
	 * it finds a match. The method will store this match in a byte buffer and iterate through all bytes in the
	 * encrypted text until the entire ct array has been decrpyted.
	 * @param ct This is the AES ECB encoded byte array you are trying to decrpyt
	 * @param key This is the randomly generated key for the purposes of this exercise
	 * @param cipher This is the encoding cipher-object which has been created for this exercise
	 */
	public static byte[] decryptAES(byte[] ct, SecretKey key, Cipher cipher) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException {
			int blockSize = blockSizeDetector(key, cipher);
			if(!aesDetectionOracle(cipher, blockSize)) {
				System.out.println("This not encrypted using AES ECB");
				return null;
			}
			byte[] decryptBlock = makeDecryptBlock(blockSize);
			byte[] testBlock = null;
			byte[] baseBlock = null;
			ArrayList<Byte> testArr = new ArrayList<>();
			ByteArrayOutputStream  fin = new ByteArrayOutputStream();
			for(int i = 0; i< 16; i++) testArr.add(new Byte((byte)decryptBlock[i]));
			for(int j = 0; j < ct.length; j++) {
				testArr.set(15, (byte) ct[j]);
				testBlock = toPrimitive(testArr);
				testBlock = cipher.doFinal(testBlock);
				for(int k = -128; k <128; k++) { 
					decryptBlock[15] = (byte) k;
					baseBlock = cipher.doFinal(decryptBlock);

					if(Arrays.equals(baseBlock, testBlock)) {
						fin.write((byte) k);
						System.out.print((char) k);
						continue;
					}
				}
			}
			return fin.toByteArray();
		}
	/*
	 *This will take the potential test key sizes of a ciphertext byte array and test to see if it is the correct
	 *blocksize.
	 */
	private static boolean testBlockSize(byte[] ct, int testKeySize) {
		for(int i = 0; i < testKeySize; i++) {
			if(ct[i] != ct[i+testKeySize]) return false;
		} 
			return true;
	}
	/*
	 * Detects whether or not the input cipher object is using AES ECB encryption by inputting a byte[] of blockSize*2
	 * and checking to see if both blocks are identical.
	 */
	public static boolean aesDetectionOracle(Cipher cipher, int blockSize) throws IllegalBlockSizeException, BadPaddingException {
		int duplicateCount = 0;
		byte[] ct = new byte[blockSize*2];
		ct = cipher.doFinal(ct);
		for (int j = 0; j< ct.length; j++)ct[j] = (byte) 65;
		for(int i = 0; i < ct.length; i+=blockSize) {
			test:
			for(int j = i+blockSize; j < ct.length-blockSize; j+=blockSize) {
				for(int k = 0; k < blockSize; k++) {
					if(ct[i+k] != ct[j+k]) {
						continue test;
					}
					else if (k ==blockSize-1) duplicateCount++;
				}
			}
		}
		if (duplicateCount > 0) {
			return true;
		}
		else return false;
		
//		if(encryptionType != encryptionGuess) wrongGuess++;
	}

}
