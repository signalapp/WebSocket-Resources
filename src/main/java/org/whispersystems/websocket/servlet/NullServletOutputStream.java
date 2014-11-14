package org.whispersystems.websocket.servlet;

import javax.servlet.ServletOutputStream;
import java.io.IOException;

public class NullServletOutputStream extends ServletOutputStream {
  @Override
  public void write(int b) throws IOException {}

  @Override
  public void write(byte[] buf) {}

  @Override
  public void write(byte[] buf, int offset, int len) {}
}
