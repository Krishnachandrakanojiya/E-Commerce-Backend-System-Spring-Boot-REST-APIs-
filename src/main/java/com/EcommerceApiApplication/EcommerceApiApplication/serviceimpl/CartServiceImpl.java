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


    @Override
    public CartDto updateItemQuantity(Long userId, Long productId, int quantity) {

        if (quantity < 0) {
            throw new RuntimeException("Quantity cannot be negative");
        }

        Cart cart = getOrCreateCart(userId);

        CartItem item = cartItemRepository
                .findByCartIdAndProductId(cart.getId(), productId)
                .orElseThrow(() -> new RuntimeException("Item not found in cart"));

        if (quantity == 0) {
            // remove item if quantity = 0
            cart.getItems().remove(item);
            cartItemRepository.delete(item);
        } else {
            item.setQuantity(quantity);
        }

        recalculateTotal(cart);
        cartRepository.save(cart);

        return mapToDto(cart);
    }

    // GET CART

    public CartDto getCart(Long userId) {
        Cart cart = getOrCreateCart(userId);
        return mapToDto(cart);
    }
    @Override
    public CartDto removeItemFromCart(Long id, Long productId) {

        Cart cart = getOrCreateCart(id);

        CartItem item = cartItemRepository
                .findByCartIdAndProductId(cart.getId(), productId)
                .orElseThrow(() -> new RuntimeException("Item not found in cart"));

        cart.getItems().remove(item);
        cartItemRepository.delete(item);

        recalculateTotal(cart);
        cartRepository.save(cart);

        return mapToDto(cart);
    }

    @Override
    public CartDto getCartByUserId(Long id) {
        return null;
    }


    @Override
    public CartDto addItemToCart(Long id, Long productId, int quantity) {

        if (quantity <= 0) {
            throw new RuntimeException("Quantity must be greater than zero");
        }

        // 1. Get or create cart
        Cart cart = getOrCreateCart(id);

        // 2. Validate product
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // 3. Check if product already exists
        CartItem item = cartItemRepository
                .findByCartIdAndProductId(cart.getId(), productId)
                .orElse(null);

        if (item != null) {
            // 4A. Increase quantity
            item.setQuantity(item.getQuantity() + quantity);
        } else {
            // 4B. Create new item
            item = new CartItem();
            item.setCart(cart);
            item.setProduct(product);
            item.setPrice(product.getPrice());
            item.setQuantity(quantity);

            cart.getItems().add(item);
        }

        // 5. Recalculate total
        recalculateTotal(cart);

        // 6. Save
        cartRepository.save(cart);

        // 7. Return response
        return mapToDto(cart);
    }


    // =======================
    // PRIVATE HELPERS
    // =======================
    private Cart getOrCreateCart(Long id) {
        return cartRepository.findByUserId(id)
                .orElseGet(() -> {
                    User user = userRepository.findById(id)
                            .orElseThrow(() -> new RuntimeException("User not found"));
                    Cart cart = new Cart();
                    cart.setUser(user);
                    cart.setTotalPrice(0);
                    cart.setItems(new ArrayList<>()); // empty list
                    return cartRepository.save(cart);
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