package com.EcommerceApiApplication.EcommerceApiApplication.serviceimpl;

import com.EcommerceApiApplication.EcommerceApiApplication.DTO.CartDto;
import com.EcommerceApiApplication.EcommerceApiApplication.DTO.CartItemDto;
import com.EcommerceApiApplication.EcommerceApiApplication.DTO.ProductDto;
import com.EcommerceApiApplication.EcommerceApiApplication.entity.Cart;
import com.EcommerceApiApplication.EcommerceApiApplication.entity.CartItem;
import com.EcommerceApiApplication.EcommerceApiApplication.entity.Product;
import com.EcommerceApiApplication.EcommerceApiApplication.entity.User;
import com.EcommerceApiApplication.EcommerceApiApplication.repository.CartItemRepository;
import com.EcommerceApiApplication.EcommerceApiApplication.repository.CartRepository;
import com.EcommerceApiApplication.EcommerceApiApplication.repository.ProductRepository;
import com.EcommerceApiApplication.EcommerceApiApplication.repository.UserRepository;
import com.EcommerceApiApplication.EcommerceApiApplication.service.CartService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;


    public CartServiceImpl(CartRepository cartRepository,
                           CartItemRepository cartItemRepository,
                           UserRepository userRepository,
                           ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    private static final Logger log = LoggerFactory.getLogger(CartServiceImpl.class);


    @Override
    public CartDto getCartByUserId(Long id) {
        log.info("Fetching cart by userId={}", id);
        Cart cart = getOrCreateCart(id);
        return mapToDto(cart);
    }



    @Override
    public CartDto updateItemQuantity(Long userId, Long productId, int quantity) {

        log.info("Update cart item quantity. userId={}, productId={}, quantity={}",
                userId, productId, quantity);

        if (quantity < 0) {
            log.warn("Negative quantity attempted. userId={}, productId={}",
                    userId, productId);
            throw new RuntimeException("Quantity cannot be negative");
        }

        Cart cart = getOrCreateCart(userId);

        CartItem item = cartItemRepository
                .findByCartIdAndProductId(cart.getId(), productId)
                .orElseThrow(() -> {
                    log.error("Cart item not found. cartId={}, productId={}",
                            cart.getId(), productId);
                    return new RuntimeException("Item not found in cart");
                });

        if (quantity == 0) {
            cart.getItems().remove(item);
            cartItemRepository.delete(item);
            log.info("Item removed from cart. productId={}", productId);
        } else {
            item.setQuantity(quantity);
            log.debug("Cart item quantity updated. productId={}, quantity={}",
                    productId, quantity);
        }

        recalculateTotal(cart);
        cartRepository.save(cart);

        log.info("Cart updated successfully. cartId={}, totalPrice={}",
                cart.getId(), cart.getTotalPrice());

        return mapToDto(cart);
    }


    // GET CART

    public CartDto getCart(Long userId) {

        log.info("Fetching cart. userId={}", userId);

        Cart cart = getOrCreateCart(userId);
        return mapToDto(cart);
    }

    @Override
    public CartDto removeItemFromCart(Long id, Long productId) {

        log.info("Remove item from cart. userId={}, productId={}", id, productId);

        Cart cart = getOrCreateCart(id);

        CartItem item = cartItemRepository
                .findByCartIdAndProductId(cart.getId(), productId)
                .orElseThrow(() -> {
                    log.error("Cart item not found for removal. cartId={}, productId={}",
                            cart.getId(), productId);
                    return new RuntimeException("Item not found in cart");
                });

        cart.getItems().remove(item);
        cartItemRepository.delete(item);

        recalculateTotal(cart);
        cartRepository.save(cart);

        log.info("Item removed successfully. cartId={}, productId={}, totalPrice={}",
                cart.getId(), productId, cart.getTotalPrice());

        return mapToDto(cart);
    }




    @Override
    public CartDto addItemToCart(Long id, Long productId, int quantity) {

        log.info("Add item to cart. userId={}, productId={}, quantity={}",
                id, productId, quantity);

        if (quantity <= 0) {
            log.warn("Invalid quantity attempt. userId={}, productId={}, quantity={}",
                    id, productId, quantity);
            throw new RuntimeException("Quantity must be greater than zero");
        }

        Cart cart = getOrCreateCart(id);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> {
                    log.error("Product not found while adding to cart. productId={}",
                            productId);
                    return new RuntimeException("Product not found");
                });

        CartItem item = cartItemRepository
                .findByCartIdAndProductId(cart.getId(), productId)
                .orElse(null);

        if (item != null) {
            item.setQuantity(item.getQuantity() + quantity);
            log.debug("Increased cart item quantity. productId={}, newQuantity={}",
                    productId, item.getQuantity());
        } else {
            item = new CartItem();
            item.setCart(cart);
            item.setProduct(product);
            item.setPrice(product.getPrice());
            item.setQuantity(quantity);
            cart.getItems().add(item);

            log.info("New item added to cart. productId={}, quantity={}",
                    productId, quantity);
        }

        recalculateTotal(cart);
        cartRepository.save(cart);

        log.info("Cart updated successfully. cartId={}, totalPrice={}",
                cart.getId(), cart.getTotalPrice());

        return mapToDto(cart);
    }



    // =======================
    // PRIVATE HELPERS
    // =======================
    private Cart getOrCreateCart(Long id) {

        return cartRepository.findByUserId(id)
                .orElseGet(() -> {
                    log.info("No cart found. Creating new cart. userId={}", id);

                    User user = userRepository.findById(id)
                            .orElseThrow(() -> {
                                log.error("User not found while creating cart. userId={}", id);
                                return new RuntimeException("User not found");
                            });

                    Cart cart = new Cart();
                    cart.setUser(user);
                    cart.setTotalPrice(0);
                    cart.setItems(new ArrayList<>());

                    Cart savedCart = cartRepository.save(cart);
                    log.info("New cart created. cartId={}, userId={}",
                            savedCart.getId(), id);

                    return savedCart;
                });
    }


    private void recalculateTotal(Cart cart) {
        double total = cart.getItems().stream()
                .mapToDouble(i -> i.getPrice() * i.getQuantity())
                .sum();
        cart.setTotalPrice(total);
    }

    private CartDto mapToDto(Cart cart) {

        List<CartItemDto> items = new ArrayList<>();
        for (CartItem item : cart.getItems()) {
            // Create ProductDto manually
            ProductDto productDto = new ProductDto();
            productDto.setId(item.getProduct().getId());
            productDto.setName(item.getProduct().getName());
            productDto.setPrice(item.getProduct().getPrice());
            productDto.setStock(item.getProduct().getStock());

            // Create CartItemDto manually
            CartItemDto cartItemDto = new CartItemDto();
            cartItemDto.setId(item.getId());
            cartItemDto.setProduct(productDto);
            cartItemDto.setQuantity(item.getQuantity());

            items.add(cartItemDto);
        }

        // Create CartDto manually
        CartDto cartDto = new CartDto();
        cartDto.setId(cart.getId());
//        cartDto.setUserId(cart.getUser().getId());
        cartDto.setItems(items);
        cartDto.setTotalPrice(cart.getTotalPrice());
        return cartDto;
    }
}