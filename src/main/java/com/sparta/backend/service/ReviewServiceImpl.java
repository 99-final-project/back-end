package com.sparta.backend.service;

import com.sparta.backend.domain.Product;
import com.sparta.backend.domain.Review;
import com.sparta.backend.domain.User;
import com.sparta.backend.dto.request.review.PutReviewRequestDto;
import com.sparta.backend.dto.response.review.GetReviewResponseDto;
import com.sparta.backend.repository.ProductRepository;
import com.sparta.backend.repository.ReviewRepository;
import com.sparta.backend.dto.request.review.PostReviewRequestDto;
import com.sparta.backend.dto.response.review.PostReviewResponseDto;
import com.sparta.backend.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;

    //리뷰 작성
    @Transactional
    public PostReviewResponseDto createReview(PostReviewRequestDto requestDto) { //todo: 매개변수 userImpl 추가
        //User user = userDetails.getUser(); //todo:로그인한 사용자 정보

        Long productId = requestDto.getProductId();     //제품 아이디

        Product product = productRepository.findById(productId).orElseThrow(
                () -> new NullPointerException("찾는 제품이 없습니다.")
        );

        Review review = new Review(requestDto, product); //todo:(requestDto, product, user)
        reviewRepository.save(review);

        //String nickname = user.getNickname();           //todo: 로그인한 사용자 닉네임
        String nickname = "aaa";                        //todo: 임시 닉네임
        String title = review.getTitle();               //제목
        String content = review.getContent();           //리뷰 내용
        String image = review.getImage();               //사진
        int star = review.getStar();                    //별점
        LocalDateTime regDate = review.getRegDate();    //작성 시간

        PostReviewResponseDto responseDto =
                new PostReviewResponseDto(productId, nickname, title, content, image, star, regDate);

        return responseDto;
    }

    //리뷰 전체 조회
   public List<GetReviewResponseDto> getReviews(Long productId) {

        Product product = productRepository.findById(productId).orElseThrow(
                () -> new NullPointerException("찾는 제품이 없습니다.")
        );

       List<GetReviewResponseDto> responseDtoList = new ArrayList<>();

        if(product != null) {
            List<Review> reviewList = product.getReviewList();

            for(Review review: reviewList) {
                Long reviewId = review.getId();
                String title = review.getTitle();
                String nickname = "aaa"; //todo: 로그인 기능 추가 시 삭제
//                String nickname = review.getUser().getNickname(); //todo: 로그인 기능 추가 시 주석 해제
                String image = review.getImage();
                String content = review.getContent();
                int star = review.getStar();
                LocalDateTime regdate = review.getRegDate();

                GetReviewResponseDto responseDto =
                        new GetReviewResponseDto(reviewId, title, nickname, image, content, star, regdate);

                responseDtoList.add(responseDto);
            }
        }

        return responseDtoList;
   }

   //해당 제품에 대한 상세 리뷰 조회
   public GetReviewResponseDto getDetailReview(Long reviewId) {

       Review review = reviewRepository.findById(reviewId).orElseThrow(
               () -> new NullPointerException("해당 게시물을 찾을 수 없습니다.")
       );

       String title = review.getTitle();
//       String nickname = review.getUser().getNickname(); //todo: 로그인 추가 시 주석 해제
       String nickname = "aaa"; //todo: 로그인 추가 시 삭제
       String image = review.getImage();
       String content = review.getContent();
       int star = review.getStar();
       LocalDateTime regDate = review.getRegDate();

       GetReviewResponseDto responseDto =
               new GetReviewResponseDto(reviewId, title, nickname, image, content, star, regDate);

       return responseDto;
   }

    //리뷰 수정
    @Transactional
    public Long updateReview(Long reviewId, PutReviewRequestDto requestDto,
                             UserDetailsImpl userDetails) {

        Review review = reviewRepository.findById(reviewId).orElseThrow(
                () -> new NullPointerException("찾는 리뷰가 없습니다.")
        );

        //게시물을 쓴 사용자 아이디와 현재 로그인한 사용자 아이디가 일치할 때
//        if(review.getUser().getEmail().equals(userDetails.getUser().getEmail())) { //todo: 로그인 추가 시 주석 해제
            String title = requestDto.getTitle();
            String content = requestDto.getContent();
            String image = requestDto.getImage();

            //이미지 이름이 다르면 update
            if(!review.getImage().equals(image)) {
                review.update(requestDto);
            } else { //이미지 이름이 같으면 이미지 수정은 안 하기
                review.updateWithoutImage(requestDto);
            }
//        } else { //todo: 로그인 추가 시 주석 해제
//            throw new IllegalArgumentException("해당 게시물을 작성한 사용자만 수정 가능합니다.");
//        }
        return review.getId();
    }


}