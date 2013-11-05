package com.hongbo.demo.barcode;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitArray;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.google.zxing.qrcode.decoder.Mode;
import com.google.zxing.qrcode.decoder.Version;
import com.google.zxing.qrcode.encoder.ByteMatrix;
import com.google.zxing.qrcode.encoder.QRCode;

import org.junit.Before;
import org.junit.Test;

public class PerformanceTest {

	public void encodeBarcode(File file, byte[] bytes, int size) throws Exception {
		QRCodeWriter writer = new QRCodeWriter();
		QRCode code = new QRCode();
		Mode mode = Mode.BYTE;
		BitArray dataBits = new BitArray();
		for (byte b : bytes) {
			dataBits.appendBits(b, 8);
		}
		int numInputBits = dataBits.getSize();
		ErrorCorrectionLevel errorCorrectionLevel = ErrorCorrectionLevel.L;
		initQRCode(numInputBits, errorCorrectionLevel, mode, code);
		BitArray headerAndDataBits = new BitArray();

		headerAndDataBits.appendBits(mode.getBits(), 4);
		int numLetters = dataBits.getSizeInBytes();
		
		int numBits = mode.getCharacterCountBits(Version.getVersionForNumber(code.getVersion()));
		if (numLetters > ((1 << numBits) - 1)) {
			throw new WriterException(numLetters + "is bigger than" + ((1 << numBits) - 1));
		}
		headerAndDataBits.appendBits(numLetters, numBits);
		headerAndDataBits.appendBitArray(dataBits);
		
		 
		 

	}

	private static void initQRCode(int numInputBits, ErrorCorrectionLevel ecLevel, Mode mode, QRCode qrCode) throws WriterException {
		qrCode.setECLevel(ecLevel);
		qrCode.setMode(mode);

		// In the following comments, we use numbers of Version 7-H.
		for (int versionNum = 1; versionNum <= 40; versionNum++) {
			Version version = Version.getVersionForNumber(versionNum);
			// numBytes = 196
			int numBytes = version.getTotalCodewords();
			// getNumECBytes = 130
			Version.ECBlocks ecBlocks = version.getECBlocksForLevel(ecLevel);
			int numEcBytes = ecBlocks.getTotalECCodewords();
			// getNumRSBlocks = 5
			int numRSBlocks = ecBlocks.getNumBlocks();
			// getNumDataBytes = 196 - 130 = 66
			int numDataBytes = numBytes - numEcBytes;
			// We want to choose the smallest version which can contain data of
			// "numInputBytes" + some
			// extra bits for the header (mode info and length info). The header
			// can be three bytes
			// (precisely 4 + 16 bits) at most.
			if (numDataBytes >= getTotalInputBytes(numInputBits, version, mode)) {
				// Yay, we found the proper rs block info!
				qrCode.setVersion(versionNum);
				qrCode.setNumTotalBytes(numBytes);
				qrCode.setNumDataBytes(numDataBytes);
				qrCode.setNumRSBlocks(numRSBlocks);
				// getNumECBytes = 196 - 66 = 130
				qrCode.setNumECBytes(numEcBytes);
				// matrix width = 21 + 6 * 4 = 45
				qrCode.setMatrixWidth(version.getDimensionForVersion());
				return;
			}
		}
		throw new WriterException("Cannot find proper rs block info (input data too big?)");
	}

	private static int getTotalInputBytes(int numInputBits, Version version, Mode mode) {
		int modeInfoBits = 4;
		int charCountBits = mode.getCharacterCountBits(version);
		int headerBits = modeInfoBits + charCountBits;
		int totalBits = numInputBits + headerBits;

		return (totalBits + 7) / 8;
	}

	public void writeBarcode(String contents, Map<EncodeHintType, Object> hints, File file) throws Exception {
		if (file == null || file.getName().trim().isEmpty())
			throw new IllegalArgumentException("File not found, or invalid file name.");
		if (contents == null || contents.trim().isEmpty())
			throw new IllegalArgumentException("Can't encode null or empty contents.");
		byte[] data = contents.getBytes("UTF-8");
		BasicByteWriter barcodeWriter = new BasicByteWriter();
		BitMatrix matrix;
		if (hints != null && !hints.isEmpty())
			matrix = barcodeWriter.encode(data, BarcodeFormat.QR_CODE, 200, 200, hints);
		else
			matrix = barcodeWriter.encode(data, BarcodeFormat.QR_CODE, 200, 200);
		String imageFormat = file.getName().substring(file.getName().indexOf(".") + 1);
		MatrixToImageWriter.writeToFile(matrix, imageFormat, file);
	}

