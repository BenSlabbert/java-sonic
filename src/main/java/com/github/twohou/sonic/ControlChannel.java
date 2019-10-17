package com.github.twohou.sonic;

import lombok.NonNull;

import java.io.IOException;

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
    this.send("TRIGGER consolidate");
    this.assertOK();
  }
}
