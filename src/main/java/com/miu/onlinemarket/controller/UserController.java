package com.miu.onlinemarket.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import com.miu.onlinemarket.domain.Buyer;
import com.miu.onlinemarket.domain.Seller;
import com.miu.onlinemarket.domain.User;
import com.miu.onlinemarket.exceptionhandling.ResourceNotFoundException;
import com.miu.onlinemarket.service.BuyerService;
import com.miu.onlinemarket.service.ProductService;
import com.miu.onlinemarket.service.SellerService;
import com.miu.onlinemarket.service.UserService;

@Controller
public class UserController {

	@Autowired
	SellerService sellerService;

	@Autowired
	BuyerService buyerService;

	@Autowired
	private UserService userService;

	@Autowired
	private ProductService productService;

	@RequestMapping(value = { "/login", "/" })
	public String login(SessionStatus status, Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (!auth.getPrincipal().toString().equalsIgnoreCase("anonymousUser")) {
			return "redirect:/home";
		}
		status.setComplete();
		String statuss = (String) model.asMap().get("status");
		model.addAttribute("status", statuss);
		return "login";
	}

	@RequestMapping(value = "/signup/{type}")
	public String signUp(@PathVariable String type, Model model, HttpSession session) {
		model.addAttribute("user", new User());
		model.addAttribute("type", type);
		session.setAttribute("type", type);
		String status = (String) model.asMap().get("status");
		model.addAttribute("status", status);
		return "signup";
	}

	@RequestMapping(value = "/signup", method = RequestMethod.POST)
	public String addUser(@Valid @ModelAttribute("user") User user, BindingResult bindingResult, Model model,
			HttpSession session, RedirectAttributes redirectAttributes) throws IOException {
		String typee = (String) session.getAttribute("type");
		if (bindingResult.hasErrors()) {
			model.addAttribute("status", "failed");
			return "signup";
		}
		if (!user.getPassword().equalsIgnoreCase(user.getPasswordCheck())) {
			bindingResult.rejectValue("passwordCheck", "error.userexists", "* Value not valid.");
			model.addAttribute("status", "failed");
			return "signup";
		}
		MultipartFile image = user.getImage();
		if (image != null && !image.isEmpty()) {
			try {
				user.setPhoto(image.getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			String fileName = "static/img/user.png";
			ClassLoader classLoader = new UserController().getClass().getClassLoader();
			File file = new File(classLoader.getResource(fileName).getFile());
			BufferedImage originalImage = ImageIO.read(file);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(originalImage, "png", baos);
			baos.flush();
			user.setPhoto(baos.toByteArray());
			baos.close();
		}
		if (typee.equalsIgnoreCase("seller")) {
			Seller seller = new Seller(user, false, null, null);
			sellerService.save(seller);
		} else {
			Buyer buyer = new Buyer(user, null, null, null);
			buyerService.save(buyer);
		}
		redirectAttributes.addFlashAttribute("status", "success");
		return "redirect:/login";
	}

	@RequestMapping(value = "/profile")
	public String profile(Model model, Principal principal) throws ResourceNotFoundException {
		if (userService.hasRole("ROLE_BUYER")) {
			model.addAttribute("user", buyerService.findByUsername(principal.getName()));
		} else if (userService.hasRole("ROLE_SELLER")) {
			model.addAttribute("user", sellerService.findSeller(principal.getName()));
		} else {
			model.addAttribute("user", userService.findByUsername(principal.getName()));
		}
		String status = (String) model.asMap().get("status");
		model.addAttribute("status", status);
		return "profile";
	}

	@RequestMapping(value = "/profile", method = RequestMethod.POST)
	public String profile(@Valid @ModelAttribute("user") User user, BindingResult bindingResult, Model model,
			Principal principal, RedirectAttributes redirectAttributes) throws IOException, ResourceNotFoundException {
		if (bindingResult.hasErrors()) {
			model.addAttribute("status", "failed");
			return "profile";
		}
		MultipartFile image = user.getImage();
		if (image != null && !image.isEmpty()) {
			try {
				user.setPhoto(image.getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (userService.hasRole("ROLE_BUYER")) {
			buyerService.update(new Buyer(user));
		} else if (userService.hasRole("ROLE_SELLER")) {
			sellerService.update(new Seller(user));
		} else {
			userService.update(user);
		}
		redirectAttributes.addFlashAttribute("status", "success");
		return "redirect:/home";
	}

	@GetMapping("/approveSeller")
	public String approveSeller(@RequestParam("id") Long id, Model model, RedirectAttributes redirectAttributes)
			throws ResourceNotFoundException {
		redirectAttributes.addFlashAttribute("tab", "2");
		Seller seller = sellerService.findSellerById(id);
		seller.setApproved(true);
		sellerService.update(seller);
		redirectAttributes.addFlashAttribute("status", "success");
		return "redirect:/home";
	}

	@GetMapping("/follow")
	public RedirectView FollowSeller(@RequestParam("id") Long idVal, Principal principal,
			RedirectAttributes redirectAttributes, Model model) throws ResourceNotFoundException {
		Long id2 = productService.findById(idVal).getSeller().getUserId();
		Seller seller = sellerService.findSellerById(id2);
		Buyer buyer = buyerService.findByUsername(principal.getName());
		Buyer tempBuyer = buyerService.findBuyerBySellerId(seller.getUserId());
		if (tempBuyer == null) {
			buyer.addSeller(seller);
			buyerService.update(buyer);
		}
		model.addAttribute("status", "success");
		redirectAttributes.addFlashAttribute("status", "success");
		return new RedirectView("/product/detail?id=" + idVal);
	}

	@GetMapping("/unfollow")
	public RedirectView unFollowSeller(@RequestParam("id") Long idVal, Principal principal,
			RedirectAttributes redirectAttributes, Model model) throws ResourceNotFoundException {

		Seller seller = sellerService.findSellerById(idVal);
		Buyer buyer = buyerService.findByUsername(principal.getName());
		buyer.removeSeller(seller);
		buyerService.update(buyer);
		model.addAttribute("status", "success");
		redirectAttributes.addFlashAttribute("status", "success");
		return new RedirectView("/product/detail?id=" + idVal);
	}

	@GetMapping("/viewFollowing")
	public String BuyerViewFollowing(Model model, Principal principal) throws ResourceNotFoundException {

		Buyer buyer = buyerService.findByUsername(principal.getName());
		List<Seller> sellers = sellerService.findSellersByBuyerId(buyer.getUserId());
		model.addAttribute("sellers", sellers);

		return "follow";
	}

	@GetMapping("/viewFollower")
	public String SellerViewFollowers(Model model, Principal principal) throws ResourceNotFoundException {

		Seller seller = sellerService.findSeller(principal.getName());
		List<Buyer> buyers = buyerService.findBuyersBySellerId(seller.getUserId());
		model.addAttribute("buyers", buyers);

		return "follow";
	}

}