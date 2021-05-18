import os
import json


class Transaction:

    tid = None
    filepath = None
    has_running_operations = False
    did_any_operation_failed = False

    def __init__(self, tid):
        self.tid = tid
        self.filepath = 'data/transactions/' + self.tid + '/'
        os.makedirs(self.filepath)

    def insert_or_update(self, entity_name, params):
        file = open(self.filepath + entity_name + '.txt', 'w+')
        file.write(json.dumps({entity_name: params}, indent=2))
        file.close()

    def can_commit(self):
        return not self.has_running_operations and not self.did_any_operation_failed
