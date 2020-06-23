package finance.services

import finance.utils.Constants
import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class MeterService @Autowired constructor(private val meterRegistry: MeterRegistry) {
    init {
        Counter.builder(Constants.TRANSACTION_RECEIVED_EVENT_COUNTER)
                .tag(Constants.ACCOUNT_NAME_TAG, "")
                .register(meterRegistry)
        Counter.builder(Constants.TRANSACTION_SUCCESSFULLY_INSERTED_COUNTER)
                .tag(Constants.ACCOUNT_NAME_TAG, "")
                .register(meterRegistry)
        Counter.builder(Constants.TRANSACTION_ALREADY_EXISTS_COUNTER)
                .tag(Constants.ACCOUNT_NAME_TAG, "")
                .register(meterRegistry)
        Counter.builder(Constants.TRANSACTION_UPDATE_CLEARED_COUNTER)
                .tag(Constants.ACCOUNT_NAME_TAG, "")
                .register(meterRegistry)
        Counter.builder(Constants.ERROR_COUNTER)
                .tag(Constants.ACCOUNT_NAME_TAG, "")
                .tag(Constants.ERROR_TYPE_TAG, "")
                .register(meterRegistry)
    }

    enum class ErrorType(val errorMessage: String) {
        VALIDATION_ERROR("validation.error"),
        DELAY("delay.error");
    }

    fun incrementTransactionUpdateClearedCounter(accountName: String) {
        meterRegistry.counter(Constants.TRANSACTION_UPDATE_CLEARED_COUNTER, Constants.ACCOUNT_NAME_TAG, accountName).increment()
    }

    fun incrementErrorCounter(accountName: String, errorType: ErrorType) {
        meterRegistry.counter(Constants.ERROR_COUNTER, Constants.ERROR_TYPE_TAG,
                errorType.errorMessage, Constants.ACCOUNT_NAME_TAG, accountName).increment()
    }

    fun incrementTransactionSuccessfullyInsertedCounter(accountName: String) {
        meterRegistry.counter(Constants.TRANSACTION_SUCCESSFULLY_INSERTED_COUNTER, Constants.ACCOUNT_NAME_TAG, accountName).increment()
    }

    fun incrementTransactionAlreadyExistsCounter(accountName: String) {
        meterRegistry.counter(Constants.TRANSACTION_ALREADY_EXISTS_COUNTER, Constants.ACCOUNT_NAME_TAG, accountName).increment()
    }

    fun incrementTransactionReceivedCounter(accountName: String) {
        meterRegistry.counter(Constants.TRANSACTION_RECEIVED_EVENT_COUNTER, Constants.ACCOUNT_NAME_TAG, accountName).increment()
    }
}