from room_management import RoomManagement


class HotelManagement:

    rooms_management = None

    def __init__(self):
        self.rooms_management = RoomManagement()

    def book_room(self, params):
        self.rooms_management.set_room_not_available(int(params['room_number']))
        return self.rooms_management.rooms
