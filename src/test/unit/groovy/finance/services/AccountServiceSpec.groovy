package finance.services

import finance.domain.Account
import finance.repositories.AccountRepository
import finance.helpers.AccountBuilder
import io.micrometer.core.instrument.MeterRegistry
import spock.lang.Specification

class AccountServiceSpec extends Specification {
    AccountRepository accountRepository = Mock(AccountRepository)
    MeterService meterService = Mock(MeterService)
    AccountService accountService = new AccountService(accountRepository, meterService)

    def "test findByAccountNameOwner"() {
        given:
        Account account = AccountBuilder.builder().build()

        when:
        accountService.findByAccountNameOwner(account.accountNameOwner)

        then:
        //result.get().is(account)
        1 * accountRepository.findByAccountNameOwner(account.accountNameOwner) >> Optional.of(account)
        0 * _
    }

    def "test insertAccount" () {

        given:
        Account account = AccountBuilder.builder().build()

        when:
        accountService.insertAccount(account)

        then:
        1 * accountRepository.saveAndFlush(account) >> true
        0 * _
    }
}
