from airline.seat_management import SeatManagement
from transactions_management import TransactionsManagement


class AirlineManagement:

    seats_management = None
    transactions_management = None

    def __init__(self):
        self.seats_management = SeatManagement()
        self.transactions_management = TransactionsManagement()

    def book_seat(self, params):
        transaction = self.transactions_management.start_or_get_transaction(params['userId'])
        self.seats_management.set_seat_not_available(transaction, params)
        return {'tid': transaction.tid}

    def can_commit(self, params):
        return {'response': self.transactions_management.get_transaction(params['tid']).can_commit()}

    def commit(self, params):
        response = self.transactions_management.get_transaction(params['tid']).commit()
        self.transactions_management.terminate_transaction(params['tid'])
        return {'status': 'committed' if response else 'tid not found'}

    def rollback(self, params):
        response = self.transactions_management.get_transaction(params['tid']).rollback()
        self.transactions_management.terminate_transaction(params['tid'])
        return {'status': 'rollbacked' if response else 'tid not found'}

    def get_seats(self, params):
        transaction = self.transactions_management.get_transaction(params['tid'])
        seats = self.seats_management.get_seats(transaction)
        return {'seats': seats}

    def get_available_seats(self, params):
        transaction = self.transactions_management.get_transaction(params['tid'])
        seats = self.seats_management.get_available_seats(transaction)
        return {'seats': seats}

    def running_transactions(self, _):
        return {'transactions': self.transactions_management.user_tids}
