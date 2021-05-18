from datetime import datetime
import hashlib


class TransactionsManagement:

    transactions = {}

    def start_transaction(self, user_id):
        date_time = datetime.now().strftime("Y%m%d%%H%M%S")
        hash_generator = hashlib.sha256()

        hash_generator.update(bytes(user_id + date_time, 'utf-8'))
        tid = hash_generator.hexdigest()

        self.transactions[user_id] = tid
        return tid

    def commit_transaction(self, tid):
        if tid not in self.transactions.values():
            return False

        self.transactions = {user_id: user_tid for user_id, user_tid in self.transactions.items() if user_tid != tid}
        return True
