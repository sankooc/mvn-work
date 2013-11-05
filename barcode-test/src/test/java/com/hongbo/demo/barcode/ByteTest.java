/*
 * Copyright 2012 Hongbo ZY Corporation
 * created by sankooc
 */
package com.hongbo.demo.barcode;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.imageio.ImageIO;

import org.junit.Test;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Result;
import com.google.zxing.ResultMetadataType;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;

/**
 * @author sankooc
 * 
 */
public class ByteTest {
	public static MessageDigest digest;
	static {
		try {
			digest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void compa() throws Exception {
		StringBuilder builder = new StringBuilder();
	    builder.append("先下载zxing,下载的包在code目录下面应该有code.jar 里面还有android项目的例子,这个例子生成的apk在网上能随处下载到叫二维码扫描,下载量哇哇的,建议先部署例子看看效果. 附件是将例子精简后的代码,附带code.jar,是2.0的. 对权限什么的也做了测试,留下需要的. 原文出处 http://www.oschina.net/q ...");
		byte[] data = builder.toString().getBytes("UTF-8");
		System.out.println(data.length);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		GZIPOutputStream gzip = new GZIPOutputStream(out);
		gzip.write(data);
		gzip.close();
		byte[] ccad = out.toByteArray();
		System.out.println(ccad.length);

		ByteArrayOutputStream gout = new ByteArrayOutputStream();
		ByteArrayInputStream in = new ByteArrayInputStream(ccad);
		GZIPInputStream gunzip = new GZIPInputStream(in);
		byte[] buffer = new byte[256];
		int n;
		while ((n = gunzip.read(buffer)) != -1) {
			gout.write(buffer, 0, n);
		}
		System.out.println(gout.toByteArray().length);
	}

	// @Test
	public void counterTest() throws Exception {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < 170; i++) {
			builder.append("啊");
		}
		byte[] data = builder.toString().getBytes("UTF-8");
		System.out.println(data.length);
		MessageDigest digest = MessageDigest.getInstance("MD5");
		byte[] md1 = digest.digest(data);

		Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();
		hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
		File file = new File("d:/test.png");
		// 2280
		BasicByteWriter barcodeWriter = new BasicByteWriter();
		BitMatrix matrix = barcodeWriter.encode(data, BarcodeFormat.QR_CODE, 200, 200, hints);
		String imageFormat = file.getName().substring(file.getName().indexOf(".") + 1);
		MatrixToImageWriter.writeToFile(matrix, imageFormat, file);

		Map<DecodeHintType, Object> dhints = new HashMap<DecodeHintType, Object>();

		dhints.put(DecodeHintType.CHARACTER_SET, "UTF-8");

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
		Result result = barcodeReader.decode(bitmap, dhints);
		List<byte[]> list = (List<byte[]>) result.getResultMetadata().get(ResultMetadataType.BYTE_SEGMENTS);
		// byte[] resultData = new byte[list.size()];
		digest = MessageDigest.getInstance("MD5");
		byte[] md2 = digest.digest(list.get(0));

		System.out.println(Arrays.equals(md1, md2));
	}

	public void valid() {

	}
}
