from room_management import RoomManagement
from transactions_management import TransactionsManagement


class HotelManagement:

    rooms_management = None
    transactions_management = None

    def __init__(self):
        self.rooms_management = RoomManagement()
        self.transactions_management = TransactionsManagement()

    def book_room(self, params):
        user_tid = self.transactions_management.start_or_get_transaction(params['user_id'])
        self.rooms_management.set_room_not_available(int(params['room_number']))
        return {'tid': user_tid}

    def commit(self, params):
        response = self.transactions_management.commit_transaction(params['tid'])
        return {'status': 'committed' if response else 'tid not found'}

    def running_transactions(self, _):
        return {'transactions': self.transactions_management.transactions}
