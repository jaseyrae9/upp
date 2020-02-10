package rs.ac.uns.ftn.upp.upp.service.entityservice.journal;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rs.ac.uns.ftn.upp.upp.exceptions.NotFoundException;
import rs.ac.uns.ftn.upp.upp.model.journal.Paper;
import rs.ac.uns.ftn.upp.upp.model.journal.Review;
import rs.ac.uns.ftn.upp.upp.repository.ReviewRepository;;

@Service
public class ReviewService {

	@Autowired
	private ReviewRepository reviewRepository;

//	public Paper findByName(String name) {
//		Paper ret = null;
//		Optional<Paper> paper = paperRepository.findByName(name);
//		if(paper.isPresent()) {
//			ret = paper.get();
//		}
//		return ret;
//	}

	public Review getReview(Integer id) throws NotFoundException {
		Optional<Review> review = reviewRepository.findById(id);

		if (!review.isPresent()) {
			throw new NotFoundException(id, Review.class.getSimpleName());
		}

		return review.get();
	}

	public Optional<Review> findById(Integer id) {
		return reviewRepository.findById(id);
	}

	public Review saveReviewr(Review review) {
		return reviewRepository.save(review);
	}

}
