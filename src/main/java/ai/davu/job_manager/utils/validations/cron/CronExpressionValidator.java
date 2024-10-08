package ai.davu.job_manager.utils.validations.cron;

import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.parser.CronParser;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CronExpressionValidator implements ConstraintValidator<ValidCron, String> {

    private final CronParser parser = new CronParser(CronDefinitionBuilder.instanceDefinitionFor(CronType.UNIX));

    @Override
    public boolean isValid(String cronExpression, ConstraintValidatorContext context) {
        if (cronExpression == null || cronExpression.isEmpty()) {
            return true;
        }
        try {
            parser.parse(cronExpression).validate();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}