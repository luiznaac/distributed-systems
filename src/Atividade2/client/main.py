from http.server import BaseHTTPRequestHandler, HTTPServer
from time import sleep
from threading import Thread

port = None
waiting_for_resource = False
resource_available = False


class Handler(BaseHTTPRequestHandler):
    def do_POST(self):
        if self.path == '/resource-available':
            set_resource_available()
        self.send_response(200)
        self.send_header('Content-type', 'text/html')
        self.end_headers()
        self.wfile.write(bytes('Ok', "utf8"))

    def log_message(self, format, *args):
        pass


def check_resource_availability(resource):
    global waiting_for_resource, resource_available
    if resource == 1:
        waiting_for_resource = True
        resource_available = False
        return

    resource_available = True


def set_resource_available():
    global resource_available
    resource_available = True


def access_resource(resource):
    global waiting_for_resource
    print('Accessing resource ' + str(resource))
    waiting_for_resource = False


def client_function():
    desired_resource = None
    while True:
        sleep(1)

        if not waiting_for_resource:
            desired_resource = int(input('Qual recurso desejas acessar? '))
            check_resource_availability(desired_resource)

        if resource_available:
            access_resource(desired_resource)
            continue


def run(server_class=HTTPServer, handler_class=Handler):
    global port
    port = int(input('Client port: '))

    client_thread = Thread(target=client_function)
    client_thread.start()

    server_address = ('', port)
    httpd = server_class(server_address, handler_class)
    httpd.serve_forever()


if __name__ == '__main__':
    run()
