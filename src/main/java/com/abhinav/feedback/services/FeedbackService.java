package com.abhinav.feedback.services;

import com.abhinav.feedback.entities.Feedback;

import java.util.List;

public interface FeedbackService {

    Feedback saveFeedback(Feedback feedback);

    List<Feedback> getAllFeedbacks();

    Feedback getFeedbackById(Integer id);
}
