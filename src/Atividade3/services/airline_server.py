from http.server import BaseHTTPRequestHandler, HTTPServer
from request_handler import RequestHandler
from airline.airline_management import AirlineManagement

request_handler: RequestHandler


class Handler(BaseHTTPRequestHandler):
    def do_POST(self):
        request_handler.handle(self)

    def log_message(self, format, *args):
        pass


def run(server_class=HTTPServer, handler_class=Handler):
    create_context()
    this_client_address = ('', 8082)
    httpd = server_class(this_client_address, handler_class)
    httpd.serve_forever()


def create_context():
    global request_handler
    airline_management = AirlineManagement()
    request_handler = RequestHandler()

    request_handler.link_handler('/bookSeat', airline_management.book_seat)
    request_handler.link_handler('/canCommit', airline_management.can_commit)
    request_handler.link_handler('/finishTransaction', airline_management.commit)
    request_handler.link_handler('/rollbackTransaction', airline_management.rollback)
    request_handler.link_handler('/getSeats', airline_management.get_seats)
    request_handler.link_handler('/getAvailableSeats', airline_management.get_available_seats)
    request_handler.link_handler('/runningTransactions', airline_management.running_transactions)


if __name__ == '__main__':
    run()
