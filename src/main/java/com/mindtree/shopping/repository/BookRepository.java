package com.mindtree.shopping.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mindtree.shopping.model.Book;

@Repository
@Transactional
public interface BookRepository extends JpaRepository<Book, Integer> {

	@Modifying(clearAutomatically = true)
	@Query(value = "update book set cart_product=?1 ,quantity=?2 where product_id=?3", nativeQuery = true)
	void addBook(@Param("cart_product") int cart_product, @Param("quantity") int quantity,
			@Param("product_id") long productId);

	@Modifying(clearAutomatically = true)
	@Query(value = "update book set cart_product=null ,quantity=0 where product_id=?1", nativeQuery = true)
	void removeBook(@Param("product_id") int productId);

	@Modifying(clearAutomatically = true)
	@Query(value = "update book set cart_product=null ,quantity=0", nativeQuery = true)
	void removeAllBook();
}
