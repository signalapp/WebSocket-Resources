package org.whispersystems.websocket.servlet;

import javax.servlet.ServletInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class BufferingServletInputStream extends ServletInputStream {

  private final ByteArrayInputStream buffer;

  public BufferingServletInputStream(byte[] body) {
    this.buffer = new ByteArrayInputStream(body);
  }

  @Override
  public int read(byte[] buf, int offset, int length) {
    return buffer.read(buf, offset, length);
  }

  @Override
  public int read(byte[] buf) {
    return read(buf, 0, buf.length);
  }

  @Override
  public int read() throws IOException {
    return buffer.read();
  }

  @Override
  public int available() {
    return buffer.available();
  }
}
