package com.github.twohou.sonic;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class IngestChannel extends Channel {
  public IngestChannel(
      @NonNull String address,
      @NonNull Integer port,
      @NonNull String password,
      @NonNull Integer connectionTimeout,
      @NonNull Integer readTimeout)
      throws IOException {
    super(address, port, password, connectionTimeout, readTimeout);
    this.start(Mode.ingest);
  }

  public void push(
      @NonNull String collection,
      @NonNull String bucket,
      @NonNull String object,
      @NonNull String text)
      throws IOException {
    String p = String.format("PUSH %s %s %s \"%s\"", collection, bucket, object, text);
    log.debug("Sonic PUSH: {}", p);
    this.send(p);
    this.assertOK();
  }

  public void push(
      @NonNull String collection,
      @NonNull String bucket,
      @NonNull Integer object,
      @NonNull String text)
      throws IOException {
    String p = String.format("PUSH %s %s %d \"%s\"", collection, bucket, object, text);
    log.debug("Sonic PUSH: {}", p);
    this.send(p);
    this.assertOK();
  }

  public void pop(
      @NonNull String collection,
      @NonNull String bucket,
      @NonNull String object,
      @NonNull String text)
      throws IOException {
    String p = String.format("POP %s %s %s \"%s\"", collection, bucket, object, text);
    log.debug("Sonic POP: {}", p);
    this.send(p);
    this.assertOK();
  }

  public Integer count(@NonNull String collection, String bucket, String object)
      throws IOException {
    if (bucket == null && object != null) {
      throw new IllegalArgumentException("bucket is required for counting an object");
    }

    String c =
        String.format(
            "COUNT %s%s%s",
            collection, bucket == null ? "" : " " + bucket, object == null ? "" : " " + object);
    log.debug("Sonic COUNT: {}", c);
    this.send(c);
    return this.assertResult();
  }

  public Integer count(@NonNull String collection, String bucket) throws IOException {
    return this.count(collection, bucket, null);
  }

  public Integer count(@NonNull String collection) throws IOException {
    return this.count(collection, null);
  }

  public Integer flushc(@NonNull String collection) throws IOException {
    String f = String.format("FLUSHC %s", collection);
    log.debug("Sonic FLUSHC: {}", f);
    this.send(f);
    return this.assertResult();
  }

  public Integer flushb(@NonNull String collection, @NonNull String bucket) throws IOException {
    String f = String.format("FLUSHB %s %s", collection, bucket);
    log.debug("Sonic FLUSHB: {}", f);
    this.send(f);
    return this.assertResult();
  }

  public Integer flusho(@NonNull String collection, @NonNull String bucket, @NonNull String object)
      throws IOException {
    String f = String.format("FLUSHO %s %s %s", collection, bucket, object);
    log.debug("Sonic FLUSHO: {}", f);
    this.send(f);
    return this.assertResult();
  }
}
