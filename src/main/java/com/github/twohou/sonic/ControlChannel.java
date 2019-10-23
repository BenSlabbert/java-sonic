package com.github.twohou.sonic;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class ControlChannel extends Channel {
  public ControlChannel(
      @NonNull String address,
      @NonNull Integer port,
      @NonNull String password,
      @NonNull Integer connectionTimeout,
      @NonNull Integer readTimeout)
      throws IOException {
    super(address, port, password, connectionTimeout, readTimeout);
    this.start(Mode.control);
  }

  public void consolidate() throws IOException {
    String t = "TRIGGER consolidate";
    log.debug("Sonic TRIGGER: {}", t);
    this.send(t);
    this.assertOK();
  }
}
