package finance.routes

import spock.lang.PendingFeature
import spock.lang.Specification

class TransactionToSedaQueueRouteSpec extends Specification {
    TransactionToDatabaseRoute processEachTransactionRoute = new TransactionToDatabaseRoute()

    @PendingFeature
    def "test TransactionToSedaQueueRoute"() {
        1==2
    }
}
