package com.sparta.backend.service.Recipe;

import com.sparta.backend.domain.Recipe.RecipeComment;
import com.sparta.backend.domain.Recipe.Recipe;
import com.sparta.backend.domain.Recipe.RecipeCommentLikes;
import com.sparta.backend.domain.User;
import com.sparta.backend.dto.request.recipes.PostCommentRequestDto;
import com.sparta.backend.dto.request.recipes.RecipeCommentUpdateRequestDto;
import com.sparta.backend.dto.response.recipes.RecipeCommentResponseDto;
import com.sparta.backend.exception.CustomErrorException;
import com.sparta.backend.repository.RecipeCommentLikeRepository;
import com.sparta.backend.repository.RecipeCommentRepository;
import com.sparta.backend.repository.RecipeRepository;
import com.sparta.backend.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class RecipeCommentService {
    private final RecipeCommentRepository commentRepository;
    private final RecipeRepository recipeRepository;
    private final RecipeCommentLikeRepository commentLikeReposiotory;

    //댓글 저장
    public void saveComment(PostCommentRequestDto requestDto, UserDetailsImpl userDetails) {
        Recipe recipe = recipeRepository.findById(requestDto.getRecipeId()).orElseThrow(()->
                new CustomErrorException("해당 댓글의 레시피가 존재하지 않습니다."));
        RecipeComment recipeComment = new RecipeComment(requestDto.getContent(),userDetails.getUser(), recipe);
        commentRepository.save(recipeComment);
    }

    //레시피에 대한 댓글들 조회- 리스트로 리턴
//    public List<RecipeCommentResponseDto> getComment(Long recipeId, UserDetailsImpl userDetails){
//        List<RecipeComment> recipeCommentList = commentRepository.findAllByRecipeIdOrderByregDateDesc(recipeId);
//        List<RecipeCommentResponseDto> responseDtoList = new ArrayList<>();
//
//        recipeCommentList.forEach(comment ->
//                responseDtoList.add(new RecipeCommentResponseDto(
//                        comment, userDetails, commentLikeReposiotory
//                        )));
//        return responseDtoList;
//    }

    //레시피에 대한 댓글들 조회- 페이지로 리턴
    public Page<RecipeCommentResponseDto> getCommentByPage(Long recipeId, int page, int size, boolean isAsc,UserDetailsImpl userDetails){

        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, "regDate");
        Pageable pageable = PageRequest.of(page,size,sort);
        Page<RecipeComment> comments = commentRepository.findAllByRecipeId(recipeId,pageable);

        //필요한 정보만 리턴
        Page<RecipeCommentResponseDto> responseDtos = comments.map((comment -> new RecipeCommentResponseDto(comment,userDetails,commentLikeReposiotory)));
        return responseDtos;
    }


    //댓글 삭제
    public void deleteComment(Long commentId, UserDetailsImpl userDetails) {
        commentRepository.deleteById(commentId);
    }

    //댓글 수정
    public void updateComment(Long commentId, RecipeCommentUpdateRequestDto updateRequestDto, UserDetailsImpl userDetails) {
        RecipeComment recipeComment = commentRepository.findById(commentId).orElseThrow(()->
                new CustomErrorException("해당 댓글이 존재하지 않습니다"));
        recipeComment.updateComment(updateRequestDto.getContent());
    }

    public Optional<RecipeComment> findById(Long recipeId) {
        return commentRepository.findById(recipeId);
    }

    public String likeComment(Long commentId, User user) {
        RecipeComment comment = commentRepository.findById(commentId).orElseThrow(()->
                new CustomErrorException("해당 게시물이 존재하지 않아요"));
        //이미 좋아요 누른건지 확인하기
        Optional<RecipeCommentLikes> foundCommentLikes = commentLikeReposiotory.findByRecipeCommentAndUser(comment, user);
        if(foundCommentLikes.isPresent()){
            //이미 좋아요를 눌렀으면 좋아요취소
            commentLikeReposiotory.delete(foundCommentLikes.get());
            return "좋아요 취소 성공";
        }else{
            RecipeCommentLikes commentLikes = new RecipeCommentLikes(user, comment);
            commentLikeReposiotory.save(commentLikes);
            return "좋아요 등록 성공";
        }
    }
}
