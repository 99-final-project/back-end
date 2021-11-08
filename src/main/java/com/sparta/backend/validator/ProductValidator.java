package com.sparta.backend.validator;

import com.sparta.backend.domain.User;

public class ProductValidator {
    public static void validateRecipeInput(String title, String content, User user){
        if(user == null){
            throw  new IllegalArgumentException("로그인 되지 않은 사용자입니다");
        }
        if(user.getId() == null || user.getId()<0){
            throw new IllegalArgumentException("회원 id가 유효하지 않습니다.");
        }

        if(title == null || title.isEmpty()){
            throw new IllegalArgumentException("제목이 입력되지 않았습니다.");
        }

        //todo title이나 글자길이도 신경써야 함.
        if(title.length() > 200){
            throw new IllegalArgumentException("제목의 길이가 200자를 초과하였습니다.");
        }

        if(content.length()>1000){
            throw new IllegalArgumentException("내용의 길이가 1000자를 초과하였습니다");
        }
    }
}