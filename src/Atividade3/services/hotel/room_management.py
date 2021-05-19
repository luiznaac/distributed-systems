class RoomManagement:

    rooms = {}

    def __init__(self):
        for i in range(1, 20):
            self.rooms.update({i: 'available'})

    def set_room_not_available(self, room_number):
        self.rooms[room_number] = 'not_available'
