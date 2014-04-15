package com.mydocket.pdfserver.codec;

public enum DecoderState {
	READ_COMMAND,
	READ_FILENAME,
	READ_LENGTH,
	READ_CONTENT,
}
