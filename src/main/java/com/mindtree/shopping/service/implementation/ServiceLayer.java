package com.mindtree.shopping.service.implementation;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mindtree.shopping.exception.ProductException;
import com.mindtree.shopping.model.Apparal;
import com.mindtree.shopping.model.Book;
import com.mindtree.shopping.model.Cart;
import com.mindtree.shopping.model.Product;
import com.mindtree.shopping.model.User;
import com.mindtree.shopping.repository.ApparalRepository;
import com.mindtree.shopping.repository.BookRepository;
import com.mindtree.shopping.repository.ProductRepository;
import com.mindtree.shopping.repository.UserRepository;
import com.mindtree.shopping.service.ServiceInterface;

@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class ServiceLayer implements ServiceInterface {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private BookRepository bookRepository;

	@Autowired
	private ApparalRepository apparalRepository;

	@Autowired
	private ProductRepository productRepository;

	Logger logger = LoggerFactory.getLogger(ServiceLayer.class);

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void saveUser(User user) {
		try {
			userRepository.saveAndFlush(user);
			logger.info("Successfully saved the: " + user);
		} catch (Exception e) {
			logger.debug("Exception occured while saving user" + e.getMessage());
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void saveApparal(Apparal apparal) {
		try {
			apparalRepository.saveAndFlush(apparal);
			logger.info("Successfully saved the: " + apparal);
		} catch (Exception e) {
			logger.debug("Exception occured while saving apparal" + e.getMessage());
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void saveBook(Book book) {
		try {
			bookRepository.saveAndFlush(book);
			logger.info("Successfully saved the: " + book);
		} catch (Exception e) {
			logger.debug("Exception occured while saving book" + e.getMessage());
		}
	}

	@Override
	public Set<Product> search(String key) throws Exception {
		List<Product> dbList = productRepository.findAll();
		List<Book> booksList = null;
		List<Apparal> apparalsList = null;
		Set<Product> productsSet = new HashSet<>();

		int id = -1;
		try {
			id = Integer.valueOf(key);
		} catch (NumberFormatException e) {
			logger.info("Search key is not a number:" + key);
		}
		for (Product dbProduct : dbList) {
			if (id == dbProduct.getProductId()) {
				productsSet.add(dbProduct);
			}
			if (key.equalsIgnoreCase(dbProduct.getProductName()) || dbProduct.getProductName().contains(key)) {
				productsSet.add(dbProduct);
			}
		}
		if (key.equalsIgnoreCase("book") || key.equalsIgnoreCase("books") || key.contains("book")) {
			logger.info("Searching for books");
			booksList = bookRepository.findAll();
			for (Book book : booksList) {
				logger.info("Adding book: " + book);
				productsSet.add(book);
			}
		}
		if (key.equalsIgnoreCase("apparal") || key.contains("apparal")) {
			logger.info("Searching for apparal");
			apparalsList = apparalRepository.findAll();
			for (Apparal apparal : apparalsList) {
				logger.info("Adding book: " + apparal);
				productsSet.add(apparal);
			}
		}
		return productsSet;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public String addProduct(int userId, int productId, int count) throws ProductException {
		User user = userRepository.findById(userId).get();
		Product product = productRepository.findById(productId).get();
		String job = "Failure";
		if (count < 0) {
			logger.debug("Product cannot be negative");
			throw new ProductException("Number of products cannot be negative");
		}
		if (count == 0) {
			removeFromCart(productId);
			return "Success";
		}
		if (product.getClass().equals(Book.class)) {
			logger.info("Adding book to cart");
			bookRepository.addBook(user.getCart().getCartId(), count, productId);
			job = "Success";
		} else {
			apparalRepository.addApparal(user.getCart().getCartId(), count, productId);
			logger.info("Adding apparal to cart");
			job = "Success";
		}
		return job;

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public String removeFromCart(int productId) {
		Product product = productRepository.findById(productId).get();
		String job = "Failure";
		if (product.getClass().equals(Book.class)) {
			logger.info("Removing book from cart");
			bookRepository.removeBook(productId);
			job = "Success";
		} else {
			apparalRepository.removeApparal(productId);
			logger.info("Removing apparal from cart");
			job = "Success";
		}
		return job;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public String removeAllProducts() {
		String job = "Failure";
		try {
			bookRepository.removeAllBook();
			apparalRepository.removeAllApparal();
			logger.info("Successfully removed all products from cart");
			job = "Success";
		} catch (Exception e) {
			logger.debug("Exception occured while removing all products" + e.getMessage());
		}
		return job;
	}

	@Override
	public Cart viewCart(int userId) {
		User user = userRepository.findById(userId).get();
		Cart cart = user.getCart();
		return cart;
	}

	@Override
	public float calculatePrice(Cart cart) {
		float totalPrice = 0;
		logger.info("Calculating the price for cart: " + cart);
		if (!cart.getItems().isEmpty()) {
			for (Product product : cart.getItems()) {
				totalPrice += product.getPrice() * product.getQuantity();
			}
		}
		return totalPrice;
	}

}
