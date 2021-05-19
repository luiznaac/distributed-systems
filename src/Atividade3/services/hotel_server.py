from http.server import BaseHTTPRequestHandler, HTTPServer
from request_handler import RequestHandler
from hotel.hotel_management import HotelManagement

request_handler: RequestHandler


class Handler(BaseHTTPRequestHandler):
    def do_POST(self):
        request_handler.handle(self)

    def log_message(self, format, *args):
        pass


def run(server_class=HTTPServer, handler_class=Handler):
    create_context()
    this_client_address = ('', 8080)
    httpd = server_class(this_client_address, handler_class)
    httpd.serve_forever()


def create_context():
    global request_handler
    hotel_management = HotelManagement()
    request_handler = RequestHandler()

    request_handler.link_handler('/bookRoom', hotel_management.book_room)
    request_handler.link_handler('/canCommit', hotel_management.can_commit)
    request_handler.link_handler('/finishTransaction', hotel_management.commit)
    request_handler.link_handler('/rollbackTransaction', hotel_management.rollback)
    request_handler.link_handler('/getRooms', hotel_management.get_rooms)
    request_handler.link_handler('/getAvailableRooms', hotel_management.get_available_rooms)
    request_handler.link_handler('/runningTransactions', hotel_management.running_transactions)


if __name__ == '__main__':
    run()
