package finance.configs

import finance.utils.Constants.METRIC_ACCOUNT_ALREADY_EXISTS_COUNTER
import finance.utils.Constants.METRIC_ACCOUNT_NOT_FOUND_COUNTER
import finance.utils.Constants.METRIC_DUPLICATE_ACCOUNT_INSERT_ATTEMPT_COUNTER
import finance.utils.Constants.METRIC_DUPLICATE_CATEGORY_INSERT_ATTEMPT_COUNTER
import finance.utils.Constants.METRIC_INSERT_TRANSACTION_TIMER
import finance.utils.Constants.METRIC_TRANSACTION_ALREADY_EXISTS_COUNTER
import finance.utils.Constants.METRIC_TRANSACTION_DATABASE_INSERT_COUNTER
import finance.utils.Constants.METRIC_TRANSACTION_VALIDATOR_FAILED_COUNTER
import io.micrometer.core.aop.TimedAspect
import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Timer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class MetricsConfig {

    @Bean
    open fun timedAspect(meterRegistry: MeterRegistry): TimedAspect {
        return TimedAspect(meterRegistry)
    }

    @Bean
    open fun duplicateAccountInsertAttempt(meterRegistry: MeterRegistry): Counter = Counter
            .builder(METRIC_DUPLICATE_ACCOUNT_INSERT_ATTEMPT_COUNTER)
            .description("Counter for duplicate account insert attempt.")
            .register(meterRegistry)

    @Bean
    open fun duplicateCategoryInsertAttempt(meterRegistry: MeterRegistry): Counter = Counter
            .builder(METRIC_DUPLICATE_CATEGORY_INSERT_ATTEMPT_COUNTER)
            .description("Counter for duplicate category insert attempt.")
            .register(meterRegistry)

    @Bean
    open fun transactionAlreadyExistsCounter(meterRegistry: MeterRegistry): Counter = Counter
            .builder(METRIC_TRANSACTION_ALREADY_EXISTS_COUNTER)
            .description("Counter for transaction already exists.")
            .register(meterRegistry)

    @Bean
    open fun vaildateFailedCounter(meterRegistry: MeterRegistry): Counter = Counter
            .builder(METRIC_TRANSACTION_VALIDATOR_FAILED_COUNTER)
            .description("Counter for failed validations.")
            .register(meterRegistry)

    @Bean
    open fun accountNotFoundCounter(meterRegistry: MeterRegistry): Counter = Counter
            .builder(METRIC_ACCOUNT_NOT_FOUND_COUNTER)
            .description("Counter for account not found.")
            .register(meterRegistry)

    @Bean
    open fun accountAlreadyExistsCounter(meterRegistry: MeterRegistry): Counter = Counter
            .builder(METRIC_ACCOUNT_ALREADY_EXISTS_COUNTER)
            .description("Counter for account already exists.")
            .register(meterRegistry)

    @Bean
    open fun databaseInsertCounter(meterRegistry: MeterRegistry): Counter = Counter
            .builder(METRIC_TRANSACTION_DATABASE_INSERT_COUNTER)
            .description("Counter for transaction inserts.")
            .register(meterRegistry)

    @Bean
    open fun databaseInsertTimer(meterRegistry: MeterRegistry): Timer = Timer
            .builder(METRIC_INSERT_TRANSACTION_TIMER)
            .description("a description of what this timer does") // optional
            .register(meterRegistry)
}
