package com.sparta.backend.controller.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sparta.backend.domain.user.User;
import com.sparta.backend.dto.request.user.*;
import com.sparta.backend.dto.response.CustomResponseDto;
import com.sparta.backend.dto.response.user.GetUserInfoResponseDto;
import com.sparta.backend.dto.response.user.UserInfoResponseDto;
import com.sparta.backend.exception.CustomErrorException;
import com.sparta.backend.security.JwtTokenProvider;
import com.sparta.backend.security.UserDetailsImpl;
import com.sparta.backend.service.user.KakaoUserService;
import com.sparta.backend.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;
    private final KakaoUserService kakaoUserService;
    private final JwtTokenProvider jwtTokenProvider;

    // 회원가입 요청
    @PostMapping("/user/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequestDto requestDto, Errors errors) {

        if(errors.hasErrors()){
            return new ResponseEntity<>(
                    new CustomResponseDto<>(-1, "회원가입 실패", errors.getAllErrors()),
                    HttpStatus.BAD_REQUEST
            );
        }

        userService.registerUser(requestDto);
        return new ResponseEntity<>(
                new CustomResponseDto<>(1, "회원가입 성공", ""), HttpStatus.OK);
    }

    // 이메일 중복 체크
    @PostMapping("/user/signup/email")
    public ResponseEntity<?> validCheckEmail(@RequestBody ValidEmailRequestDto requestDto) {

        int result = userService.validCheckEmail(requestDto.getEmail());

        if (result == 0) {
            return new ResponseEntity<>(
                    new CustomResponseDto<>(1, "사용할 수 있는 이메일입니다", ""), HttpStatus.OK);
        } else if (result == 1) {
            return new ResponseEntity<>(
                    new CustomResponseDto<>(-1, "이미 존재하는 이메일입니다", ""), HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<>(
                    new CustomResponseDto<>(-1, "이메일 형식이 아닙니다", ""), HttpStatus.BAD_REQUEST);
        }
    }

    // 닉네임 중복 체크
    @PostMapping("/user/signup/nickname")
    public ResponseEntity<?> validCheckNickname(@RequestBody ValidNicknameRequestDto requestDto) {

        log.info("nickname = {}", requestDto.getNickname());

        int result = userService.validCheckNickname(requestDto.getNickname());

        if (result == 0) {
            return new ResponseEntity<>(
                    new CustomResponseDto<>(1, "사용할 수 있는 닉네임입니다", ""), HttpStatus.OK);
        } else if (result == 1) {
            return new ResponseEntity<>(
                    new CustomResponseDto<>(-1, "이미 존재하는 닉네임입니다", ""), HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<>(
                    new CustomResponseDto<>(-1, "잘못된 닉네임 형식입니다", ""), HttpStatus.BAD_REQUEST);
        }
    }


    // 로그인 요청
    @PostMapping("/user/login")
    public ResponseEntity<?> login(@RequestBody SignupRequestDto requestDto) {

        GetUserInfoResponseDto responseDto = userService.login(requestDto);

        return new ResponseEntity<>(
                new CustomResponseDto<>(1, "환영합니다", responseDto), HttpStatus.OK);
    }

    // 카카오 로그인
    @GetMapping("/user/kakao/callback")
    public ResponseEntity<?> kakaoLogin(@RequestParam String code) throws JsonProcessingException {

        User kakaoUser = kakaoUserService.kakaoLogin(code);

        String token = jwtTokenProvider.createToken(kakaoUser.getEmail());
        String nickname = kakaoUser.getNickname();
        String image = kakaoUser.getImage();

        GetUserInfoResponseDto responseDto = new GetUserInfoResponseDto(token, nickname, image);

        return new ResponseEntity<>(
                new CustomResponseDto<>(1, "환영합니다", responseDto), HttpStatus.OK);
    }

    // 회원 정보 조회
    @GetMapping("/user/info")
    public ResponseEntity<?> userInfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {

        checkLogin(userDetails);

        UserInfoResponseDto responseDto =
                new UserInfoResponseDto(userDetails.getUser().getNickname(), userDetails.getUser().getImage());

        return new ResponseEntity<>(
                new CustomResponseDto<>(1, "회원 정보 조회 성공", responseDto), HttpStatus.OK);
    }

    // 회원 정보 수정
    @PutMapping("/user/info")
    public ResponseEntity<?> updateUser(UpdateUserRequestDto requestDto,
                                        @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {

        checkLogin(userDetails);

        userService.updateUser(userDetails, requestDto);

        return new ResponseEntity<>(
                new CustomResponseDto<>(1, "회원 정보 수정 성공", ""), HttpStatus.OK);
    }

    // 닉네임 수정
    @PutMapping("/user/info/nickname")
    public ResponseEntity<?> updateNickname(@RequestBody UpdateNicknameRequestDto requestDto,
                                            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        checkLogin(userDetails);

        userService.updateNickname(userDetails, requestDto);

        return new ResponseEntity<>(
                new CustomResponseDto<>(1, "닉네임 수정 성공", ""), HttpStatus.OK);
    }

    // 회원 탈퇴
    @PutMapping("/user/delete")
    public ResponseEntity<?> deleteUser(@AuthenticationPrincipal UserDetailsImpl userDetails) {

        checkLogin(userDetails);

        userService.deleteUser(userDetails);

        return new ResponseEntity<>(
                new CustomResponseDto<>(1, "회원 탈퇴 성공", ""), HttpStatus.OK);
    }

    private void checkLogin(@AuthenticationPrincipal UserDetailsImpl userDetails) {

        if (userDetails == null) {
            throw new CustomErrorException("로그인된 유저만 사용가능한 기능입니다.");
        }
    }
}
