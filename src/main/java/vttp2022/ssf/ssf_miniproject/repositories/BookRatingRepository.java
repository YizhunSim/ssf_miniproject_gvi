package vttp2022.ssf.ssf_miniproject.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import vttp2022.ssf.ssf_miniproject.models.BookRating;

@Repository
public interface BookRatingRepository extends CrudRepository<BookRating, String>{
  
}
