package com.credo.credo_server.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.credo.credo_server.client.WeChatClient;
import com.credo.credo_server.client.WeChatPhoneResult;
import com.credo.credo_server.client.WeChatSessionResult;
import com.credo.credo_server.common.BusinessException;
import com.credo.credo_server.common.ErrorCode;
import com.credo.credo_server.config.WeChatProperties;
import com.credo.credo_server.dto.auth.PhoneLoginResponse;
import com.credo.credo_server.entity.UserAccount;
import com.credo.credo_server.entity.UserWechatBind;
import com.credo.credo_server.mapper.UserAccountMapper;
import com.credo.credo_server.mapper.UserWechatBindMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

	@Mock
	private WeChatClient weChatClient;

	@Mock
	private WeChatProperties weChatProperties;

	@Mock
	private UserAccountMapper userAccountMapper;

	@Mock
	private UserWechatBindMapper userWechatBindMapper;

	@Mock
	private JwtService jwtService;

	@InjectMocks
	private AuthService authService;

	@Test
	@DisplayName("registers new user and binds openid")
	void phoneLogin_newUser_returnsTokenAndIsNewUser() {
		when(weChatProperties.getMiniAppId()).thenReturn("wxbda744f66076ee8e");
		when(weChatClient.code2Session("login-code")).thenReturn(new WeChatSessionResult("openid-1", null));
		when(weChatClient.getPhoneNumber("phone-code"))
			.thenReturn(new WeChatPhoneResult("13800138000", "86"));
		when(userAccountMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);
		when(userAccountMapper.insert(any(UserAccount.class))).thenAnswer(invocation -> {
			UserAccount user = invocation.getArgument(0);
			user.setId(1L);
			return 1;
		});
		when(userWechatBindMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);
		when(jwtService.generateToken(1L, "13800138000")).thenReturn("jwt-token");

		PhoneLoginResponse response = authService.phoneLogin("login-code", "phone-code");

		assertThat(response.isNewUser()).isTrue();
		assertThat(response.getToken()).isEqualTo("jwt-token");
		assertThat(response.getUser().getPhone()).isEqualTo("13800138000");

		ArgumentCaptor<UserWechatBind> bindCaptor = ArgumentCaptor.forClass(UserWechatBind.class);
		verify(userWechatBindMapper).insert(bindCaptor.capture());
		assertThat(bindCaptor.getValue().getOpenId()).isEqualTo("openid-1");
		assertThat(bindCaptor.getValue().getUserId()).isEqualTo(1L);
	}

	@Test
	@DisplayName("logs in existing active user")
	void phoneLogin_existingUser_returnsTokenAndIsNotNewUser() {
		when(weChatProperties.getMiniAppId()).thenReturn("wxbda744f66076ee8e");
		UserAccount existing = new UserAccount();
		existing.setId(2L);
		existing.setPhone("13800138000");
		existing.setStatus(1);

		when(weChatClient.code2Session("login-code")).thenReturn(new WeChatSessionResult("openid-2", "union-2"));
		when(weChatClient.getPhoneNumber("phone-code"))
			.thenReturn(new WeChatPhoneResult("13800138000", "86"));
		when(userAccountMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(existing);
		when(userWechatBindMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);
		when(jwtService.generateToken(2L, "13800138000")).thenReturn("jwt-token");

		PhoneLoginResponse response = authService.phoneLogin("login-code", "phone-code");

		assertThat(response.isNewUser()).isFalse();
		assertThat(response.getToken()).isEqualTo("jwt-token");
		verify(userAccountMapper).updateById(existing);
		verify(userAccountMapper, never()).insert(any(UserAccount.class));
	}

	@Test
	@DisplayName("rejects disabled user")
	void phoneLogin_disabledUser_throwsAccountDisabled() {
		UserAccount disabled = new UserAccount();
		disabled.setId(3L);
		disabled.setPhone("13800138000");
		disabled.setStatus(0);

		when(weChatClient.code2Session("login-code")).thenReturn(new WeChatSessionResult("openid-3", null));
		when(weChatClient.getPhoneNumber("phone-code"))
			.thenReturn(new WeChatPhoneResult("13800138000", "86"));
		when(userAccountMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(disabled);

		assertThatThrownBy(() -> authService.phoneLogin("login-code", "phone-code"))
			.isInstanceOf(BusinessException.class)
			.extracting(ex -> ((BusinessException) ex).getErrorCode())
			.isEqualTo(ErrorCode.ACCOUNT_DISABLED);
	}

	@Test
	@DisplayName("propagates invalid login code from WeChat client")
	void phoneLogin_invalidLoginCode_throwsWeChatAuthFailed() {
		when(weChatClient.code2Session("bad-login"))
			.thenThrow(new BusinessException(ErrorCode.WECHAT_AUTH_FAILED));

		assertThatThrownBy(() -> authService.phoneLogin("bad-login", "phone-code"))
			.isInstanceOf(BusinessException.class)
			.extracting(ex -> ((BusinessException) ex).getErrorCode())
			.isEqualTo(ErrorCode.WECHAT_AUTH_FAILED);
	}

	@Test
	@DisplayName("propagates invalid phone code from WeChat client")
	void phoneLogin_invalidPhoneCode_throwsWeChatAuthFailed() {
		when(weChatClient.code2Session("login-code")).thenReturn(new WeChatSessionResult("openid-4", null));
		when(weChatClient.getPhoneNumber("bad-phone"))
			.thenThrow(new BusinessException(ErrorCode.WECHAT_AUTH_FAILED));

		assertThatThrownBy(() -> authService.phoneLogin("login-code", "bad-phone"))
			.isInstanceOf(BusinessException.class)
			.extracting(ex -> ((BusinessException) ex).getErrorCode())
			.isEqualTo(ErrorCode.WECHAT_AUTH_FAILED);
	}

	@Test
	@DisplayName("updates existing openid bind to current user")
	void phoneLogin_existingOpenIdBind_updatesBind() {
		when(weChatProperties.getMiniAppId()).thenReturn("wxbda744f66076ee8e");
		UserAccount existing = new UserAccount();
		existing.setId(5L);
		existing.setPhone("13800138000");
		existing.setStatus(1);

		UserWechatBind bind = new UserWechatBind();
		bind.setId(10L);
		bind.setUserId(99L);
		bind.setOpenId("openid-5");

		when(weChatClient.code2Session("login-code")).thenReturn(new WeChatSessionResult("openid-5", "union-5"));
		when(weChatClient.getPhoneNumber("phone-code"))
			.thenReturn(new WeChatPhoneResult("13800138000", "86"));
		when(userAccountMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(existing);
		when(userWechatBindMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(bind);
		when(jwtService.generateToken(5L, "13800138000")).thenReturn("jwt-token");

		authService.phoneLogin("login-code", "phone-code");

		assertThat(bind.getUserId()).isEqualTo(5L);
		assertThat(bind.getUnionId()).isEqualTo("union-5");
		verify(userWechatBindMapper).updateById(bind);
		verify(userWechatBindMapper, never()).insert(any(UserWechatBind.class));
	}
}
