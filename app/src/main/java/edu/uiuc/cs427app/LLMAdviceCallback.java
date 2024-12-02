package edu.uiuc.cs427app;


public interface LLMAdviceCallback {
    void onAdviceReceived(String advice);
    void onError(Throwable t);
}