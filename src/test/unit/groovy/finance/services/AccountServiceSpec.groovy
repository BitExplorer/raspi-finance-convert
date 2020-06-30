package finance.services

import finance.domain.Account
import finance.helpers.AccountBuilder
import finance.repositories.AccountRepository
import spock.lang.Specification

class AccountServiceSpec extends Specification {
    AccountRepository mockAccountRepository = GroovyMock(AccountRepository)
    MeterService mockMeterService = GroovyMock(MeterService)
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

    def "test insertAccount"() {

        given:
        Account account = AccountBuilder.builder().build()

        when:
        def result = accountService.insertAccount(account)

        then:
        result.is(true)
        1 * mockAccountRepository.saveAndFlush(account) >> true
        0 * _
    }
}
