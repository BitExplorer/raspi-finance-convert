package finance.services

import finance.domain.Account
import finance.repositories.AccountRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import java.util.*
import java.util.Optional.empty

@Service
class AccountService @Autowired constructor(
        private val accountRepository: AccountRepository,
        private val meterService: MeterService
) {
    fun findByAccountNameOwner(accountNameOwner: String): Optional<Account> {
        logger.info(accountNameOwner)

        val accountOptional: Optional<Account> = accountRepository.findByAccountNameOwner(accountNameOwner)
        if (accountOptional.isPresent) {
            return accountOptional
        }
        return empty()
    }

    //@Timed("insert.account.timer")
    fun insertAccount(account: Account): Boolean {
        logger.debug("insertAccount")
        try {
            accountRepository.saveAndFlush(account)
//        } catch ( jsicv: JdbcSQLIntegrityConstraintViolationException) {
//            //meterRegistry.counter(Constants.METRIC_DUPLICATE_ACCOUNT_INSERT_ATTEMPT_COUNTER).increment()
//            logger.info("accountRepository.saveAndFlush(account) - JdbcSQLIntegrityConstraintViolationException")
//            return false
        } catch (dive: DataIntegrityViolationException) {
            logger.info("accountRepository.saveAndFlush(account)" + dive.message)
            return false
        } catch (e: Exception) {
            logger.info("accountRepository.saveAndFlush(account)" + e.message)
            return false
        }
        return true
    }

    companion object {
        val logger: Logger
            get() = LoggerFactory.getLogger(AccountService::class.java)
    }
}