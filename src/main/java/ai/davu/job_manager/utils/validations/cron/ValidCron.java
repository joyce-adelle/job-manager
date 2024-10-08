package ai.davu.job_manager.utils.validations.cron;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CronExpressionValidator.class)
public @interface ValidCron {

    String message() default "Invalid cron expression";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}
