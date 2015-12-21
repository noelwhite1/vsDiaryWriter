package net.viperfish.journal.secureAlgs;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;


import net.viperfish.journal.secure.AlgorithmSpec;
import net.viperfish.journal.secure.Encryptor;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.paddings.BlockCipherPadding;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

public class BCBlockCipherEncryptor extends Encryptor {

	private byte[] key;
	private byte[] iv;
	private SecureRandom rand;

	public BCBlockCipherEncryptor() {
		rand = new SecureRandom();
	}

	private PaddedBufferedBlockCipher initCipherSuite() {
		String[] parts = getMode().split("/");
		BlockCipher engine = AlgorithmSpec.getBlockCipherEngine(parts[0]);
		BlockCipher modedEngine = AlgorithmSpec.wrapBlockCipherMode(engine,
				parts[1]);
		BlockCipherPadding padding = AlgorithmSpec
				.getBlockCipherPadding(parts[2]);
		PaddedBufferedBlockCipher result = new PaddedBufferedBlockCipher(
				modedEngine, padding);
		return result;
	}

	private byte[] transformData(PaddedBufferedBlockCipher cipher, byte[] data)
			throws DataLengthException, IllegalStateException,
			InvalidCipherTextException {
		int minSize = cipher.getOutputSize(data.length);
		byte[] outBuf = new byte[minSize];
		int length1 = cipher.processBytes(data, 0, data.length, outBuf, 0);
		int length2 = cipher.doFinal(outBuf, length1);
		int actualLength = length1 + length2;
		byte[] result = new byte[actualLength];
		System.arraycopy(outBuf, 0, result, 0, result.length);
		return result;
	}

	@Override
	public byte[] getKey() {
		return key;
	}

	@Override
	public void setKey(byte[] key) {
		this.key = key;
	}

	@Override
	public byte[] getIv() {
		return iv;
	}

	@Override
	public void setIv(byte[] iv) {
		this.iv = iv;
	}

	@Override
	public byte[] encrypt(byte[] text) throws InvalidKeyException,
			InvalidAlgorithmParameterException, IllegalBlockSizeException,
			BadPaddingException {
		PaddedBufferedBlockCipher encryptor = initCipherSuite();
		iv = new byte[encryptor.getBlockSize()];
		rand.nextBytes(iv);
		encryptor.init(true, new ParametersWithIV(new KeyParameter(key), iv));
		try {
			return transformData(encryptor, text);
		} catch (DataLengthException | IllegalStateException
				| InvalidCipherTextException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public byte[] decrypt(byte[] cipher) throws InvalidKeyException,
			InvalidAlgorithmParameterException, IllegalBlockSizeException,
			BadPaddingException {
		PaddedBufferedBlockCipher decryptor = initCipherSuite();
		decryptor.init(false, new ParametersWithIV(new KeyParameter(key), iv));
		try {
			return transformData(decryptor, cipher);
		} catch (DataLengthException | IllegalStateException
				| InvalidCipherTextException e) {
			throw new RuntimeException(e);
		}
	}

}
