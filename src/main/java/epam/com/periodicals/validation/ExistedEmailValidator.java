package epam.com.periodicals.validation;

import epam.com.periodicals.exceptions.NoSuchUserException;
import epam.com.periodicals.services.UserService;

import javax.annotation.Resource;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ExistedEmailValidator implements
        ConstraintValidator<ExistedEmail, String> {
    @Resource
    private UserService userService;

    @Override
    public void initialize(ExistedEmail constraintAnnotation) {
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext ctx){
        try{
            userService.getByEmail(s);
            return false;
        } catch (NullPointerException | NoSuchUserException e){
            return true;
        }
    }
}
