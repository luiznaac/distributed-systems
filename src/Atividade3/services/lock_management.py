import time

instance = None


def resolve():
    global instance
    instance = instance if instance else LockManagement()
    return instance


class LockManagement:

    locks = {}

    def get_lock(self, tid, entity_name, entity_id):
        lock_uid = entity_name + '_' + entity_id

        if lock_uid in self.locks.keys() and self.locks[lock_uid] == tid:
            return lock_uid

        while lock_uid in self.locks.keys() and self.locks[lock_uid] is not None:
            time.sleep(0.1)
            pass

        if lock_uid not in self.locks.keys():
            self.locks.update({lock_uid: tid})
            return lock_uid

        self.locks[lock_uid] = tid
        return lock_uid

    def release_lock(self, lock_uid):
        self.locks[lock_uid] = None
