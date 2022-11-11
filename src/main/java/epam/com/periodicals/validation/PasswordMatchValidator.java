package epam.com.periodicals.validation;

import epam.com.periodicals.dto.users.CreateUserDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordMatchValidator implements ConstraintValidator<PasswordMatch, CreateUserDto> {
    @Override
    public void initialize(PasswordMatch constraintAnnotation) {
    }

    @Override
    public boolean isValid(CreateUserDto createUserDto, ConstraintValidatorContext constraintValidatorContext) {
        return createUserDto.getPassword().equals(createUserDto.getConfirmPassword());
    }
}
