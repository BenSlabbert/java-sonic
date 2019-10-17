package com.github.twohou.sonic;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class ChannelFactory {

  private String address;

  private Integer port;

  private String password;

  private Integer connectionTimeout;

  private Integer readTimeout;

  public ChannelFactory(
      @NonNull String address,
      @NonNull Integer port,
      @NonNull String password,
      @NonNull Integer connectionTimeout,
      @NonNull Integer readTimeout) {
    this.address = address;
    this.port = port;
    this.password = password;
    this.connectionTimeout = connectionTimeout;
    this.readTimeout = readTimeout;
  }

  public IngestChannel newIngestChannel() throws IOException {
    log.debug("Creating new Ingest Channel");
    return new IngestChannel(
        this.address, this.port, this.password, this.connectionTimeout, this.readTimeout);
  }

  public SearchChannel newSearchChannel() throws IOException {
    log.debug("Creating new Search Channel");
    return new SearchChannel(
        this.address, this.port, this.password, this.connectionTimeout, this.readTimeout);
  }

  public ControlChannel newControlChannel() throws IOException {
    log.debug("Creating new Control Channel");
    return new ControlChannel(
        this.address, this.port, this.password, this.connectionTimeout, this.readTimeout);
  }
}
