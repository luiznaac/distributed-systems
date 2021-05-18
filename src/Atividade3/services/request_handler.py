from http.server import BaseHTTPRequestHandler
import json


def parse_request(request: BaseHTTPRequestHandler):
    content_len = int(request.headers.get('content-length', 0))
    return {param.split('=')[0]: param.split('=')[1] for param in request.rfile.read(content_len).decode('utf-8').split('&')}


class RequestHandler:

    handlers_link = {}

    def link_handler(self, endpoint, handler):
        self.handlers_link.update({endpoint: handler})

    def handle(self, request: BaseHTTPRequestHandler):
        handler = self.handlers_link[request.path]

        response = handler(parse_request(request))

        request.send_response(200)
        request.send_header('Content-type', 'application/json')
        request.end_headers()
        request.wfile.write(bytes(json.dumps({'message': response}), 'utf8'))
