package com.miu.onlinemarket.service.impl;

import com.miu.onlinemarket.domain.Buyer;
import com.miu.onlinemarket.domain.Seller;
import com.miu.onlinemarket.domain.User;
import com.miu.onlinemarket.repository.BuyerRepository;
import com.miu.onlinemarket.repository.SellerRepository;
import com.miu.onlinemarket.service.BuyerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class BuyerServiceImp implements BuyerService {

    @Autowired
    private BuyerRepository buyerRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public Buyer findBuyer(String username) {
        return buyerRepository.findByUsername(username);
    }

    @Override
    public User save(Buyer buyer) {
        buyer.setPassword(encodePassword(buyer.getPassword()));
        return buyerRepository.save(buyer);
    }

    private String encodePassword(String password) {
        return bCryptPasswordEncoder.encode(password);
    }
}