package com.mindtree.shopping.service;

import java.util.Set;

import com.mindtree.shopping.exception.ProductException;
import com.mindtree.shopping.model.Apparal;
import com.mindtree.shopping.model.Book;
import com.mindtree.shopping.model.Cart;
import com.mindtree.shopping.model.Product;
import com.mindtree.shopping.model.User;

public interface ServiceInterface {

	void saveUser(User user);

	void saveApparal(Apparal apparal);

	void saveBook(Book book);

	Set<Product> search(String key) throws Exception;

	String addProduct(int userId, int productId, int count) throws ProductException;

	String removeFromCart(int productId);

	Cart viewCart(int userId);
	
	float calculatePrice(Cart cart);

	String removeAllProducts();
}
