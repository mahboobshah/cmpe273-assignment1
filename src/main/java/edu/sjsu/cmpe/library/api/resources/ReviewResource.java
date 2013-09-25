package edu.sjsu.cmpe.library.api.resources;

import java.util.ArrayList;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.yammer.dropwizard.jersey.params.LongParam;
import com.yammer.metrics.annotation.Timed;

import edu.sjsu.cmpe.library.domain.Review;
import edu.sjsu.cmpe.library.dto.LinkDto;
import edu.sjsu.cmpe.library.dto.LinksDto;
import edu.sjsu.cmpe.library.dto.ReviewDto;
import edu.sjsu.cmpe.library.repository.ReviewRepositoryInterface;

@Path("/v1/books/{isbn}/reviews")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ReviewResource {
	private ReviewRepositoryInterface reviewRepository;
	
    /**
     * ReviewResource constructor
     * 
     * @param reviewRepository
     *            a ReviewRepository instance
     *            
     */
	
	/*public ReviewResource(){
		
	}*/
    public ReviewResource(ReviewRepositoryInterface reviewRepository) {
	this.reviewRepository = reviewRepository;
    }
    
    //Create Review API
    @POST
    @Timed(name = "create-review")
    public Response createReview(@PathParam("isbn") LongParam isbn, Review request) {
	// Store the new book in the ReviewRepository so that we can retrieve it.
	Review savedReview = reviewRepository.saveReview(request);
	
	LinksDto links = new LinksDto();
	links.addLink(new LinkDto("view-review", 
			"/books/" + isbn + "/reviews/" + savedReview.getId(), "GET"));
	return Response.status(201).entity(links).build();
    }
	
    //View review by ID
    @GET
    @Path("/{id}")
    @Timed(name = "view-review")
    public ReviewDto getReviewById(@PathParam("isbn") LongParam isbn, @PathParam("id") Integer id) {
    Review review = reviewRepository.getReviewById(id);
    
    ReviewDto reviewResponse = new ReviewDto(review);
    reviewResponse.addLink(new LinkDto("view-review", 
    		"/books/" + isbn + "/reviews/" + review.getId(), "GET"));
	return reviewResponse;
    }
    
    
    //View all reviews
    @GET
    @Timed(name = "view-all-reviews")
    public ArrayList<Review> viewAllReviews(@PathParam("isbn") LongParam isbn) {
    	ArrayList<Review> reviews = reviewRepository.getAllReviews();
    	return reviews;
    	
    }
    
}