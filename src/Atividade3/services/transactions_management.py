from datetime import datetime
import hashlib
from transaction import Transaction
from typing import Optional
import random


class TransactionsManagement:

    user_tids = {}
    transactions = {}

    def start_or_get_transaction(self, user_id) -> Transaction:
        if user_id in self.user_tids.keys():
            return self.transactions[self.user_tids[user_id]]

        date_time = datetime.now().strftime("Y%m%d%%H%M%S")
        hash_generator = hashlib.sha256()

        hash_generator.update(bytes(user_id + date_time + str(random.randint(0, 200)), 'utf-8'))
        tid = hash_generator.hexdigest()

        transaction = Transaction(tid)
        self.transactions.update({tid: transaction})
        self.user_tids[user_id] = tid

        return transaction

    def get_transaction(self, tid) -> Optional[Transaction]:
        if tid in self.transactions.keys():
            return self.transactions[tid]
        return self.start_or_get_transaction('ROOT')

    def terminate_transaction(self, tid):
        self.user_tids = {user_id: user_tid for user_id, user_tid in self.user_tids.items() if user_tid != tid}
        self.transactions.pop(tid, None)
