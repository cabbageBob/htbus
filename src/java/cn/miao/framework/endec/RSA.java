package cn.miao.framework.endec;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.crypto.Cipher;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import cn.miao.framework.util.StringUtil;

/**
 * 解密成功率80%左右
 * 
 * 打包到Jar后，要用相对路径读取，绝对路径读不到
 * <p>
 * 封装同RSA非对称加密算法有关的方法，可用于数字签名，RSA加密解密
 * 带了填充算法，与stanford的js配套
 * </p>
 */
public class RSA {
	
	static Logger logger = Logger.getLogger(RSA.class); 
	//private static String RSAKeyStore = "C:/RSAKey.txt";
	private static String RSAKeyStore ;
	private static KeyPair keyPair;
	
	static {
		RSAKeyStore = RSA.class.getResource("RSAKey.txt").getFile();
		keyPair = getKeyPair();
	}
	
	public RSA() {
	}

	/**
	 * 使用私钥加密数据 用一个已打包成byte[]形式的私钥加密数据，即数字签名
	 * 
	 * @param keyInByte
	 *            打包成byte[]的私钥
	 * @param source
	 *            要签名的数据，一般应是数字摘要
	 * @return 签名 byte[]
	 */
	public static byte[] sign(byte[] keyInByte, byte[] source) {
		try {
			PKCS8EncodedKeySpec priv_spec = new PKCS8EncodedKeySpec(keyInByte);
			KeyFactory mykeyFactory = KeyFactory.getInstance("RSA");
			PrivateKey privKey = mykeyFactory.generatePrivate(priv_spec);
			Signature sig = Signature.getInstance("SHA1withRSA");
			sig.initSign(privKey);
			sig.update(source);
			return sig.sign();
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 验证数字签名
	 * 
	 * @param keyInByte
	 *            打包成byte[]形式的公钥
	 * @param source
	 *            原文的数字摘要
	 * @param sign
	 *            签名(对原文的数字摘要的签名)
	 * @return 是否证实 boolean
	 */
	public static boolean verify(byte[] keyInByte, byte[] source, byte[] sign) {
		try {
			KeyFactory mykeyFactory = KeyFactory.getInstance("RSA");
			Signature sig = Signature.getInstance("SHA1withRSA");
			X509EncodedKeySpec pub_spec = new X509EncodedKeySpec(keyInByte);
			PublicKey pubKey = mykeyFactory.generatePublic(pub_spec);
			sig.initVerify(pubKey);
			sig.update(source);
			return sig.verify(sign);
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 建立新的密钥对，返回打包的byte[]形式私钥和公钥
	 * 
	 * @return 
	 *         包含打包成byte[]形式的私钥和公钥的object[],其中，object[0]为私钥byte[],object[1]为公钥byte
	 *         []
	 */
	public static Object[] giveRSAKeyPairInByte() {
		// KeyPair newKeyPair = creatmyKey();
		KeyPair newKeyPair = getKeyPair();
		if (newKeyPair == null) {
			return null;
		} else  {
			Object[] re = new Object[2];
			PrivateKey priv = newKeyPair.getPrivate();
			byte[] b_priv = priv.getEncoded();
			PublicKey pub = newKeyPair.getPublic();
			byte[] b_pub = pub.getEncoded();
			re[0] = b_priv;
			re[1] = b_pub;
			return re;
		}
	}
	
	public static KeyPair getKeyPair() {
		InputStream fis=RSA.class.getResourceAsStream("/cn/miao/framework/endec/RSAKey.txt");
		//FileInputStream fis;
		try {
			//fis = new FileInputStream(is);
			ObjectInputStream oos = new ObjectInputStream(fis);
			KeyPair kp = (KeyPair) oos.readObject();
			oos.close();
			fis.close();
			return kp;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void saveKeyPair(KeyPair kp) throws Exception {
		System.out.println(RSAKeyStore);
		FileOutputStream fos = new FileOutputStream(RSAKeyStore);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		// 生成密钥
		oos.writeObject(kp);
		oos.close();
		fos.close();
	}

	/**
	 * 新建密钥对
	 * 
	 * @return KeyPair对象
	 */
	public static KeyPair creatmyKey() {
		KeyPair myPair;
		long mySeed;
		mySeed = System.currentTimeMillis();
		try {
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA",
					new BouncyCastleProvider());
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
			random.setSeed(mySeed);
			keyGen.initialize(512, random);
			myPair = keyGen.generateKeyPair();
			saveKeyPair(myPair);
		} catch (Exception e1) {
			e1.printStackTrace();
			return null;
		}
		return myPair;
	}

	/**
	 * 使用RSA公钥加密数据
	 * 
	 * @param pubKeyInByte
	 *            打包的byte[]形式公钥
	 * @param data
	 *            要加密的数据
	 * @return 加密数据
	 */
	public static byte[] encryptByRSA(byte[] pubKeyInByte, byte[] data) {
			KeyFactory mykeyFactory;
			try {
				mykeyFactory = KeyFactory.getInstance("RSA");
				X509EncodedKeySpec pub_spec = new X509EncodedKeySpec(pubKeyInByte);
				PublicKey pubKey = mykeyFactory.generatePublic(pub_spec);
				// "RSA/ECB/PKCS1Padding"  RSA/NONE/NoPadding
				Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", new BouncyCastleProvider());
				cipher.init(Cipher.ENCRYPT_MODE, pubKey);
				return cipher.doFinal(data);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
	}
	
	/**
	 * 用RSA私钥解密
	 * 
	 * @param privKeyInByte
	 *            私钥打包成byte[]形式
	 * @param data
	 *            要解密的数据
	 * @return 解密数据
	 */
	public static byte[] decryptByRSA(byte[] privKeyInByte, byte[] data) {
			PKCS8EncodedKeySpec priv_spec = new PKCS8EncodedKeySpec(
					privKeyInByte);
			KeyFactory mykeyFactory;
			try {
				mykeyFactory = KeyFactory.getInstance("RSA");
				PrivateKey privKey = mykeyFactory.generatePrivate(priv_spec);
				// "RSA/ECB/PKCS1Padding"
				Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", new BouncyCastleProvider());
				cipher.init(Cipher.DECRYPT_MODE, privKey);
				if (data.length > 64) {
					// return decryptByRSA(privKeyInByte, data);
					byte[] data2 = new byte[64];
					System.arraycopy(data, 1, data2, 0, 64);
					return cipher.doFinal(data2);
				} else {
					return cipher.doFinal(data);
				}
			} catch (Exception e) {
				// e.printStackTrace();
				return null;
			}
	}
	
	/**
	 * 获取公钥信息
	 * 
	 * @return Map<String,String>
	 * @since v 1.0
	 */
	public static Map<String, String> getPublicKeyMap() {
		Map<String, String> rtMap = new HashMap<String, String>();
		JSONObject object = JSONObject.fromObject(keyPair);
		JSONObject publicObj = object.getJSONObject("public");
		rtMap.put("modulus", StringUtil.toHexStr(publicObj.get("modulus").toString()));
		rtMap.put("exponent", StringUtil.toHexStr(publicObj.get("publicExponent").toString()));
		return rtMap;
	}

	
	public static String toHex(byte b) {
		return ("" + "0123456789ABCDEF".charAt(0xf & b >> 4) + "0123456789ABCDEF"
				.charAt(b & 0xf)).toLowerCase();
	}
	
	/**
	 * 加密,返回十六进制数
	 * 
	 * @param plainText
	 * @return String
	 * @since v 1.0
	 */
	public static String encrypt(String plainText) {
		byte[] encrypt = encryptByRSA(keyPair.getPublic().getEncoded(), plainText.getBytes());
		String rtStr = "";
		for (byte b : encrypt) {
			rtStr += toHex(b);
		}
		return rtStr;
	}
	
	/**
	 * 解密
	 * 
	 * @param ciphertext
	 * @return String
	 * @since v 1.0
	 */
	public static String decrypt(String ciphertext) {
		byte[] cipher = new BigInteger(ciphertext, 16).toByteArray();
		byte[] decrypt = decryptByRSA(keyPair.getPrivate().getEncoded(),
				cipher);
		if (null == decrypt) {
			return "";
		}
		return new String(decrypt);
	}
	
	public static void printByte(byte[] b) {
		byte[] c = new byte[64];
		System.arraycopy(b, 1, c, 0, 64);
		for (int i =0; i < 64; i++) {
			System.out.println(b[i]+ "," + c[i]);
		}
		System.out.println(b[64]);
	}
	
	/**
	 * 获取指定的public key
	 * @param modulus
	 * @param exponent
	 * @return RSAPublicKey
	 */
	public static RSAPublicKey getPublicKey(String modulus, String exponent) {
		try {
			BigInteger exponentBI = new BigInteger(exponent, 16);
			BigInteger modulusBI = new BigInteger(modulus, 16);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			RSAPublicKeySpec keySpec = new RSAPublicKeySpec(modulusBI,
					exponentBI);
			return (RSAPublicKey) keyFactory.generatePublic(keySpec);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static void main(String[] args) throws Exception {
		String test = "123";
		Map<String, String> keyMap = getPublicKeyMap();
		RSAPublicKey publicKey = getPublicKey(keyMap.get("modulus"), keyMap.get("exponent"));
		System.out.println("1.new public key=========================");
		System.out.println(publicKey);
		KeyPair kp = getKeyPair();
		System.out.println("2.old public key=========================");
		System.out.println(kp.getPublic());
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		byte[] encrypt = cipher.doFinal(test.getBytes());
		String rtStr = "";
		for (byte b : encrypt) {
			rtStr += toHex(b);
		}
		System.out.println("1.new result=========================");
		System.out.println(rtStr);
		System.out.println(decrypt(rtStr));
		System.out.println("2.old result=========================");
		String encryptStr = encrypt(test);
		System.out.println(encryptStr);
		System.out.println(decrypt(encryptStr));
		
		String string = "7f9f4f24b70c18be4bf3a247a52bf9015c15c2e6fe3dab202e17be09c897ec9738018b8ed0fde751f5c3af5a87678854e1bc31e22a6678528fe8fe7ab87185eb";
		System.out.println(decrypt(string));
	}
	
	/**
	 *测试
	 */
	public static void main2(String[] args) {
		try {
			//creatmyKey();
			// 私钥加密 公钥解密
			// 生成私钥-公钥对
			Object[] v = giveRSAKeyPairInByte();
			// 获得摘要
			byte[] source = ("test").getBytes();
			// 使用私钥对摘要进行加密 获得密文 即数字签名
			byte[] sign = sign((byte[]) v[0], source);
			// 使用公钥对密文进行解密,解密后与摘要进行匹配
			boolean yes = verify((byte[]) v[1], source, sign);
			if (yes)
				System.out.println("匹配成功 合法的签名!");
			KeyPair kp = getKeyPair();
			System.out.println(kp.getPublic());
			System.out.println(kp.getPrivate());
			// 获得摘要
			byte[] source1 = ("test").getBytes();
			// 使用公钥对摘要进行加密 获得密文
			byte[] sign1 = encryptByRSA(kp.getPublic().getEncoded(), source1);
			// 使用私钥对密文进行解密 返回解密后的数据
			byte[] newSource1 = decryptByRSA(kp.getPrivate().getEncoded(),
					sign1);
			// 对比源数据与解密后的数据
			System.out.println(sign1.length);
			for (byte b : sign1) {
				System.out.print(toHex(b));
			}
			System.out.println();
			//System.out.println(new String(newSource1));

			if (Arrays.equals(source1, newSource1))
				System.out.println("匹配成功 合法的私钥!");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	static int fail = 0;
	static int success = 0;
	public static void main1(String[] args) {
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				try {
					if (fail+success > 40) {
						System.exit(-1);
					}
					String tString = "123456";
					String ee = RSA.encrypt(tString);
					System.out.println(ee);
					String de = RSA.decrypt(ee);
					System.out.println(de);
					if (de.length() > 0) {
						success++;
					} else {
						fail++;
					}
					System.out.println("fail/success:" + fail + "/" + success + "\t success ratio:" + success*1.0f/(fail+success));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		Timer t = new Timer();
		t.schedule(task, 0, 1000);
	}
}