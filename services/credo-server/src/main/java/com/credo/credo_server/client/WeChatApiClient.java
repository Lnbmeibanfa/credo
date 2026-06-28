package com.credo.credo_server.client;

import com.credo.credo_server.common.BusinessException;
import com.credo.credo_server.common.ErrorCode;
import com.credo.credo_server.config.WeChatProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class WeChatApiClient implements WeChatClient {

	private final RestClient restClient;
	private final WeChatProperties weChatProperties;
	private final ObjectMapper objectMapper;

	public WeChatApiClient(
		RestClient.Builder restClientBuilder,
		WeChatProperties weChatProperties,
		ObjectMapper objectMapper
	) {
		this.restClient = restClientBuilder.build();
		this.weChatProperties = weChatProperties;
		this.objectMapper = objectMapper;
	}

	@Override
	public WeChatSessionResult code2Session(String loginCode) {
		String raw = restClient.get()
			.uri(uriBuilder -> uriBuilder
				.scheme("https")
				.host("api.weixin.qq.com")
				.path("/sns/jscode2session")
				.queryParam("appid", weChatProperties.getMiniAppId())
				.queryParam("secret", weChatProperties.getMiniAppSecret())
				.queryParam("js_code", loginCode)
				.queryParam("grant_type", "authorization_code")
				.build())
			.retrieve()
			.body(String.class);

		JsonNode response = parseJsonResponse(raw);
		assertWeChatSuccess(response);
		String openId = textOrNull(response, "openid");
		if (openId == null || openId.isBlank()) {
			throw new BusinessException(ErrorCode.WECHAT_AUTH_FAILED, "Missing openid from WeChat");
		}
		return new WeChatSessionResult(openId, textOrNull(response, "unionid"));
	}

	@Override
	public WeChatPhoneResult getPhoneNumber(String phoneCode) {
		String accessToken = fetchAccessToken();
		String raw = restClient.post()
			.uri("https://api.weixin.qq.com/wxa/business/getuserphonenumber?access_token={accessToken}", accessToken)
			.body("{\"code\":\"" + phoneCode + "\"}")
			.header("Content-Type", "application/json")
			.retrieve()
			.body(String.class);

		JsonNode response = parseJsonResponse(raw);
		assertWeChatSuccess(response);
		JsonNode phoneInfo = response.get("phone_info");
		if (phoneInfo == null || phoneInfo.isNull()) {
			throw new BusinessException(ErrorCode.WECHAT_AUTH_FAILED, "Missing phone_info from WeChat");
		}
		String purePhoneNumber = textOrNull(phoneInfo, "purePhoneNumber");
		if (purePhoneNumber == null || purePhoneNumber.isBlank()) {
			throw new BusinessException(ErrorCode.WECHAT_AUTH_FAILED, "Missing purePhoneNumber from WeChat");
		}
		String countryCode = textOrNull(phoneInfo, "countryCode");
		return new WeChatPhoneResult(purePhoneNumber, countryCode != null ? countryCode : "86");
	}

	private String fetchAccessToken() {
		String raw = restClient.get()
			.uri(uriBuilder -> uriBuilder
				.scheme("https")
				.host("api.weixin.qq.com")
				.path("/cgi-bin/token")
				.queryParam("grant_type", "client_credential")
				.queryParam("appid", weChatProperties.getMiniAppId())
				.queryParam("secret", weChatProperties.getMiniAppSecret())
				.build())
			.retrieve()
			.body(String.class);

		JsonNode response = parseJsonResponse(raw);
		assertWeChatSuccess(response);
		String accessToken = textOrNull(response, "access_token");
		if (accessToken == null || accessToken.isBlank()) {
			throw new BusinessException(ErrorCode.WECHAT_AUTH_FAILED, "Missing access_token from WeChat");
		}
		return accessToken;
	}

	private JsonNode parseJsonResponse(String body) {
		if (body == null || body.isBlank()) {
			throw new BusinessException(ErrorCode.WECHAT_AUTH_FAILED, "Empty WeChat response");
		}
		try {
			return objectMapper.readTree(body);
		} catch (JsonProcessingException e) {
			throw new BusinessException(ErrorCode.WECHAT_AUTH_FAILED, "Invalid WeChat response");
		}
	}

	private static void assertWeChatSuccess(JsonNode response) {
		if (response == null) {
			throw new BusinessException(ErrorCode.WECHAT_AUTH_FAILED, "Empty WeChat response");
		}
		JsonNode errcodeNode = response.get("errcode");
		if (errcodeNode != null && !errcodeNode.isNull() && errcodeNode.asInt() != 0) {
			String errmsg = textOrNull(response, "errmsg");
			throw new BusinessException(ErrorCode.WECHAT_AUTH_FAILED,
				errmsg != null ? errmsg : "WeChat API error");
		}
	}

	private static String textOrNull(JsonNode node, String field) {
		JsonNode value = node.get(field);
		if (value == null || value.isNull()) {
			return null;
		}
		return value.asText();
	}
}
