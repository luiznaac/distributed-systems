import os
import shutil
import persistence


class Transaction:

    tid = None
    filepath = None
    has_running_operations = False
    did_any_operation_failed = False

    def __init__(self, tid):
        self.tid = tid
        self.filepath = 'data/transactions/' + self.tid + '/'
        os.makedirs(self.filepath)

    def insert_or_update(self, entity_name, set_values, conditions):
        data_rows = persistence.get_file_data('data/persisted/', entity_name)

        if conditions['id'] not in data_rows.keys():
            return False

        row = data_rows[conditions['id']]

        if not self.all_conditions_apply(row, conditions):
            return False

        persistence.write_file_data(self.filepath, entity_name, {entity_name: {set_values['id']: set_values}})

    def commit(self):
        files = os.listdir(self.filepath)
        modified_entities = list(map(lambda file_name: file_name.strip('.txt'), files))

        for entity in modified_entities:
            self.persist_entity(entity)

        shutil.rmtree(self.filepath)
        return True

    def rollback(self):
        try:
            shutil.rmtree(self.filepath)
            return True
        except:
            return False

    def all_conditions_apply(self, row, conditions):
        for condition_key in conditions:
            desired_value = conditions[condition_key]
            actual_value = row[condition_key]
            if not desired_value == actual_value:
                return False
        return True

    def persist_entity(self, entity_name):
        modified_rows = persistence.get_file_data(self.filepath, entity_name)
        actual_rows = persistence.get_file_data('data/persisted/', entity_name)

        for modified_row_key in modified_rows:
            actual_rows[modified_row_key] = modified_rows[modified_row_key]

        persistence.write_file_data('data/persisted/', entity_name, {entity_name: actual_rows})

    def can_commit(self):
        return not self.has_running_operations and not self.did_any_operation_failed