	/**
	 * Decode method used to read image or barcode itself, and recognize the
	 * barcode, get the encoded contents and returns it.
	 * 
	 * @param file
	 *            image that need to be read.
	 * @param config
	 *            configuration used when reading the barcode.
	 * @return decoded results from barcode.
	 */
	public static String decode(File file, Map<DecodeHintType, Object> hints) throws Exception {
		// check the required parameters
		if (file == null || file.getName().trim().isEmpty())
			throw new IllegalArgumentException("File not found, or invalid file name.");
		BufferedImage image;
		try {
			image = ImageIO.read(file);
		} catch (IOException ioe) {
			throw new Exception(ioe.getMessage());
		}
		if (image == null)
			throw new IllegalArgumentException("Could not decode image.");
		LuminanceSource source = new BufferedImageLuminanceSource(image);
		BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
		MultiFormatReader barcodeReader = new MultiFormatReader();
		Result result;
		String finalResult = null;
		try {
			if (hints != null && !hints.isEmpty())
				result = barcodeReader.decode(bitmap, hints);
			else
				result = barcodeReader.decode(bitmap);
			finalResult = String.valueOf(result.getText());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return finalResult;
	}

	 @Test
	public void common() {
		Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();
		hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
		Map<DecodeHintType, Object> dhints = new HashMap<DecodeHintType, Object>();
		dhints.put(DecodeHintType.CHARACTER_SET, "UTF-8");
		String contents = "啊哈";
		File file = new File("d:/TEST.png");
		try {
			writeBarcode(contents, hints, file);
			String str = decode(file, dhints);
			System.out.println(str);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Map<EncodeHintType, Object> encodeHints = new HashMap<EncodeHintType, Object>();
	public Map<DecodeHintType, Object> decodeHints = new HashMap<DecodeHintType, Object>();

	protected long getResult(int count) throws Exception {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < count; i++) {
			builder.append("敂");
		}
		File file = new File("test.png");
		String contents = builder.toString();
		writeBarcode(contents, encodeHints, file);
		long start = System.currentTimeMillis();
		String ret = decode(file, decodeHints);
		if (contents.equals(ret)) {
			long end = System.currentTimeMillis();
			return end - start;
		} else {
			int size = ret.length();
			throw new Exception("not match count is " + count + " detect count : " + ret.length());
		}

	}

	@Before
	public void setup() {
		encodeHints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
		decodeHints.put(DecodeHintType.CHARACTER_SET, "UTF-8");
	}

//	@Test
	public void performance() {

		List<Integer> cases = new LinkedList<Integer>();
		cases.add(500);
		// cases.add(600);
		// cases.add(700);
		// cases.add(2000);
		// cases.add(2500);
		// cases.add(3000);

		Map<Integer, Long> result = new LinkedHashMap<Integer, Long>();

		for (int count : cases) {
			try {
				long ret = getResult(count);
				result.put(count, ret);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		for (int count : result.keySet()) {
			System.out.println("count : " + count + " cost times " + result.get(count));
		}

	}
	private static final int QUIET_ZONE_SIZE = 4;
	
	 private static BitMatrix renderResult(QRCode code, int width, int height) {
		    ByteMatrix input = code.getMatrix();
		    if (input == null) {
		      throw new IllegalStateException();
		    }
		    int inputWidth = input.getWidth();
		    int inputHeight = input.getHeight();
		    int qrWidth = inputWidth + (QUIET_ZONE_SIZE << 1);
		    int qrHeight = inputHeight + (QUIET_ZONE_SIZE << 1);
		    int outputWidth = Math.max(width, qrWidth);
		    int outputHeight = Math.max(height, qrHeight);

		    int multiple = Math.min(outputWidth / qrWidth, outputHeight / qrHeight);
		    // Padding includes both the quiet zone and the extra white pixels to accommodate the requested
		    // dimensions. For example, if input is 25x25 the QR will be 33x33 including the quiet zone.
		    // If the requested size is 200x160, the multiple will be 4, for a QR of 132x132. These will
		    // handle all the padding from 100x100 (the actual QR) up to 200x160.
		    int leftPadding = (outputWidth - (inputWidth * multiple)) / 2;
		    int topPadding = (outputHeight - (inputHeight * multiple)) / 2;

		    BitMatrix output = new BitMatrix(outputWidth, outputHeight);

		    for (int inputY = 0, outputY = topPadding; inputY < inputHeight; inputY++, outputY += multiple) {
		      // Write the contents of this row of the barcode
		      for (int inputX = 0, outputX = leftPadding; inputX < inputWidth; inputX++, outputX += multiple) {
		        if (input.get(inputX, inputY) == 1) {
		          output.setRegion(outputX, outputY, multiple, multiple);
		        }
		      }
		    }

		    return output;
		  }
}
