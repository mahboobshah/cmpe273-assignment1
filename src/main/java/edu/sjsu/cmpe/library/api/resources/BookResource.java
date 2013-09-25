package edu.sjsu.cmpe.library.api.resources;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.yammer.dropwizard.jersey.params.LongParam;
import com.yammer.metrics.annotation.Timed;

import edu.sjsu.cmpe.library.domain.Author;
import edu.sjsu.cmpe.library.domain.Book;
import edu.sjsu.cmpe.library.domain.Review;
import edu.sjsu.cmpe.library.dto.AuthorDto;
import edu.sjsu.cmpe.library.dto.BooksDto;
import edu.sjsu.cmpe.library.dto.LinkDto;
import edu.sjsu.cmpe.library.dto.LinksDto;
import edu.sjsu.cmpe.library.dto.ReviewDto;
import edu.sjsu.cmpe.library.repository.BookRepositoryInterface;
import edu.sjsu.cmpe.library.repository.ReviewRepository;
import edu.sjsu.cmpe.library.repository.ReviewRepositoryInterface;

@Path("/v1/books")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BookResource {
	/** instance for BookRepository interface */
	private final BookRepositoryInterface bookRepository;

	/**
	 * BookResource constructor
	 * 
	 * @param bookRepository
	 *            a BookRepository instance
	 */
	public BookResource(BookRepositoryInterface bookRepository) {
		this.bookRepository = bookRepository;
	}

	//View Book API code 
	@GET
	@Path("/{isbn}")
	@Timed(name = "view-book")
	public BooksDto getBookByIsbn(@PathParam("isbn") LongParam isbn) {
		Book book = bookRepository.getBookByISBN(isbn.get());
		BooksDto bookResponse = new BooksDto(book);
		bookResponse.addLink(new LinkDto("view-book", "/books/" + book.getIsbn(),
				"GET"));
		bookResponse.addLink(new LinkDto("update-book",
				"/books/" + book.getIsbn(), "POST"));
		// add more links
		bookResponse.addLink(new LinkDto("delete-book",
				"/books/" + book.getIsbn(), "DELETE"));
		bookResponse.addLink(new LinkDto("create-review",
				"/books/" + book.getIsbn() + "/reviews", "POST"));
		//ReviewDto reviewDto;
		
		bookResponse.addLink(new LinkDto("view-all-reviews", 
					"/books/" + book.getIsbn() + "reviews", "GET"));
		
		return bookResponse;
	}
	
	
	//Create Book API
	@POST
	@Timed(name = "create-book")
	public Response createBook(Book request) {
		// Store the new book in the BookRepository so that we can retrieve it.
		Book savedBook = bookRepository.saveBook(request);

		String location = "/books/" + savedBook.getIsbn();
		BooksDto bookResponse = new BooksDto(savedBook);
		bookResponse.addLink(new LinkDto("view-book", location, "GET"));
		bookResponse.addLink(new LinkDto("update-book", location, "PUT"));
		bookResponse.addLink(new LinkDto("delete-book", location, "DELETE"));
		bookResponse.addLink(new LinkDto("create-review", location + "/reviews", "POST"));
		// Add other links if needed

		return Response.status(201).entity(bookResponse.getLinks()).build();
	}

	//Delete Book API
	@DELETE
	@Path("/{isbn}")
	@Timed(name = "delete-book")
	public Response deleteBook(@PathParam("isbn") LongParam isbn) throws Exception {

		Book deleteResponse = bookRepository.removeBookByISBN(isbn.get());

		if(deleteResponse == null) {
			throw new Exception();
		}
		else {
			LinksDto links = new LinksDto();
			links.addLink(new LinkDto("create-book", "/books/", "POST"));
			return Response.ok(links).build();
		}

	}

	//Update API
	@PUT
	@Path("/{isbn}")
	@Timed(name = "update-book")
	public Response updateBook(@PathParam("isbn") LongParam isbn, @Context UriInfo uriInfo, String content) throws Exception {

		MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();
		//Set queryKeys =  queryParams.keySet();
		Book updateResponse = bookRepository.getBookByISBN(isbn.get());
		for(Map.Entry<String, java.util.List<String>> entry : queryParams.entrySet()){
			bookRepository.updateBookInfo(updateResponse, entry);
		}
		LinksDto links = new LinksDto();
		
		String location = "/books/" + isbn;
		links.addLink(new LinkDto("view-book", location, "GET"));
		links.addLink(new LinkDto("update-book", location, "PUT"));
		links.addLink(new LinkDto("delete-book", location, "DELETE"));
		links.addLink(new LinkDto("create-review", location + "/reviews", "POST"));
		links.addLink(new LinkDto("view-all-reviews", location + "/reviews", "GET"));
		return Response.ok(links).build();
	}

	//View Author by ID API
	@GET
	@Path("/{isbn}/authors/{id}")
	@Timed(name = "view-book-author")
	public AuthorDto viewBookAuthor(@PathParam("isbn") LongParam isbn, @PathParam("id") int id) {
		Book book = bookRepository.getBookByISBN(isbn.get());
		Author author = book.getAuthors().get(id-1);
		AuthorDto authorResponse = new AuthorDto(author);
		authorResponse.addLink(new LinkDto("view-author", 
				"/books/" + book.getIsbn() + "/authors/" + author.getId(), "GET"));
		return authorResponse;
	}

	//View all authors API
	@GET
	@Path("/{isbn}/authors")
	@Timed(name = "view-book-author")
	public ArrayList<Author> viewAllAuthors(@PathParam("isbn") LongParam isbn) {
		Book book = bookRepository.getBookByISBN(isbn.get());
		ArrayList<Author> authors = book.getAuthors();
		return authors;
	}

}