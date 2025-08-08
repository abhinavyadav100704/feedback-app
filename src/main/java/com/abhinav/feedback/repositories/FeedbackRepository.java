package com.abhinav.feedback.repositories;

import com.abhinav.feedback.entities.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackRepository extends JpaRepository<Feedback, Integer> {
}
