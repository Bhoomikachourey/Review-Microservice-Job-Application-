package com.pack.reviews;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.pack.reviews.messaging.ReviewMessageProducer;

@Controller
@RequestMapping("/reviews")
public class ReviewController {

	private ReviewService reviewService;
	private ReviewMessageProducer reviewMessageProducer;
	@Autowired
	private ReviewRepository reviewRepository;
	
	
	public ReviewController(ReviewService reviewService, ReviewMessageProducer reviewMessageProducer) {
		super();
		this.reviewService = reviewService;
		this.reviewMessageProducer = reviewMessageProducer;
	}

	@GetMapping
	public ResponseEntity<List<Review>> getAllReviews(@RequestParam Long companyId){
		return new ResponseEntity<>(reviewService.getAllReviews(companyId), HttpStatus.OK);
	}
	
	@PostMapping
	public ResponseEntity<String> addReview(@RequestParam Long companyId,@RequestBody Review review)
	{
		boolean isReviewSaved= reviewService.addReview(companyId, review);
		if(isReviewSaved) {
			
			reviewMessageProducer.sendMessage(review);
		return new ResponseEntity<String>("Review added successfully", HttpStatus.OK);
		
		}else {
			return new ResponseEntity<String>("Review not saved", HttpStatus.NOT_FOUND);
		}
		}
	
	@GetMapping("/{reviewId}")
	public ResponseEntity<Review> getReview( @PathVariable Long reviewId){
		return new ResponseEntity<Review>(reviewService.getReview(reviewId), HttpStatus.OK);
	}
	
	@PutMapping("/{reviewId}")
	public ResponseEntity<String> updateReview(@PathVariable Long reviewId, @RequestBody Review review){
		boolean isReviewUpdated = reviewService.updateReview(reviewId, review);
		if(isReviewUpdated) {
			return new ResponseEntity<String>("review updated successfully", HttpStatus.OK);
			
		}else {
			return new ResponseEntity<String>("review not updated ", HttpStatus.NOT_FOUND);
			
		}
		}
	
	@DeleteMapping("/{reviewId}")
	public ResponseEntity<String> deleteReview(@PathVariable Long reviewId){
		boolean isReviewDeleted = reviewService.deleteReview( reviewId);
		if(isReviewDeleted) {
			return new ResponseEntity<String>("review deleted successfully", HttpStatus.OK);
		}else {
			return new ResponseEntity<String>("review not deleted", HttpStatus.NOT_FOUND);
		}
	}
	
	@GetMapping("/averageRating")
	public Double getAverageRatingForCompany(@RequestParam("companyId") Long companyId) {
	    List<Review> reviews = reviewRepository.findByCompanyId(companyId);
	    if (reviews.isEmpty()) {
	        return 0.0;
	    }
	    return reviews.stream()
	                  .mapToDouble(Review::getRating)
	                  .average()
	                  .orElse(0.0);
	}

}