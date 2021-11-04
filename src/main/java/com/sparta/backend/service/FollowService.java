package com.sparta.backend.service;

import com.sparta.backend.domain.Follow;
import com.sparta.backend.domain.User;
import com.sparta.backend.repository.FollowRepository;
import com.sparta.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class FollowService {

    private final UserRepository userRepository;
    private final FollowRepository followRepository;

    // 팔로우
    public void follow(Long userId, String nickname) {

        // 로그인한 사용자
        User fromUser = userRepository.findById(userId).orElseThrow(
                () -> new NullPointerException("존재하지 않는 사용자입니다")
        );

        // 팔로우 신청하려는 사용자
        User toUser = userRepository.findByNickname(nickname).orElseThrow(
                () -> new NullPointerException("존재하지 않는 사용자입니다")
        );

        Follow follow = new Follow(fromUser, toUser);

        followRepository.save(follow);
    }
}