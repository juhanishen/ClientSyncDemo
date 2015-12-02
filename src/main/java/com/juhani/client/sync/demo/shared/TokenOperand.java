package com.juhani.client.sync.demo.shared;

public class TokenOperand {
  private boolean tokenTaken;
  private String clientName;

  public boolean isTokenTaken() {
    return tokenTaken;
  }

  public void setTokenTaken(boolean tokenTaken) {
    this.tokenTaken = tokenTaken;
  }

  public String getClientName() {
    return clientName;
  }

  public void setClientName(String clientName) {
    this.clientName = clientName;
  }
}
