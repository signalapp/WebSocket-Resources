package org.whispersystems.websocket.servlet;

import javax.servlet.ServletOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class BufferingServletOutputStream extends ServletOutputStream {

  private final ByteArrayOutputStream buffer;

  public BufferingServletOutputStream(ByteArrayOutputStream buffer) {
    this.buffer = buffer;
  }

  @Override
  public void write(byte[] buf, int offset, int length) {
    buffer.write(buf, offset, length);
  }

  @Override
  public void write(byte[] buf) {
    write(buf, 0, buf.length);
  }

  @Override
  public void write(int b) throws IOException {
    buffer.write(b);
  }

  @Override
  public void flush() {

  }

  @Override
  public void close() {

  }
}
