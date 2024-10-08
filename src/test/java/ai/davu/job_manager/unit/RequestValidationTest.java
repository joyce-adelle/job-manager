package ai.davu.job_manager.unit;

import static org.junit.jupiter.api.Assertions.*;

import ai.davu.job_manager.dtos.requests.TaskRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

class RequestValidationTest extends BaseTest {

    private Validator validator;

    @BeforeEach
    public void setUp() {
        LocalValidatorFactoryBean localValidatorFactoryBean = new LocalValidatorFactoryBean();
        localValidatorFactoryBean.afterPropertiesSet();
        this.validator = localValidatorFactoryBean;
    }

    @Test
    void whenValidCronExpression_thenNoValidationErrors() {
        TaskRequest task = new TaskRequest();
        task.setName("task_1");
        task.setCommand("echo Hello World");
        task.setScheduleInterval("0 12 * * *");  // Valid cron expression

        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(task, "task");
        validator.validate(task, bindingResult);

        assertFalse(bindingResult.hasErrors(), "Task with valid cron expression should not have errors");
    }

    @Test
    void whenInvalidCronExpression_thenValidationErrors() {
        TaskRequest task = new TaskRequest();
        task.setName("task_1");
        task.setCommand("echo Hello World");
        task.setScheduleInterval("invalid_cron");  // Invalid cron expression

        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(task, "task");
        validator.validate(task, bindingResult);

        assertTrue(bindingResult.hasErrors(), "Task with invalid cron expression should have validation errors");
        assertEquals("Invalid cron expression", bindingResult.getFieldError("scheduleInterval").getDefaultMessage());
    }

    @Test
    void whenNullCronExpression_thenNoValidationErrors() {
        TaskRequest task = new TaskRequest();
        task.setName("task_1");
        task.setCommand("echo Hello World");
        task.setScheduleInterval(null);  // Null schedule, should pass as valid

        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(task, "task");
        validator.validate(task, bindingResult);

        assertFalse(bindingResult.hasErrors(), "Task with null cron expression should not have errors");
    }
}

