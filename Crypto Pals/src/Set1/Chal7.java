package Set1;

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
import javax.crypto.spec.SecretKeySpec;

public class Chal7 {
	/*
	 * The Base64-encoded content in this file has been encrypted via AES-128 in ECB mode under the key

	"YELLOW SUBMARINE".

	Decrypt it. You know the key, after all.

	Easiest way: use OpenSSL::Cipher and give it AES-128-ECB as the cipher. 
	
	I chose to make a new class for Challenge 7 because the original class was getting congested
	and this one has a new 'flavor' to it so I wanted to start fresh.
	 */
	public static void main(String[] args) throws FileNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		File file = new File("D:\\Coding\\Eclipse Workspace\\Crypto Pals\\src\\Set1\\Chal7Data");
		Scanner scanner = new Scanner(file);
		String msg = "";
		while(scanner.hasNext()) {
			msg += scanner.next();
		}
		byte[] msgBytes = Base64.getDecoder().decode(msg);
		String keyTxt = "YELLOW SUBMARINE";
		Cipher cipher = Cipher.getInstance("AES");
		SecretKey key = new SecretKeySpec(keyTxt.getBytes(), "AES");
		cipher.init(Cipher.DECRYPT_MODE, key);
		msgBytes = cipher.doFinal(msgBytes);
		System.out.println(new String(msgBytes));
	}
}
