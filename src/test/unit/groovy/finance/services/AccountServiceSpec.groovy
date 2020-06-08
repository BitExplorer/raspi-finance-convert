package finance.services

import finance.domain.Account
import finance.repositories.AccountRepository
import finance.helpers.AccountBuilder
import spock.lang.Specification

class AccountServiceSpec extends Specification {
    AccountRepository mockAccountRepository = Mock(AccountRepository)
    MeterService mockMeterService = Mock(MeterService)
    AccountService accountService = new AccountService(mockAccountRepository, mockMeterService)

    def "test findByAccountNameOwner"() {
        given:
        Account account = AccountBuilder.builder().build()

        when:
        accountService.findByAccountNameOwner(account.accountNameOwner)

        then:
        //result.get().is(account)
        1 * mockAccountRepository.findByAccountNameOwner(account.accountNameOwner) >> Optional.of(account)
        0 * _
    }

    def "test insertAccount" () {

        given:
        Account account = AccountBuilder.builder().build()

        when:
        accountService.insertAccount(account)

        then:
        1 * mockAccountRepository.saveAndFlush(account) >> true
        0 * _
    }
}
