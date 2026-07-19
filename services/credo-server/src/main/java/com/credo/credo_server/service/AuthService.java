package com.credo.credo_server.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.credo.credo_server.client.WeChatClient;
import com.credo.credo_server.client.WeChatSessionResult;
import com.credo.credo_server.common.BusinessException;
import com.credo.credo_server.common.ErrorCode;
import com.credo.credo_server.config.WeChatProperties;
import com.credo.credo_server.dto.auth.PhoneLoginResponse;
import com.credo.credo_server.dto.auth.UserDto;
import com.credo.credo_server.entity.UserAccount;
import com.credo.credo_server.entity.UserWechatBind;
import com.credo.credo_server.mapper.UserAccountMapper;
import com.credo.credo_server.mapper.UserWechatBindMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AuthService {

	private static final int STATUS_ACTIVE = 1;

	private final WeChatClient weChatClient;
	private final WeChatProperties weChatProperties;
	private final UserAccountMapper userAccountMapper;
	private final UserWechatBindMapper userWechatBindMapper;
	private final JwtService jwtService;

	public AuthService(
		WeChatClient weChatClient,
		WeChatProperties weChatProperties,
		UserAccountMapper userAccountMapper,
		UserWechatBindMapper userWechatBindMapper,
		JwtService jwtService
	) {
		this.weChatClient = weChatClient;
		this.weChatProperties = weChatProperties;
		this.userAccountMapper = userAccountMapper;
		this.userWechatBindMapper = userWechatBindMapper;
		this.jwtService = jwtService;
	}

	@Transactional
	public PhoneLoginResponse wechatLogin(String loginCode) {
		WeChatSessionResult session = weChatClient.code2Session(loginCode);
		String openId = session.openId();

		UserWechatBind bind = userWechatBindMapper.selectOne(
			new LambdaQueryWrapper<UserWechatBind>().eq(UserWechatBind::getOpenId, openId)
		);

		boolean isNewUser = false;
		UserAccount user;

		if (bind == null) {
			user = createUserWithoutPhone();
			isNewUser = true;
			insertWechatBind(user.getId(), openId, session.unionId());
		} else {
			user = userAccountMapper.selectById(bind.getUserId());
			if (user == null) {
				throw new BusinessException(ErrorCode.INTERNAL_ERROR, "WeChat bind references missing user");
			}
			if (user.getStatus() == null || user.getStatus() != STATUS_ACTIVE) {
				throw new BusinessException(ErrorCode.ACCOUNT_DISABLED);
			}
			user.setLastLoginAt(LocalDateTime.now());
			userAccountMapper.updateById(user);
			updateWechatBind(bind, session.unionId());
		}

		String token = jwtService.generateToken(user.getId(), user.getPhone());
		UserDto userDto = new UserDto(user.getId(), user.getPhone(), user.getNickname(), user.getAvatarUrl());
		return new PhoneLoginResponse(token, userDto, isNewUser);
	}

	private UserAccount createUserWithoutPhone() {
		UserAccount user = new UserAccount();
		user.setPhone(null);
		user.setCountryCode("86");
		user.setStatus(STATUS_ACTIVE);
		user.setLastLoginAt(LocalDateTime.now());
		userAccountMapper.insert(user);
		return user;
	}

	private void insertWechatBind(Long userId, String openId, String unionId) {
		UserWechatBind bind = new UserWechatBind();
		bind.setUserId(userId);
		bind.setAppId(weChatProperties.getMiniAppId());
		bind.setOpenId(openId);
		bind.setUnionId(unionId);
		userWechatBindMapper.insert(bind);
	}

	private void updateWechatBind(UserWechatBind bind, String unionId) {
		bind.setAppId(weChatProperties.getMiniAppId());
		bind.setUnionId(unionId);
		userWechatBindMapper.updateById(bind);
	}
}
