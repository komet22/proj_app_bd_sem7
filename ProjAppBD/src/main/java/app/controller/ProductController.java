package app.controller;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import app.dataTransportObject.ProductDTO;
import app.model.Producer;
import app.model.Product;
import app.model.ProductCategory;
import app.operations.ProducerService;
import app.operations.ProductCategoryService;
import app.operations.ProductService;
import app.viewObject.ProductVO;

@Controller
public class ProductController {

	@Autowired ProductService productService;
	
	@Autowired ProducerService producerService;
	
	@Autowired ProductCategoryService productCategoryService;
	
    @GetMapping("/owner/productList")
    public String productList(@RequestParam(value="page", required=false) Integer page, Model model) {
    	productService.getProductsByPagination(page, model);
    	return "product_list";
    }
	
	@GetMapping("/owner/productList/addProduct")
	public String addNewProductGET(ProductVO productVO, Model m) {
		ArrayList<Producer> producerList = producerService.findAllProducers();
		ArrayList<ProductCategory> productCategoryList = productCategoryService.findAllProductCategory();
		m.addAttribute("producerList", producerList );
		m.addAttribute("productCategoryList", productCategoryList );
		return "product_add";
	}

	@PostMapping("/owner/productList/addProduct")
	public String addNewProductPOST(@RequestParam("productPhoto") MultipartFile productPhoto, ProductVO productVO, BindingResult bindingResult, Model m, RedirectAttributes redirectAttributes) {
		ProductDTO productDTO = new ProductDTO(productVO);
		productDTO = productService.saveProduct(productPhoto, productDTO);
		if(productDTO.getViewObject().isInvalidOverall()) {
			m.addAttribute("msg", "Błąd, nie można utworzyć produktu!");
			ArrayList<Producer> producerList = producerService.findAllProducers();
			ArrayList<ProductCategory> productCategoryList = productCategoryService.findAllProductCategory();
			m.addAttribute("producerList", producerList );
			m.addAttribute("productCategoryList", productCategoryList );
			return "product_add";	
		}
		redirectAttributes.addAttribute("productId", productDTO.getViewObject().getProductId());
		return "redirect:/owner/productList/product";
	}
	
    @GetMapping("/owner/productList/deleteProduct")
    public String deleteProductGET(@RequestParam(value="page", required=true) Integer page,
    							   @RequestParam(value="productId", required=true) Integer productId) {
    	productService.deleteProduct(productId);
    	return "redirect:/owner/productList?page=" + page;
	}

	@GetMapping("/owner/productList/product")
	public String productGET(@RequestParam(value="productId", required=false) Integer productId, Model m) {
		if(!productService.existsProduct(productId))
			return "bad_request";
		Product product = productService.getProductById(productId);
		m.addAttribute("product", product);
		return "product";
	}
	
}
