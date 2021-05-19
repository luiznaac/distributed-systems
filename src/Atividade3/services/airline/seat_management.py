from transaction import Transaction


class SeatManagement:

    def set_seat_not_available(self, transaction: Transaction, params):
        transaction.start_action(
            transaction.update,
            {
                'entity_name': 'seats',
                'set_values': {
                    'id': params['seatNumber'],
                    'userId': params['userId'],
                    'status': 'not_available',
                },
                'conditions': {
                    'id': params['seatNumber'],
                    'userId': None,
                    'status': 'available',
                }
            }
        )

    def get_seats(self, transaction: Transaction):
        return transaction.select('seats', {})

    def get_available_seats(self, transaction: Transaction):
        return transaction.select(
            'seats',
            {
                'status': 'available',
            }
        )
