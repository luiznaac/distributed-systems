from http.server import BaseHTTPRequestHandler
import json


def parse_request(request: BaseHTTPRequestHandler):
    content_len = int(request.headers.get('content-length', 0))
    if content_len == 0:
        return {}
    return json.loads(request.rfile.read(content_len).decode('utf-8'))


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
        request.wfile.write(bytes(json.dumps(response), 'utf8'))
