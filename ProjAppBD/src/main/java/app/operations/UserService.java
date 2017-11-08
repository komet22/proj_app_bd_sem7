package app.operations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import app.dataTransportObject.CustomerDTO;
import app.dataTransportObject.NewPasswordDTO;
import app.dataTransportObject.OwnerDTO;
import app.model.Country;
import app.model.Customer;
import app.model.CustomerDetails;
import app.model.User;
import app.model.repository.CustomerRepository;
import app.model.repository.UserRepository;
import app.validation.CustomerValidation;
import app.validation.OwnerValidation;
import app.validation.UserValidation;
import app.viewObject.CustomerVO;
import app.viewObject.OwnerVO;

@Service
public class UserService {

    private static int MAX_ROWS_PER_PAGE = 10;	

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserValidation userValidation;
	
	@Autowired
	private CustomerRepository customerRepository;
	
	@Autowired
	private CustomerValidation customerValidation;
	
	@Autowired
	private OwnerValidation ownerValidation;
	
    @Bean
    public PasswordEncoder passwordEncoder(){
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder;
    }
    
    public String encode(String password) {
    	return passwordEncoder().encode(password);
    }
	
	public boolean updateUserPassword(NewPasswordDTO newPasswordDTO, String username) {
		try {
			newPasswordDTO = userValidation.validateNewPassword(newPasswordDTO);
			if(newPasswordDTO.isValid()) {
				User user = userRepository.findByUsername(username);
				user.setPassword(encode(newPasswordDTO.getViewObject().getNewPassword_1()));
				userRepository.save(user);
				return true;
			} else {
				return false;
			}
		} catch (Exception ex) {
			return false;
		}
	}
	
	public boolean blockUser(String username) {
		try {
			User user = userRepository.findByUsername(username);
	    	if(user.hasOwnerRole())
	    		return false;
			user.setEnabled(false);
			userRepository.save(user);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean unlockUser(String username) {
		try {
			User user = userRepository.findByUsername(username);
			user.setEnabled(true);
			userRepository.save(user);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public void getUsersByPagination(Integer pageReq, Model model) {
    	int usersNumber = (int) userRepository.count();
    	int maxPagesNumber = (int) (Math.ceil(1.0*usersNumber/MAX_ROWS_PER_PAGE));
    	int pageNumber = 1;
    	if( maxPagesNumber == 0 )
    		maxPagesNumber = 1;
    	if(pageReq != null)
    		pageNumber = pageReq;
    	Page<User> userList = (Page<User>) userRepository.findAll(new PageRequest(pageNumber - 1, MAX_ROWS_PER_PAGE, Sort.Direction.ASC, "username"));
    	if(userList.getNumberOfElements() == 0) 
    		model.addAttribute("isEmpty", true); 
    	else 
    		model.addAttribute("isEmpty", false);
    	model.addAttribute("userList", userList);
    	model.addAttribute("pageNumber",pageNumber);
    	model.addAttribute("maxPagesNumber",maxPagesNumber);    	
	}

	public CustomerDTO registerCustomer(CustomerDTO customerDTO) {
		
		customerDTO = customerValidation.validateNewCustomer(customerDTO);
		if(customerDTO.isValid()) {
			try {
				CustomerVO vo = customerDTO.getViewObject();
				CustomerDetails customerDetails = new CustomerDetails(vo.getCustomerDetailsVO());
				User user = new User(vo.getUserVO().getUsername(), encode(vo.getUserVO().getPassword_1()), vo.getUserVO().getEmail(), true, "ROLE_CUSTOMER") ;
				Country country = new Country("Polska","POL");
				Customer customer = new Customer(user, customerDetails, country);
			    customer = customerRepository.save(customer);
			    customerDTO.getViewObject().setCustomerId(customer.getCustomerId());
			return customerDTO;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		} else {
			return null;
		}
	}
	
	public OwnerDTO registerOwner(OwnerDTO ownerDTO) {
		ownerDTO = ownerValidation.validateNewOwner(ownerDTO);
		if(ownerDTO.isValid()) {
			try {
				OwnerVO vo = ownerDTO.getViewObject();
				User user = new User(vo.getUserVO().getUsername(), encode(vo.getUserVO().getPassword_1()), vo.getUserVO().getEmail(), true, "ROLE_OWNER");
				user = userRepository.save(user);
			    ownerDTO.getViewObject().setOwnerId(user.getUserId());
			return ownerDTO;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		} else {
			return null;
		}
	}
	
}
