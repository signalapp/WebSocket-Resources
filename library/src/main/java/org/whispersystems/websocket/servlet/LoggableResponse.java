package org.whispersystems.websocket.servlet;

import org.eclipse.jetty.http.HttpContent;
import org.eclipse.jetty.http.HttpCookie;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.server.HttpOutput;
import org.eclipse.jetty.server.Response;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Locale;

public class LoggableResponse extends Response {

  private final HttpServletResponse response;

  public LoggableResponse(HttpServletResponse response) {
    super(null, null);
    this.response = response;
  }

  @Override
  public void setHeaders(HttpContent httpContent) {
    throw new AssertionError();
  }

  @Override
  public HttpOutput getHttpOutput() {
    throw new AssertionError();
  }

  @Override
  public boolean isIncluding() {
    throw new AssertionError();
  }

  @Override
  public void include() {
    throw new AssertionError();
  }

  @Override
  public void included() {
    throw new AssertionError();
  }

  @Override
  public void addCookie(HttpCookie cookie) {
    throw new AssertionError();
  }

  @Override
  public void addCookie(Cookie cookie) {
    throw new AssertionError();
  }

  @Override
  public boolean containsHeader(String name) {
    return response.containsHeader(name);
  }

  @Override
  public String encodeURL(String url) {
    return response.encodeURL(url);
  }

  @Override
  public String encodeRedirectURL(String url) {
    return response.encodeRedirectURL(url);
  }

  @Override
  public String encodeUrl(String url) {
    return response.encodeUrl(url);
  }

  @Override
  public String encodeRedirectUrl(String url) {
    return response.encodeRedirectUrl(url);
  }

  @Override
  public void sendError(int sc) throws IOException {
    throw new AssertionError();
  }

  @Override
  public void sendError(int code, String message) throws IOException {
    throw new AssertionError();
  }

  @Override
  public void sendProcessing() throws IOException {
    throw new AssertionError();
  }

  @Override
  public void sendRedirect(String location) throws IOException {
    throw new AssertionError();
  }

  @Override
  public void setDateHeader(String name, long date) {
    throw new AssertionError();
  }

  @Override
  public void addDateHeader(String name, long date) {
    throw new AssertionError();
  }

  @Override
  public void setHeader(HttpHeader name, String value) {
    throw new AssertionError();
  }

  @Override
  public void setHeader(String name, String value) {
    throw new AssertionError();
  }

  @Override
  public Collection<String> getHeaderNames() {
    return response.getHeaderNames();
  }

  @Override
  public String getHeader(String name) {
    return response.getHeader(name);
  }

  @Override
  public Collection<String> getHeaders(String name) {
    return response.getHeaders(name);
  }

  @Override
  public void addHeader(String name, String value) {
    throw new AssertionError();
  }

  @Override
  public void setIntHeader(String name, int value) {
    throw new AssertionError();
  }

  @Override
  public void addIntHeader(String name, int value) {
    throw new AssertionError();
  }

  @Override
  public void setStatus(int sc) {
    throw new AssertionError();
  }

  @Override
  public void setStatus(int sc, String sm) {
    throw new AssertionError();
  }

  @Override
  public void setStatusWithReason(int sc, String sm) {
    throw new AssertionError();
  }

  @Override
  public String getCharacterEncoding() {
    return response.getCharacterEncoding();
  }

  @Override
  public String getContentType() {
    return response.getContentType();
  }

  @Override
  public ServletOutputStream getOutputStream() throws IOException {
    throw new AssertionError();
  }

  @Override
  public boolean isWriting() {
    throw new AssertionError();
  }

  @Override
  public PrintWriter getWriter() throws IOException {
    throw new AssertionError();
  }

  @Override
  public void setContentLength(int len) {
    throw new AssertionError();
  }

  @Override
  public boolean isAllContentWritten(long written) {
    throw new AssertionError();
  }

  @Override
  public void closeOutput() throws IOException {
    throw new AssertionError();
  }

  @Override
  public long getLongContentLength() {
    return response.getBufferSize();
  }

  @Override
  public void setLongContentLength(long len) {
    throw new AssertionError();
  }

  @Override
  public void setCharacterEncoding(String encoding) {
    throw new AssertionError();
  }

  @Override
  public void setContentType(String contentType) {
    throw new AssertionError();
  }

  @Override
  public void setBufferSize(int size) {
    throw new AssertionError();
  }

  @Override
  public int getBufferSize() {
    return response.getBufferSize();
  }

  @Override
  public void flushBuffer() throws IOException {
    throw new AssertionError();
  }

  @Override
  public void reset() {
    throw new AssertionError();
  }

  @Override
  public void reset(boolean preserveCookies) {
    throw new AssertionError();
  }

  @Override
  public void resetForForward() {
    throw new AssertionError();
  }

  @Override
  public void resetBuffer() {
    throw new AssertionError();
  }

  @Override
  public boolean isCommitted() {
    throw new AssertionError();
  }

  @Override
  public void setLocale(Locale locale) {
    throw new AssertionError();
  }

  @Override
  public Locale getLocale() {
    return response.getLocale();
  }

  @Override
  public int getStatus() {
    return response.getStatus();
  }

  @Override
  public String getReason() {
    throw new AssertionError();
  }

  @Override
  public HttpFields getHttpFields() {
    throw new AssertionError();
  }

  @Override
  public long getContentCount() {
    throw new AssertionError();
  }

  @Override
  public String toString() {
    return response.toString();
  }
}
