class Transaction:

    tid = None
    has_running_operations = False
    did_any_operation_failed = False

    def __init__(self, tid):
        self.tid = tid

    def can_commit(self):
        return not self.has_running_operations and not self.did_any_operation_failed
