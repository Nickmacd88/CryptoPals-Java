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
