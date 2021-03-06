from hotel.room_management import RoomManagement
from transactions_management import TransactionsManagement


class HotelManagement:

    rooms_management = None
    transactions_management = None

    def __init__(self):
        self.rooms_management = RoomManagement()
        self.transactions_management = TransactionsManagement()

    def book_room(self, params):
        transaction = self.transactions_management.start_or_get_transaction(params['userId'])
        self.rooms_management.set_room_not_available(transaction, params)
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

    def get_rooms(self, params):
        transaction = self.transactions_management.get_transaction(params['tid'])
        rooms = self.rooms_management.get_rooms(transaction)
        return {'rooms': rooms}

    def get_available_rooms(self, params):
        transaction = self.transactions_management.get_transaction(params['tid'])
        rooms = self.rooms_management.get_available_rooms(transaction)
        return {'rooms': rooms}

    def running_transactions(self, _):
        return {'transactions': self.transactions_management.user_tids}
