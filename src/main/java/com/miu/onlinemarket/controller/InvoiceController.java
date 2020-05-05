package com.miu.onlinemarket.controller;

import static java.lang.String.format;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_PDF;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

import com.miu.onlinemarket.domain.OrderModel;
import com.miu.onlinemarket.service.InvoiceService;

@Controller
public class InvoiceController {

	@Autowired
	private InvoiceService invoiceService;

	@PostMapping(value = "/generate", produces = "application/pdf")
	public ResponseEntity<InputStreamResource> invoiceGenerate() throws IOException {
		final OrderModel order = invoiceService.getOrderByCode("XYZ123456789");
		final File invoicePdf = invoiceService.generateInvoiceFor(order, Locale.forLanguageTag("en"));
		final HttpHeaders httpHeaders = getHttpHeaders("XYZ123456789", "en", invoicePdf);
		return new ResponseEntity<>(new InputStreamResource(new FileInputStream(invoicePdf)), httpHeaders, OK);
	}

	private HttpHeaders getHttpHeaders(String code, String lang, File invoicePdf) {
		HttpHeaders respHeaders = new HttpHeaders();
		respHeaders.setContentType(APPLICATION_PDF);
		respHeaders.setContentLength(invoicePdf.length());
		respHeaders.setContentDispositionFormData("attachment", format("%s-%s.pdf", code, lang));
		return respHeaders;
	}

}