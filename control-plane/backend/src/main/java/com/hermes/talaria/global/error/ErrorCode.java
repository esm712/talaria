package com.hermes.talaria.global.error;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ErrorCode {
	// 인증
	EXPIRED_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "401001", "토큰이 만료되었습니다."),
	NOT_VALID_TOKEN(HttpStatus.UNAUTHORIZED, "401002", "해당 토큰은 유효한 토큰이 아닙니다."),
	EMPTY_AUTHORIZATION(HttpStatus.UNAUTHORIZED, "401003", "Authorization Header가 빈값입니다."),
	NOT_VALID_BEARER_GRANT_TYPE(HttpStatus.UNAUTHORIZED, "401004", "인증 타입이 Bearer 타입이 아닙니다."),
	REFRESH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "401005", "해당 refresh token은 존재하지 않습니다."),
	NOT_EXPIRED_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "401006", "토큰이 만료되지 않았습니다."),

	// 인가
	WRONG_AUTHORITY(HttpStatus.FORBIDDEN, "403001", "잘못된 Role 입니다."),

	// 회원
	ALREADY_REGISTERED_MEMBER(HttpStatus.BAD_REQUEST, "404001", "이미 가입된 회원입니다."),
	NOT_EXIST_MEMBER(HttpStatus.BAD_REQUEST, "404002", "존재하지 않는 회원입니다."),
	DELETED_MEMBER(HttpStatus.BAD_REQUEST, "404003", "삭제된 회원입니다."),

	//KEY
	NOT_EXIST_KEY(HttpStatus.BAD_REQUEST, "404011", "존재하지 않는 KEY입니다."),

	// APIS
	NOT_EXIST_APIS(HttpStatus.BAD_REQUEST, "404021", "존재하지 않는 API 그룹입니다."),
	DUPLICATED_APIS_NAME(HttpStatus.BAD_REQUEST, "404022", "중복되는 API 그룹입니다. 다른 이름을 입력하세요."),

	// Subscription
	NOT_EXIST_SUBSCRIPTION(HttpStatus.BAD_REQUEST, "404031", "존재하지 않는 구독 요청입니다."),
	NOT_EXIST_SUBSCRIPTION_STATUS(HttpStatus.BAD_REQUEST, "404032", "존재하지 않는 상태입니다.")
	;

	private HttpStatus httpStatus;
	private String errorCode;
	private String message;

	ErrorCode(HttpStatus httpStatus, String errorCode, String message) {
		this.httpStatus = httpStatus;
		this.errorCode = errorCode;
		this.message = message;
	}
}