package app.validation;

import org.springframework.stereotype.Service;

import app.dataTransportObject.NewPasswordDTO;

@Service
public class UserValidation {

	public NewPasswordDTO validateNewPassword(NewPasswordDTO newPasswordDTO) {
		if(newPasswordDTO.getViewObject().getNewPassword_1().equals(newPasswordDTO.getViewObject().getNewPassword_2())) {
			newPasswordDTO.setValid(true);
		} else {
			newPasswordDTO.setValid(false);
		}
		return newPasswordDTO;
	}

}
