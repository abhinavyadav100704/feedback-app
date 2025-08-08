package com.abhinav.feedback.services;

import com.abhinav.feedback.entities.Feedback;
import com.abhinav.feedback.exception.FeedbackNotFoundException;
import com.abhinav.feedback.repositories.FeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeedbackServiceImpl implements FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    public FeedbackServiceImpl(FeedbackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }

    @Override
    public Feedback saveFeedback(Feedback feedback) {
        try {
            return feedbackRepository.save(feedback);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save feedback: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Feedback> getAllFeedbacks() {
        try {
            return feedbackRepository.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve feedbacks: " + e.getMessage(), e);
        }
    }

    @Override
    public Feedback getFeedbackById(Integer id) {
        return feedbackRepository.findById(id)
                .orElseThrow(() -> new FeedbackNotFoundException("Feedback with ID " + id + " not found."));
    }
}
