from transaction import Transaction


class RoomManagement:

    def set_room_not_available(self, transaction: Transaction, params):
        transaction.start_action(
            transaction.update,
            {
                'entity_name': 'rooms',
                'set_values': {
                    'id': params['roomNumber'],
                    'userId': params['userId'],
                    'status': 'not_available',
                },
                'conditions': {
                    'id': params['roomNumber'],
                    'userId': None,
                    'status': 'available',
                }
            }
        )

    def get_rooms(self, transaction: Transaction):
        return transaction.select('rooms', {})

    def get_available_rooms(self, transaction: Transaction):
        return transaction.select(
            'rooms',
            {
                'status': 'available',
            }
        )
