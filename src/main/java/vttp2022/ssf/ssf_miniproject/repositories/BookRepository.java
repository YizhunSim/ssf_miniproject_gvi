package vttp2022.ssf.ssf_miniproject.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import vttp2022.ssf.ssf_miniproject.models.Book;

public interface BookRepository extends PagingAndSortingRepository<Book, String>{
  
}
