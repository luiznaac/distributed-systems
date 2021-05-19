from room_management import RoomManagement
from transactions_management import TransactionsManagement


class HotelManagement:

    rooms_management = None
    transactions_management = None

    def __init__(self):
        self.rooms_management = RoomManagement()
        self.transactions_management = TransactionsManagement()

    def book_room(self, params):
        transaction = self.transactions_management.start_or_get_transaction(params['userId'])
        transaction.insert_or_update(
            'rooms',
            {
                'id': params['roomNumber'],
                'userId': params['userId'],
                'status': 'not_available',
            }
        )
        return {'tid': transaction.tid}

    def can_commit(self, params):
        return {'response': self.transactions_management.get_transaction(params['tid']).can_commit()}

    def commit(self, params):
        response = self.transactions_management.get_transaction(params['tid']).commit()
        return {'status': 'committed' if response else 'tid not found'}

    def running_transactions(self, _):
        return {'transactions': self.transactions_management.user_tids}
