from transaction import Transaction

class RoomManagement:

    def set_room_not_available(self, transaction: Transaction, params):
        transaction.update(
            'rooms',
            {
                'id': params['roomNumber'],
                'userId': params['userId'],
                'status': 'not_available',
            },
            {
                'id': params['roomNumber'],
                'userId': None,
                'status': 'available',
            }
        )
