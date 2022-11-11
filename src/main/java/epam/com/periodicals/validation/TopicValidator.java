package epam.com.periodicals.validation;


import epam.com.periodicals.model.Topics;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class TopicValidator implements ConstraintValidator<TopicValid, String> {
    @Override
    public void initialize(TopicValid constraintAnnotation) {
    }

    @Override
    public boolean isValid(String topic, ConstraintValidatorContext constraintValidatorContext) {
        try {
            Topics.valueOf(topic);
            return true;
        } catch (IllegalArgumentException e){
            return false;
        }
    }
}
