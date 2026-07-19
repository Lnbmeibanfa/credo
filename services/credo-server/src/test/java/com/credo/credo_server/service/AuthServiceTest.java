package com.credo.credo_server.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.credo.credo_server.client.WeChatClient;
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
	@DisplayName("registers new user by openid with null phone")
	void wechatLogin_newUser_returnsTokenAndIsNewUser() {
		when(weChatProperties.getMiniAppId()).thenReturn("wxbda744f66076ee8e");
		when(weChatClient.code2Session("login-code")).thenReturn(new WeChatSessionResult("openid-1", null));
		when(userWechatBindMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);
		when(userAccountMapper.insert(any(UserAccount.class))).thenAnswer(invocation -> {
			UserAccount user = invocation.getArgument(0);
			user.setId(1L);
			return 1;
		});
		when(jwtService.generateToken(1L, null)).thenReturn("jwt-token");

		PhoneLoginResponse response = authService.wechatLogin("login-code");

		assertThat(response.isNewUser()).isTrue();
		assertThat(response.getToken()).isEqualTo("jwt-token");
		assertThat(response.getUser().getPhone()).isNull();

		ArgumentCaptor<UserAccount> userCaptor = ArgumentCaptor.forClass(UserAccount.class);
		verify(userAccountMapper).insert(userCaptor.capture());
		assertThat(userCaptor.getValue().getPhone()).isNull();

		ArgumentCaptor<UserWechatBind> bindCaptor = ArgumentCaptor.forClass(UserWechatBind.class);
		verify(userWechatBindMapper).insert(bindCaptor.capture());
		assertThat(bindCaptor.getValue().getOpenId()).isEqualTo("openid-1");
		assertThat(bindCaptor.getValue().getUserId()).isEqualTo(1L);
		verify(weChatClient, never()).getPhoneNumber(any());
	}

	@Test
	@DisplayName("logs in existing bound active user")
	void wechatLogin_existingUser_returnsTokenAndIsNotNewUser() {
		when(weChatProperties.getMiniAppId()).thenReturn("wxbda744f66076ee8e");
		UserAccount existing = new UserAccount();
		existing.setId(2L);
		existing.setPhone("13800138000");
		existing.setStatus(1);

		UserWechatBind bind = new UserWechatBind();
		bind.setId(10L);
		bind.setUserId(2L);
		bind.setOpenId("openid-2");

		when(weChatClient.code2Session("login-code")).thenReturn(new WeChatSessionResult("openid-2", "union-2"));
		when(userWechatBindMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(bind);
		when(userAccountMapper.selectById(2L)).thenReturn(existing);
		when(jwtService.generateToken(2L, "13800138000")).thenReturn("jwt-token");

		PhoneLoginResponse response = authService.wechatLogin("login-code");

		assertThat(response.isNewUser()).isFalse();
		assertThat(response.getToken()).isEqualTo("jwt-token");
		verify(userAccountMapper).updateById(existing);
		verify(userAccountMapper, never()).insert(any(UserAccount.class));
		verify(userWechatBindMapper).updateById(bind);
		assertThat(bind.getUnionId()).isEqualTo("union-2");
	}

	@Test
	@DisplayName("rejects disabled bound user")
	void wechatLogin_disabledUser_throwsAccountDisabled() {
		UserAccount disabled = new UserAccount();
		disabled.setId(3L);
		disabled.setStatus(0);

		UserWechatBind bind = new UserWechatBind();
		bind.setUserId(3L);
		bind.setOpenId("openid-3");

		when(weChatClient.code2Session("login-code")).thenReturn(new WeChatSessionResult("openid-3", null));
		when(userWechatBindMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(bind);
		when(userAccountMapper.selectById(3L)).thenReturn(disabled);

		assertThatThrownBy(() -> authService.wechatLogin("login-code"))
			.isInstanceOf(BusinessException.class)
			.extracting(ex -> ((BusinessException) ex).getErrorCode())
			.isEqualTo(ErrorCode.ACCOUNT_DISABLED);
	}

	@Test
	@DisplayName("propagates invalid login code from WeChat client")
	void wechatLogin_invalidLoginCode_throwsWeChatAuthFailed() {
		when(weChatClient.code2Session("bad-login"))
			.thenThrow(new BusinessException(ErrorCode.WECHAT_AUTH_FAILED));

		assertThatThrownBy(() -> authService.wechatLogin("bad-login"))
			.isInstanceOf(BusinessException.class)
			.extracting(ex -> ((BusinessException) ex).getErrorCode())
			.isEqualTo(ErrorCode.WECHAT_AUTH_FAILED);

		verify(userAccountMapper, never()).insert(any(UserAccount.class));
	}
}
