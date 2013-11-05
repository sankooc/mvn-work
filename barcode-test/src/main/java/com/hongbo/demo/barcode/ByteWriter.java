/*
 * Copyright 2012 Hongbo ZY Corporation
 * created by sankooc
 */
package com.hongbo.demo.barcode;

import java.util.Map;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

/**
 * @author sankooc
 * 
 */
public interface ByteWriter {

	BitMatrix encode(byte[] contents, BarcodeFormat format, int width, int height) throws WriterException;

	BitMatrix encode(byte[] contents, BarcodeFormat format, int width, int height, Map<EncodeHintType, ?> hints) throws WriterException;

}
