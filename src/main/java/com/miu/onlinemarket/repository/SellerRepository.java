package com.miu.onlinemarket.repository;

import com.miu.onlinemarket.domain.Buyer;
import com.miu.onlinemarket.domain.Product;
import com.miu.onlinemarket.domain.Seller;
import com.miu.onlinemarket.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface SellerRepository extends JpaRepository<Seller, Long> {

    @Query("select p from Product p where p.seller.userId = :id AND (UPPER(p.name) like UPPER(concat('%',:name,'%'))  OR UPPER(p.description) like UPPER(concat('%',:name,'%')))")
    List<Product> SearchByName(String name , Long id);

    @Query("select s from Seller s where s.approved = false")
    List<Seller> findUnApproved();

    Seller findByUsername(String username);

    @Query("select s from Seller s join s.buyers b where b.userId=:id")
    List<Seller> findSellersByBuyerId(Long id);
    
}
