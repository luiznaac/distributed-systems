import os
import shutil
import persistence
from action import Action
from threading import Thread
import lock_management


class Transaction:

    def __init__(self, tid):
        self.tid = tid
        self.filepath = 'data/transactions/' + self.tid + '/'
        self.actions = []
        self.owned_locks = []
        os.makedirs(self.filepath)

    def start_action(self, desired_action, params):
        action = Action(desired_action, params)
        self.actions.append(action)
        thread_action = Thread(target=action.perform)
        thread_action.start()

    def update(self, params):
        entity_name = params['entity_name']
        set_values = params['set_values']
        conditions = params['conditions']
        self.get_lock(entity_name, conditions['id'])
        data_rows = self.get_rows_merged_with_transaction_ones(entity_name)

        if conditions['id'] not in data_rows.keys():
            return False

        row = data_rows[conditions['id']]

        if not self.all_conditions_apply(row, conditions):
            return False

        return self.temporarily_persist_entity_row(entity_name, row, set_values)

    def select(self, entity_name, conditions):
        rows_seen_by_transaction = self.get_rows_merged_with_transaction_ones(entity_name)

        return {key: row_data for key, row_data in rows_seen_by_transaction.items()
                if self.all_conditions_apply(row_data, conditions)}

    def commit(self):
        files = os.listdir(self.filepath)
        modified_entities = list(map(lambda file_name: file_name.strip('.txt'), files))

        for entity in modified_entities:
            self.persist_entity(entity)

        self.release_locks()
        shutil.rmtree(self.filepath)
        return True

    def rollback(self):
        try:
            self.release_locks()
            shutil.rmtree(self.filepath)
            return True
        except:
            return False

    def get_lock(self, entity_name, entity_id):
        lock_uid = lock_management.resolve().get_lock(self.tid, entity_name, entity_id)
        if lock_uid not in self.owned_locks:
            self.owned_locks.append(lock_uid)

    def release_locks(self):
        for lock_uid in self.owned_locks:
            lock_management.resolve().release_lock(lock_uid)

    def all_conditions_apply(self, row, conditions):
        for condition_key in conditions:
            desired_value = conditions[condition_key]
            actual_value = row[condition_key]
            if not desired_value == actual_value:
                return False
        return True

    def temporarily_persist_entity_row(self, entity_name, row, new_values):
        try:
            new_row = {new_values['id']: self.build_new_row(row, new_values)}
            temporarily_rows = persistence.get_file_data(self.filepath, entity_name)

            temporarily_rows = self.merge_rows(temporarily_rows, new_row)
            persistence.write_file_data(self.filepath, entity_name, {entity_name: temporarily_rows})
            return True
        except:
            return False

    def build_new_row(self, row, new_values):
        for value_kew in new_values:
            row[value_kew] = new_values[value_kew]
        return new_values

    def persist_entity(self, entity_name):
        new_rows = self.get_rows_merged_with_transaction_ones(entity_name)
        persistence.write_file_data('data/persisted/', entity_name, {entity_name: new_rows})

    def get_rows_merged_with_transaction_ones(self, entity_name):
        actual_rows = persistence.get_file_data('data/persisted/', entity_name)
        modified_rows = persistence.get_file_data(self.filepath, entity_name)

        return self.merge_rows(actual_rows, modified_rows)

    def merge_rows(self, rows_1: dict, rows_2: dict):
        for row_key in rows_2:
            if row_key in rows_1.keys():
                rows_1[row_key] = rows_2[row_key]
                continue
            rows_1.update({row_key: rows_2[row_key]})
        return rows_1

    def can_commit(self):
        return not self.has_running_actions() and not self.has_failed_actions()

    def has_failed_actions(self):
        for action in self.actions:
            if action.status == 'failed':
                return True
        return False

    def has_running_actions(self):
        for action in self.actions:
            while action.status == 'running':
                pass
        return False
