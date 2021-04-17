from http.server import BaseHTTPRequestHandler, HTTPServer
from time import sleep
from threading import Thread
import json
import requests

server_address = 'http://localhost:8080'

port = None
waiting_for_resource = False
resource_available = False


class Handler(BaseHTTPRequestHandler):
    def do_POST(self):
        if self.path == '/resource-available':
            set_resource_available()

        self.send_response(200)
        self.send_header('Content-type', 'application/json')
        self.end_headers()
        self.wfile.write(bytes(json.dumps({'message': 'ok'}), 'utf8'))

    def log_message(self, format, *args):
        pass


def make_request_to_server(request, request_content):
    global port
    url = server_address + request
    request_body = {**{'port': port}, **request_content}

    response = requests.post(url, data=request_body)

    return response.json()


def check_resource_availability(resource):
    global waiting_for_resource, resource_available
    response = make_request_to_server('/check-resource', {'resource': resource})
    is_available = response['available']

    if is_available:
        resource_available = True
        return

    resource_available = False
    waiting_for_resource = True


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

    this_client_address = ('', port)
    httpd = server_class(this_client_address, handler_class)
    httpd.serve_forever()


if __name__ == '__main__':
    run()
