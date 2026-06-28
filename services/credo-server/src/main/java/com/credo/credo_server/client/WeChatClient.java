package com.credo.credo_server.client;

public interface WeChatClient {

	WeChatSessionResult code2Session(String loginCode);

	WeChatPhoneResult getPhoneNumber(String phoneCode);
}
