package com.ssafy.db.repository;

import com.ssafy.db.entity.TutorialType;
import com.ssafy.db.entity.UserTutorialProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 유저 스탯 모델 관련 디비 쿼리 생성을 위한 JPA Query Method 인터페이스 정의.
 */
@Repository
public interface TutorialTypeRepository extends JpaRepository<TutorialType, Long> {
    TutorialType findByTutorialName(String tutorialName);
}
